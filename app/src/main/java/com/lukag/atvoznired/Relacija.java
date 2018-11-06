package com.lukag.atvoznired;

import java.util.ArrayList;
import java.util.List;

public class Relacija {
    private String fromID;
    private String fromName;
    private String toID;
    private String toName;
    private List<Pot> urnik;

    Relacija() {

    }

    public Relacija(String fromID, String fromName, String toID, String toName, List<Pot> urnik) {
        this.fromID = fromID;
        this.fromName = fromName;
        this.toID = toID;
        this.toName = toName;
        this.urnik = urnik;
    }

    public void initUrnik() {
        this.urnik = new ArrayList<>();
    }

    public void urnikAdd(Pot novaPot) {
        this.urnik.add(novaPot);
    }

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToID() {
        return toID;
    }

    public void setToID(String toID) {
        this.toID = toID;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public List<Pot> getUrnik() {
        return urnik;
    }

    public void setUrnik(List<Pot> urnik) {
        this.urnik = urnik;
    }
}
