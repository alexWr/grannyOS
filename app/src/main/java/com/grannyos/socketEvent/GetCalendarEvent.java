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

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class GetCalendarEvent {

    private static final String TAG = "GetCalendarGrannyOs";
    private RestInterface       restInterface;
    private Context             context;

    public GetCalendarEvent(Context context){
        OkHttpClient okHttp = new OkHttpClient();
        okHttp.setReadTimeout(6000 * 100, TimeUnit.MILLISECONDS);
        String endPoint = context.getResources().getString(R.string.endpoint);
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(endPoint).setClient(new OkClient(okHttp)).build();
        restInterface = restAdapter.create(RestInterface.class);
        this.context = context;
        createDir();
        getEventsFromApi();
    }

    private void getEventsFromApi(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String sessionId = sharedPreferences.getString("sessionId", null);
        if(sessionId != null) {

            restInterface.getCalendarEvents(sessionId, new Callback<List<ResponseRest.calendarEventsResponse>>() {
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
            });
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
