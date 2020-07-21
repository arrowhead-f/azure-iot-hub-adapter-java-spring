# Arrowhead Adapter for Azure IoT Hub (Java Spring-Boot)
##### The project provides an adapter for data coming from Azure IoT Hub through Event Hubs. This project complies with the the Arrowhead Framework 4.1.3.

Introduction here...

### Requirements

The project has the following dependencies:
* JRE/JDK 11 [Download from here](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html)
* Maven 3.5+ [Download from here](http://maven.apache.org/download.cgi) | [Install guide](https://www.baeldung.com/install-maven-on-windows-linux-mac)
* GitHub Packages [Configuring Maven for GitHub Packages](https://help.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-apache-maven-for-use-with-github-packages)

### Quick Start Guide

#### Compile & Run

Navigate to the project root directory and compile with Maven:

```shell
$ mvn install
```

then run the application jar that is created in the `target/` folder:

```shell
$ java -jar ./target/azure-iot-hub-adapter-4.1.3.7.jar
```

#### Certificates

In order to be able to register your services into a secure Arrowhead Cloud you need to generate the appropriate certificates. [Create](https://github.com/arrowhead-f/core-java-spring#certificates) your own client certificate or for demo purpose use the provided one (**Note** that the provided one only works for a local cloud since only localhost was added to *Subject Alternative Name*).

#### application.properties

Modify the properties in accordance with your needs.

Location: `src/main/resources`

##### *Secure Mode*

* Decide the required security level and set the `server.ssl.enabled` and `token.security.filter.enabled` properties accordingly.
* If `token.security.filter.enabled` is true, `server.ssl.enabled` also has to be true!
* [Create](https://github.com/arrowhead-f/core-java-spring#certificates) your own client certificate (or for demo purpose use the provided one) and update the further `server.ssl...` properties accordingly. *(**Note** that `server.ssl.key-store-password` and `server.ssl.key-password` must be the same.)*

##### *Azure IoT Hub Configuration*

This project assumes that you have already got an IoT Hub resource set up and it is sending its data in JSON format.

You can get your configuration properties by opening Azure Shell and typing the following command lines:

* for *event-hubs-compatible-endpoint* run 
```
az iot hub show --query properties.eventHubEndpoints.events.endpoint --name {your IoT Hub name}
```
* for *event-hubs-compatible-path* run 
```
az iot hub show --query properties.eventHubEndpoints.events.path --name {your IoT Hub name}
```
* for *sas-key* run 
```
az iot hub policy show --name service --query primaryKey --hub-name {your IoT Hub name}
``` 

For further help with setting the IoT Hub configuration properties refer to the following [article](https://docs.microsoft.com/en-us/azure/iot-hub/iot-hub-live-data-visualization-in-web-apps) 

##### *Custom Parameters*

* Change the `client_system_name` property to your system name. *(**Note** that it should be in line with your certificate common name e.g.: when your certificate common name is `my_awesome_client.my_cloud.my_company.arrowhed.eu`, then your system name is  `my_awesome_client`)*
* Adjust the Service Registry Core System location by the `sr_address` and `sr_port` properties.
* Set the provider's web-server parameters by the `server.address` and `server.port` properties.

#### Project

In `ProviderConstants.class` add your service constants that you will use in the further parts of the application code.

The `ProviderApplicationInitListener.class` initializes the Azure Event Hub and registers the services that this client is going to provide. *(**Look for the 'TODO' mark** within this class where you can register your own services)*

The `IoTHubData.class` represents the data coming from the IoTHub. (In `ProviderApplicationInitListener.class` the incoming JSON is mapped to this class and this will be available globally in the application through the `DataSingleton.class` bean.) Add your variables here in accordance with your data structure. Don't forget to add getters for the variables as well.

Implement your service provider related REST endpoints in `ProviderController.class`. The `IoTHubData` object is also available here through the `DataSingleton.class` bean. The endpoints can return with a response in SenML format or you can also create your own response format.

### Example

Instead of providing an empty skeleton, this project comes with a direct example in implementing an adapter. It assumes a [DHT11](https://www.mouser.com/datasheet/2/758/DHT11-Technical-Data-Sheet-Translated-Version-1143054.pdf) sensor with an ESP32 microcontroller that sends measurement data to the IoTHub in the following format:

```json
{"deviceId":"aitia-esp32-1", "messageId":18,"temperature":26.600000, "relative_humidity":62.000000}
```
From the JSON Object above only the `temperature` and `relative_humidity` properties are mapped to a `IoTHubData` object. The provider registers two services, a *temperature* and a `humidity` service. The REST endpoints are implemented that are returning with a response (the actual temperature or humidity) in SenML format. (There is also a custom response DTO class for further example.)

If you use the provided `azuretesttemperature.p12` certificate (for demo purposes only) make sure you leave the client system name as *azuretesttemperature*.
