package com.grannyos;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grannyos.call.CallPageFragment;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection deprecation
        mainIcon = new Drawable[] {
                getActivity().getResources().getDrawable(R.drawable.call),
                getActivity().getResources().getDrawable(R.drawable.events),
                getActivity().getResources().getDrawable(R.drawable.main_icon_album),
                getActivity().getResources().getDrawable(R.drawable.health),
                getActivity().getResources().getDrawable(R.drawable.reminder),
                getActivity().getResources().getDrawable(R.drawable.memoirs),
                getActivity().getResources().getDrawable(R.drawable.media),
                getActivity().getResources().getDrawable(R.drawable.weather)};
        position = getArguments().getInt("page");
        mainDescription = getActivity().getResources().getStringArray(R.array.description_main_icon);
        Log.d(TAG, "page position: " + position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gos_main_item_layout, container, false);
        ImageView ivMainIcon = (ImageView) rootView.findViewById(R.id.mainIcon);
        ivMainIcon.setImageDrawable(mainIcon[position]);
        TextView tvDescription = (TextView) rootView.findViewById(R.id.descriptionMainIcon);
        ImageView ivIndicator = (ImageView) rootView.findViewById(R.id.indicator);
        TextView tvCountMissingCall = (TextView) rootView.findViewById(R.id.countMissingCall);
        ivMainIcon.setOnClickListener(this);
        ivIndicator.setVisibility(View.GONE);
        tvCountMissingCall.setVisibility(View.GONE);
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
