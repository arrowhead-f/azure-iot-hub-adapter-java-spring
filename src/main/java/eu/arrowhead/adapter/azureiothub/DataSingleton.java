package eu.arrowhead.adapter.azureiothub;

import org.springframework.stereotype.Component;

@Component
public class DataSingleton<T> {

    //=================================================================================================
    // members
    private T data;

    //=================================================================================================
    // methods

    //-------------------------------------------------------------------------------------------------

    public T getData() { return data; }

    //-------------------------------------------------------------------------------------------------

    public void setData(final T data) { this.data = data; }
}
