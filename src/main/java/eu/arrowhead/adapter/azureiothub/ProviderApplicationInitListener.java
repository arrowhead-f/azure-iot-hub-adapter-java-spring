package eu.arrowhead.adapter.azureiothub;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.arrowhead.adapter.azureiothub.configuration.AzureIoTHubConfProperties;
import eu.arrowhead.adapter.azureiothub.data.IoTHubData;
import eu.arrowhead.common.dto.shared.ServiceRegistryRequestDTO;
import eu.arrowhead.common.dto.shared.ServiceSecurityType;
import eu.arrowhead.common.dto.shared.SystemRequestDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpMethod;

import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.client.library.config.ApplicationInitListener;
import eu.arrowhead.client.library.util.ClientCommonConstants;
import eu.arrowhead.adapter.azureiothub.security.ProviderSecurityConfig;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.core.CoreSystem;
import eu.arrowhead.common.exception.ArrowheadException;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubConsumerAsyncClient;

@Component
public class ProviderApplicationInitListener extends ApplicationInitListener {
	
	//=================================================================================================
	// members
	
	@Autowired
	private ArrowheadService arrowheadService;
	
	@Autowired
	private ProviderSecurityConfig providerSecurityConfig;

	@Autowired
	private AzureIoTHubConfProperties iotHubConfig;

	@Autowired
	private DataSingleton<IoTHubData> dataSingleton;
	
	@Value(ClientCommonConstants.$TOKEN_SECURITY_FILTER_ENABLED_WD)
	private boolean tokenSecurityFilterEnabled;
	
	@Value(CommonConstants.$SERVER_SSL_ENABLED_WD)
	private boolean sslEnabled;

	@Value(ClientCommonConstants.$CLIENT_SYSTEM_NAME)
	private String mySystemName;
	
	@Value(ClientCommonConstants.$CLIENT_SERVER_ADDRESS_WD)
	private String mySystemAddress;
	
	@Value(ClientCommonConstants.$CLIENT_SERVER_PORT_WD)
	private int mySystemPort;

