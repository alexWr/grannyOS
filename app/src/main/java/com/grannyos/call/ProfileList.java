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
import com.grannyos.network.SocketService;
import com.grannyos.utils.DecodeBitmap;

import java.io.File;
import java.util.ArrayList;

import io.socket.emitter.Emitter;


/**
 * Fragment show the list of all relatives
 */

public class ProfileList extends Fragment implements View.OnClickListener{


    private static final String         TAG = "ProfileListGrannyOs";
    private int                         position =0;
    private DecodeBitmap                decodeBitmap=new DecodeBitmap();
    private ImageView                   onlineOffline;
    private RelativeLayout              callToRegion;
    private Activity                    activity;
    private TextView                    tvHelpDescription;
    private String                      relativeId;
    private String                      firstName;
    private String                      lastName;
    private ImageView                   arrow;
    private String                      textOffline;
    private String                      textOnline;
    private ArrayList<RelativesData>    relativesData = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("page");
        activity = getActivity();
        arrow = CallPageFragment.arrow;
        arrow.setVisibility(View.INVISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_list_layout, container, false);
        callToRegion = (RelativeLayout) rootView.findViewById(R.id.callToRegion);
        ImageView mainProfileIcon = (ImageView) rootView.findViewById(R.id.mainProfileIcon);
        onlineOffline = (ImageView) rootView.findViewById(R.id.onlineOffline);
        TextView firstLastName = (TextView) rootView.findViewById(R.id.firstLastName);
        tvHelpDescription = (TextView) rootView.findViewById(R.id.helpDescription);
        if(CallPageFragment.online.size() == 0 ){
            new LoadDataFromDatabase("relatives", getActivity(), "");
        }
        relativesData = LoadDataFromDatabase.getRelativeData();
        try {
            if (relativesData.size() != 0) {
                relativeId = relativesData.get(position).getRelativesId();
                firstName = relativesData.get(position).getFirstName();
                lastName = relativesData.get(position).getLastName();
                firstLastName.setText(firstName + " " + lastName);
                textOnline = "To call " + firstName + " " + lastName + " tap here";
                textOffline = "You can't call " + firstName + " " + lastName + " because relative offline";
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
                arrow.setVisibility(View.INVISIBLE);
                tvHelpDescription.setVisibility(View.INVISIBLE);
                onlineOffline.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e){
            Log.d(TAG, "Something went wrong in ProfileList");
        }
        if(CallPageFragment.online.size()==0){
            changeUIOnlineOffline(R.drawable.offline, View.INVISIBLE, false, textOffline);
        }
        else {
            for(int i = 0; i< CallPageFragment.online.size(); i++) {
                if (CallPageFragment.online.get(i).equals(relativeId)) {
                    changeUIOnlineOffline(R.drawable.online, View.VISIBLE, true, textOnline);
                }
                else {
                    changeUIOnlineOffline(R.drawable.offline, View.INVISIBLE, false, textOffline);
                }
            }
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        online();
        offline();
    }

    @Override
    public void onClick(View v) {
        CallPageFragment.currentPosition = position;
        Log.d(TAG, "save currentPosition " + CallPageFragment.currentPosition);
        Bundle bundle = new Bundle();
        bundle.putString("firstName", firstName);
        bundle.putString("lastName", lastName);
        bundle.putString("avatar", relativesData.get(position).getAvatar());
        bundle.putString("id", relativeId);
        Fragment fragment = new CallToFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = activity.getFragmentManager();
        fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.content_frame, fragment).commit();
    }

    private void online(){
        if (SocketService.getSocket() != null)
            SocketService.getSocket().on("online", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "online " + args[0] + "relativeId " + relativeId);
                    if(args[0].equals(relativeId)) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "online ");
                                changeUIOnlineOffline(R.drawable.online, View.VISIBLE, true, textOnline);
                            }
                        });
                    }
                }
            });
        else{
            Log.e(TAG, "Socket is null in someone online");
        }
    }

    private void offline() {
        if(SocketService.getSocket() != null) {
            SocketService.getSocket().on("offline", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "offline " + args[0] + "relativeId " + relativesData.get(position).getRelativesId());
                    if(args[0].equals(relativeId)) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "offline");
                                changeUIOnlineOffline(R.drawable.offline, View.INVISIBLE, false, textOffline);
                            }
                        });
                    }
                }
            });
        }
        else{
            Log.e(TAG, "Socket is null in offline");
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


    public void changeUIOnlineOffline(int resId, int visibility, boolean clickListener, String setText){

        onlineOffline.setImageResource(resId);
        if(clickListener){
            callToRegion.setOnClickListener(ProfileList.this);
        }
        else{
            callToRegion.setOnClickListener(null);
        }
        arrow.setVisibility(visibility);
        tvHelpDescription.setText(setText);
    }
}
