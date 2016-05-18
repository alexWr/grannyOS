package com.grannyos.notification;

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
import com.grannyos.ViewPagerAdapter;
import com.grannyos.utils.HideViews;


public class NotificationPageFragment extends Fragment implements View.OnClickListener{

    private final static String TAG = "NotificationGrannyOs";
    private ViewPager           notificationPager;
    private ImageView           nextButton;
    private ImageView           prevButton;
    private TextView            previouslyPageDescription;
    private TextView            nextPageDescription;
    private ChangeListener      mListener = new ChangeListener();
    private HideViews           hideViews;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notification_page_layout, container, false);
        notificationPager=(ViewPager)rootView.findViewById(R.id.notificationPager);
        Button okClose = (Button) rootView.findViewById(R.id.okClose);
        prevButton = (ImageView) rootView.findViewById(R.id.previousPage);
        nextButton = (ImageView) rootView.findViewById(R.id.nextPage);
        previouslyPageDescription = (TextView) rootView.findViewById(R.id.previouslyPageDescription);
        nextPageDescription = (TextView) rootView.findViewById(R.id.nextPageDescription);
        hideViews = new HideViews(notificationPager);
        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        okClose.setOnClickListener(this);
        ViewPagerAdapter notificationPageAdapter = new ViewPagerAdapter(getActivity(), getChildFragmentManager(), ListNotification.class, 1);
        notificationPager.setAdapter(notificationPageAdapter);
        notificationPager.setCurrentItem(0);
        notificationPager.addOnPageChangeListener(mListener);
        hideViews.visibility(prevButton, previouslyPageDescription, nextButton, nextPageDescription);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.previousPage:
                notificationPager.setCurrentItem(notificationPager.getCurrentItem() - 1);
                break;
            case R.id.nextPage:
                notificationPager.setCurrentItem(notificationPager.getCurrentItem() + 1);
                break;
            case R.id.okClose:
                getActivity().finish();
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
