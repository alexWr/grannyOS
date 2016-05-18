package com.grannyos.remainder;

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


public class RemainderPageFragment extends Fragment implements View.OnClickListener{


    private final static String TAG = "RemainderGrannyOs";
    private ViewPager           remainderPager;
    private ImageView           nextButton;
    private ImageView           prevButton;
    private TextView            previouslyPageDescription;
    private TextView            nextPageDescription;
    private TextView            countRemainder;
    private ChangeListener      mListener = new ChangeListener();
    private int                 showPos=1;
    private HideViews           hideViews;
    private ViewPagerAdapter    remainderPageAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.remainder_page_layout, container, false);
        remainderPager =(ViewPager)rootView.findViewById(R.id.remainderPager);
        Button backButton = (Button) rootView.findViewById(R.id.backButton);
        prevButton = (ImageView) rootView.findViewById(R.id.previousPage);
        nextButton = (ImageView) rootView.findViewById(R.id.nextPage);
        countRemainder = (TextView) rootView.findViewById(R.id.countRemainder);
        previouslyPageDescription = (TextView) rootView.findViewById(R.id.previouslyPageDescription);
        nextPageDescription = (TextView) rootView.findViewById(R.id.nextPageDescription);
        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        try {
            new LoadDataFromDatabase("event", getActivity(), "");
            remainderPageAdapter = new ViewPagerAdapter(getActivity(), getChildFragmentManager(), RemainderList.class, 0);
            remainderPager.setAdapter(remainderPageAdapter);
            remainderPager.setCurrentItem(0);
            remainderPager.addOnPageChangeListener(mListener);
            hideViews = new HideViews(remainderPager);
            hideViews.visibility(prevButton, previouslyPageDescription, nextButton, nextPageDescription);
        } catch(Exception e){
            Log.d(TAG, "Error in RemainderPageFragment");
            e.printStackTrace();
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.previousPage:
                remainderPager.setCurrentItem(remainderPager.getCurrentItem() - 1);
                break;
            case R.id.nextPage:
                remainderPager.setCurrentItem(remainderPager.getCurrentItem() + 1);
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
            showPos = position;
            showPos++;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                if(remainderPageAdapter != null) {
                    countRemainder.setText("" + showPos + " of " + remainderPageAdapter.getCount());
                    hideViews.visibility(prevButton, previouslyPageDescription, nextButton, nextPageDescription);
                }
            }
        }
    }
}
