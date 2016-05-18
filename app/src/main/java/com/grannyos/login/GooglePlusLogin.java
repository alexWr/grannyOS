package com.grannyos.login;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;
import com.grannyos.R;
import com.grannyos.network.LoginUserData;
import com.grannyos.network.ResponseRest;
import com.grannyos.network.RestInterface;
import com.grannyos.network.SocketService;
import com.grannyos.socketEvent.GetAlbum;
import com.grannyos.socketEvent.GetAlbumAssets;
import com.grannyos.socketEvent.GetCalendarEvent;
import com.grannyos.socketEvent.GetRelatives;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;


public class GooglePlusLogin implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{


    private static final String         TAG = "GooglePlusGrannyOs";
    public static                       GoogleApiClient googleApiClient;//google client
    public static final int             REQUEST_CODE_SIGN_IN = 1;//request code if result ok
    private ProgressDialog              mConnectionProgressDialog;
    private Context                     context;
    private String                      token;
    private Activity                    activity;
    private Fragment                    fragment;
    private SharedPreferences.Editor    editor;
    private Person                      currentPerson;
    private String                      firsName,lastName,email,
                                        language,manufacture,device,osName,
                                        osType="Android", osVersion, udid;
    private int                         width,height;
    private float                       scale;
    private RestInterface               restInterface;
    private String                      endPoint;
    private LoginUserData               register;
    private File                        myDir;


    public GooglePlusLogin(Context con, Activity act,Fragment frag,ProgressDialog pd){
        this.endPoint = act.getResources().getString(R.string.endpoint);
        OkHttpClient okhttp = new OkHttpClient();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(act);
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(endPoint).setClient(new OkClient(okhttp)).build();
        this.context=con;
        this.activity=act;
        this.fragment=frag;
        this.mConnectionProgressDialog=pd;
        this.editor = sharedPreferences.edit();
        this.restInterface = restAdapter.create(RestInterface.class);
        okhttp.setReadTimeout(60 * 100, TimeUnit.MILLISECONDS);
        googleApiClient=LoginWithGoogle();
    }


    private GoogleApiClient LoginWithGoogle(){
        return new GoogleApiClient.Builder(context)
                .addApi(Plus.API, Plus.PlusOptions.builder().addActivityTypes(Const.ACTIONS).build())
                .addApi(Fitness.SENSORS_API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(new Scope(Scopes.EMAIL))
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.PLUS_ME))
                .addScope(Fitness.SCOPE_ACTIVITY_READ)
                .addScope(Fitness.SCOPE_BODY_READ_WRITE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }
    @Override
    public void onConnected(Bundle connectionHint) {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object... params) {
                String scope = "oauth2:" + Scopes.PLUS_LOGIN ;
                Log.d(TAG,"onConnected");
                try {
                    token = GoogleAuthUtil.getToken(context, Plus.AccountApi.getAccountName(googleApiClient), scope);
                    Log.d(TAG, token);
                } catch (IOException | GoogleAuthException e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Object result) {
                super.onPostExecute(result);
                if (Plus.PeopleApi.getCurrentPerson(googleApiClient) != null) {
                    currentPerson = Plus.PeopleApi.getCurrentPerson(googleApiClient);
                    firsName = currentPerson.getName().getGivenName();
                    lastName = currentPerson.getName().getFamilyName();
                    email = Plus.AccountApi.getAccountName(googleApiClient);
                }
                language = Locale.getDefault().getLanguage();
                manufacture = Build.MANUFACTURER;
                device = Build.DEVICE;
                osVersion=Build.VERSION.RELEASE;
                osName = osType + " " + osVersion;
                DisplayMetrics displaymetrics = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                width=displaymetrics.widthPixels;
                height=displaymetrics.heightPixels;
                scale = displaymetrics.density;
                udid = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                register = new LoginUserData(firsName,lastName,email,token,udid,language,manufacture,device,scale,width,height,osName,osVersion);
                Gson gson = new Gson();
                Log.d(TAG, gson.toJson(register));
                Log.d(TAG, endPoint);
                restInterface.loginUser("application/json",register, new Callback<ResponseRest.LoginUserResponse>() {
                    @Override
                    public void success(ResponseRest.LoginUserResponse loginResponse, Response response) {
                        Log.d(TAG, "response session" + loginResponse.getSession());
                        Log.d(TAG, "response body" + loginResponse.toString());
                        editor.putBoolean("showLoginScreen", true);
                        editor.putString("sessionId", loginResponse.getSession());
                        editor.apply();
                        Intent intent = new Intent(activity, SocketService.class);
                        activity.startService(intent);
                        new GetRelatives(context);
                        new GetCalendarEvent(context);
                        new GetAlbum(context);
                        new GetAlbumAssets(context);
                        restInterface.getProfileInfo(loginResponse.getSession(), new Callback<ResponseRest.ProfileInfoResponse>() {
                            @SuppressWarnings("ResultOfMethodCallIgnored")
                            @Override
                            public void success(ResponseRest.ProfileInfoResponse profileInfoResponse, Response response) {
                                Log.d(TAG, "my own info " + profileInfoResponse.toString());
                                Log.d(TAG, "my own id " + profileInfoResponse.getMyId());
                                editor.putString("myId", profileInfoResponse.getMyId());
                                editor.apply();
                                String root = Environment.getExternalStorageDirectory().toString();
                                myDir = new File(root + "/grannyos");
                                myDir.mkdirs();
                                FragmentManager fragmentManager = activity.getFragmentManager();
                                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.content_frame, fragment).commit();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.d(TAG, "error while get user info" + error.toString());
                            }
                        });
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "error while login/register user" + error.toString());
                    }
                });
                mConnectionProgressDialog.dismiss();
            }
        };
        task.execute((Void) null);
    }
    @Override
    public void onConnectionSuspended(int cause) {
        googleApiClient.connect();
        Log.d(TAG, "onConnectionSuspend");
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        mConnectionProgressDialog.dismiss();
        Log.d(TAG, "onConnectionFailed");
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(activity, REQUEST_CODE_SIGN_IN);
            }
            catch (IntentSender.SendIntentException e) {
                // Fetch a new result to start.
                googleApiClient.connect();
            }
        }
        else{
            //Toast.makeText(context,resources.getString(R.string.alert_message),Toast.LENGTH_LONG).show();
        }
    }

    public static GoogleApiClient getGoogleApiClient(){
        if(googleApiClient == null) {
            Log.e(TAG, "googleApiClient == null");
        }
        return googleApiClient;
    }
}
