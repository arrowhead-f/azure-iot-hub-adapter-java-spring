package eu.arrowhead.adapter.azureiothub.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class IoTHubData {

    //=================================================================================================
    // members

    //TODO: add your variables here that are coming from the IoT Hub (JSON Object)

    @JsonProperty("temperature") // property name in your incoming JSON Object
    private Double temperature; // property name in this application code

    @JsonProperty("relative_humidity") // property name in your incoming JSON Object
    private Double humidity; // property name in this application code

    //=================================================================================================
    // methods

    //-------------------------------------------------------------------------------------------------

    //TODO: add your getters here

    public Double getTemperature() { return temperature; }
    public Double getHumidity() { return humidity; }
}
