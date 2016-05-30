package com.grannyos.network;


import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface RestInterface {


    @PUT("/users")
    void loginUser(@Header("Content-Type") String contentType, @Body LoginUserData loginUserData, Callback<ResponseRest.LoginUserResponse> callback);

    @GET("/sessions/{session}/profile")
    void getProfileInfo(@Path("session") String session, Callback<ResponseRest.ProfileInfoResponse> profileInfoResponseCallback);

    @GET("/sessions/{session}/relatives")
    void getListRelatives(@Path("session") String session, Callback<List<ResponseRest.ListRelativesResponse>> listRelativesResponseCallback);

    @GET("/sessions/{session}/albums")
    void getAlbumList(@Path("session") String session, Callback<List<ResponseRest.albumListResponse>> albumListResponseCallback);

    @GET("/sessions/{session}/calendar")
    void getCalendarEvents(@Path("session") String session, Callback<List<ResponseRest.calendarEventsResponse>> calendarEventsCallback);

    @GET("/data/2.5/forecast/daily")
    void getWeather(@Query("lat") Double lat, @Query("lon") Double lon, @Query("cnt") int count, @Query("units") String units, @Query("appid") String appid, Callback<WeatherResponse> weatherResponseCallback);

}
