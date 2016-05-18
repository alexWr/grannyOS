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


public class GetAlbum {

    private static final String TAG = "GetAlbumGrannyOs";
    private RestInterface restInterface;
    private File myDir;
    private OkHttpClient okHttp;
    private Context context;

    public GetAlbum(Context context){
        okHttp = new OkHttpClient();
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

            restInterface.getAlbumList(sessionId, new Callback<List<ResponseRest.albumListResponse>>() {
                @Override
                public void success(final List<ResponseRest.albumListResponse> albumListResponses, Response response) {

                    for (int i = 0; i < albumListResponses.size(); i++) {
                        final ResponseRest.albumListResponse album = albumListResponses.get(i);

                        if(album.getCover() != null) {
                            Request request = new Request.Builder()
                                    .url(album.getCover())
                                    .build();
                            okHttp.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
                                @Override
                                public void onFailure(Request request, IOException e) {
                                    Log.d(TAG, "error while download cover image" + request.body());
                                }

                                @Override
                                public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                                    if (!response.isSuccessful())
                                        throw new IOException("Unexpected code " + response);

                                    Headers responseHeaders = response.headers();
                                    for (int i = 0; i < responseHeaders.size(); i++) {
                                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                                    }
                                    String mimeType = MimeTypeMap.getFileExtensionFromUrl(album.getCover());
                                    String fname = "Cover" + album.getAlbumId() + "." + mimeType;

                                    File saveFile = new File(myDir, fname);
                                    BufferedSink sink = Okio.buffer(Okio.sink(saveFile));
                                    sink.writeAll(response.body().source());
                                    sink.close();
                                    String cover;
                                    cover = saveFile.getPath();

                                    Log.d(TAG, "cover after check " + cover);
                                    ContentValues albumData = album.getValues();
                                    albumData.put(DatabaseHelper.ALBUM_COVER, cover);
                                    new LoadDataFromDatabase("album", context, albumData);
                                }
                            });
                        }
                        else{
                            ContentValues albumData = album.getValues();
                            albumData.put(DatabaseHelper.ALBUM_COVER, "none");
                            new LoadDataFromDatabase("album", context, albumData);
                        }
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d(TAG, "" + error);
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
