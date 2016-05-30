package com.grannyos.memoir;

import android.app.Fragment;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;

import com.grannyos.R;


public class MemoirFragment extends Fragment implements View.OnClickListener {


    private Chronometer chronometer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.memoir_layout, container, false);
        Button stopRecord = (Button) rootView.findViewById(R.id.stopRecord);
        chronometer = (Chronometer) rootView.findViewById(R.id.chronometer);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        stopRecord.setOnClickListener(this);
        return rootView;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.stopRecord:
                chronometer.stop();
                getActivity().onBackPressed();
                break;
        }
    }
}
