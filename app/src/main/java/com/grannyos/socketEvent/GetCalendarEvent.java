package com.grannyos.socketEvent;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.grannyos.R;
import com.grannyos.database.DatabaseHelper;
import com.grannyos.database.LoadDataFromDatabase;
import com.grannyos.network.ResponseRest;
import com.grannyos.network.RestInterface;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetCalendarEvent {

    private static final String TAG = "GetCalendarGrannyOs";
    private RestInterface       restInterface;
    private Context             context;

    public GetCalendarEvent(Context context){
        OkHttpClient okHttp = new OkHttpClient();
        okHttp.setReadTimeout(6000 * 100, TimeUnit.MILLISECONDS);
        String endPoint = context.getResources().getString(R.string.endpoint);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(endPoint)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        restInterface = retrofit.create(RestInterface.class);
        this.context = context;
        createDir();
        getEventsFromApi();
    }

    private void getEventsFromApi(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String sessionId = sharedPreferences.getString("sessionId", null);
        if(sessionId != null) {
            Call<List<ResponseRest.calendarEventsResponse>> call = restInterface.getCalendarEvents(sessionId);
            call.enqueue(new Callback<List<ResponseRest.calendarEventsResponse>>() {
                @Override
                public void onResponse(Call<List<ResponseRest.calendarEventsResponse>> call, Response<List<ResponseRest.calendarEventsResponse>> response) {
                    if(response.isSuccessful()) {
                        for (int i = 0; i < response.body().size(); i++) {
                            Log.d(TAG, "" + i + response.body().get(i).getDate() + " " + response.body().get(i).getTitle());
                            ResponseRest.calendarEventsResponse event = response.body().get(i);
                            new LoadDataFromDatabase("event", context, event.getValues());
                            if (event.getAlbumAssetId().equals("none")) {
                                ContentValues contentValue = new ContentValues();
                                contentValue.put(DatabaseHelper.ASSET_ID, "none");
                                contentValue.put(DatabaseHelper.ASSET_RESOURCE, "none");
                                new LoadDataFromDatabase("photo", context, contentValue);
                            }
                        }
                    }
                    else{
                        Log.d(TAG, "error " + response.code() + " " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<ResponseRest.calendarEventsResponse>> call, Throwable t) {
                    Log.d(TAG, "event retrofit error");
                    t.printStackTrace();
                }
            });
            /*restInterface.getCalendarEvents(sessionId, new Callback<List<ResponseRest.calendarEventsResponse>>() {
                @Override
                public void success(List<ResponseRest.calendarEventsResponse> calendarEventsResponses, Response response) {
                    for (int i = 0; i < calendarEventsResponses.size(); i++) {
                        Log.d(TAG, "" + i + calendarEventsResponses.get(i).getDate() + " " + calendarEventsResponses.get(i).getTitle());
                        ResponseRest.calendarEventsResponse event = calendarEventsResponses.get(i);
                        new LoadDataFromDatabase("event", context, event.getValues());
                        if(event.getAlbumAssetId().equals("none")) {
                            ContentValues contentValue = new ContentValues();
                            contentValue.put(DatabaseHelper.ASSET_ID, "none");
                            contentValue.put(DatabaseHelper.ASSET_RESOURCE, "none");
                            new LoadDataFromDatabase("photo", context, contentValue);
                        }
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d(TAG, "event retrofit error" + error);
                }
            });*/
        }
        else{
            Log.d(TAG, "sessionId is null");
        }
    }

    private void createDir(){
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/grannyos");
        if(!myDir.exists()) {
            if(myDir.mkdirs()){
                Log.d(TAG, "create grannyOs repo ");
            }
            else{
                Log.d(TAG, " can not create grannyOs repo ");
            }
        }
    }
}
