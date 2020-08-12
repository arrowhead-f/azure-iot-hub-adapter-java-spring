package eu.arrowhead.adapter.azureiothub.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SenMLDTO implements Serializable {

    //=================================================================================================
    // members

    private String bn; // base name (your provider system name)
    private Long t; // timestamp
    private String bu; // base unit
    private int ver; // version
    private List<SenMLMeasurementDTO> e; // array of measurements

    //=================================================================================================
    // methods

    //-------------------------------------------------------------------------------------------------

    public SenMLDTO(final String bn, final String bu, final int ver) {
        this.bn = bn;
        this.t = System.currentTimeMillis() / 1000L; // get unix timestamp
        this.bu = bu;
        this.ver = ver;
        this.e = new ArrayList<>();
    }

    //-------------------------------------------------------------------------------------------------

    public String getBn() { return bn; }
    public Long getT() { return t; }
    public String getBu() { return bu; }
    public int getVer() { return ver; }
    public List<SenMLMeasurementDTO> getE() { return e; }

    //-------------------------------------------------------------------------------------------------

    public void setBn(final String bn) { this.bn = bn; }
    public void setT(final Long t) { this.t = t; }
    public void setBu(final String bu) { this.bu = bu; }
    public void setVer(final int ver) { this.ver = ver; }
    public void setE(final List<SenMLMeasurementDTO> e) { this.e = e; }

    //-------------------------------------------------------------------------------------------------

    public boolean addMeasurement(final SenMLMeasurementDTO measurement) {
        return this.e.add(measurement);
    }
}
