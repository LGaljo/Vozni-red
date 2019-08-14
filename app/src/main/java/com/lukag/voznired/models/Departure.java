package com.lukag.voznired.models;

public class Departure {
    private String OVR_SIF; // idk what is that - sifra
    private String REG_ISIF; // nek id postaje
    private int ROD_CAS; // čas potovanja
    private String ROD_IODH; // čas odhoda
    private String ROD_IPRI; // čas prihoda
    private int ROD_KM; // razdalja
    private String ROD_OPO; // ikd what is that
    private String ROD_PER; // peron
    private String ROD_ZAPK; // končna postaja
    private String ROD_ZAPZ; // zacetna postaja
    private String RPR_NAZ; // naziv podjetja
    private String RPR_SIF; // id podjetja
    private int SPOD_SIF; // nek id sifra
    private int VVLN_ZL; // idk what is that
    private double VZCL_CEN; // cena potovanja

    private Integer ID;
    private boolean status;

    public Departure() {
    }

    public Integer getID() {
        return ID;
    }
    public void setID(Integer ID) {
        this.ID = ID;
    }
    public String getOVR_SIF() {
        return OVR_SIF;
    }
    public void setOVR_SIF(String OVR_SIF) {
        this.OVR_SIF = OVR_SIF;
    }
    public String getREG_ISIF() {
        return REG_ISIF;
    }
    public void setREG_ISIF(String REG_ISIF) {
        this.REG_ISIF = REG_ISIF;
    }
    public int getROD_CAS() {
        return ROD_CAS;
    }
    public void setROD_CAS(int ROD_CAS) {
        this.ROD_CAS = ROD_CAS;
    }
    public String getROD_IODH() {
        return ROD_IODH;
    }
    public void setROD_IODH(String ROD_IODH) {
        this.ROD_IODH = ROD_IODH;
    }
    public String getROD_IPRI() {
        return ROD_IPRI;
    }
    public void setROD_IPRI(String ROD_IPRI) {
        this.ROD_IPRI = ROD_IPRI;
    }
    public int getROD_KM() {
        return ROD_KM;
    }
    public void setROD_KM(int ROD_KM) {
        this.ROD_KM = ROD_KM;
    }
    public String getROD_OPO() {
        return ROD_OPO;
    }
    public void setROD_OPO(String ROD_OPO) {
        this.ROD_OPO = ROD_OPO;
    }
    public String getROD_PER() {
        return ROD_PER;
    }
    public void setROD_PER(String ROD_PER) {
        this.ROD_PER = ROD_PER;
    }
    public String getROD_ZAPK() {
        return ROD_ZAPK;
    }
    public void setROD_ZAPK(String ROD_ZAPK) {
        this.ROD_ZAPK = ROD_ZAPK;
    }
    public String getROD_ZAPZ() {
        return ROD_ZAPZ;
    }
    public void setROD_ZAPZ(String ROD_ZAPZ) {
        this.ROD_ZAPZ = ROD_ZAPZ;
    }
    public String getRPR_NAZ() {
        return RPR_NAZ;
    }
    public void setRPR_NAZ(String RPR_NAZ) {
        this.RPR_NAZ = RPR_NAZ;
    }
    public String getRPR_SIF() {
        return RPR_SIF;
    }
    public void setRPR_SIF(String RPR_SIF) {
        this.RPR_SIF = RPR_SIF;
    }
    public int getSPOD_SIF() {
        return SPOD_SIF;
    }
    public void setSPOD_SIF(int SPOD_SIF) {
        this.SPOD_SIF = SPOD_SIF;
    }
    public int getVVLN_ZL() {
        return VVLN_ZL;
    }
    public void setVVLN_ZL(int VVLN_ZL) {
        this.VVLN_ZL = VVLN_ZL;
    }
    public double getVZCL_CEN() {
        return VZCL_CEN;
    }
    public void setVZCL_CEN(double VZCL_CEN) {
        this.VZCL_CEN = VZCL_CEN;
    }
    public boolean isStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
}
