package com.grannyos.call;

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
import com.grannyos.network.SocketService;
import com.grannyos.utils.HideViews;
import com.grannyos.utils.ZoomOutPageTransformer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Ack;

/**
 * Fragment is appear when user press list of relatives
 */

public class CallPageFragment extends Fragment implements View.OnClickListener{


    private static final String     TAG = "CallPagerGrannyOs";
    private ViewPager               callPager;
    public static int               currentPosition = 0;
    private ImageView               nextButton;
    private ImageView               prevButton;
    private ChangeListener          mListener = new ChangeListener();
    public static ArrayList<String> online = new ArrayList<>();
    public static ImageView         arrow;
    private TextView                previouslyPageDescription;
    private TextView                nextPageDescription;
    private HideViews               hideViews;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.call_page_layout, container, false);
        callPager=(ViewPager)rootView.findViewById(R.id.callPager);
        Button backButton = (Button) rootView.findViewById(R.id.backButton);
        prevButton = (ImageView) rootView.findViewById(R.id.previousPage);
        nextButton = (ImageView) rootView.findViewById(R.id.nextPage);
        arrow = (ImageView) rootView.findViewById(R.id.arrow);
        arrow.setVisibility(View.INVISIBLE);
        previouslyPageDescription = (TextView) rootView.findViewById(R.id.previouslyPageDescription);
        nextPageDescription = (TextView) rootView.findViewById(R.id.nextPageDescription);
        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        callPager.setPageTransformer(true, new ZoomOutPageTransformer());
        if(SocketService.getSocket() != null && SocketService.getSocket().connected()) {
            getOnline();
        }
        else {
            Log.e(TAG, "Socket == null");
            new LoadDataFromDatabase("relatives", getActivity(), "");
            setAdapter();
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.previousPage:
                callPager.setCurrentItem(callPager.getCurrentItem() - 1);
                break;
            case R.id.nextPage:
                callPager.setCurrentItem(callPager.getCurrentItem() + 1);
                break;
            case R.id.backButton:
                currentPosition=0;
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

    private void setAdapter(){
        try {
            ViewPagerAdapter callPagerAdapter = new ViewPagerAdapter(getActivity(), getChildFragmentManager(),
                    ProfileList.class, LoadDataFromDatabase.getRelativeData().size());
            callPager.setAdapter(callPagerAdapter);
            callPager.setCurrentItem(currentPosition);
            callPager.addOnPageChangeListener(mListener);
            hideViews = new HideViews(callPager);
            hideViews.visibility(prevButton, previouslyPageDescription, nextButton, nextPageDescription);
        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "Error in CallPageFragment while set adapter");
        }
    }

    private void getOnline(){
        try {
            SocketService.getSocket().emit("relatives", new Ack() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "online clients" + args[1]);
                    online.clear();
                    try {
                        JSONArray array = new JSONArray(args[1].toString());
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject json = array.getJSONObject(i);
                            Log.d(TAG, "relativeId " + json.getString("relativeId"));
                            online.add(json.getString("relativeId"));
                        }
                        new LoadDataFromDatabase("relatives", getActivity(), "");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setAdapter();
                            }
                        });
                    } catch (Throwable e) {
                        Log.e(TAG, "failed to get online clients", e);
                    }
                }
            });
        }
        catch (Throwable e) {
            Log.d(TAG, "WTF????!!!!", e);
        }
    }
}
