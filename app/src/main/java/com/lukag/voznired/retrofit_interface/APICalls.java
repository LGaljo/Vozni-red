package com.lukag.voznired.retrofit_interface;

import com.lukag.voznired.models.ResponseDepartureStations;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APICalls {
    @GET("WS_ArrivaSLO_TimeTable_DepartureStations.aspx")
    Call<List<ResponseDepartureStations>> getDepartureStations(@Query("cTimeStamp") String timestamp,
                                                               @Query("cToken") String token,
                                                               @Query("json") String json);
}
