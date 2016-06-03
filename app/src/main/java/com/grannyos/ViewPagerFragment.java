package com.grannyos;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grannyos.network.RestInterface;
import com.grannyos.network.WeatherResponse;
import com.grannyos.utils.GPSTracker;
import com.grannyos.utils.HideViews;
import com.grannyos.utils.ZoomOutPageTransformer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewPagerFragment extends Fragment implements View.OnClickListener{

    private final static String TAG = "ViewPagerGrannyOs";
    private int                 elementCount;
    private ViewPager           viewPager;
    private ImageView           nextButton;
    private ImageView           prevButton;
    private ChangeListener      mListener = new ChangeListener();
    private TextView            tvHelpDescription;
    private TextView            previouslyPageDescription;
    private TextView            nextPageDescription;
    private String[]            helpDescription;
    private GPSTracker          gps;
    private HideViews           hideViews;
    public static String        city = "";
    public static Double        lat = 0.0;
    public static Double        lon = 0.0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_pager, container, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        elementCount = getActivity().getResources().getStringArray(R.array.description_main_icon).length;
        helpDescription = getActivity().getResources().getStringArray(R.array.help_description);
        viewPager=(ViewPager)rootView.findViewById(R.id.pagerSurveys);
        prevButton = (ImageView) rootView.findViewById(R.id.previousPage);
        nextButton = (ImageView) rootView.findViewById(R.id.nextPage);
        tvHelpDescription = (TextView) rootView.findViewById(R.id.helpDescription);
        previouslyPageDescription = (TextView) rootView.findViewById(R.id.previouslyPageDescription);
        nextPageDescription = (TextView) rootView.findViewById(R.id.nextPageDescription);
        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity(), getChildFragmentManager(), GrannyOsListElement.class, elementCount, null ,null);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(mListener);
        editor.putBoolean("firstTime", true);
        editor.apply();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        tvHelpDescription.setText(helpDescription[viewPager.getCurrentItem()]);
        hideViews = new HideViews(viewPager);
        hideViews.visibility(prevButton, previouslyPageDescription, nextButton, nextPageDescription);
        gps = new GPSTracker(getActivity(), locationResult);
        if (gps.canGetLocation()) {
            gps.getLocation(getActivity(), locationResult);
        } else {
            gps.showSettingsAlert();
        }
    }

    public class ChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                hideViews.visibility(prevButton, previouslyPageDescription, nextButton, nextPageDescription);
                tvHelpDescription.setText(helpDescription[viewPager.getCurrentItem()]);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.previousPage:
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                break;
            case R.id.nextPage:
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                break;
        }
    }


    GPSTracker.LocationResult locationResult = new GPSTracker.LocationResult() {
        @Override
        public void gotLocation(Location location) {
            if(location!=null && getActivity() != null) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                Log.d(TAG, "current location lat " + lat + " lon " + lon);
                String endPoint = getActivity().getApplicationContext().getResources().getString(R.string.weatherEndpoint);
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(endPoint)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                RestInterface restInterface = retrofit.create(RestInterface.class);
                Call<WeatherResponse> call = restInterface.getWeather(lat, lon, 1, "metric", "f14cffd70e58afbe1ecc268c56ffd507");
                call.enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                        if(response.isSuccessful()) {
                            city = response.body().getCity().getName();
                        }
                        else{
                            Log.d(TAG, "error " + response.code() + " " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherResponse> call, Throwable t) {
                        Log.d(TAG, "error while get city name ");
                        t.printStackTrace();
                    }
                });
                /*restInterface.getWeather(lat, lon, 1, "metric", "f14cffd70e58afbe1ecc268c56ffd507", new Callback<WeatherResponse>() {
                    @Override
                    public void success(WeatherResponse weatherResponse, Response response) {
                        city = weatherResponse.getCity().getName();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "error while get city name " + error);
                    }
                });*/
            }
            else{
                if(gps != null){
                    gps.stopUsingGPS();
                }
            }
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        if(gps!=null){
            gps.stopUsingGPS();
        }
    }
}
