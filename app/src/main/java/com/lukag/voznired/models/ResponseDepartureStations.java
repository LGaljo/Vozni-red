package com.lukag.voznired.models;

import java.util.ArrayList;

public class ResponseDepartureStations {
    private ArrayList<Station> DepartureStations;
    private String Error;
    private String ErrorMsg;

    public ResponseDepartureStations() {
    }

    public ArrayList<Station> getDepartureStations() {
        return DepartureStations;
    }
    public void setDepartureStations(ArrayList<Station> departureStations) {
        this.DepartureStations = departureStations;
    }
    public String getError() {
        return Error;
    }
    public void setError(String error) {
        Error = error;
    }
    public String getErrorMsg() {
        return ErrorMsg;
    }
    public void setErrorMsg(String errorMsg) {
        ErrorMsg = errorMsg;
    }
}
