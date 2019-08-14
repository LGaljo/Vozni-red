package com.lukag.voznired.models;

public class StationsList {
    private Double ROD_LAT; // Latitude
    private Double ROD_LON; // Longitude
    private Integer ROD_POS; // Dolžina postanka
    private Integer ROD_STOP; // Ali se ustavi?
    private Integer ROD_ZAP; // Zaporedno število postaje
    private String POS_NAZ; // Naziv postaje
    private String ROD_IODH; // Čas odhoda
    private String ROD_IPRI; // Čas prihoda

    public StationsList() {
    }

    public Integer getROD_ZAP() {
        return ROD_ZAP;
    }
    public void setROD_ZAP(Integer ROD_ZAP) {
        this.ROD_ZAP = ROD_ZAP;
    }
    public Integer getROD_POS() {
        return ROD_POS;
    }
    public void setROD_POS(Integer ROD_POS) {
        this.ROD_POS = ROD_POS;
    }
    public Integer getROD_STOP() {
        return ROD_STOP;
    }
    public void setROD_STOP(Integer ROD_STOP) {
        this.ROD_STOP = ROD_STOP;
    }
    public Double getROD_LAT() {
        return ROD_LAT;
    }
    public void setROD_LAT(Double ROD_LAT) {
        this.ROD_LAT = ROD_LAT;
    }
    public Double getROD_LON() {
        return ROD_LON;
    }
    public void setROD_LON(Double ROD_LON) {
        this.ROD_LON = ROD_LON;
    }
    public String getPOS_NAZ() {
        return POS_NAZ;
    }
    public void setPOS_NAZ(String POS_NAZ) {
        this.POS_NAZ = POS_NAZ;
    }
    public String getROD_IPRI() {
        return ROD_IPRI;
    }
    public void setROD_IPRI(String ROD_IPRI) {
        this.ROD_IPRI = ROD_IPRI;
    }
    public String getROD_IODH() {
        return ROD_IODH;
    }
    public void setROD_IODH(String ROD_IODH) {
        this.ROD_IODH = ROD_IODH;
    }
}
