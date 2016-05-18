package com.grannyos.media;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.grannyos.MainActivity;
import com.grannyos.R;


public class ShowMusicFragment extends Fragment {

    private VideoView video;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.show_music_fragment_layout, container, false);
        video = (VideoView) rootView.findViewById(R.id.showVideo);
        getInit(Environment.getExternalStorageDirectory().toString() + "/grannyos/musicplay.mp4");
        MainActivity.relativeLayout.setVisibility(View.GONE);
        return rootView;
    }

    public void getInit(String path) {
        video.setVideoPath(path);
        video.requestFocus();
        video.start();
    }

}
