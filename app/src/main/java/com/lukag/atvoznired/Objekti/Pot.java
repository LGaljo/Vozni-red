package com.lukag.atvoznired.Objekti;

public class Pot {
    private Integer ID;

    private String ovr_sif; // idk what is that

    private String reg_isif; // nek id postaje

    private int rod_cas; // čas potovanja
    private String rod_iodh; // čas odhoda
    private String rod_ipri; // čas prihoda
    private int rod_km; // razdalja
    private String rod_opo; // ikd what is that
    private String rod_per; // peron
    private String rod_zapk; // ikd what is that
    private String rod_zapz; // ikd what is that

    private String rpr_naz; // naziv podjetja
    private String rpr_sif; // id podjetja

    private int spod_sif; // nek id

    private int vvln_zl; // idk what is that

    private int vzcl_cen; // cena potovanja

    private boolean status;

    public Pot(Integer ID, String ovr_sif, String reg_isif, int rod_cas, String rod_iodh, String rod_ipri, int rod_km, String rod_opo, String rod_per, String rod_zapk, String rod_zapz, String rpr_naz, String rpr_sif, int spod_sif, int vvln_zl, int vzcl_cen, boolean status) {
        this.ID = ID;
        this.ovr_sif = ovr_sif;
        this.reg_isif = reg_isif;
        this.rod_cas = rod_cas;
        this.rod_iodh = rod_iodh;
        this.rod_ipri = rod_ipri;
        this.rod_km = rod_km;
        this.rod_opo = rod_opo;
        this.rod_per = rod_per;
        this.rod_zapk = rod_zapk;
        this.rod_zapz = rod_zapz;
        this.rpr_naz = rpr_naz;
        this.rpr_sif = rpr_sif;
        this.spod_sif = spod_sif;
        this.vvln_zl = vvln_zl;
        this.vzcl_cen = vzcl_cen;
        this.status = status;
    }

    public Pot() {
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getOvr_sif() {
        return ovr_sif;
    }

    public void setOvr_sif(String ovr_sif) {
        this.ovr_sif = ovr_sif;
    }

    public String getReg_isif() {
        return reg_isif;
    }

    public void setReg_isif(String reg_isif) {
        this.reg_isif = reg_isif;
    }

    public int getRod_cas() {
        return rod_cas;
    }

    public void setRod_cas(int rod_cas) {
        this.rod_cas = rod_cas;
    }

    public String getRod_iodh() {
        return rod_iodh;
    }

    public void setRod_iodh(String rod_iodh) {
        this.rod_iodh = rod_iodh;
    }

    public String getRod_ipri() {
        return rod_ipri;
    }

    public void setRod_ipri(String rod_ipri) {
        this.rod_ipri = rod_ipri;
    }

    public int getRod_km() {
        return rod_km;
    }

    public void setRod_km(int rod_km) {
        this.rod_km = rod_km;
    }

    public String getRod_opo() {
        return rod_opo;
    }

    public void setRod_opo(String rod_opo) {
        this.rod_opo = rod_opo;
    }

    public String getRod_per() {
        return rod_per;
    }

    public void setRod_per(String rod_per) {
        this.rod_per = rod_per;
    }

    public String getRod_zapk() {
        return rod_zapk;
    }

    public void setRod_zapk(String rod_zapk) {
        this.rod_zapk = rod_zapk;
    }

    public String getRod_zapz() {
        return rod_zapz;
    }

    public void setRod_zapz(String rod_zapz) {
        this.rod_zapz = rod_zapz;
    }

    public String getRpr_naz() {
        return rpr_naz;
    }

    public void setRpr_naz(String rpr_naz) {
        this.rpr_naz = rpr_naz;
    }

    public String getRpr_sif() {
        return rpr_sif;
    }

    public void setRpr_sif(String rpr_sif) {
        this.rpr_sif = rpr_sif;
    }

    public int getSpod_sif() {
        return spod_sif;
    }

    public void setSpod_sif(int spod_sif) {
        this.spod_sif = spod_sif;
    }

    public int getVvln_zl() {
        return vvln_zl;
    }

    public void setVvln_zl(int vvln_zl) {
        this.vvln_zl = vvln_zl;
    }

    public int getVzcl_cen() {
        return vzcl_cen;
    }

    public void setVzcl_cen(int vzcl_cen) {
        this.vzcl_cen = vzcl_cen;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
