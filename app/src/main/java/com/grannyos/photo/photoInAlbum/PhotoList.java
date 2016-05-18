package com.grannyos.photo.photoInAlbum;

import android.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.grannyos.R;
import com.grannyos.database.LoadDataFromDatabase;
import com.grannyos.utils.DecodeBitmap;

import java.io.File;


public class PhotoList extends Fragment{

    private static final String TAG = "PhotoListGrannyOs";
    private int                 position = 0;
    private VideoView           video;
    private DecodeBitmap        decodeBitmap = new DecodeBitmap();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("page");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.photo_list_layout, container, false);
        ImageView photo = (ImageView) rootView.findViewById(R.id.photo);
        video = (VideoView) rootView.findViewById(R.id.video);
        RelativeLayout mainRelativePhoto = (RelativeLayout) rootView.findViewById(R.id.mainRelativePhoto);
        Log.d(TAG, "position type " + position);
        Log.d(TAG, "photo size " + LoadDataFromDatabase.getPhotoData().size());
        try {
            if (LoadDataFromDatabase.getPhotoData().size() == 0) {
                photo.setVisibility(View.INVISIBLE);
                video.setVisibility(View.INVISIBLE);
                mainRelativePhoto.setBackgroundColor(getActivity().getResources().getColor(R.color.background_grey));
            } else {
                if (LoadDataFromDatabase.getPhotoData().get(position).getAssetType().equals("video")) {
                    photo.setVisibility(View.GONE);
                    video.setVisibility(View.VISIBLE);
                    getInit(LoadDataFromDatabase.getPhotoData().get(position).getAssetResource());
                } else {
                    photo.setVisibility(View.VISIBLE);
                    video.setVisibility(View.GONE);
                    File imgFile = new File(LoadDataFromDatabase.getPhotoData().get(position).getAssetResource());
                    if (imgFile.exists()) {
                        Point displaySize = new Point();
                        getActivity().getWindowManager().getDefaultDisplay().getSize(displaySize);
                        photo.setImageBitmap(decodeBitmap.decodeSampledBitmapFromFile(LoadDataFromDatabase.getPhotoData().get(position).getAssetResource(),
                                displaySize.x, displaySize.y));
                    }
                }
            }
        } catch(Exception e){
            Log.d(TAG, "Error in PhotoList");
            e.printStackTrace();
        }
        return rootView;
    }


    @Override
    public void onStop() {
        super.onStop();
        if(video.isPlaying())
            video.stopPlayback();
    }

    public void getInit(String path) {
        video.setVideoPath(path);
        video.requestFocus();
        video.start();
    }
}
