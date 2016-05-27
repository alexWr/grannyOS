package com.grannyos.call;

import android.app.Activity;
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
import com.grannyos.database.pojo.RelativesData;
import com.grannyos.network.SocketService;
import com.grannyos.utils.HideViews;
import com.grannyos.utils.ZoomOutPageTransformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Ack;
import io.socket.emitter.Emitter;

/**
 * Fragment is appear when user press list of relatives
 */

public class CallPageFragment extends Fragment implements View.OnClickListener{


    private static final String         TAG = "CallPagerGrannyOs";
    private ViewPager                   callPager;
    public static int                   currentPosition = 0;
    private ImageView                   nextButton;
    private ImageView                   prevButton;
    private ChangeListener              mListener = new ChangeListener();
    public static ArrayList<String>     online = new ArrayList<>();
    public static ImageView             arrow;
    private TextView                    tvHelpDescription;
    private TextView                    previouslyPageDescription;
    private TextView                    nextPageDescription;
    private HideViews                   hideViews;
    private String                      relativeId;
    private String                      firstName;
    private String                      lastName;
    private String                      textOffline;
    private String                      textOnline;
    private ArrayList<RelativesData>    relativesData;
    private Activity                    activity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.call_page_layout, container, false);
        callPager=(ViewPager)rootView.findViewById(R.id.callPager);
        Button backButton = (Button) rootView.findViewById(R.id.backButton);
        prevButton = (ImageView) rootView.findViewById(R.id.previousPage);
        nextButton = (ImageView) rootView.findViewById(R.id.nextPage);
        arrow = (ImageView) rootView.findViewById(R.id.arrow);
        tvHelpDescription = (TextView) rootView.findViewById(R.id.helpDescription);
        previouslyPageDescription = (TextView) rootView.findViewById(R.id.previouslyPageDescription);
        nextPageDescription = (TextView) rootView.findViewById(R.id.nextPageDescription);
        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        callPager.setPageTransformer(true, new ZoomOutPageTransformer());
        try {
            new LoadDataFromDatabase("relatives", getActivity(), "");
        } catch(Exception e){
            Log.d(TAG, "Error while get relatives");
            e.printStackTrace();
        }
        relativesData = LoadDataFromDatabase.getRelativeData();
        if(SocketService.getSocket() != null && SocketService.getSocket().connected()) {
            getOnline();
        }
        else {
            Log.e(TAG, "Socket == null");
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
                getInfo();
                checkRelativesOnline();
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
            getInfo();
            checkRelativesOnline();
            online();
            offline();
            hideViews = new HideViews(callPager);
            hideViews.visibility(prevButton, previouslyPageDescription, nextButton, nextPageDescription);
        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "Error in CallPageFragment while set adapter");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(SocketService.getSocket() != null) {
            SocketService.getSocket().off("online");
            SocketService.getSocket().off("offline");
        }
    }

    /**
     * All method for online/offline relatives
     * Register socket IO listener for online/offline relatives
     */


    private void getOnline(){
        try {
            SocketService.getSocket().emit("relatives", new Ack() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "online clients in getOnline " + args[1]);
                    online.clear();
                    try {
                        JSONArray array = new JSONArray(args[1].toString());
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject json = array.getJSONObject(i);
                            online.add(json.getString("relativeId"));
                        }
                        activity.runOnUiThread(new Runnable() {
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

    private void online(){
        if (SocketService.getSocket() != null) {
            SocketService.getSocket().on("online", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "online " + args[0] + " relativeId " + relativeId);
                    String relative= "";
                    if(args[0] != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(args[0].toString());
                            relative = jsonObject.getString("relativeId");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(!online.contains(relative))
                            online.add(relative);
                        if (relative.equals(relativeId)) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "online ");
                                    changeUIOnlineOffline(View.VISIBLE, textOnline);
                                }
                            });
                        }
                    }
                }
            });
        }
        else{
            Log.e(TAG, "Socket is null in someone online");
        }
    }

    private void offline() {
        if(SocketService.getSocket() != null) {
            SocketService.getSocket().on("offline", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "offline " + args[0] + " relativeId " + relativeId);
                    String relative= "";
                    if(args[0] != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(args[0].toString());
                            relative = jsonObject.getString("relativeId");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(online.contains(relative))
                            online.remove(relative);
                        if (relative.equals(relativeId)) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "offline");
                                    changeUIOnlineOffline(View.INVISIBLE, textOffline);
                                }
                            });
                        }
                    }
                }
            });
        }
        else{
            Log.e(TAG, "Socket is null in offline");
        }
    }

    private void checkRelativesOnline(){
        if(online.size()!=0)
            for(int i = 0; i< online.size(); i++) {
                if (online.get(i).equals(LoadDataFromDatabase.getRelativeData().get(callPager.getCurrentItem()).getRelativesId()) )
                    changeUIOnlineOffline(View.VISIBLE, textOnline);
                else
                    changeUIOnlineOffline(View.INVISIBLE, textOffline);
            }
        else
            changeUIOnlineOffline(View.INVISIBLE, textOffline);
    }

    private void getInfo(){
        if(relativesData.size() != 0){
            relativeId = relativesData.get(callPager.getCurrentItem()).getRelativesId();
            firstName = relativesData.get(callPager.getCurrentItem()).getFirstName();
            lastName = relativesData.get(callPager.getCurrentItem()).getLastName();
            textOnline = "To call " + firstName + " " + lastName + " tap here";
            textOffline = "You can't call " + firstName + " " + lastName + " because relative offline";
        }
        else{
            textOnline = "Can't find someone";
            textOffline = "Can't find someone";
        }
    }

    public void changeUIOnlineOffline(int visibility, String setText){
        arrow.setVisibility(visibility);
        tvHelpDescription.setText(setText);
    }
}
