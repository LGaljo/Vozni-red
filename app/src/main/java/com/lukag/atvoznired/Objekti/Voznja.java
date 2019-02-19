package com.lukag.atvoznired.Objekti;

public class Voznja {
    public int ROD_ZAP; // Zaporedno število postaje
    public String POS_NAZ; // Naziv postaje
    public String ROD_IPRI; // Čas prihoda
    public int ROD_POS; // Dolžina postanka
    public String ROD_IODH; // Čas odhoda
    public int ROD_STOP; // Ali se ustavi?
    public Double ROD_LAT; // Latitude
    public Double ROD_LON; // Longitude

    public Voznja() {
    }

    public Voznja(int ROD_ZAP, String POS_NAZ, String ROD_IPRI, int ROD_POS, String ROD_IODH, int ROD_STOP, Double ROD_LAT, Double ROD_LON) {
        this.ROD_ZAP = ROD_ZAP;
        this.POS_NAZ = POS_NAZ;
        this.ROD_IPRI = ROD_IPRI;
        this.ROD_POS = ROD_POS;
        this.ROD_IODH = ROD_IODH;
        this.ROD_STOP = ROD_STOP;
        this.ROD_LAT = ROD_LAT;
        this.ROD_LON = ROD_LON;
    }

    public int getROD_ZAP() {
        return ROD_ZAP;
    }

    public void setROD_ZAP(int ROD_ZAP) {
        this.ROD_ZAP = ROD_ZAP;
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

    public int getROD_POS() {
        return ROD_POS;
    }

    public void setROD_POS(int ROD_POS) {
        this.ROD_POS = ROD_POS;
    }

    public String getROD_IODH() {
        return ROD_IODH;
    }

    public void setROD_IODH(String ROD_IODH) {
        this.ROD_IODH = ROD_IODH;
    }

    public int getROD_STOP() {
        return ROD_STOP;
    }

    public void setROD_STOP(int ROD_STOP) {
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

    public void setROD_LON(Double ROD_LOM) {
        this.ROD_LON = ROD_LOM;
    }
}
