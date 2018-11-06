package com.lukag.atvoznired;

public class Pot {
    private Integer ID;
    private String start;
    private String end;
    private String duration;
    private String length;
    private String cost;
    private Boolean status;

    Pot() {
    }

    public Pot(Integer ID, String start, String end, String duration, String length, String cost, Boolean status) {
        this.ID = ID;
        this.start = start;
        this.end = end;
        this.duration = duration;
        this.length = length;
        this.cost = cost;
        this.status = status;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
