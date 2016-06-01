package com.grannyos.call;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.grannyos.R;
import com.grannyos.network.SocketService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.CameraEnumerationAndroid;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import io.socket.client.Ack;
import io.socket.emitter.Emitter;


public class VideoChat extends Activity {


    private static final String                     TAG = "VideoChatGrannyOs";
    // Local preview screen position after call is connected.
    // Remote video screen position

    private static final int                        LOCAL_X_CONNECTED = 72;
    private static final int                        LOCAL_Y_CONNECTED = 72;
    private static final int                        LOCAL_WIDTH_CONNECTED = 25;
    private static final int                        LOCAL_HEIGHT_CONNECTED = 25;
    private int                                     REMOTE_X = 0;
    private static final int                        REMOTE_Y = 0;
    private static final int                        REMOTE_HEIGHT = 100;

    //status bar

    private ProgressBar                             pbBatteryIndicator;
    private TextView                                tvPercentIndicator;
    private ImageView                               ivStrengthSignal;
    private WifiManager                             mWifiManager;
    private Drawable                                strengthWifiIcon[];

    //Other data for webRTC

    private RendererCommon.ScalingType              scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;
    private VideoRenderer.Callbacks                 localRender;
    private ArrayList<VideoRenderer.Callbacks>      remoteRender = new ArrayList<>();
    private ArrayList<String>                       peerId = new ArrayList<>();
    public String                                   roomType="video", to, from, sid, type, prefix = "webkit";
    private HashMap<String, Peer>                   peers = new HashMap<>();
    private MediaConstraints                        pcConstraints = new MediaConstraints();
    private MediaStream                             lMS;
    private PeerConnectionFactory                   factory;
    private final MessageHandler                    messageHandler = new MessageHandler();
    public  LinkedList<PeerConnection.IceServer>    iceServers = new LinkedList<>();
    private int                                     countMediaStream=0;
    private VideoView                               video;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        mWifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        strengthWifiIcon = new Drawable[]{
                ResourcesCompat.getDrawable(getResources(), R.drawable.signal_0, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.signal_1, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.signal_2, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.signal_3, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.signal_4, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.signal_5, null)
        };
        setContentView(R.layout.video_chat_layout);
        video = (VideoView) findViewById(R.id.videoChat);
        pbBatteryIndicator = (ProgressBar) findViewById(R.id.batteryIndicator);
        tvPercentIndicator = (TextView) findViewById(R.id.percentIndicator);
        ivStrengthSignal = (ImageView) findViewById(R.id.wifiStrength);
        Button finishCall = (Button) findViewById(R.id.finishCall);
        finishCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(video.isPlaying()) {
                    video.stopPlayback();
                }
                finish();
            }
        });
        boolean whatVideo;
        whatVideo = getIntent().getBooleanExtra("whatVideo", false);
        String fileName;
        if(whatVideo){
            fileName = "1.mp4";
        }
    else{
        fileName = "2.mp4";
    }
        getInit(Environment.getExternalStorageDirectory().toString() + "/grannyos/" + fileName);
        /*peers.clear();
        peerId.clear();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sid = sharedPreferences.getString("sessionId", null);
        peerId = getIntent().getStringArrayListExtra("peerId");
        Log.d(TAG, "peerId" + peerId);
        GLSurfaceView videoView = (GLSurfaceView) findViewById(R.id.gl_surface);
        pbBatteryIndicator = (ProgressBar) findViewById(R.id.batteryIndicator);
        tvPercentIndicator = (TextView) findViewById(R.id.percentIndicator);
        ivStrengthSignal = (ImageView) findViewById(R.id.wifiStrength);
        Button finishCall = (Button) findViewById(R.id.finishCall);
        finishCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketService.getSocket().emit("leave");
                setResult(RESULT_OK);
                finish();
            }
        });
        videoView.setPreserveEGLContextOnPause(true);
        videoView.setKeepScreenOn(true);
        VideoRendererGui.setView(videoView, new Runnable() {
            @Override
            public void run() {
            }
        });
        if(peerId.size()==0){
            setResult(RESULT_OK);
            finish();
            Toast.makeText(getApplicationContext(),"Error to join room", Toast.LENGTH_LONG).show();
        }
        else {
            REMOTE_X = 100 / peerId.size();
        }
        try {
            for (int i = 0; i < peerId.size(); i++) {
                Log.d(TAG,"REMOTE_X " + REMOTE_X*i);
                remoteRender.add(VideoRendererGui.create(REMOTE_X*i, REMOTE_Y,
                        REMOTE_X, REMOTE_HEIGHT, scalingType, false));//for remote video

            }
            localRender = VideoRendererGui.create(LOCAL_X_CONNECTED, LOCAL_Y_CONNECTED,
                    LOCAL_WIDTH_CONNECTED, LOCAL_HEIGHT_CONNECTED, scalingType, true);//for local video
        } catch (Exception e) {
            Log.e(TAG, "VideoRendererGui.create", e);
        }

        Log.d(TAG, "get stun servers" + " my id " + SocketService.getSocket().id() + "size remote render ");

        getStunTurnServers();*/


    }

    public void getInit(String path) {
        video.setVideoPath(path);
        video.requestFocus();
        video.start();
    }

    private void getStunTurnServers(){
        SocketService.getSocket().emit("stunservers", new Ack() {
            @Override
            public void call(Object... args) {
                if (args[0] == null) {
                    try {
                        JSONArray array = new JSONArray(args[1].toString());
                        Log.d(TAG, "array stun servers length" + array.length());
                        for (int i = 0; i < 50; i++) {
                            JSONObject url = array.getJSONObject(i);
                            Log.d(TAG, "stun " + url.getString("url"));
                            iceServers.add(new PeerConnection.IceServer(url.getString("url")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SocketService.getSocket().emit("turnservers", new Ack() {
                        @Override
                        public void call(Object... args) {
                            if (args[0] == null) {
                                try {
                                    JSONArray array = new JSONArray(args[1].toString());
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject url = array.getJSONObject(i);
                                        Log.d(TAG, "turn " + url.getString("url"));
                                        iceServers.add(new PeerConnection.IceServer(url.getString("url"), url.getString("username"), url.getString("credential")));
                                    }
                                } catch (Throwable e) {
                                    Log.e(TAG, "failed to add stun/turn servers", e);
                                }

                                try {
                                    init();
                                } catch (Throwable e) {
                                    Log.e(TAG, "failed to init!!!!", e);
                                }

                            } else {
                                Log.d(TAG, "turn servers emit" + args[0]);
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "error stunServers emit" + args[0]);
                }
            }
        });
    }

    private void init(){
        Log.d(TAG, "init");
        boolean peerConnection = PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true);
        Log.d(TAG, "peerConnection android globals " + peerConnection);
        factory = new PeerConnectionFactory();
        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        lMS = factory.createLocalMediaStream("ARDAMS");
        MediaConstraints videoConstraints = new MediaConstraints();
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxHeight",Integer.toString(displaySize.x)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxWidth", Integer.toString(displaySize.y)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxFrameRate", Integer.toString(30)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minFrameRate", Integer.toString(20)));
        VideoSource videoSource = factory.createVideoSource(getVideoCapture(), videoConstraints);
        AudioSource audioSource = factory.createAudioSource(new MediaConstraints());
        VideoTrack localVideoTrack = factory.createVideoTrack("ARDAMSv0", videoSource);
        AudioTrack localAudioTrack =factory.createAudioTrack("ARDAMSa0", audioSource);
        lMS.addTrack(localAudioTrack);
        lMS.addTrack(localVideoTrack);
        onLocalStream(lMS);
        Log.d(TAG, "peerId " + peerId.toString() + "size " + peerId.size());
        Log.d(TAG, "peers " + peers.toString() + "size " + peers.size());
        SocketService.getSocket().on("message", messageHandler.onMessage);
        for(int i=0;i<peerId.size();i++){
            Peer peer = new Peer(peerId.get(i));

            // We have to provide local streams
            //peer.pc.addStream(lMS);

            peers.put(peerId.get(i), peer);
            Log.d(TAG, "create offer from init");
            peer.pc.createOffer(peer, pcConstraints);
        }
        Log.d(TAG, "size peers" + peers);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(this.mBatInfoReceiver);
        unregisterReceiver(this.mWifiInfoReceiver);
        //this.videoSource.stop();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        registerReceiver(this.mWifiInfoReceiver, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
    }

    /**
     * All class and function data for WebRTC
     */


    public interface Command{
        void execute(String peerId, JSONObject payload) throws JSONException;
    }

    public class CreateAnswerCommand implements Command{
        @Override
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Log.i(TAG, "CreateAnswerCommand peer id " + peerId);
            Peer peer = peers.get(peerId);
            SessionDescription sdp = new SessionDescription(SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
                    payload.getString("sdp")
            );
            peer.pc.setRemoteDescription(peer, sdp);
            peer.pc.createAnswer(peer, pcConstraints);
        }
    }
    public class SetRemoteSDPCommand implements Command{
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Log.i(TAG, "SetRemoteSdpCommand peer id " + peerId);
            Peer peer = peers.get(peerId);
            SessionDescription sdp = new SessionDescription(SessionDescription.Type.fromCanonicalForm(payload.getString("type")), payload.getString("sdp"));
            peer.pc.setRemoteDescription(peer, sdp);

        }
    }

    public class AddIceCandidateCommand implements Command{
        public void execute(String peerId, JSONObject payload) throws JSONException {
            JSONObject jsonCandidate = payload.getJSONObject("candidate");
            Log.i(TAG, "AddIceCandidateCommand peer id " + peerId);
            PeerConnection pc = peers.get(peerId).pc;
            if (pc.getRemoteDescription() != null) {
                Log.d(TAG, "AddIceCandidateCommand "+jsonCandidate.toString());
                IceCandidate candidate = new IceCandidate(
                        jsonCandidate.getString("sdpMid"),
                        jsonCandidate.getInt("sdpMLineIndex"),
                        jsonCandidate.getString("candidate")
                );
                pc.addIceCandidate(candidate);
            }
        }
    }

    private void sendMessage(String to, String type, JSONObject payload) throws JSONException {
        Log.d(TAG,"sendMessage" + " " + type + " " + payload.toString());
        JSONObject message = new JSONObject();
        message.put("to", to);
        message.put("sid", sid);
        message.put("roomType", roomType);
        message.put("type", type);
        message.put("payload", payload);
        message.put("prefix",prefix);
        Log.d(TAG, "send message with type ˜" + type);
        SocketService.getSocket().emit("message", message);
    }

    private class MessageHandler {
        private HashMap<String, Command> commandMap;

        private MessageHandler() {
            this.commandMap = new HashMap<>();
            commandMap.put("offer", new CreateAnswerCommand());
            commandMap.put("answer", new SetRemoteSDPCommand());
            commandMap.put("candidate", new AddIceCandidateCommand());
        }

        private Emitter.Listener onMessage = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "on message listener");
                if (args[0] != null) {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        sid = data.getString("sid");
                        from = data.getString("from");
                        type = data.getString("type");

                        if(type == null) {
                            Log.e(TAG, "onMessage got type == null");
                        }

                        // if unknown command - just skip it
                        if(!commandMap.containsKey(type)) {
                            return;
                        }

                        Log.d(TAG, "onMessage " + type + " " + data.toString());

                        JSONObject payload = null;

                        if(data.has("payload")) {
                            payload = data.getJSONObject("payload");
                        }

                        Log.d(TAG, "equals ignore case answer" + from);
                        if(!peers.containsKey(from)) {
                            Log.w(TAG, "unknown peer " + from + " send command " + type);

                            addPeer(from);

                            REMOTE_X = 100/peers.size();
                            Log.d(TAG, "create new Render " + REMOTE_X);
                            remoteRender.add(VideoRendererGui.create(REMOTE_X * remoteRender.size(), REMOTE_Y,
                                    REMOTE_X, REMOTE_HEIGHT, scalingType, false));//for remote video

                            Log.d(TAG, "peers size after addPeer" + peers);
                            commandMap.get(type).execute(from, payload);
                        }
                        else{
                            commandMap.get(type).execute(from, payload);
                        }
                    } catch (Throwable e) {
                        Log.e(TAG, "CommandListener.call", e);
                    }
                }
            }
        };
    }


    private class Peer implements SdpObserver, PeerConnection.Observer{

        private PeerConnection pc;
        private String id;

        @Override
        public void onIceConnectionReceivingChange(boolean b) {

        }

        @Override
        public void onCreateSuccess(final SessionDescription sdp) {
            Log.d(TAG,"onCreateSuccess " + sdp.type.canonicalForm());
            try {
                JSONObject payload = new JSONObject();
                payload.put("type", sdp.type.canonicalForm());
                payload.put("sdp", sdp.description);
                sendMessage(id, sdp.type.canonicalForm(), payload);
                pc.setLocalDescription(Peer.this, sdp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSetSuccess() {
            Log.d(TAG,"onSetSuccess");
        }

        @Override
        public void onCreateFailure(String s) {
            Log.d(TAG, "onCreateFailure " + s);
        }

        @Override
        public void onSetFailure(String s) {
            Log.d(TAG, "onSetFailure " + s);
        }

        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            Log.d(TAG, "onSignalChange " + signalingState);
            if(signalingState == PeerConnection.SignalingState.CLOSED){
                //finish();
            }
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            Log.d(TAG, "onIceConnectionChange " + iceConnectionState);
            if(iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED){
                removePeer(id);
            }
        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
            Log.d(TAG, "onIceGatheringChange " + iceGatheringState);
            if(iceGatheringState == PeerConnection.IceGatheringState.COMPLETE){
                JSONObject message = new JSONObject();

                try {
                    message.put("to", this.id);
                    message.put("sid", sid);
                    message.put("roomType", roomType);
                    message.put("type", "endOfCandidates");
                    message.put("prefix",prefix);
                    Log.d(TAG, "send message with type endOfCandidates");

                    SocketService.getSocket().emit("message", message);
                } catch (Throwable e) {
                    Log.e(TAG, "failed to end candidate", e);
                }
            }
        }

        @Override
        public void onIceCandidate(final IceCandidate candidate) {
            Log.d(TAG,"onIceCandidate " + candidate.sdp);
            if(candidate.sdp.isEmpty()){
                Log.d(TAG,"candidate is empty");
            }
            try {
                JSONObject payload = new JSONObject();
                JSONObject candidateJson = new JSONObject();
                candidateJson.put("candidate", candidate.sdp);
                candidateJson.put("sdpMid", candidate.sdpMid);
                candidateJson.put("sdpMLineIndex", candidate.sdpMLineIndex);
                payload.put("candidate", candidateJson);
                sendMessage(id, "candidate", payload);
            } catch (Throwable e) {
                Log.e(TAG, "failed to send candidate", e);
            }
        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {
            Log.d(TAG, "onDataChannel");
        }

        @Override
        public void onRenegotiationNeeded() {
            Log.d(TAG, "onRenegotiationNeeded");
        }

        @Override
        public void onAddStream(MediaStream mediaStream) {
            Log.d(TAG, "onAddRemoteStream "     + mediaStream.label());
            Log.d(TAG, "onAddRemoteStream countMediaStream " + countMediaStream);
            onAddRemoteStream(mediaStream, countMediaStream);
            countMediaStream++;
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {
            Log.d(TAG, "onRemoveStream "+mediaStream.label());
            mediaStream.videoTracks.get(0).dispose();
            removePeer(id);
        }

        public Peer(String id) {
            Log.d(TAG, "initialize peer id");
            if(iceServers.size()>0)
                this.pc = factory.createPeerConnection(iceServers, pcConstraints, this);
            else{
                Log.d(TAG,"error to get turn and stun servers ");
            }
            this.id = id;
            this.pc.addStream(lMS);
        }
    }

    public VideoCapturer getVideoCapture() {
        Log.d(TAG, "getVideoCapture");
        String cameraDeviceName;

        cameraDeviceName = CameraEnumerationAndroid.getNameOfFrontFacingDevice();

        if(cameraDeviceName == null) {
            cameraDeviceName = CameraEnumerationAndroid.getNameOfBackFacingDevice();
        }

        VideoCapturer videoCapturer;

        videoCapturer = VideoCapturerAndroid.create(cameraDeviceName);

        return videoCapturer;
    }

    public void addPeer(String id) {
        Log.d(TAG, "addPeer " + id);
        Peer peer = new Peer(id);
        //peer.pc.addStream(lMS);
        peers.put(id, peer);
    }

    public void removePeer(String id) {
        Log.d(TAG, "removePeer " + id);
        Peer peer = peers.get(id);
        peer.pc.close();
        peers.remove(peer.id);
        if(peers.size()==0){
            setResult(RESULT_OK);
            finish();
        }
        else{
            REMOTE_X = 100 / peers.size();
            updateRender();
        }
    }

    private void onLocalStream(MediaStream localStream){
        localStream.videoTracks.get(0).addRenderer(new VideoRenderer(localRender));
        VideoRendererGui.update(localRender, LOCAL_X_CONNECTED, LOCAL_Y_CONNECTED,
                LOCAL_WIDTH_CONNECTED, LOCAL_HEIGHT_CONNECTED, scalingType, true);
    }

    private void onAddRemoteStream(MediaStream remoteStream, int count){
        Log.d(TAG, "remote stream size" + remoteStream.videoTracks.size());
        Log.d(TAG, "peers size in onAddRemoteStream" + peers.size());
        remoteStream.videoTracks.get(0).addRenderer(new VideoRenderer(remoteRender.get(count)));
        updateRender();
    }

    private void updateRender(){
        for (int i = 0; i < remoteRender.size(); i++) {
            VideoRendererGui.update(remoteRender.get(i), REMOTE_X * i, REMOTE_Y,
                    REMOTE_X, REMOTE_HEIGHT, scalingType, false);//for remote video
        }
        VideoRendererGui.update(localRender, LOCAL_X_CONNECTED, LOCAL_Y_CONNECTED,
                LOCAL_WIDTH_CONNECTED, LOCAL_HEIGHT_CONNECTED, scalingType,true);
    }

    /**
     * end declaration WebRTC
     */
    //---------------------------status bar-------------------------------------
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
}
