package com.grannyos.media;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.grannyos.MainActivity;
import com.grannyos.R;


public class MediaFragment extends Fragment implements View.OnClickListener{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.media_layout, container, false);
        ImageView image = (ImageView) rootView.findViewById(R.id.mainIcon);
        MainActivity.relativeLayout.setVisibility(View.VISIBLE);
        Button back = (Button) rootView.findViewById(R.id.backButton);
        back.setOnClickListener(this);
        image.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.backButton:
                getActivity().onBackPressed();
                break;
            case R.id.mainIcon:
                Fragment fragment = new ShowMusicFragment();
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.content_frame, fragment).commit();
                break;
        }
    }
}
