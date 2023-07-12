package com.example.eric.application;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
/**
 * Description of the class:
 * This class provides the functionality during the first part of the experiment.
 * The
 */
public class alarmActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class variables                                                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    // for the connection to the Wearable
    private com.google.android.gms.wearable.Node mNode;
    private GoogleApiClient mApiClient;
    private static final String DEVICE_MAIN = "DeviceMain";
    private static final String WEAR_PATH = "/from_device";

    // variables for the alarm
    protected static final int CONTINUE_REQUEST_CODE = 10;
    private AlarmProvider alarm = new AlarmProvider();
    private int [] type = alarm.getAlarmType();
    private int [] delay = alarm.getDelay();
    private boolean alarmOn;

    // connection to the database
    databaseSource dataSource;

    // log data which is passed between activities
    UserInputLog ui_Log;

    //helper variables to
    private final String ALARM_A = "A";
    private final String ALARM_B = "B";
    private final String ALARM_C = "C";
    private final String ALARM_D = "D";

    Timer caretaker, caretaker1, caretaker2, caretaker3, caretaker4;
    //Long caretaker_time, caretaker1_time, caretaker2_time_time, caretaker4_time;

    //date for log of popup and click time
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'um' HH:mm:ss:SS");


    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // handler on behaviour of functions                                             //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    //Handler for the Timer, action on Alarm
    Handler handlerAlarmOn = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //TextView textView = (TextView) findViewById(R.id.textView2);
            //String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            // textView is the TextView view that should display it
            //textView.setText( textView.getText() + currentDateTimeString +" "+ msg.arg1 + "\n");
            Button button = (Button) findViewById(R.id.id_button_alarm);
            button.setBackgroundColor(0xffff0000);

            switch(msg.arg1){
                case 0:{
                    ui_Log.setAlarmtyp(ALARM_A);//textView.setText(textView.getText() + " " + get_alarmType() + "\n");
                }break;

                case 1: {
                    ui_Log.setAlarmtyp(ALARM_B);
                    //textView.setText(textView.getText() + " " + get_alarmType() + "\n");
                }break;

                case 2: {
                    ui_Log.setAlarmtyp(ALARM_C);
                    //textView.setText(textView.getText() + " " + get_alarmType() + "\n");
                }break;
                case 3: {
                    ui_Log.setAlarmtyp(ALARM_D);
                    //textView.setText(textView.getText() + " " + get_alarmType() + "\n");
                }break;
                default: break;
            }

            sendMessageToWear("Alarm " + ui_Log.getAlarmtyp());

            sendMessageToWear("On");

            button.setText("Alarm " + ui_Log.getAlarmtyp());
            ui_Log.setPopuptime(sdf.format(new Date()));
            setAlarmOn(true);
            button.setVisibility(View.VISIBLE);
        }
    };

    //Handler to get supervisor after this modification is dealt with
    Handler handlerContinue = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            goToContinueActivity();
        }
    };

    //Handler gives the user feedback about the alarm correction for 3 sec
    Handler handlerCorrectionFadeIn = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            TextView tv = (TextView) findViewById(R.id.id_fehler_behoben);
            if(getAlarmOn()){
                tv.setText(R.string.fehler_nicht_behoben);
            }else {
                tv.setText(R.string.fehler_behoben);
                sendMessageToWear("Off");
            }
            tv.setVisibility(View.VISIBLE);
            Button button = (Button) findViewById(R.id.id_button_alarm);
            Button button_a = (Button) findViewById(R.id.id_button_alarm_a_beheben);
            Button button_b = (Button) findViewById(R.id.id_button_alarm_b_beheben);
            Button button_c = (Button) findViewById(R.id.id_button_alarm_c_beheben);
            Button button_d = (Button) findViewById(R.id.id_button_alarm_d_beheben);
            button.setVisibility(View.INVISIBLE);
            button_a.setVisibility(View.INVISIBLE);
            button_b.setVisibility(View.INVISIBLE);
            button_c.setVisibility(View.INVISIBLE);
            button_d.setVisibility(View.INVISIBLE);
        }
    };

    //Handler fades out the feedback about the alarm correction
    Handler handlerCorrectionFadeOut = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            TextView tv = (TextView) findViewById(R.id.id_fehler_behoben);
            tv.setVisibility(View.INVISIBLE);
            Button button_a = (Button) findViewById(R.id.id_button_alarm_a_beheben);
            Button button_b = (Button) findViewById(R.id.id_button_alarm_b_beheben);
            Button button_c = (Button) findViewById(R.id.id_button_alarm_c_beheben);
            Button button_d = (Button) findViewById(R.id.id_button_alarm_d_beheben);
            button_a.setVisibility(View.VISIBLE);
            button_b.setVisibility(View.VISIBLE);
            button_c.setVisibility(View.VISIBLE);
            button_d.setVisibility(View.VISIBLE);

            if (getAlarmOn()){
                Button button = (Button) findViewById(R.id.id_button_alarm);
                button.setVisibility(View.VISIBLE);
            }
        }
    };

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // constructors, getters, setters                                                //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    private boolean getAlarmOn() {
        return alarmOn;
    }

    private void setAlarmOn(boolean alarmOn) {
        this.alarmOn = alarmOn;
    }

    //set the timers for the alarms
    private void setTimers() {

        TimerTask action = new TimerTask() {
            public void run() {
                Message msg = handlerAlarmOn.obtainMessage();
                msg.arg1 = type[0];
                handlerAlarmOn.sendMessage(msg);
            }
        };
        caretaker = new Timer();
        caretaker.schedule(action, delay[0]);

        TimerTask action1 = new TimerTask() {
            public void run() {
                Message msg = handlerAlarmOn.obtainMessage();
                msg.arg1 = type[1];
                handlerAlarmOn.sendMessage(msg);
            }
        };
        caretaker1 = new Timer();
        caretaker1.schedule(action1, delay[1]);

        TimerTask action2 = new TimerTask() {
            public void run() {
                Message msg = handlerAlarmOn.obtainMessage();
                msg.arg1 = type[2];
                handlerAlarmOn.sendMessage(msg);
            }
        };
        caretaker2 = new Timer();
        caretaker2.schedule(action2, delay[2]);


        TimerTask action3 = new TimerTask() {
            public void run() {
                Message msg = handlerAlarmOn.obtainMessage();
                msg.arg1 = type[3];
                handlerAlarmOn.sendMessage(msg);
            }
        };
        caretaker3 = new Timer();
        caretaker3.schedule(action3, delay[3]);

        TimerTask action4 = new TimerTask() {
            public void run() {
                handlerContinue.sendEmptyMessage(0);
            }
        };
        caretaker4 = new Timer();

        /**The experiment ends after 8 min*/
        caretaker4.schedule(action4, 480000);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // activity methods, reaction on changes to the application                      //
    // the functions are self-explaining by their name                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onBackPressed() {
        //set cancel dialog behaviour
        new AlertDialog.Builder(this)
                .setTitle("Abbrechen")
                .setMessage("Die Eingaben dieses Teils des Versuchsteils 1 wurden nicht gespeichert. Sind Sie sicher, dass Sie abbrechen möchten?")

                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        cancelTimers();
                        dataSource.deleteData(ui_Log.getUser_id(), ui_Log.getModalitaet(), databaseHelper.TABLENAME);
                        dataSource.close();
                        //do not use onBackPressed, but use finish()
                        alarmActivity.this.finish();
                    }
                })
                .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    //do this on start
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //instantiate the data source
        dataSource = new databaseSource(this);

        //Initialize mGoolgeAPIClient
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
            mApiClient.connect();

        //Get the parcelable object to move around data
        Bundle b = getIntent().getExtras();
        ui_Log = b.getParcelable(".hmi.UserInputLog");

        //this is the first part of the lab
        setTimers();
        setAlarmOn(false);

        setContentView(R.layout.activity_alarm);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_alarm, menu);
        return true;
    }

    //on connection to the wearable
    @Override
    public void onConnected(Bundle bundle) {
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
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //refers to the option button on the upper right corner of the application
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Handle item selection
        try{
            switch (id) {
                case R.id.action_settings:
                    cancelTimers();
                    dataSource.close();
                    showSettings();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }catch(Exception e) {
            Log.e("StartActivity", "Error in Settings");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }
    @Override

    protected void onResume() {
        super.onResume();
        dataSource.open();
    }

    @Override
    protected void onStart() {
        super.onStart();
        dataSource.open();
        mApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mApiClient.disconnect();
        dataSource.close();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // click handlers for the buttons in this activity                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    //Click Handler for the Buttons
    public void button_alarm_AClickHandler(View view) {
        ui_Log.setClickedButtonType(ALARM_A);
        button_alarm_ClickHandler_Helper();
    }

    //Click Handler for the Button
    public void button_alarm_BClickHandler(View view) {
        ui_Log.setClickedButtonType(ALARM_B);
        button_alarm_ClickHandler_Helper();
    }

    //Click Handler for the Button
    public void button_alarm_CClickHandler(View view) {
        ui_Log.setClickedButtonType(ALARM_C);
        button_alarm_ClickHandler_Helper();
    }

    //Click Handler for the Button
    public void button_alarm_DClickHandler(View view) {
        ui_Log.setClickedButtonType(ALARM_D);
        button_alarm_ClickHandler_Helper();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class functions to provide the essential class functionality                  //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    //Passes the the user input log data to the database, then a row is added to the database
    //which includes all current log information
    private void addClickData(){
        ui_Log.setClicktime(sdf.format(new Date()));
        dataSource.create(ui_Log);
    }

    //Helping Method for the Button Click Handler
    private void button_alarm_ClickHandler_Helper(){
        if(getAlarmOn()) {
            if (ui_Log.getAlarmtyp() == ui_Log.getClickedButtonType()) {
                Button button = (Button) findViewById(R.id.id_button_alarm);
                setAlarmOn(false);
                button.setVisibility(View.INVISIBLE);
            }
            addClickData();
            showCorrection();
        }
    }

    private void cancelTimers(){
        caretaker.cancel();
        caretaker1.cancel();
        caretaker2.cancel();
        caretaker3.cancel();
        caretaker4.cancel();
        Log.i("Activity", "Timers canceled");
    }

    //handler helper to call the get supervisor to continue screen
    private void goToContinueActivity(){
        dataSource.close();
        Intent continueIntent = new Intent(this, continueActivity.class);
        continueIntent.putExtra(".hmi.UserInputLog",  ui_Log);
        startActivityForResult(continueIntent, CONTINUE_REQUEST_CODE);
    }

    //sends a message to the wearable
    private void sendMessageToWear(String alarmType){
        if(mNode != null && mApiClient != null){
            Wearable.MessageApi.sendMessage(mApiClient,
                    mNode.getId(), WEAR_PATH, alarmType.getBytes())
                    .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            if (!sendMessageResult.getStatus().isSuccess()) {
                                Log.d(WEAR_PATH, "Failed message");
                            } else {
                                Log.d(WEAR_PATH, "Message succeeded");
                            }
                        }
                    });
        }
    }

    //This method calls the Correction Show handlerAlarmOn
    private void showCorrection() {
        Runnable r = new Runnable() {
            //use a Thread to wait for two seconds
            @Override
            public void run() {
                long futureTime = System.currentTimeMillis() + 3000;
                handlerCorrectionFadeIn.sendEmptyMessage(0);
                while(System.currentTimeMillis() < futureTime){
                    synchronized (this){
                        try{
                            wait(futureTime - System.currentTimeMillis());
                        }catch(Exception ignored){}
                    }
                }
                handlerCorrectionFadeOut.sendEmptyMessage(0);
            }
        };
        Thread waitThread = new Thread(r);
        waitThread.start();
    }

    //Opens the options menu window
    private void showSettings() {
        Intent settings = new Intent(this, optionActivity.class);
        settings.putExtra(".hmi.UserInputLog", ui_Log);
        startActivity(settings);
    }


}

