package com.grannyos.weather;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.grannyos.R;
import com.grannyos.utils.HideViews;
import com.grannyos.utils.ZoomOutPageTransformer;


public class WeatherPageFragment extends Fragment implements View.OnClickListener{


    private final static String TAG = "WeatherPageGrannyOs";
    private ViewPager           weatherPager;
    private ImageView           nextButton;
    private ImageView           prevButton;
    private TextView            previouslyPageDescription;
    private TextView            nextPageDescription;
    private ChangeListener      mListener = new ChangeListener();
    private double              lat;
    private double              lon;
    private HideViews           hideViews;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.weather_page_layout, container, false);
        weatherPager =(ViewPager)rootView.findViewById(R.id.weatherPager);
        Button backButton = (Button) rootView.findViewById(R.id.backButton);
        prevButton = (ImageView) rootView.findViewById(R.id.previousPage);
        nextButton = (ImageView) rootView.findViewById(R.id.nextPage);
        lat = getArguments().getDouble("lat", 0.0);
        lon = getArguments().getDouble("lon", 0.0);
        previouslyPageDescription = (TextView) rootView.findViewById(R.id.previouslyPageDescription);
        nextPageDescription = (TextView) rootView.findViewById(R.id.nextPageDescription);
        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        weatherPager.setPageTransformer(true, new ZoomOutPageTransformer());
        WeatherPageAdapter weatherPageAdapter = new WeatherPageAdapter(getChildFragmentManager(), 7, lat,lon);
        weatherPager.setAdapter(weatherPageAdapter);
        weatherPager.setCurrentItem(0);
        weatherPager.addOnPageChangeListener(mListener);
        hideViews = new HideViews(weatherPager);
        hideViews.visibility(prevButton, previouslyPageDescription, nextButton, nextPageDescription);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.previousPage:
                weatherPager.setCurrentItem(weatherPager.getCurrentItem() - 1);
                break;
            case R.id.nextPage:
                weatherPager.setCurrentItem(weatherPager.getCurrentItem() + 1);
                break;
            case R.id.backButton:
                getActivity().onBackPressed();
                break;
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
            }
        }
    }
}
