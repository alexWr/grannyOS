package com.grannyos.call;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grannyos.MainActivity;
import com.grannyos.R;
import com.grannyos.network.SocketService;
import com.grannyos.utils.DecodeBitmap;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;



public class CallToFragment extends Fragment implements View.OnClickListener{

    //private String roomName;
    private static final String TAG = "CallToGrannyOs";
    private String              inviteId;
    private DecodeBitmap        decodeBitmap = new DecodeBitmap();
    private ArrayList<String>   arrayPeers = new ArrayList<>();
    private SharedPreferences   sharedPreferences;
    private Socket              socket;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.call_to_layout, container, false);
        socket = SocketService.getSocket();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        RelativeLayout endCallRegion = (RelativeLayout) rootView.findViewById(R.id.endCallRegion);
        Button endCall = (Button) rootView.findViewById(R.id.endCall);
        ImageView mainProfileIcon = (ImageView) rootView.findViewById(R.id.mainProfileIcon);
        TextView endCallTitle = (TextView) rootView.findViewById(R.id.endCallTitle);
        endCallTitle.setText(getActivity().getResources().getString(R.string.callTo) + getArguments().getString("firstName") + " " + getArguments().getString("lastName"));
        try {
            inviteId = getArguments().getString("id");
            final String avatar = getArguments().getString("avatar");
            if (avatar != null && !avatar.equals("none")) {
                File imgFile = new File(avatar);
                if (imgFile.exists()) {
                    mainProfileIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    mainProfileIcon.setImageBitmap(decodeBitmap.decodeSampledBitmapFromFile(getArguments().getString("avatar"), 300, 300));
                }
            } else {
                mainProfileIcon.setImageResource(R.drawable.default_avatar);
            }
        }catch (Exception e){
            Log.d(TAG, "Error while set avatar in callTo");
            e.printStackTrace();
        }
        endCall.setOnClickListener(this);
        endCallRegion.setOnClickListener(this);
        if(socket != null) {
            Log.d(TAG, "my own id " + socket.id());
            dialRelative();
            setJoinedEvent();
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        declineCall();
        getActivity().onBackPressed();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(socket != null) {
            socket.off("declined");
            socket.off("joined");
        }
    }

    public void declineCall(){
        socket.on("declined", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "declined call");
                getActivity().onBackPressed();
            }
        });
    }

    private void dialRelative(){
        socket.emit("create", "", new Ack() {
            @Override
            public void call(Object... args) {
                if (args[0] == null) {
                    try {

                        Log.d(TAG, "return name: " + args[1].toString());
                        Log.d(TAG, "invite id " + inviteId);

                        socket.emit("invite", inviteId, new Ack() {
                            @Override
                            public void call(Object... args) {
                            }
                        });

                    } catch (NullPointerException e) {
                        Log.d(TAG, "error to invite NullPointerException");
                    }
                } else {
                    Log.d(TAG, "error in create" + args[0]);
                }
            }
        });
    }

    private void setJoinedEvent(){
        socket.on("joined", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args[0] != null) {
                    Log.d(TAG, "joined contact");
                    try {
                        JSONObject peerId = new JSONObject(args[0].toString());
                        arrayPeers.add(peerId.getString("peerId"));
                        String relativeId = peerId.getString("relativeId");
                        if (!relativeId.equals(sharedPreferences.getString("myId", ""))) {
                            Intent intent = new Intent(getActivity(), VideoChat.class);
                            intent.putExtra("peerId", arrayPeers);
                            intent.putExtra("whatVideo", true);
                            getActivity().startActivityForResult(intent, MainActivity.REQUEST_CODE_VIDEO_CHAT);
                            getActivity().onBackPressed();
                        } else {
                            arrayPeers.clear();
                        }
                    } catch (Exception e ) {
                        Log.d(TAG, "error in while join in callTo");
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
