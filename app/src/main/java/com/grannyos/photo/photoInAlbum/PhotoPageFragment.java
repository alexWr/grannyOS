package com.grannyos.photo.photoInAlbum;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.grannyos.MainActivity;
import com.grannyos.R;
import com.grannyos.ViewPagerAdapter;
import com.grannyos.database.LoadDataFromDatabase;
import com.grannyos.utils.ZoomOutPageTransformer;


public class PhotoPageFragment extends Fragment implements View.OnClickListener{


    private static final String TAG = "PhotoPageGrannyOs";
    public static ViewPager     photoAlbumPager;
    private ProgressBar         pbBatteryIndicator;
    private TextView            tvPercentIndicator;
    private ImageView           ivStrengthSignal;
    private WifiManager         mWifiManager;
    public static ImageView     ivNext;
    public static ImageView     ivPrev;
    public static TextView      countPhoto;
    public static TextView      descriptionPhoto;
    private Drawable            strengthWifiIcon[];
    private int                 showPos=1;
    private int                 position;
    private ViewPagerAdapter    photoPageAdapter;
    private ChangeListener      mListener = new ChangeListener();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWifiManager = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);
        //noinspection deprecation
        strengthWifiIcon = new Drawable[]{getResources().getDrawable(R.drawable.photo_signal0),
        getResources().getDrawable(R.drawable.photo_signal1),
        getResources().getDrawable(R.drawable.photo_signal2),
        getResources().getDrawable(R.drawable.photo_signal3),
        getResources().getDrawable(R.drawable.photo_signal4),
        getResources().getDrawable(R.drawable.photo_signal5) };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.photo_page_layout, container, false);
        MainActivity.relativeLayout.setVisibility(View.GONE);
        String checkAlbumId = getArguments().getString("albumId");
        Log.d(TAG, "getting albumId " + checkAlbumId);
        photoAlbumPager=(ViewPager)rootView.findViewById(R.id.photoAlbumPager);
        Button backButton = (Button) rootView.findViewById(R.id.backButton);
        pbBatteryIndicator = (ProgressBar) rootView.findViewById(R.id.batteryIndicator);
        tvPercentIndicator = (TextView) rootView.findViewById(R.id.percentIndicator);
        ivStrengthSignal = (ImageView) rootView.findViewById(R.id.wifiStrength);
        descriptionPhoto = (TextView) rootView.findViewById(R.id.descriptionPhoto);
        countPhoto = (TextView) rootView.findViewById(R.id.countPhoto);
        ivNext = (ImageView) rootView.findViewById(R.id.nextPage);
        ivPrev = (ImageView) rootView.findViewById(R.id.previousPage);
        ivNext.setOnClickListener(this);
        ivPrev.setOnClickListener(this);
        backButton.setOnClickListener(this);
        photoAlbumPager.setPageTransformer(true, new ZoomOutPageTransformer());
        try {
            new LoadDataFromDatabase("photo", getActivity(), checkAlbumId);
            photoPageAdapter = new ViewPagerAdapter(getActivity(), getChildFragmentManager(), PhotoList.class, LoadDataFromDatabase.getPhotoData().size());
            photoAlbumPager.setAdapter(photoPageAdapter);
            photoAlbumPager.setCurrentItem(0);
            countPhoto.setText("" + showPos + " of " + photoPageAdapter.getCount());
            Log.d(TAG, LoadDataFromDatabase.getPhotoData().toString());
            if (LoadDataFromDatabase.getPhotoData().size() == 0) {
                descriptionPhoto.setVisibility(View.INVISIBLE);
            } else {
                if (LoadDataFromDatabase.getPhotoData().get(position).getAssetTitle().equals("empty")) {
                    descriptionPhoto.setVisibility(View.GONE);
                } else {
                    descriptionPhoto.setVisibility(View.VISIBLE);
                    descriptionPhoto.setText(LoadDataFromDatabase.getPhotoData().get(position).getAssetTitle());
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "Error in PhotoPageFragment");
        }
        photoAlbumPager.addOnPageChangeListener(mListener);
        try {
            getActivity().registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            getActivity().registerReceiver(this.mWifiInfoReceiver, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
        } catch(Exception e){
            Log.d(TAG, "Cannot set the receiver");
            e.printStackTrace();
        }
        return rootView;
    }

    public class ChangeListener implements ViewPager.OnPageChangeListener{
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            showPos = position;
            showPos++;
            PhotoPageFragment.this.position = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if(state== ViewPager.SCROLL_STATE_IDLE){
                countPhoto.setText("" + showPos + " of " + photoPageAdapter.getCount());
                if(LoadDataFromDatabase.getPhotoData().get(position).getAssetTitle().equals("empty")){
                    descriptionPhoto.setVisibility(View.GONE);
                }
                else{
                    descriptionPhoto.setVisibility(View.VISIBLE);
                    descriptionPhoto.setText(LoadDataFromDatabase.getPhotoData().get(position).getAssetTitle());
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.backButton:
                getActivity().onBackPressed();
                break;
            case R.id.nextPage:
                if(photoAlbumPager.getCurrentItem()==photoPageAdapter.getCount()-1)
                    photoAlbumPager.setCurrentItem(0);
                else
                    photoAlbumPager.setCurrentItem(showPos);
                break;
            case R.id.previousPage:
                if(photoAlbumPager.getCurrentItem()==0)
                    photoAlbumPager.setCurrentItem(photoPageAdapter.getCount()-1);
                else
                    photoAlbumPager.setCurrentItem(photoAlbumPager.getCurrentItem()-1);
                break;
        }
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            if (currentLevel >= 0 && scale > 0) {
                level = (currentLevel * 100) / scale;
            }
            Drawable batteryProgressD = pbBatteryIndicator.getProgressDrawable();
            batteryProgressD.setLevel(level*100);
            pbBatteryIndicator.setProgress(level);
            tvPercentIndicator.setText(Integer.toString(level)+"%");
        }
    };

    private BroadcastReceiver mWifiInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int numberOfLevels=6;
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            int level= WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
            ivStrengthSignal.setImageDrawable(strengthWifiIcon[level]);
        }
    };


    @Override
    public void onStop() {
        super.onStop();
        try {
            getActivity().unregisterReceiver(this.mBatInfoReceiver);
            getActivity().unregisterReceiver(this.mWifiInfoReceiver);
        }
        catch(IllegalArgumentException e){
            Log.d(TAG, "Receiver is not register");
        }
    }
}
