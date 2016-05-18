package com.grannyos.memoir;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.grannyos.R;


public class MainMemoirFragment extends Fragment implements View.OnClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_memoirs_layout_frgment, container, false);
        ImageView startMemoirs = (ImageView) rootView.findViewById(R.id.mainIcon);
        Button backButton = (Button) rootView.findViewById(R.id.backButton);
        backButton.setOnClickListener(this);
        startMemoirs.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.backButton:
                getActivity().onBackPressed();
                break;
            case R.id.mainIcon:
                Fragment fragment = new MemoirFragment();
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.content_frame, fragment).commit();
                break;
        }
    }
}
