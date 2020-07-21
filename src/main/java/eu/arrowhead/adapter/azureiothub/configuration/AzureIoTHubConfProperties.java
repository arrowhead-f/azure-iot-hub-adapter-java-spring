package eu.arrowhead.adapter.azureiothub.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "iothub")
public class AzureIoTHubConfProperties {

    //=================================================================================================
    // members

    private String eventHubsCompatibleEndpoint;
    private String eventHubsCompatiblePath;
    private String sasKey;
    private String sasKeyName;


    //=================================================================================================
    // methods

    //-------------------------------------------------------------------------------------------------

    public String getEventHubsCompatibleEndpoint() { return eventHubsCompatibleEndpoint; }
    public String getEventHubsCompatiblePath() { return eventHubsCompatiblePath; }
    public String getSasKey() { return sasKey; }
    public String getSasKeyName() { return sasKeyName; }

    //-------------------------------------------------------------------------------------------------

    public void setEventHubsCompatibleEndpoint(final String eventHubsCompatibleEndpoint) { this.eventHubsCompatibleEndpoint = eventHubsCompatibleEndpoint; }
    public void setEventHubsCompatiblePath(final String eventHubsCompatiblePath) { this.eventHubsCompatiblePath = eventHubsCompatiblePath; }
    public void setSasKey(final String sasKey) { this.sasKey = sasKey; }
    public void setSasKeyName(final String sasKeyName) { this.sasKeyName = sasKeyName; }
}
