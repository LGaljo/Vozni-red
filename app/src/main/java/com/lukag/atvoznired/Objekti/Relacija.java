package com.lukag.atvoznired.Objekti;

import java.util.ArrayList;
import java.util.List;

public class Relacija {
    private String fromID;
    private String fromName;
    private String toID;
    private String toName;
    private List<Pot> urnik;
    private String[] nextRide;

    public Relacija() {
        this.urnik = new ArrayList<>();
        this.nextRide = new String[3];
    }

    public Relacija(String fromID, String fromName, String toID, String toName, List<Pot> urnik) {
        this.fromID = fromID;
        this.fromName = fromName;
        this.toID = toID;
        this.toName = toName;
        this.urnik = urnik;
        this.nextRide = new String[3];
    }

    public void urnikAdd(Pot novaPot) {
        if (this.urnik == null) {
            this.urnik = new ArrayList<>();
        }
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

    public String[] getNextRide() {
        return nextRide;
    }

    public void setNextRide(String[] nextRide) {
        this.nextRide = nextRide;
    }
}
