package com.grannyos;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grannyos.call.CallPageFragment;
import com.grannyos.database.LoadDataFromDatabase;
import com.grannyos.database.pojo.RelativesData;
import com.grannyos.event.EventPageFragment;
import com.grannyos.health.HealthFragment;
import com.grannyos.media.MediaFragment;
import com.grannyos.memoir.MainMemoirFragment;
import com.grannyos.photo.AlbumPageFragment;
import com.grannyos.remainder.RemainderPageFragment;
import com.grannyos.weather.WeatherPageFragment;


public class GrannyOsListElement extends Fragment implements View.OnClickListener{

    private static final    String TAG = "GrannyOsListElement";
    private int             position = 0;
    private Drawable        mainIcon[];
    private String[]        mainDescription;
    private RelativeLayout  missingCall;
    private TextView        countMissingCall;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainIcon = new Drawable[] {
                ResourcesCompat.getDrawable(getActivity().getResources(), R.drawable.call, null),
                ResourcesCompat.getDrawable(getActivity().getResources(), R.drawable.events, null),
                ResourcesCompat.getDrawable(getActivity().getResources(), R.drawable.main_icon_album, null),
                ResourcesCompat.getDrawable(getActivity().getResources(), R.drawable.health, null),
                ResourcesCompat.getDrawable(getActivity().getResources(), R.drawable.reminder, null),
                ResourcesCompat.getDrawable(getActivity().getResources(), R.drawable.memoirs,null),
                ResourcesCompat.getDrawable(getActivity().getResources(), R.drawable.media,null),
                ResourcesCompat.getDrawable(getActivity().getResources(), R.drawable.weather, null)};
        position = getArguments().getInt("page");
        mainDescription = getActivity().getResources().getStringArray(R.array.description_main_icon);
        Log.d(TAG, "page position: " + position);
        try {
            new LoadDataFromDatabase("relatives", getActivity(), "");
        } catch(Exception e){
            Log.d(TAG, "Error while get relatives");
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gos_main_item_layout, container, false);
        ImageView ivMainIcon = (ImageView) rootView.findViewById(R.id.mainIcon);
        ivMainIcon.setImageDrawable(mainIcon[position]);
        TextView tvDescription = (TextView) rootView.findViewById(R.id.descriptionMainIcon);
        missingCall = (RelativeLayout) rootView.findViewById(R.id.relativeMissingCall);
        countMissingCall = (TextView) rootView.findViewById(R.id.countMissingCall);
        ivMainIcon.setOnClickListener(this);
        for(RelativesData data : LoadDataFromDatabase.getRelativeData()){
            if(data.getMissing() > 0){
                missingCall.setVisibility(View.VISIBLE);
                countMissingCall.setText(data.getMissing());
            }
            else{
                missingCall.setVisibility(View.INVISIBLE);
            }
        }

        try{
            if(position == (mainIcon.length - 1)) {
                tvDescription.setText(mainDescription[position] + " in " + ViewPagerFragment.city);
            }
            else {
                tvDescription.setText(mainDescription[position]);
            }
        }catch (Exception e) {
            Log.d(TAG, "Error in position of main icon");
            e.printStackTrace();
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.mainIcon){
            Fragment fragment;
            Bundle bundle;
            switch(position){
                case 0:
                    fragment = new CallPageFragment();
                    break;
                case 1:
                    fragment = new EventPageFragment();
                    break;
                case 2:
                    fragment = new AlbumPageFragment();
                    break;
                case 3:
                    fragment = new HealthFragment();
                    break;
                case 4:
                    fragment = new RemainderPageFragment();
                    break;
                case 5:
                    fragment = new MainMemoirFragment();
                    break;
                case 6:
                    fragment = new MediaFragment();
                    break;
                case 7:
                    bundle = new Bundle();
                    bundle.putDouble("lat", ViewPagerFragment.lat);
                    bundle.putDouble("lon", ViewPagerFragment.lon);
                    fragment = new WeatherPageFragment();
                    fragment.setArguments(bundle);
                    break;
                default:
                    fragment = null;
                    break;
            }
            if(fragment != null) {
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.content_frame, fragment).commit();
            }
            else{
                Log.d(TAG, "Error in program. Fragment is null ");
            }
        }
    }
}
