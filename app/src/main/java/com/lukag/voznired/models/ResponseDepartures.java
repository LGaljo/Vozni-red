package com.lukag.voznired.models;

import java.util.ArrayList;

public class ResponseDepartures {
    private ArrayList<Departure> Departures;
    private String Error;
    private String ErrorMsg;

    public ResponseDepartures() {
    }

    public ArrayList<Departure> getDepartures() {
        return Departures;
    }
    public void setDepartures(ArrayList<Departure> departures) {
        this.Departures = departures;
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
