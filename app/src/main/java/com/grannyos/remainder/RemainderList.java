package com.grannyos.remainder;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grannyos.R;
import com.grannyos.database.LoadDataFromDatabase;
import com.grannyos.utils.DecodeBitmap;

import java.io.File;


public class RemainderList extends Fragment{

    private static final String TAG = "RemainderListGrannyOs";
    private int                 position = 0;
    private DecodeBitmap        decodeBitmap = new DecodeBitmap();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("page");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.remainder_list_layout, container, false);
        ImageView iconRemainder = (ImageView) rootView.findViewById(R.id.iconRemainder);
        TextView descriptionRemainder = (TextView) rootView.findViewById(R.id.descriptionRemainder);
        TextView helpDescription = (TextView) rootView.findViewById(R.id.helpDescription);
        descriptionRemainder.setText("");
        helpDescription.setText("Some description");
        try {
            if (LoadDataFromDatabase.getAlbumResource().size() == 0) {
                iconRemainder.setVisibility(View.INVISIBLE);
            } else {
                File imgFile = new File(LoadDataFromDatabase.getAlbumResource().get(position));
                if (imgFile.exists()) {
                    iconRemainder.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    iconRemainder.setImageBitmap(decodeBitmap.decodeSampledBitmapFromFile(LoadDataFromDatabase.getAlbumResource().get(position), 300, 300));
                }
            }
        } catch( NullPointerException | IndexOutOfBoundsException e){
            Log.d(TAG, "Error in RemainderList");
            e.printStackTrace();
        }
        return rootView;
    }
}
