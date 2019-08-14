package com.lukag.voznired.retrofit_interface;

import com.lukag.voznired.models.ResponseDepartureStationList;
import com.lukag.voznired.models.ResponseDepartureStations;
import com.lukag.voznired.models.ResponseDepartures;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APICalls {
    @GET("WS_ArrivaSLO_TimeTable_DepartureStations.aspx")
    Call<List<ResponseDepartureStations>> getDepartureStations(@Query("cTimeStamp") String timestamp,
                                                               @Query("cToken") String token,
                                                               @Query("json") String json);

    @POST("WS_ArrivaSLO_TimeTable_TimeTableDepartureStationList.aspx")
    Call<List<ResponseDepartureStationList>> getDepartureStationList(@Query("cTimeStamp") String timestamp,
                                                                     @Query("cToken") String token,
                                                                     @Query("SPOD_SIF") String SPOD_SIF,
                                                                     @Query("REG_ISIF") String REG_ISIF,
                                                                     @Query("OVR_SIF") String OVR_SIF,
                                                                     @Query("VVLN_ZL") String VVLN_ZL,
                                                                     @Query("ROD_ZAPZ") String ROD_ZAPZ,
                                                                     @Query("ROD_ZAPK") String ROD_ZAPK,
                                                                     @Query("json") String json);

    @POST("WS_ArrivaSLO_TimeTable_TimeTableDepartures.aspx")
    Call<List<ResponseDepartures>> getDepartures(@Query("cTimeStamp") String timestamp,
                                                 @Query("cToken") String token,
                                                 @Query("JPOS_IJPPZ") String JPOS_IJPPZ,
                                                 @Query("JPOS_IJPPK") String JPOS_IJPPK,
                                                 @Query("VZVK_DAT") String VZVK_DAT,
                                                 @Query("ClientId") String ClientId,
                                                 @Query("ClientIdType") String ClientIdType,
                                                 @Query("ClientLocationLatitude") String ClientLocationLatitude,
                                                 @Query("ClientLocationLongitude") String ClientLocationLongitude,
                                                 @Query("json") String json);
}