	private final Logger logger = LogManager.getLogger(ProviderApplicationInitListener.class);
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit(final ContextRefreshedEvent event) {

		//Checking the availability of necessary core systems
		checkCoreSystemReachability(CoreSystem.SERVICE_REGISTRY);
		if (sslEnabled && tokenSecurityFilterEnabled) {
			checkCoreSystemReachability(CoreSystem.AUTHORIZATION);			

			//Initialize Arrowhead Context
			arrowheadService.updateCoreServiceURIs(CoreSystem.AUTHORIZATION);			
		
			setTokenSecurityFilter();
		
		}else {
			logger.info("TokenSecurityFilter in not active");
		}

		// Azure - Build the Event Hubs compatible connection string.
		final String eventHubCompatibleConnectionString = String.format(
				"Endpoint=%s/;EntityPath=%s;SharedAccessKeyName=%s;SharedAccessKey=%s",
				iotHubConfig.getEventHubsCompatibleEndpoint(),
				iotHubConfig.getEventHubsCompatiblePath(),
				iotHubConfig.getSasKeyName(),
				iotHubConfig.getSasKey()
		);

		// Azure - Setup the EventHubBuilder by configuring various options as needed.
		final EventHubClientBuilder eventHubClientBuilder = new EventHubClientBuilder()
				.consumerGroup(EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME)
				.connectionString(eventHubCompatibleConnectionString);

		// Azure - Create an async consumer client as configured in the builder.
		final EventHubConsumerAsyncClient eventHubConsumerAsyncClient = eventHubClientBuilder.buildAsyncConsumerClient();
		eventHubConsumerAsyncClient
				.receive(false) // set this to false to read only the newly available events
				.subscribe(partitionEvent -> {
					final ObjectMapper mapper = new ObjectMapper();
					try {
						dataSingleton.setData(mapper.readValue(partitionEvent.getData().getBodyAsString(), IoTHubData.class));
					}
					catch (final IOException ex) {
						logger.error(ex.getMessage(), ex);
						throw new ArrowheadException(ex.getMessage());
					}
				}, ex -> {
					logger.error("Error receiving events: ", ex);
				}, () -> {
					logger.info("Completed receiving events");
				});

        //TODO: register your services here

        //Register services into ServiceRegistry
        final ServiceRegistryRequestDTO temperatureServiceRequest = createServiceRegistryRequest(
                ProviderConstants.TEMPERATURE_SERVICE_DEFINITION,
                ProviderConstants.TEMPERATURE_URI,
                HttpMethod.GET
        );
        temperatureServiceRequest.getMetadata().put(ProviderConstants.MEASUREMENT_UNIT, ProviderConstants.TEMPERATURE_UNIT);
        arrowheadService.forceRegisterServiceToServiceRegistry(temperatureServiceRequest);

        final ServiceRegistryRequestDTO humidityServiceRequest = createServiceRegistryRequest(
                ProviderConstants.HUMIDITY_SERVICE_DEFINITION,
                ProviderConstants.HUMIDITY_URI,
                HttpMethod.GET
        );
        humidityServiceRequest.getMetadata().put(ProviderConstants.MEASUREMENT_UNIT, ProviderConstants.HUMIDITY_UNIT);
        arrowheadService.forceRegisterServiceToServiceRegistry(humidityServiceRequest);
	}
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public void customDestroy() {
		//TODO: implement here any custom behavior on application shout down
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private void setTokenSecurityFilter() {
		final PublicKey authorizationPublicKey = arrowheadService.queryAuthorizationPublicKey();
		if (authorizationPublicKey == null) {
			throw new ArrowheadException("Authorization public key is null");
		}
		
		KeyStore keystore;
		try {
			keystore = KeyStore.getInstance(sslProperties.getKeyStoreType());
			keystore.load(sslProperties.getKeyStore().getInputStream(), sslProperties.getKeyStorePassword().toCharArray());
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException ex) {
			throw new ArrowheadException(ex.getMessage());
		}			
		final PrivateKey providerPrivateKey = Utilities.getPrivateKey(keystore, sslProperties.getKeyPassword());
		
		providerSecurityConfig.getTokenSecurityFilter().setAuthorizationPublicKey(authorizationPublicKey);
		providerSecurityConfig.getTokenSecurityFilter().setMyPrivateKey(providerPrivateKey);

	}

	//-------------------------------------------------------------------------------------------------
	private ServiceRegistryRequestDTO createServiceRegistryRequest(final String serviceDefinition, final String serviceUri, final HttpMethod httpMethod) {
		final ServiceRegistryRequestDTO serviceRegistryRequest = new ServiceRegistryRequestDTO();
		serviceRegistryRequest.setServiceDefinition(serviceDefinition);
		final SystemRequestDTO systemRequest = new SystemRequestDTO();
		systemRequest.setSystemName(mySystemName);
		systemRequest.setAddress(mySystemAddress);
		systemRequest.setPort(mySystemPort);		

		if (tokenSecurityFilterEnabled) {
			systemRequest.setAuthenticationInfo(Base64.getEncoder().encodeToString(arrowheadService.getMyPublicKey().getEncoded()));
			serviceRegistryRequest.setSecure(ServiceSecurityType.TOKEN.name());
			serviceRegistryRequest.setInterfaces(List.of(ProviderConstants.INTERFACE_SECURE));
		} else if (sslEnabled) {
			systemRequest.setAuthenticationInfo(Base64.getEncoder().encodeToString(arrowheadService.getMyPublicKey().getEncoded()));
			serviceRegistryRequest.setSecure(ServiceSecurityType.CERTIFICATE.name());
			serviceRegistryRequest.setInterfaces(List.of(ProviderConstants.INTERFACE_SECURE));
		} else {
			serviceRegistryRequest.setSecure(ServiceSecurityType.NOT_SECURE.name());
			serviceRegistryRequest.setInterfaces(List.of(ProviderConstants.INTERFACE_INSECURE));
		}
		serviceRegistryRequest.setProviderSystem(systemRequest);
		serviceRegistryRequest.setServiceUri(serviceUri);
		serviceRegistryRequest.setMetadata(new HashMap<>());
		serviceRegistryRequest.getMetadata().put(ProviderConstants.HTTP_METHOD, httpMethod.name());
		return serviceRegistryRequest;
	}
}
