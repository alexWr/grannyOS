package com.grannyos.event;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.grannyos.R;
import com.grannyos.ViewPagerAdapter;
import com.grannyos.database.LoadDataFromDatabase;
import com.grannyos.utils.HideViews;


public class EventPageFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "EventPageGrannyOs";
    private ViewPager           eventPager;
    private ImageView           nextButton;
    private ImageView           prevButton;
    private TextView            previouslyPageDescription;
    private TextView            nextPageDescription;
    private ChangeListener      mListener = new ChangeListener();
    private HideViews           hideViews;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.event_page_layout, container, false);
        eventPager=(ViewPager)rootView.findViewById(R.id.eventPager);
        Button backButton = (Button) rootView.findViewById(R.id.backButton);
        prevButton = (ImageView) rootView.findViewById(R.id.previousPage);
        nextButton = (ImageView) rootView.findViewById(R.id.nextPage);
        previouslyPageDescription = (TextView) rootView.findViewById(R.id.previouslyPageDescription);
        nextPageDescription = (TextView) rootView.findViewById(R.id.nextPageDescription);
        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        try {
            new LoadDataFromDatabase("event", getActivity(), "");
            Log.d(TAG, "current event " + LoadDataFromDatabase.getEventData().size());
            Log.d(TAG, "current event album resource " + LoadDataFromDatabase.getAlbumResource().size());
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity(), getChildFragmentManager(),
                    ListEvent.class, LoadDataFromDatabase.getEventData().size());
            eventPager.setAdapter(viewPagerAdapter);
            eventPager.setCurrentItem(0);
            eventPager.addOnPageChangeListener(mListener);
            hideViews = new HideViews(eventPager);
            hideViews.visibility(prevButton, previouslyPageDescription, nextButton, nextPageDescription);
        } catch (Exception e){
            Log.d(TAG, "Error in EventPageFragment");
            e.printStackTrace();
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.previousPage:
                eventPager.setCurrentItem(eventPager.getCurrentItem() - 1);
                break;
            case R.id.nextPage:
                eventPager.setCurrentItem(eventPager.getCurrentItem() + 1);
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
