package com.grannyos;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.grannyos.call.IncomingCall;
import com.grannyos.database.DatabaseHelper;
import com.grannyos.database.LoadDataFromDatabase;
import com.grannyos.login.GooglePlusLogin;
import com.grannyos.login.LoginFragment;
import com.grannyos.network.SocketService;
import com.grannyos.notification.NotificationPageFragment;
import com.grannyos.socketEvent.GetAlbum;
import com.grannyos.socketEvent.GetAlbumAssets;
import com.grannyos.socketEvent.GetCalendarEvent;
import com.grannyos.socketEvent.GetRelatives;
import com.grannyos.utils.DifferentEvents;

public class MainActivity extends AppCompatActivity {

    private String                  TAG = "GrannyOs";
    public static final int         REQUEST_CODE_VIDEO_CHAT = 100;
    private Fragment                fragment;
    private ProgressBar             pbBatteryIndicator;
    private TextView                tvPercentIndicator;
    private ImageView               ivStrengthSignal;
    private WifiManager             wifiManager;
    private Drawable                strengthWifiIcon[];
    public static RelativeLayout    relativeLayout;
    public static TextClock         textClock;
    public static boolean           onStop = false;
    private ReceiverIncomingCall    receiverIncomingCall;
    private ReceiverEvents          receiverEvents;
    private boolean                 showLoginScreen;
    private SharedPreferences       sharedPreferences;
    private Intent                  socketService;
    private boolean                 startNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        showLoginScreen= sharedPreferences.getBoolean("showLoginScreen", false);
        startNotification = getIntent().getBooleanExtra("startNotification", false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        strengthWifiIcon = new Drawable[]{
                getResources().getDrawable(R.drawable.signal_0),
                getResources().getDrawable(R.drawable.signal_1),
                getResources().getDrawable(R.drawable.signal_2),
                getResources().getDrawable(R.drawable.signal_3),
                getResources().getDrawable(R.drawable.signal_4),
                getResources().getDrawable(R.drawable.signal_5)};
        relativeLayout = (RelativeLayout) findViewById(R.id.statusBarRelative);
        pbBatteryIndicator = (ProgressBar) findViewById(R.id.batteryIndicator);
        tvPercentIndicator = (TextView) findViewById(R.id.percentIndicator);
        ivStrengthSignal = (ImageView) findViewById(R.id.wifiStrength);
        textClock = (TextClock) findViewById(R.id.mainClock);
        registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        registerReceiver(this.mWifiInfoReceiver, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SocketService.INCOMING);
        receiverIncomingCall = new ReceiverIncomingCall();
        registerReceiver(receiverIncomingCall, intentFilter);
        receiverEvents = new ReceiverEvents();
        IntentFilter events = new IntentFilter();
        events.addAction(SocketService.EVENTS);
        registerReceiver(receiverEvents, events);
        if(!startNotification) {
            if (!showLoginScreen) {
                fragment = new LoginFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            } else {
                if (SocketService.getSocket() == null) {
                    socketService = new Intent(this, SocketService.class);
                    startService(socketService);
                }
                new GetRelatives(this);
                new GetCalendarEvent(this);
                new GetAlbum(this);
                new GetAlbumAssets(this);
                Log.d(TAG, "sessionId " + sharedPreferences.getString("sessionId", " null"));
                fragment = new ViewPagerFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.content_frame, fragment).commit();
            }
            if (onStop) {
                if (getFragmentManager().getBackStackEntryCount() > 1) {
                    onBackPressed();
                }
            }
        }
        else{
            Fragment fragment = new NotificationPageFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.content_frame, fragment).commit();
        }
        startNotification = false;
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1 ){
            getFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(this.mBatInfoReceiver);
            unregisterReceiver(this.mWifiInfoReceiver);
            unregisterReceiver(this.receiverIncomingCall);
            unregisterReceiver(this.receiverEvents);
        } catch(NullPointerException | IllegalArgumentException e){
            Log.d(TAG, "can not unregister receiver");
            e.printStackTrace();
        }
        Log.d(TAG, "activity onStop");
        onStop = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "activity onDestroy");
        if(SocketService.getSocket()!=null && SocketService.getSocket().connected()){
            SocketService.getSocket().disconnect();
        }
        if(socketService!=null) {
            stopService(socketService);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode== GooglePlusLogin.REQUEST_CODE_SIGN_IN){
            LoginFragment fragment = (LoginFragment) getFragmentManager().findFragmentById(R.id.content_frame);
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        if(requestCode == REQUEST_CODE_VIDEO_CHAT){
            Log.d(TAG, "onActivityResult setResult" + resultCode);
            if(resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult setResult");
                if (getFragmentManager().getBackStackEntryCount() > 1) {
                    Log.d(TAG, "onActivityResult setResult");
                    onBackPressed();
                    onBackPressed();
                }
            }
        }
    }

    /**
     * Receiver for different event(add relatives, add albums, add events and etc)
     */

    private class ReceiverEvents extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            String id = intent.getStringExtra("gettingId");
            switch ((DifferentEvents)intent.getSerializableExtra("differentEvents")){
                case albumAdded:
                    new GetAlbum(MainActivity.this);
                    break;

                case albumUpdated:
                    new GetAlbum(MainActivity.this);
                    break;

                case albumRemoved:
                    new LoadDataFromDatabase(MainActivity.this, DatabaseHelper.TABLE_ALBUM,
                            DatabaseHelper.ALBUM_ID, DatabaseHelper.ALBUM_COVER, id);
                    break;

                case relativeAdded:
                    new GetRelatives(MainActivity.this);
                    break;

                case relativeUpdated:
                    new GetRelatives(MainActivity.this);
                    break;

                case relativeRemoved:
                    new LoadDataFromDatabase(MainActivity.this, DatabaseHelper.TABLE_RELATIVES,
                            DatabaseHelper.RELATIVES_ID, DatabaseHelper.RELATIVES_ICON, id);
                    break;

                case albumAssetAdded:
                    new GetAlbumAssets(MainActivity.this);
                    break;

                case albumAssetUpdated:
                    new GetAlbumAssets(MainActivity.this);
                    break;

                case albumAssetRemoved:
                    new LoadDataFromDatabase(MainActivity.this, DatabaseHelper.TABLE_PHOTO,
                            DatabaseHelper.ASSET_ID, DatabaseHelper.ASSET_RESOURCE, id);
                    break;

                case eventAdded:
                    new GetCalendarEvent(MainActivity.this);
                    break;

                case eventUpdated:
                    new GetCalendarEvent(MainActivity.this);
                    break;

                case eventRemoved:
                    new LoadDataFromDatabase(MainActivity.this, DatabaseHelper.TABLE_PHOTO,
                            DatabaseHelper.ASSET_ID, DatabaseHelper.ASSET_RESOURCE, id);
                    break;

                case callMissed:

                    break;

                default:
                    Log.d(TAG, "Error in receive events");
                    break;
            }
        }
    }


    /**
     * Receiver for incoming call.
     */

    private class ReceiverIncomingCall extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if(arg1.getBooleanExtra("calling",false)) {
                Log.d(TAG, "getting relative id from caller" + arg1.getStringArrayListExtra("relativeId"));
                Bundle bundle = new Bundle();
                bundle.putString("roomName", arg1.getStringExtra("roomName"));
                bundle.putStringArrayList("relativeId", arg1.getStringArrayListExtra("relativeId"));
                fragment = new IncomingCall();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.content_frame, fragment).commit();
            }
        }
    }

    /**
     * Receiver for status bar(wi-fi and battery)
     */

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
            tvPercentIndicator.setText(Integer.toString(level) + "%");
        }
    };

    private BroadcastReceiver mWifiInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int numberOfLevels=6;
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int level= WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
            ivStrengthSignal.setImageDrawable(strengthWifiIcon[level]);
        }
    };
}
