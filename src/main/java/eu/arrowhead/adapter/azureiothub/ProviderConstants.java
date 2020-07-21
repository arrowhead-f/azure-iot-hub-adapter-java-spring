package eu.arrowhead.adapter.azureiothub;

public class ProviderConstants {

    //=================================================================================================
    // members

    //TODO: add your service constants here

    // Temperature Service
    public static final String TEMPERATURE_SERVICE_DEFINITION = "temperature";
    public static final String TEMPERATURE_URI = "/get-temperature";
    public static final String TEMPERATURE_UNIT = "celsius";

    // Humidity Service
    public static final String HUMIDITY_SERVICE_DEFINITION = "humidity";
    public static final String HUMIDITY_URI = "/get-humidity";
    public static final String HUMIDITY_UNIT = "percentage";

    //-------------------------------------------------------------------------------------------------

    // Interface and Key Constants
    public static final String INTERFACE_SECURE = "HTTPS-SECURE-JSON";
    public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
    public static final String HTTP_METHOD = "http-method";
    public static final String MEASUREMENT_UNIT = "unit";

    //=================================================================================================
    // assistant methods

    //-------------------------------------------------------------------------------------------------
    private ProviderConstants() {
        throw new UnsupportedOperationException();
    }
}
