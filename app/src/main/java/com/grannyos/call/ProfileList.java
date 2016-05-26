package com.grannyos.call;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grannyos.R;
import com.grannyos.database.LoadDataFromDatabase;
import com.grannyos.database.pojo.RelativesData;
import com.grannyos.utils.DecodeBitmap;

import java.io.File;
import java.util.ArrayList;


/**
 * Fragment show the list of all relatives
 */

public class ProfileList extends Fragment implements OnlineOfflineListener{


    private static final String         TAG = "ProfileListGrannyOs";
    private int                         position = 0;
    private DecodeBitmap                decodeBitmap = new DecodeBitmap();
    private ImageView            onlineOffline;
    private RelativeLayout       callToRegion;
    private Activity                    activity;
    private String                      relativeId;
    private ArrayList<RelativesData>    relativesData = new ArrayList<>();
    public static TextView              firstLastName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("page");
        activity = getActivity();
        Log.d(TAG, "page position" + position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_list_layout, container, false);
        callToRegion = (RelativeLayout) rootView.findViewById(R.id.callToRegion);
        ImageView mainProfileIcon = (ImageView) rootView.findViewById(R.id.mainProfileIcon);
        onlineOffline = (ImageView) rootView.findViewById(R.id.onlineOffline);
        firstLastName = (TextView) rootView.findViewById(R.id.firstLastName);
        relativesData = LoadDataFromDatabase.getRelativeData();
        try {
            if (relativesData.size() != 0) {
                relativeId = relativesData.get(position).getRelativesId();
                firstLastName.setText(relativesData.get(position).getFirstName() + " " + relativesData.get(position).getLastName());
                if (!relativesData.get(position).getAvatar().equals("none")) {
                    File imgFile = new File(relativesData.get(position).getAvatar());
                    if (imgFile.exists()) {
                        mainProfileIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        mainProfileIcon.setImageBitmap(decodeBitmap.decodeSampledBitmapFromFile(relativesData.get(position).getAvatar(), 300, 300));
                    }
                } else
                    mainProfileIcon.setImageResource(R.drawable.default_avatar);
            }
            else{
                onlineOffline.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e){
            Log.d(TAG, "Something went wrong in ProfileList");
        }
        new CallPageFragment().setOnlineListener(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if(CallPageFragment.online.size()==0){
            changeUIOnlineOffline(R.drawable.offline, false);
        }
        else {
            for(int i = 0; i< CallPageFragment.online.size(); i++) {
                if (CallPageFragment.online.get(i).equals(relativeId)) {
                    Log.d(TAG, "changeUIOnlineOffline in for if");
                    changeUIOnlineOffline(R.drawable.online, true);
                }
                else {
                    Log.d(TAG, "changeUIOnlineOffline in for else");
                    changeUIOnlineOffline(R.drawable.offline, false);
                }
            }
        }
    }

    public class OnClickListenerProfileList implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            CallPageFragment.currentPosition = position;
            Log.d(TAG, "save currentPosition " + CallPageFragment.currentPosition);
            Bundle bundle = new Bundle();
            bundle.putString("firstName", relativesData.get(position).getFirstName());
            bundle.putString("lastName", relativesData.get(position).getLastName());
            bundle.putString("avatar", relativesData.get(position).getAvatar());
            bundle.putString("id", relativeId);
            Fragment fragment = new CallToFragment();
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = activity.getFragmentManager();
            fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.content_frame, fragment).commit();
        }
    }

    @Override
    public void relativesOnlineOffline(boolean onlineOffline, String id) {
        if(onlineOffline)
            changeUIOnlineOffline(R.drawable.online, true);
        else
            changeUIOnlineOffline(R.drawable.offline, false);
    }

    public void changeUIOnlineOffline(int resId, boolean clickListener){
        if(onlineOffline != null) {
            Log.d(TAG, "onlineOffline != null");
            onlineOffline.setImageResource(resId);
        }
        else{
            Log.d(TAG, "change on ui online offline == null");
        }
        if(callToRegion != null) {
            Log.d(TAG, "callToRegion != null");
            if (clickListener) {
                callToRegion.setOnClickListener(new OnClickListenerProfileList());
            } else {
                callToRegion.setOnClickListener(null);
            }
        }
        else{
            Log.d(TAG, "change on ui online offline call to region == null");
        }
    }
}
