package com.grannyos.weather;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grannyos.R;
import com.grannyos.network.RestInterface;
import com.grannyos.network.WeatherResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class WeatherList extends Fragment{

    private static final String TAG = "WeatherListGrannyOs";
    private int                 position =0;
    private double              lat;
    private double              lon;


    static WeatherList newInstance(int position, double lat, double lon) {
        WeatherList pageFragment = new WeatherList();
        Bundle arguments = new Bundle();
        arguments.putInt("page", position);
        arguments.putDouble("lat", lat);
        arguments.putDouble("lon",lon);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("page");
        lat = getArguments().getDouble("lat");
        lon = getArguments().getDouble("lon");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.weather_list_layout, container, false);
        final TextView tempMorning = (TextView) rootView.findViewById(R.id.tempMorning);
        final TextView tempNoon = (TextView) rootView.findViewById(R.id.tempNoon);
        final TextView tempEvening = (TextView) rootView.findViewById(R.id.tempEvening);
        final TextView tempNight = (TextView) rootView.findViewById(R.id.tempNight);
        final TextView pressureMorning = (TextView) rootView.findViewById(R.id.pressureMorning);
        final TextView pressureNoon = (TextView) rootView.findViewById(R.id.pressureNoon);
        final TextView pressureEvening = (TextView) rootView.findViewById(R.id.pressureEvening);
        final TextView pressureNight = (TextView) rootView.findViewById(R.id.pressureNight);
        final TextView windMorning = (TextView) rootView.findViewById(R.id.windMorning);
        final TextView windNoon = (TextView) rootView.findViewById(R.id.windNoon);
        final TextView windEvening = (TextView) rootView.findViewById(R.id.windEvening);
        final TextView windNight = (TextView) rootView.findViewById(R.id.windNight);
        final TextView airMorning = (TextView) rootView.findViewById(R.id.airMorning);
        final TextView airNoon = (TextView) rootView.findViewById(R.id.airNoon);
        final TextView airEvening = (TextView) rootView.findViewById(R.id.airEvening);
        final TextView airNight = (TextView) rootView.findViewById(R.id.airNight);
        final TextView day = (TextView) rootView.findViewById(R.id.day);
        final TextView mainTemp = (TextView) rootView.findViewById(R.id.mainTemp);
        final ImageView mainWeatherIcon = (ImageView) rootView.findViewById(R.id.mainWeatherIcon);
        Log.d(TAG, "my current location in weather list lat " + lat + " lon " + lon );
        String endPoint = getResources().getString(R.string.weatherEndpoint);
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(endPoint).build();
        RestInterface restInterface = restAdapter.create(RestInterface.class);
        restInterface.getWeather(lat, lon, 7, "metric", "f14cffd70e58afbe1ecc268c56ffd507", new Callback<WeatherResponse>() {
            @Override
            public void success(WeatherResponse weatherResponse, Response response) {
                mainTemp.setText((int) weatherResponse.getWeather().get(position).getTemp().getTempDay() + "°");
                tempMorning.setText((int) weatherResponse.getWeather().get(position).getTemp().getTempMorn() + "°");
                tempNoon.setText((int) weatherResponse.getWeather().get(position).getTemp().getTempDay() + "°");
                tempEvening.setText((int) weatherResponse.getWeather().get(position).getTemp().getTempEve() + "°");
                tempNight.setText((int) weatherResponse.getWeather().get(position).getTemp().getTempNight() + "°");
                pressureEvening.setText("" + (int) (weatherResponse.getWeather().get(position).getPressure() * 0.71) + " mm Hg");
                pressureNoon.setText("" + (int) (weatherResponse.getWeather().get(position).getPressure() * 0.71) + " mm Hg");
                pressureMorning.setText("" + (int) (weatherResponse.getWeather().get(position).getPressure() * 0.71) + " mm Hg");
                pressureNight.setText("" + (int) (weatherResponse.getWeather().get(position).getPressure() * 0.71) + " mm Hg");
                windMorning.setText("" + String.format(Locale.ENGLISH, "%.1f", weatherResponse.getWeather().get(position).getSpeed()) + " mph SW");
                windNoon.setText("" + String.format(Locale.ENGLISH, "%.1f", weatherResponse.getWeather().get(position).getSpeed()) + " mph SW");
                windEvening.setText("" + String.format(Locale.ENGLISH, "%.1f", weatherResponse.getWeather().get(position).getSpeed()) + " mph SW");
                windNight.setText("" + String.format(Locale.ENGLISH, "%.1f", weatherResponse.getWeather().get(position).getSpeed()) + " mph SW");
                airMorning.setText("" + weatherResponse.getWeather().get(position).getHumidity() + " %");
                airNoon.setText("" + weatherResponse.getWeather().get(position).getHumidity() + " %");
                airEvening.setText("" + weatherResponse.getWeather().get(position).getHumidity() + " %");
                airNight.setText("" + weatherResponse.getWeather().get(position).getHumidity() + " %");
                if (position == 0) {
                    day.setText("Today");
                }
                else {
                    Long date = weatherResponse.getWeather().get(position).getDate();
                    Date d = new Date(date * 1000);
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                    String dayOfTheWeek = sdf.format(d);
                    day.setText(dayOfTheWeek);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Error in weather request " + error);
            }
        });
        return rootView;
    }
}
