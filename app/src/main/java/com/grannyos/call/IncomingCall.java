package com.grannyos.call;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.grannyos.MainActivity;
import com.grannyos.R;
import com.grannyos.database.LoadDataFromDatabase;
import com.grannyos.database.pojo.RelativesData;
import com.grannyos.network.SocketService;
import com.grannyos.utils.DecodeBitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Ack;


/**
 * Fragment is appear when someone of the relative is calling
 */

public class IncomingCall extends Fragment implements View.OnClickListener{

    private static final String         TAG = "IncomingCallGrannyOs";
    private ArrayList<String> peerId = new ArrayList<>();
    private Intent                      intent;
    private String                      roomName;
    private String                      callerName;
    private String                      callerLastName;
    private DecodeBitmap                decodeBitmap = new DecodeBitmap();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.incoming_call_layout, container, false);
        Button startAnswer = (Button) rootView.findViewById(R.id.startAnswer);
        TextView startCallTitle = (TextView) rootView.findViewById(R.id.startCallTitle);
        ImageView incomingProfileIcon = (ImageView) rootView.findViewById(R.id.incomingProfileIcon);
        Button declineCall = (Button) rootView.findViewById(R.id.declineCall);
        startAnswer.setOnClickListener(this);
        declineCall.setOnClickListener(this);
        try {
            roomName = getArguments().getString("roomName");
            peerId = getArguments().getStringArrayList("relativeId");
            Log.d(TAG, "join relatives roomName " + roomName);
            Log.d(TAG, "find relativeId " + peerId);
        } catch(NullPointerException e){
            Log.d(TAG, "Error in getting arguments in IncomingCall");
            e.printStackTrace();
        }
        new LoadDataFromDatabase("relatives", getActivity(), "");
        ArrayList<RelativesData> relativesData = LoadDataFromDatabase.getRelativeData();
        if(relativesData != null && !relativesData.isEmpty()) {
            try {
                for (int i = 0; i < relativesData.size(); i++) {
                    Log.d(TAG, peerId.get(0) + " " + relativesData.get(i).getRelativesId());
                    if (peerId.get(0).equals(relativesData.get(i).getRelativesId())) {
                        callerName = relativesData.get(i).getFirstName();
                        callerLastName = relativesData.get(i).getLastName();
                        if (!relativesData.get(i).getAvatar().equals("none")) {
                            incomingProfileIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            incomingProfileIcon.setImageBitmap(decodeBitmap.decodeSampledBitmapFromFile(relativesData.get(i).getAvatar(), 300, 300));
                        } else {
                            incomingProfileIcon.setImageResource(R.drawable.default_avatar);
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "Something went wrong while get data from database in IncomingCall");
                e.printStackTrace();
            }
        }
        if(callerLastName.isEmpty() && callerName.isEmpty() ){
            callerName = "Someone";
            callerLastName = "Someone";
        }
        startCallTitle.setText("Calls: " + callerName + " " + callerLastName);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.startAnswer:
                answerTheCall();
                break;
            case R.id.declineCall:
                declineCall(roomName);
                getActivity().onBackPressed();
                break;
        }
    }
    private void answerTheCall(){
        SocketService.getSocket().emit("join", roomName, new Ack() {
            @Override
            public void call(Object... args) {
                if (args[0] == null) {
                    peerId.clear();
                    Log.d(TAG, "join answer" + args[1].toString());
                    intent = new Intent(getActivity(), VideoChat.class);
                    try {
                        JSONObject answer = new JSONObject(args[1].toString());
                        JSONObject clients = answer.getJSONObject("clients");
                        for (int i = 0; i < clients.names().length(); i++) {
                            Log.d(TAG, "peerId " + clients.names().get(i).toString());
                            peerId.add(clients.names().get(i).toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        Log.d(TAG, "error while join to room NullPointerException");
                        getActivity().onBackPressed();
                    }
                    Log.d(TAG, "incoming call peerId" + peerId);
                    intent.putExtra("peerId", peerId);
                    startActivityForResult(intent, MainActivity.REQUEST_CODE_VIDEO_CHAT);
                    getActivity().onBackPressed();
                } else
                    Log.d(TAG, "error answer the call: " + args[0].toString());
            }
        });
    }

    public void declineCall(String roomName){
        Log.d(TAG, "decline the call");
        SocketService.getSocket().emit("decline",roomName);
    }
}
