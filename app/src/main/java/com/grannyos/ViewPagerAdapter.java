package com.grannyos;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v13.app.FragmentPagerAdapter;
import android.view.ViewGroup;


public class ViewPagerAdapter  extends FragmentPagerAdapter {

    private int PAGE_COUNT;
    private Class<?> fragmentName;
    private Context context;


    public ViewPagerAdapter(Context context, FragmentManager fm, Class<?> className, int count) {
        super(fm);

        this.PAGE_COUNT = count;
        this.fragmentName = className;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("page", position);
        return Fragment.instantiate(context, fragmentName.getName(), bundle); //fragmentInstance.newInstance(position);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
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
