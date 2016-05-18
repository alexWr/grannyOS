package com.grannyos.network;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.grannyos.R;
import com.grannyos.utils.DifferentEvents;

import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.PollingXHR;

/**
 * Socket connect to web service and determine listener for different
 * event, like incoming call, and socket events
 */

public class SocketService extends Service {

    private static final String TAG = "SocketServiceGrannyOs";
    public final static String  INCOMING = "ACTION_INCOMING_CALL";
    public final static String  EVENTS = "ACTION_EVENTS";
    private static Socket       socket;
    private ArrayList<String>   arrayPeers = new ArrayList<>();



    public static Socket getSocket() {
        if(socket == null) {
            Log.e(TAG, "socket == null");
        }
        return socket;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sessionId = sharedPreferences.getString("sessionId", null);
        connectSocket(getResources().getString(R.string.endpointSocketURL),getResources().getString(R.string.endpointSocketPath), sessionId);
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void sendDataToActivity(boolean calling, String room, ArrayList<String> peerId){
        Intent sendIntent = new Intent();
        sendIntent.setAction(INCOMING);
        sendIntent.putExtra("calling", calling);
        sendIntent.putExtra("roomName", room);
        sendIntent.putExtra("relativeId", peerId);
        sendBroadcast(sendIntent);
    }

    public void sendDataToActivityFromEvent(DifferentEvents differentEvents){
        Intent sendIntent = new Intent();
        sendIntent.setAction(EVENTS);
        sendIntent.putExtra("differentEvents", differentEvents);
        sendBroadcast(sendIntent);
    }

    private void connectSocket(String address, String path, String sessionId){

        IO.Options opts= new IO.Options();
        opts.transports = new String[] {PollingXHR.NAME};
        try {
            opts.path = path;
            opts.query="sessionId="+sessionId;
            socket = IO.socket(new URI(address), opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socket.on(Socket.EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "socket " + socket.id() + " error " + args[0]);
            }
        });

        socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "socket " + socket.id() + " error " + args[0]);
            }
        });
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "socket connected " + socket.id());
            }

            // this is the emit from the server
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "socket " + socket.id() + " disconnected because of: " + args[0]);
            }
        });
        socket.on("invite", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args[0] != null) {
                    arrayPeers.clear();
                    try {
                        JSONObject answer = new JSONObject(args[0].toString());
                        Log.d(TAG,"answer from caller invite " + answer);
                        String room = answer.getString("room");
                        JSONObject clients = answer.getJSONObject("clients");
                        for (int i = 0; i < clients.names().length(); i++) {
                            Log.d(TAG, "peerId " + clients.names().get(i).toString());
                            Log.d(TAG, "relativeId " + clients.getJSONObject(clients.names().get(i).toString()).getString("relativeId"));
                            arrayPeers.add(clients.getJSONObject(clients.names().get(i).toString()).getString("relativeId"));
                        }
                        sendDataToActivity(true, room, arrayPeers);
                    } catch (Throwable e) {
                        Log.e(TAG, "sendDataToActivity", e);
                    }

                } else {
                    Log.d(TAG, "error getting invite");
                }
            }
        });

        /**
         * Different listener for management event
         */


        socket.on("relativeAdded", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                sendDataToActivityFromEvent(DifferentEvents.relativeAdded);
            }
        }).on("relativeRemoved", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                sendDataToActivityFromEvent(DifferentEvents.relativeRemoved);
            }
        }).on("relativeUpdated", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                sendDataToActivityFromEvent(DifferentEvents.relativeUpdated);
            }
        }).on("albumAdded", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                sendDataToActivityFromEvent(DifferentEvents.albumAdded);
            }
        }).on("albumRemoved", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                sendDataToActivityFromEvent(DifferentEvents.albumRemoved);
            }
        }).on("albumUpdated", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                sendDataToActivityFromEvent(DifferentEvents.albumAssetUpdated);
            }
        }).on("albumAssetAdded", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                sendDataToActivityFromEvent(DifferentEvents.albumAssetAdded);
            }
        }).on("albumAssetUpdated", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                sendDataToActivityFromEvent(DifferentEvents.albumAssetUpdated);
            }
        }).on("albumAssetRemoved", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                sendDataToActivityFromEvent(DifferentEvents.albumAssetRemoved);
            }
        }).on("eventAdded", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                sendDataToActivityFromEvent(DifferentEvents.eventAdded);
            }
        }).on("eventUpdated", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                sendDataToActivityFromEvent(DifferentEvents.eventUpdated);
            }
        }).on("eventRemoved", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                sendDataToActivityFromEvent(DifferentEvents.eventRemoved);
            }
        }).on("callMissed", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                sendDataToActivityFromEvent(DifferentEvents.callMissed);
            }
        });

        socket.connect();
    }
}
