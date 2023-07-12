package com.example.landonhotels;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by paulruiz on 9/26/14.
 */
public class WearMessageListenerService extends WearableListenerService {
    private static final String WEARPATH = "/message";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if( messageEvent.getPath().equals(WEARPATH) ) {
            String message = new String(messageEvent.getData());

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("message", message);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Log.d("Listener", "Received: " + message);
            startActivity(intent);
        }
    }
}
