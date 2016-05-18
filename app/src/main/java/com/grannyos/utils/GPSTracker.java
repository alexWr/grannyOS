package com.grannyos.utils;


import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class GPSTracker extends Service {

    private static final String TAG = "GPSTrackerGrannyOs";
    private Timer               timer1;
    private Context             mContext;
    private boolean             canGetLocation = false;
    private LocationManager     lm;
    private LocationResult      locationResult;
    private boolean             gps_enabled = false;
    private boolean             network_enabled = false;

    public GPSTracker(Context context, LocationResult result) {
        this.mContext = context;
        this.locationResult = result;
        getLocation(mContext, locationResult);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean getLocation(Context context, LocationResult result) {
        locationResult = result;
        if (lm == null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            Log.d(TAG, "Oops error ");
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            Log.d(TAG, "Oops error");
        }
        if (!gps_enabled && !network_enabled)
            return false;
        else {
            this.canGetLocation = true;
            if (gps_enabled) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
            }
            if (network_enabled) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
            }
        }
        timer1 = new Timer();
        timer1.schedule(new GetLastLocation(), 20000);
        return true;
    }

    public void stopUsingGPS() {
        if (lm != null) {

        lm.removeUpdates(locationListenerGps);
        lm.removeUpdates(locationListenerNetwork);
    }
}

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("GPS is settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.gotLocation(location);

            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerNetwork);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.gotLocation(location);

            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerGps);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    class GetLastLocation extends TimerTask {
        @Override
        public void run() {

            lm.removeUpdates(locationListenerGps);
            lm.removeUpdates(locationListenerNetwork);
            Location net_loc=null, gps_loc=null;
            if(gps_enabled)
                gps_loc=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(network_enabled)
                net_loc=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(gps_loc!=null && net_loc!=null){
                if(gps_loc.getTime()>net_loc.getTime())
                    locationResult.gotLocation(gps_loc);
                else
                    locationResult.gotLocation(net_loc);
                return;
            }
            if(gps_loc!=null){
                locationResult.gotLocation(gps_loc);
                return;
            }
            if(net_loc!=null){
                locationResult.gotLocation(net_loc);
                return;
            }
            locationResult.gotLocation(null);
        }
    }
    public static abstract class LocationResult{
        public abstract void gotLocation(Location location);
    }
}
