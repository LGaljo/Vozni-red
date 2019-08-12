package com.lukag.voznired.models;

import java.util.List;

public class ResponseDepartureStations {
    private List<Station> DepartureStations;
    private String Error;
    private String ErrorMsg;

    public ResponseDepartureStations() {
    }

    public List<Station> getDepartureStations() {
        return DepartureStations;
    }
    public void setDepartureStations(List<Station> departureStations) {
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
