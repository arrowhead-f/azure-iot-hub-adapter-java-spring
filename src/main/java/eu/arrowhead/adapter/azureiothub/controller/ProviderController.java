package eu.arrowhead.adapter.azureiothub.controller;

import eu.arrowhead.adapter.azureiothub.DataSingleton;
import eu.arrowhead.adapter.azureiothub.ProviderConstants;
import eu.arrowhead.adapter.azureiothub.dto.CustomResponseDTO;
import eu.arrowhead.adapter.azureiothub.dto.SenMLDTO;
import eu.arrowhead.adapter.azureiothub.dto.SenMLMeasurementDTO;
import eu.arrowhead.adapter.azureiothub.entity.IoTHubData;
import eu.arrowhead.client.library.util.ClientCommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eu.arrowhead.common.CommonConstants;

@RestController
public class ProviderController {
	
	//=================================================================================================
	// members

	@Autowired
	private DataSingleton<IoTHubData> dataSingleton;

	@Value(ClientCommonConstants.$CLIENT_SYSTEM_NAME)
	private String systemName;

	//TODO: add your variables here

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = CommonConstants.ECHO_URI)
	public String echoService() {
		return "Got it!";
	}
	
	//-------------------------------------------------------------------------------------------------
	//TODO: implement here your provider related REST endpoints

	@GetMapping(path = ProviderConstants.TEMPERATURE_URI)
	@ResponseBody
	public SenMLDTO getTemperature() { // return temperature measurement data in SenML format
		SenMLDTO response = new SenMLDTO(systemName, ProviderConstants.TEMPERATURE_UNIT, 1);
		response.addMeasurement(new SenMLMeasurementDTO(systemName, dataSingleton.getData().getTemperature().toString()));
		return response;
	}

	@GetMapping(path = ProviderConstants.HUMIDITY_URI)
	@ResponseBody
	public SenMLDTO getHumidity() { // return humidity measurement data in SenML format
		SenMLDTO response = new SenMLDTO(systemName, ProviderConstants.HUMIDITY_UNIT, 1);
		response.addMeasurement(new SenMLMeasurementDTO(systemName, dataSingleton.getData().getHumidity().toString()));
		return response;
	}

	/*@GetMapping(path = ProviderConstants.TEMPERATURE_URI)
	@ResponseBody
	public CustomResponseDTO getTemperature() { // return temperature measurement data in custom response format
		CustomResponseDTO response = new CustomResponseDTO(dataSingleton.getData().getTemperature().toString(), ProviderConstants.TEMPERATURE_UNIT);
		return response;
	}*/
}
