package com.lukag.voznired.models;

import java.util.ArrayList;
import java.util.List;

public class Relacija {
    private String fromID;
    private String fromName;
    private String toID;
    private String toName;
    private List<Departure> urnik;
    private String[] nextRide;

    public Relacija() {
        this.urnik = new ArrayList<>();
        this.nextRide = new String[3];
    }

    public Relacija(String fromID, String fromName, String toID, String toName, List<Departure> urnik) {
        this.fromID = fromID;
        this.fromName = fromName;
        this.toID = toID;
        this.toName = toName;
        this.urnik = urnik;
        this.nextRide = new String[3];
    }

    public void urnikAdd(Departure novaDeparture) {
        if (this.urnik == null) {
            this.urnik = new ArrayList<>();
        }
        this.urnik.add(novaDeparture);
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
    public List<Departure> getUrnik() {
        return urnik;
    }
    public void setUrnik(List<Departure> urnik) {
        this.urnik = urnik;
    }
    public String[] getNextRide() {
        return nextRide;
    }
    public void setNextRide(String[] nextRide) {
        this.nextRide = nextRide;
    }
}
