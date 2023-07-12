package com.example.eric.application;

import android.os.Bundle;
import android.util.Log;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks {

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class variables                                                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    //provides the functionality to connect to the watch
    private static final String DEVICE_MAIN = "DeviceMain";
    private static final String MAIN_WEAR = "Wearable Main";
    private static final String WEAR_PATH = "/from_device";
    private GoogleApiClient mApiClient;
    private com.google.android.gms.wearable.Node mNode;

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // activity methods, reaction on changes to the application                      //
    // the functions are self-explaining by their name                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener( mApiClient, this );
        Wearable.NodeApi.getConnectedNodes(mApiClient)
                .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                        for (com.google.android.gms.wearable.Node node : nodes.getNodes()) {
                            if (node != null && node.isNearby()) {
                                mNode = node;
                                Log.d(DEVICE_MAIN, "Connected to" + node.getDisplayName());
                            }
                            if (mNode == null) {
                                Log.d(DEVICE_MAIN, "Not connected!");
                            }
                        }
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initGoogleApiClient();
    }

    @Override
    protected void onDestroy() {
        if( mApiClient != null )
            mApiClient.unregisterConnectionCallbacks( this );
        super.onDestroy();
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
    }

    @Override
    public void onMessageReceived( final MessageEvent messageEvent ) {
        Log.d(MAIN_WEAR, "message received");
        if(messageEvent.getPath().equals(WEAR_PATH)) {
            String message_event_getData = new String(messageEvent.getData());
            TextView appText = (TextView) findViewById(R.id.text_alarm_wear);
            ImageView image;

            switch (message_event_getData)
            {
                //TODO Set background instead
                case "ALARM_ON":
                    image = (ImageView) findViewById(R.id.imageViewAlarm);
                    image.setVisibility(View.INVISIBLE);
                    appText.setVisibility(View.INVISIBLE);
                    break;
                case "ALARM_OFF":
                    image = (ImageView) findViewById(R.id.imageViewAlarm);
                    image.setVisibility(View.VISIBLE);
                    appText.setVisibility(View.VISIBLE);
                    break;
                case "PROCESS_NEW":
                    image = (ImageView) findViewById(R.id.imageViewNewProcess);
                    image.setVisibility(View.VISIBLE);
                    appText.setVisibility(View.VISIBLE);
                    break;
                case "PROCESS_CONFIRMED":
                    image = (ImageView) findViewById(R.id.imageViewNewProcess);
                    image.setVisibility(View.INVISIBLE);
                    appText.setVisibility(View.INVISIBLE);
                    break;
                default: appText.setText(message_event_getData);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
            mApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        if ( mApiClient != null ) {
            Wearable.MessageApi.removeListener( mApiClient, this );
            if ( mApiClient.isConnected() ) {
                mApiClient.disconnect();
            }
        }
        super.onStop();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class functions to provide the essential class functionality                  //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    //realises the connection to the watch
    private void initGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks( this )
                .build();

        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting()) )
            mApiClient.connect();
    }

}
