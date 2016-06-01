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

public class GetAlbumAssets {


    private static final String TAG = "GetAlbumGrannyOs";
    private RestInterface       restInterface;
    private File                myDir;
    private OkHttpClient        okHttp;
    private Context             context;


    public GetAlbumAssets(Context context){
        okHttp = new OkHttpClient();
        okHttp.setReadTimeout(6000 * 100, TimeUnit.MILLISECONDS);
        String endPoint = context.getResources().getString(R.string.endpoint);
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(endPoint).setClient(new OkClient(okHttp)).build();
        restInterface = restAdapter.create(RestInterface.class);
        this.context = context;
        createDir();
        getAlbumAssetFromApi();
    }

    private void getAlbumAssetFromApi(){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String sessionId = sharedPreferences.getString("sessionId", null);
        if(sessionId != null) {

            restInterface.getAlbumList(sessionId, new Callback<List<ResponseRest.albumListResponse>>() {
                @Override
                public void success(final List<ResponseRest.albumListResponse> albumListResponses, Response response) {

                    for (int i = 0; i < albumListResponses.size(); i++) {
                        final ResponseRest.albumListResponse album = albumListResponses.get(i);

                        for (int j = 0; j < album.getAssets().size(); j++) {
                            final ResponseRest.albumListResponse.Assets asset = albumListResponses.get(i).getAssets().get(j);

                            Log.d(TAG, asset.getResource());
                            Request request = new Request.Builder()
                                    .url(asset.getResource())
                                    .build();
                            try{
                                Thread.sleep(100);
                            } catch(InterruptedException e){
                                Log.d(TAG, "Error while thread sleep");
                                e.printStackTrace();
                            }
                            okHttp.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
                                @Override
                                public void onFailure(Request request, IOException e) {
                                    Log.d(TAG, "error while download image to album" + request.body());
                                }

                                @Override
                                public void onResponse(com.squareup.okhttp.Response response) throws IOException {

                                    if (!response.isSuccessful())
                                        throw new IOException("Unexpected code " + response);


                                    String mimeType = MimeTypeMap.getFileExtensionFromUrl(asset.getResource());
                                    String fname;
                                    switch (asset.getType()) {
                                        case "photo":
                                            fname = "Image" + asset.assetId + "." + mimeType;
                                            break;
                                        case "video":
                                            fname = "Video" + asset.assetId + "." + mimeType;
                                            break;
                                        default:
                                            fname = "error";
                                            break;
                                    }
                                    File saveFile = new File(myDir, fname);
                                    BufferedSink sink = Okio.buffer(Okio.sink(saveFile));
                                    sink.writeAll(response.body().source());
                                    sink.close();

                                    ContentValues contentValue = asset.getValues();

                                    contentValue.put(DatabaseHelper.ASSET_RESOURCE, saveFile.getPath());
                                    contentValue.put(DatabaseHelper.ASSET_ALBUM_ID, album.albumId);
                                    new LoadDataFromDatabase("photo", context, contentValue);
                                }
                            });
                        }
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d(TAG, " " + error);
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
