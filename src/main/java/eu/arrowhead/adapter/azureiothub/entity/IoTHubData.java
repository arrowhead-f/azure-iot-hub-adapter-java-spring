package eu.arrowhead.adapter.azureiothub.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class IoTHubData {

    //=================================================================================================
    // members

    //TODO: add your variables here that are coming from the IoT Hub (JSON Object)

    @JsonProperty("temperature") // property name in your incoming JSON Object
    private Float temperature; // property name in this application code

    @JsonProperty("relative_humidity") // property name in your incoming JSON Object
    private Float humidity; // property name in this application code

    //=================================================================================================
    // methods

    //-------------------------------------------------------------------------------------------------

    //TODO: add your getters here

    public Float getTemperature() { return temperature; }
    public Float getHumidity() { return humidity; }
}
