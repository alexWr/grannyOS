package com.grannyos.weather;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Parcelable;
import android.support.v13.app.FragmentPagerAdapter;
import android.view.ViewGroup;


public class WeatherPageAdapter extends FragmentPagerAdapter {

    private int PAGE_COUNT;
    private double lat;
    private double lon;

    public WeatherPageAdapter(FragmentManager fm, int count, double lat, double lon) {
        super(fm);
        this.PAGE_COUNT = count;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public Fragment getItem(int position) {
        return WeatherList.newInstance(position, lat, lon);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub
        super.destroyItem(container,position,object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Question " + position;
    }

    @Override
    public Parcelable saveState(){
        return null;
    }
}
