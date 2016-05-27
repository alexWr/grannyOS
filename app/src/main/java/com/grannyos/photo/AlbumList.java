package com.grannyos.photo;

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

import com.grannyos.MainActivity;
import com.grannyos.R;
import com.grannyos.database.LoadDataFromDatabase;
import com.grannyos.photo.photoInAlbum.PhotoPageFragment;
import com.grannyos.utils.DecodeBitmap;

import java.io.File;


public class AlbumList extends Fragment implements View.OnClickListener{

    private final static String TAG = "AlbumListGrannyOs";
    private int                 position = 0;
    private DecodeBitmap        decodeBitmap = new DecodeBitmap();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("page");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.album_list_layout, container, false);
        RelativeLayout showPhotoIcon = (RelativeLayout) rootView.findViewById(R.id.showPhotoIcon);
        RelativeLayout showPhoto = (RelativeLayout) rootView.findViewById(R.id.showPhoto);
        ImageView mainProfileIcon = (ImageView) rootView.findViewById(R.id.mainProfileIcon);
        TextView descriptionMainIcon = (TextView) rootView.findViewById(R.id.descriptionMainIcon);
        mainProfileIcon.setScaleType(ImageView.ScaleType.CENTER);
        showPhoto.setOnClickListener(this);
        showPhotoIcon.setOnClickListener(this);
        mainProfileIcon.setOnClickListener(this);
        MainActivity.relativeLayout.setVisibility(View.VISIBLE);
        Log.d(TAG, "albumData size" + LoadDataFromDatabase.getAlbumData().size());
        if(LoadDataFromDatabase.getAlbumData().get(position).getCover() == null || LoadDataFromDatabase.getAlbumData().size()==0){
            mainProfileIcon.setImageResource(R.drawable.default_avatar);
        }
        else {
            if(!LoadDataFromDatabase.getAlbumData().get(position).getCover().equals("none")) {
                File imgFile = new File(LoadDataFromDatabase.getAlbumData().get(position).getCover());
                if (imgFile.exists()) {
                    mainProfileIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    mainProfileIcon.setImageBitmap(decodeBitmap.decodeSampledBitmapFromFile(LoadDataFromDatabase.getAlbumData().get(position).getCover(), 300, 300));
                }
            }
            else{
                mainProfileIcon.setImageResource(R.drawable.default_avatar);

            }
        }
        Log.d(TAG, "coverTitle " + LoadDataFromDatabase.getAlbumData().get(position).getCoverTitle() + " " + position);
        Log.d(TAG, "cover " + LoadDataFromDatabase.getAlbumData().get(position).getCover());
        if(LoadDataFromDatabase.getAlbumData().size()>0) {
            descriptionMainIcon.setText(LoadDataFromDatabase.getAlbumData().get(position).getCoverTitle());
        }
        else{
            descriptionMainIcon.setText("");
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        AlbumPageFragment.currentPosition = position;
        Fragment fragment=new PhotoPageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("albumId", LoadDataFromDatabase.getAlbumData().get(position).getAlbumId());
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
