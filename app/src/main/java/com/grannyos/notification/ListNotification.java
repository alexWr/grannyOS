package com.grannyos.notification;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.grannyos.R;
import com.grannyos.utils.DecodeBitmap;

import java.util.Calendar;


public class ListNotification extends Fragment implements View.OnClickListener{

    private static final String TAG = "ListNotificationGranny";
    private int position = 0;
    private DecodeBitmap decodeBitmap = new DecodeBitmap();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("page");
        Log.d(TAG, "page position: " + position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notification_list_layout, container, false);
        ImageView firstIconNotif = (ImageView) rootView.findViewById(R.id.iconNotif);
        ImageView secondIconNotif = (ImageView) rootView.findViewById(R.id.iconNotifSecond);
        Button btnNotifFirst = (Button) rootView.findViewById(R.id.btnNotifFirst);
        Button btnNotifSecond = (Button) rootView.findViewById(R.id.btnNotifSecond);
        TextView titleReminderSecond = (TextView) rootView.findViewById(R.id.titleReminderSecond);
        TextView descriptionNotifFirst = (TextView) rootView.findViewById(R.id.descriptionNotifFirst);
        TextView descriptionNotifSecond = (TextView) rootView.findViewById(R.id.descriptionNotifSecond);
        TextView helpDescription = (TextView) rootView.findViewById(R.id.helpDescription);
        Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        helpDescription.setText("Take a blood pressure pills at " + hours + ":" + (minute + 5) + " PM");
        //firstIconNotif.setImageBitmap(decodeBitmap.decodeSampledBitmapFromResource(LoadDataFromDatabase.albumResource.get(position), 200, 200));
        //secondIconNotif.setImageBitmap(decodeBitmap.decodeSampledBitmapFromResource(LoadDataFromDatabase.albumResource.get(position), 200, 200));
        btnNotifFirst.setOnClickListener(this);
        btnNotifSecond.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnNotifFirst:
                Log.d(TAG, "");
                break;
            case R.id.btnNotifSecond:
                Log.d(TAG, "");
                break;
        }
    }
}
