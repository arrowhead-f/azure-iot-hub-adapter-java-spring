package eu.arrowhead.adapter.azureiothub.dto;

import java.io.Serializable;

public class CustomResponseDTO implements Serializable {

    //=================================================================================================
    // members

    private String temperature;
    private String unit;

    //=================================================================================================
    // methods

    //-------------------------------------------------------------------------------------------------

    public CustomResponseDTO(final String temperature, final String unit) {
        this.temperature = temperature;
        this.unit = unit;
    }

    //-------------------------------------------------------------------------------------------------

    public String getTemperature() { return temperature; }
    public String getUnit() { return unit; }

    //-------------------------------------------------------------------------------------------------

    public void setTemperature(final String temperature) { this.temperature = temperature; }
    public void setUnit(final String unit) { this.unit = unit; }

}
