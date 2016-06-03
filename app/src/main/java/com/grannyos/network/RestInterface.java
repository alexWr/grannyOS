package com.grannyos.network;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface RestInterface {


    @PUT("users")
    Call<ResponseRest.LoginUserResponse> loginUser(@Header("Content-Type") String contentType, @Body LoginUserData loginUserData);

    @GET("sessions/{session}/profile")
    Call<ResponseRest.ProfileInfoResponse> getProfileInfo(@Path("session") String session);

    @GET("sessions/{session}/relatives")
    Call<List<ResponseRest.ListRelativesResponse>> getListRelatives(@Path("session") String session);

    @GET("sessions/{session}/albums")
    Call<List<ResponseRest.albumListResponse>> getAlbumList(@Path("session") String session);

    @GET("sessions/{session}/calendar")
    Call<List<ResponseRest.calendarEventsResponse>> getCalendarEvents(@Path("session") String session);

    @GET("data/2.5/forecast/daily")
    Call<WeatherResponse> getWeather(@Query("lat") Double lat, @Query("lon") Double lon, @Query("cnt") int count, @Query("units") String units, @Query("appid") String appid);

}
