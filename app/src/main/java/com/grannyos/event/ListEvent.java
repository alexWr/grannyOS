package com.grannyos.event;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ListEvent extends Fragment{

    private final static String TAG = "ListEventGrannyOs";
    private int                 position = 0;
    private DecodeBitmap        decodeBitmap = new DecodeBitmap();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("page");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_event_layout, container, false);
        ImageView iconEvent = (ImageView) rootView.findViewById(R.id.iconEvent);
        TextView descriptionMainIcon = (TextView) rootView.findViewById(R.id.descriptionMainIcon);
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        DateFormat outputFormatMonth = new SimpleDateFormat("MMMM", Locale.ENGLISH);
        DateFormat outputFormatDay = new SimpleDateFormat("dd", Locale.ENGLISH);
        String month;
        String day;
        try {
            Date date = inputFormat.parse(LoadDataFromDatabase.getEventData().get(position).getCalendarDate());
            month = outputFormatMonth.format(date);
            day = outputFormatDay.format(date);
            descriptionMainIcon.setText("" + day + " " + month + ". " + LoadDataFromDatabase.getEventData().get(position).getCalendarTitle());
            if (LoadDataFromDatabase.getAlbumResource().size() == 0) {
                iconEvent.setVisibility(View.INVISIBLE);
            } else {
                if (!LoadDataFromDatabase.getEventData().get(position).getAlbumAssetId().equals("none")) {
                    File imgFile = new File(LoadDataFromDatabase.getAlbumResource().get(position));
                    if (imgFile.exists()) {
                        iconEvent.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        iconEvent.setImageBitmap(decodeBitmap.decodeSampledBitmapFromFile(LoadDataFromDatabase.getAlbumResource().get(position), 300, 300));
                    }
                } else {
                    iconEvent.setImageResource(R.drawable.default_avatar);
                }
            }
        }
        catch (ParseException | NullPointerException | IndexOutOfBoundsException e) {
            Log.d(TAG, "Error in listFragment while get some data");
            e.printStackTrace();
        }
        return rootView;
    }
}
