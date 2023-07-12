package com.example.landonhotels;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.IntegerRes;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements  GoogleApiClient.ConnectionCallbacks {

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class variables                                                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    //provides the functionality to connect to the watch
    private static final String WEAR_MESSAGE_PATH = "/message";
    private GoogleApiClient mApiClient;
    private com.google.android.gms.wearable.Node mNode;
    String intentExtra;
    String messageHolder;
    Vibrator vibrator;
    Handler handlerSecondOver;
    Timer caretakerSeconds;
    TextView timer;
    TextView appText;
    int currentProcessDuration;

    //Time that is subtracted on the watch timer in milliseconds to synchronize the timers
    int synchDuration = 700;

    //Length of vibration in milli seconds
    int vibrationLength = 500;
    //int vibrationLength = 0;

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // activity methods, reaction on changes to the application                      //
    // the functions are self-explaining by their name                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onConnected(Bundle bundle) {
        //Wearable.MessageApi.addListener(mApiClient, this);
        Wearable.NodeApi.getConnectedNodes(mApiClient)
                .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                        for (com.google.android.gms.wearable.Node node : nodes.getNodes()) {
                            if (node != null && node.isNearby()) {
                                mNode = node;
                                Log.d(WEAR_MESSAGE_PATH, "Connected to" + node.getDisplayName());
                            }
                            if (mNode == null) {
                                Log.d(WEAR_MESSAGE_PATH, "Not connected!");
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
        timer = (TextView) findViewById(R.id.timer);
        appText = (TextView) findViewById(R.id.text_alarm_wear);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            intentExtra= extras.getString("message");
            messageReceived( intentExtra );
        }

        //Aktualiere den Timerstring nach Ablauf einer Sekunde
        handlerSecondOver = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                Log.d("curr duration before ", ""+currentProcessDuration);

                //Verbleibende Prozess Dauer um eine Sekunde verringern
                currentProcessDuration -=1000;
                Log.d("current duration ", ""+currentProcessDuration);
                //Prozessdauer im timer aktualisieren
                Log.d("AlarmActivity2", "Second: " + currentProcessDuration);

                if(currentProcessDuration < 11000) {
                    FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
                    frameLayout.setBackgroundColor(Color.RED);
                    timer.setTextColor(Color.WHITE);
                    appText.setTextColor(Color.WHITE);
                }

                //we need this if statement because when Critical is sent, then currentProzess
                //duration is set 0 while reloading the screen and we dont want sth. like
                //currentProcessDuration-1 to show on the screen
                if(currentProcessDuration != -1000)
                    timer.setText(getTime());

                Log.d("AlarmActivity2", "Second: " + timer.getText());
                //TODO: Change if time should be displayed on watch include seconds
                //if(use_watch)
                //    sendAlarmMessage("Prozess " + (currentProcessId + 1) + " läuft.");

                //countdown next second
                if(currentProcessDuration >= 0)
                    startSecond();
            }
        };
    }

    @Override
    protected void onDestroy() {
            if( mApiClient != null )
            mApiClient.unregisterConnectionCallbacks(this);
        super.onDestroy();
        }

        @Override
        public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
    }

    private void messageReceived( String message ) {
        Log.d(WEAR_MESSAGE_PATH, "Received Message");

        Log.d("Message is: ", message);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        //TODO: change colors here
        switch (message)
        {
            //Versuch 1
            case alarmConstants.START:
                appText.setText("");
                frameLayout.setBackgroundColor(Color.WHITE);
                vibrate();
                break;
            case alarmConstants.ALARM_A:
                vibrate();
                appText.setText(message);
                appText.setTextColor(Color.WHITE);
                frameLayout.setBackgroundColor(Color.RED);
                break;
            case alarmConstants.ALARM_B:
                vibrate();
                appText.setText(message);
                appText.setTextColor(Color.WHITE);
                frameLayout.setBackgroundColor(Color.RED);
                break;
            case alarmConstants.ALARM_C:
                vibrate();
                appText.setText(message);
                appText.setTextColor(Color.WHITE);
                frameLayout.setBackgroundColor(Color.RED);
                break;
            case alarmConstants.ALARM_D:
                vibrate();
                appText.setText(message);
                appText.setTextColor(Color.WHITE);
                frameLayout.setBackgroundColor(Color.RED);
                break;
            case alarmConstants.ALARM_OFF:
                appText.setText("");
                appText.setTextColor(Color.BLACK);
                frameLayout.setBackgroundColor(Color.WHITE);
                break;

            //Versuch 2
            case alarmConstants.P_1_RUN:
                //frameLayout.setBackgroundColor(Color.WHITE);
                //vibrate();
                appText.setText(message);
                messageHolder = message;
                appText.setTextColor(Color.BLACK);
                frameLayout.setBackgroundColor(Color.WHITE);
                Log.d("In Prozess", "P_1_RUN");
                break;
            case alarmConstants.P_2_RUN:
                appText.setVisibility(View.VISIBLE);
                appText.setText(message);
                messageHolder = message;
                appText.setTextColor(Color.BLACK);
                //frameLayout.setBackgroundColor(Color.WHITE);
                Log.d("In Prozess", "P_2_RUN");
                break;
            case alarmConstants.P_3_RUN:
                appText.setText(message);
                messageHolder = message;
                appText.setTextColor(Color.BLACK);
                frameLayout.setBackgroundColor(Color.WHITE);
                break;
            case alarmConstants.P_4_RUN:
                appText.setText(message);
                messageHolder = message;
                appText.setTextColor(Color.BLACK);
                frameLayout.setBackgroundColor(Color.WHITE);
                break;
            case alarmConstants.P_1_START:
                vibrate();
                timer.setTextColor(Color.BLACK);
                appText.setText(message);
                appText.setTextColor(Color.BLACK);
                frameLayout.setBackgroundColor(Color.WHITE);
                break;
            case alarmConstants.P_2_START:
                vibrate();
                timer.setTextColor(Color.BLACK);
                appText.setText(message);
                appText.setTextColor(Color.BLACK);
                frameLayout.setBackgroundColor(Color.WHITE);
                break;
            case alarmConstants.P_3_START:
                vibrate();
                timer.setTextColor(Color.BLACK);
                appText.setText(message);
                appText.setTextColor(Color.BLACK);
                frameLayout.setBackgroundColor(Color.WHITE);
                break;
            case alarmConstants.P_4_START:
                vibrate();
                timer.setTextColor(Color.BLACK);
                appText.setText(message);
                appText.setTextColor(Color.BLACK);
                frameLayout.setBackgroundColor(Color.WHITE);
                break;
            case alarmConstants.DONE:
                vibrate();
                timer.setTextColor(Color.BLACK);
                appText.setText(message);
                appText.setTextColor(Color.BLACK);
                frameLayout.setBackgroundColor(Color.WHITE);
                break;
            case alarmConstants.P_CRITICAL:
                Log.d("in critical", "kritischer prozess case");
                //vibrate();
                appText.setTextColor(Color.BLACK);
                frameLayout.setBackgroundColor(Color.RED);
                //startSecond();
                //vibrate();
                break;
            default:
                String regexStr = "^[0-9]*$";
                if(message.matches(regexStr))
                {
                    //sets the process Duration if it is a numeric value
                    Log.d("in regex", "in regex");
                    currentProcessDuration = Integer.parseInt(message)-synchDuration;

                    appText.setText("Prozess läuft.");
                    appText.setTextColor(Color.BLACK);
                    //and starts the countdown timer
                    startSecond();
                }
                else{
                    Log.d("in else", "");
                    appText.setText("Fehler");
                    frameLayout.setBackgroundColor(Color.WHITE);
                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
            mApiClient.connect();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    @Override
    protected void onStop() {
        if ( mApiClient != null ) {
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

    private void vibrate(){
        vibrator.vibrate(vibrationLength);
    }

    //Sekundentimer
    private void startSecond() {
        Log.d("Start Second", ": "+currentProcessDuration);
        TimerTask action = new TimerTask() {
            public void run() {
                Message msg = handlerSecondOver.obtainMessage();
                handlerSecondOver.sendMessage(msg);
            }
        };
        caretakerSeconds = new Timer();
        caretakerSeconds.schedule(action, 999);
    }

    private String getTime(){
        String timerHelper;
        //Prozessdauer im timer aktualisieren
        long seconds = TimeUnit.MILLISECONDS.toSeconds(currentProcessDuration) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentProcessDuration));

        if(TimeUnit.MILLISECONDS.toMinutes(currentProcessDuration)==0) {
            if ( seconds > 9 )
                timerHelper = String.format("00:%d", seconds);
            else
                timerHelper = String.format("00:0%d", seconds);
        }else{
            if ( seconds > 9 )
                timerHelper =
                        String.format("0%d:%d",
                                TimeUnit.MILLISECONDS.toMinutes(currentProcessDuration), seconds);
            else
                timerHelper =
                        String.format("0%d:0%d",
                                TimeUnit.MILLISECONDS.toMinutes(currentProcessDuration), seconds);
        }
        return timerHelper;
    }
}

