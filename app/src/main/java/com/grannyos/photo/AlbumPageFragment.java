package com.grannyos.photo;

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
import com.grannyos.database.LoadDataFromDatabase;
import com.grannyos.utils.HideViews;


public class AlbumPageFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "AlbumPageGrannyOs";
    private ViewPager           photoPager;
    public static int           currentPosition=0;
    private ImageView           nextButton;
    private ImageView           prevButton;
    private TextView            previouslyPageDescription;
    private TextView            nextPageDescription;
    private ChangeListener      mListener = new ChangeListener();
    private HideViews           hideViews;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.album_page_layout, container, false);
        photoPager=(ViewPager)rootView.findViewById(R.id.albumPage);
        Button backButton = (Button) rootView.findViewById(R.id.backButton);
        prevButton = (ImageView) rootView.findViewById(R.id.previousPage);
        nextButton = (ImageView) rootView.findViewById(R.id.nextPage);
        previouslyPageDescription = (TextView) rootView.findViewById(R.id.previouslyPageDescription);
        nextPageDescription = (TextView) rootView.findViewById(R.id.nextPageDescription);
        new LoadDataFromDatabase("album", getActivity(), "");
        ViewPagerAdapter photoPagerAdapter = new ViewPagerAdapter(getActivity(), getChildFragmentManager(), AlbumList.class, LoadDataFromDatabase.getAlbumData().size());
        photoPager.setAdapter(photoPagerAdapter);
        photoPager.setCurrentItem(currentPosition);
        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        photoPager.addOnPageChangeListener(mListener);
        hideViews = new HideViews(photoPager);
        hideViews.visibility(prevButton, previouslyPageDescription, nextButton, nextPageDescription);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.previousPage:
                photoPager.setCurrentItem(photoPager.getCurrentItem() - 1);
                break;
            case R.id.nextPage:
                photoPager.setCurrentItem(photoPager.getCurrentItem() + 1);
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
