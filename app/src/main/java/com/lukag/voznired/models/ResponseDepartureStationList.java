package com.lukag.voznired.models;

import java.util.ArrayList;

public class ResponseDepartureStationList {
    private ArrayList<StationsList> DepartureStationList;
    private String Error;
    private String ErrorMsg;

    public ResponseDepartureStationList() {
    }

    public ArrayList<StationsList> getDepartureStationList() {
        return DepartureStationList;
    }
    public void setDepartureStationList(ArrayList<StationsList> departureStationList) {
        DepartureStationList = departureStationList;
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
