package com.grannyos.socketEvent;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.grannyos.R;
import com.grannyos.database.DatabaseHelper;
import com.grannyos.database.LoadDataFromDatabase;
import com.grannyos.network.ResponseRest;
import com.grannyos.network.RestInterface;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okio.BufferedSink;
import okio.Okio;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class GetRelatives {

    private static final String TAG = "GetRelativesGrannyOs";
    private RestInterface restInterface;
    private File myDir;
    private OkHttpClient okHttp;
    private Context context;


    public GetRelatives(Context context){
        okHttp = new OkHttpClient();
        okHttp.setReadTimeout(6000 * 100, TimeUnit.MILLISECONDS);
        String endPoint = context.getResources().getString(R.string.endpoint);
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(endPoint).setClient(new OkClient(okHttp)).build();
        restInterface = restAdapter.create(RestInterface.class);
        this.context = context;
        createDir();
        getRelativesFromApi();
    }

    private void getRelativesFromApi(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String sessionId = sharedPreferences.getString("sessionId", null);
        if(sessionId != null) {

            restInterface.getListRelatives(sessionId, new Callback<List<ResponseRest.ListRelativesResponse>>() {
                @Override
                public void success(List<ResponseRest.ListRelativesResponse> listRelativesResponses, Response response) {
                    Log.d(TAG, "response from relatives" + response.getBody());
                    for (int i = 0; i < listRelativesResponses.size(); i++) {

                        final ResponseRest.ListRelativesResponse relative = listRelativesResponses.get(i);
                        Log.d(TAG, "avatar url " + relative.getAvatar());
                        String tmp;
                        if (relative.getAvatar() == null)
                            tmp = "none";
                        else
                            tmp = relative.getAvatar();
                        final String avatarURL = tmp;
                        if (!avatarURL.equals("none")) {
                            Request request = new Request.Builder()
                                    .url(avatarURL)
                                    .build();
                            okHttp.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
                                @Override
                                public void onFailure(Request request, IOException e) {
                                    Log.d(TAG, "error while download image to relatives" + request.body());
                                    ContentValues values = relative.getValues();

                                    values.put(DatabaseHelper.RELATIVES_ICON, "none");
                                    new LoadDataFromDatabase("relatives", context, values);
                                }

                                @Override
                                public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                                    if (!response.isSuccessful())
                                        throw new IOException("Unexpected code " + response);

                                    Headers responseHeaders = response.headers();
                                    for (int i = 0; i < responseHeaders.size(); i++) {
                                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                                    }
                                    String mimeType = MimeTypeMap.getFileExtensionFromUrl(avatarURL);
                                    String fname = "Relative" + relative.relativesId + "." + mimeType;
                                    File saveFile = new File(myDir, fname);
                                    BufferedSink sink = Okio.buffer(Okio.sink(saveFile));
                                    sink.writeAll(response.body().source());
                                    sink.close();
                                    ContentValues values = relative.getValues();

                                    values.put(DatabaseHelper.RELATIVES_ICON, saveFile.getPath());
                                    new LoadDataFromDatabase("relatives", context, values);
                                }
                            });
                        } else {
                            ContentValues values = relative.getValues();

                            values.put(DatabaseHelper.RELATIVES_ICON, avatarURL);
                            new LoadDataFromDatabase("relatives", context, values);
                        }
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, error.toString());
                }
            });
        }
        else{
            Log.d(TAG, "sessionId is null");
        }
    }

    private void createDir(){
        String root = Environment.getExternalStorageDirectory().toString();
        myDir = new File(root + "/grannyos");
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
