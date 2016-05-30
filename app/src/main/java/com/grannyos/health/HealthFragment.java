package com.grannyos.health;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.grannyos.R;
import com.grannyos.login.GooglePlusLogin;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class HealthFragment extends Fragment{


    private final static String TAG = "HealthGrannyOs";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.health_layout, container, false);
        Button backButton = (Button) rootView.findViewById(R.id.backButton);
        TextView bloodPressureValue = (TextView) rootView.findViewById(R.id.bloodPressureValue);
        TextView bodyTempValue = (TextView) rootView.findViewById(R.id.bodyTempValue);
        TextView heartRateValue = (TextView) rootView.findViewById(R.id.heartRateValue);
        TextView oxLevelValue = (TextView) rootView.findViewById(R.id.oxLevelValue);
        TextView sleepPhasesLevel = (TextView) rootView.findViewById(R.id.sleepPhasesLevel);
        bloodPressureValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setScheduleMessage(getActivity().getApplicationContext());
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        if(GooglePlusLogin.getGoogleApiClient()!=null) {
            initSensors();
        }
        return rootView;
    }

    private void setScheduleMessage(Context context) {
        final long REPEAT_TIME = 20 * 1000;
        Intent intent = new Intent(context, StartNotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        alarmManager.set(AlarmManager.RTC_WAKEUP, (calendar.getTimeInMillis() + REPEAT_TIME), pendingIntent);
    }

    private void initSensors(){
        Fitness.SensorsApi.findDataSources(GooglePlusLogin.getGoogleApiClient(), new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_HEART_RATE_BPM)
                .setDataSourceTypes(DataSource.TYPE_RAW, DataSource.TYPE_DERIVED)
        .build()).setResultCallback(new ResultCallbacks<DataSourcesResult>() {
            @Override
            public void onSuccess(@NonNull DataSourcesResult dataSourcesResult) {

                for(DataSource dataSource : dataSourcesResult.getDataSources()){

                    final DataType dataType = dataSource.getDataType();

                    if(dataType.equals(DataType.TYPE_HEART_RATE_BPM)){

                        Fitness.SensorsApi.add(GooglePlusLogin.getGoogleApiClient(), new SensorRequest.Builder()
                                .setDataSource(dataSource)
                                .setDataType(dataType)
                                .setSamplingRate(5, TimeUnit.SECONDS)
                                .build(), new OnDataPointListener() {
                            @Override
                            public void onDataPoint(DataPoint dataPoint) {

                                for(Field field : dataPoint.getDataType().getFields()){
                                    Value value = dataPoint.getValue(field);
                                    Log.d(TAG, "Fitness result onDataPoint: " + field + " value " + value);
                                }
                            }
                        }).setResultCallback(new ResultCallbacks<Status>() {
                            @Override
                            public void onSuccess(@NonNull Status status) {
                                if(status.isSuccess()){
                                    Log.d(TAG, "listener for " + dataType.getName() + "is register");
                                }
                                else{
                                    Log.d(TAG, "Failed to register listener for " + dataType.getName());
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Status status) {
                                Log.d(TAG, "error while setResultCallback " + status);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Status status) {

            }
        });
    }
}
