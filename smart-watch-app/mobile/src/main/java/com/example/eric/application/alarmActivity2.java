package com.example.eric.application;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.util.concurrent.TimeUnit;

public class alarmActivity2 extends AppCompatActivity
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    /**
    Der Versuch wird beendet und "Wenden Sie sich an die Versuchsleitung"
    wird aufgerufen bei Ablauf des vierten Prozesses.
     */

    //Connection to the Process Durations
    ProzessProvider processProvider;

    // connection to the database
    databaseSource dataSource;

    //currentProcessId is an array index variable
    //one needs to be added to write it to the database
    int currentProcessId = 0;
    //Time when the user gets an information, that the process ends
    //Zeit des Hinweises vor Ende in ms
    private final int hinweisZeit = 10000;

    //Dauer des aktuellen Prozesses
    private int currentProcessDuration = 0;

    //Enthaelt alle vier Prozessdauern in Millisekunden
    //mit Index 4 vier erhaelt man das Start Delay
    //This can be casted to integer
    //
    int [] processDurations;

    // log data which is passed between activities
    UserInputLog ui_Log;

    //Timers
    Timer caretakerDelay, caretaker, caretakerProcessEnds, caretakerSeconds;

    //date for log of popup and click time
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'um' HH:mm:ss:SS");

    //for the connection to the Wearable
    private com.google.android.gms.wearable.Node mNode;
    private GoogleApiClient mApiClient;
    private static final String DEVICE_MAIN = "DeviceMain";
    private static final String WEAR_PATH = "/from_device";

    //String Messages an die Watch
    final String PROCESS_NEW = "PROCESS_NEW";
    final String PROCESS_CONFIRMED = "PROCESS_CONFIRMED";
    final String PROCESS_CRITICAL = "PROCESS_CRITICAL";
    final String PROCESS_ENDED = "PROCESS_ENDED";

    //include watch?
    boolean use_watch;

    //TODO: Check: Is this variable necessary?
    protected static final int CONTINUE_REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("A2", "On Create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm2);

        //instantiate the data source
        dataSource = new databaseSource(this);

        //Get the parcelable object to move around data
        Bundle b = getIntent().getExtras();
        ui_Log = b.getParcelable(".hmi.UserInputLog");

        //check if watch should be included
        if(ui_Log.getModalitaet()==ui_Log.WATCH)
            use_watch = true;

        //Initialize mGoolgeAPIClient
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this )
                .addOnConnectionFailedListener(this)
                .build();

        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
            mApiClient.connect();

        //calculate the Process Durations
        processProvider = new ProzessProvider();
        processDurations = processProvider.getProcesses();

        //run delay until process 1 gets visible
        startDelayTimer((int)processDurations[4]);

    }

    //Prozess wird durch Userinteraktion gestartet
    public void onClickButton_Prozess_starten(View view) {

        //track user interaction
        ui_Log.setConfirmationtime(sdf.format(new Date()));

        //write row to database, includes when process
        //blended in and when it was started by the user
        dataSource.create(ui_Log);

        if(use_watch)
            //Auf der watch Prozess Starten ausblenden
            sendMessageToWear(PROCESS_CONFIRMED);

        //Halte die Prozesszeit in einer schnellen Variablen fest
        currentProcessDuration = (int) processDurations[currentProcessId];

        //Lege die Anzeige fuer den Timer unter Prozess laeuft fest
        Button button_Prozess_läuft = (Button)findViewById(R.id.button_Prozess_läuft);
        button_Prozess_läuft.setText("Prozess " + (currentProcessId + 1) + " läuft.");

        //Belege den Timer mit der Zeitanzeige
        TextView timer = (TextView) findViewById(R.id.textView_TimerAnzeige);
        String timeHelper = String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(currentProcessDuration),
                TimeUnit.MILLISECONDS.toSeconds(currentProcessDuration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentProcessDuration)));

        timer.setText(timeHelper);

        if(use_watch)
            //Send time to watch
            sendMessageToWear(timeHelper);

        //Starte den Prozess
        startProcess(currentProcessDuration);

        //Starte den Hinweistimer
        startProcesEndsInformation(currentProcessDuration);

        //Aktualisiere den Timerstring jede Sekunde
        startSecond();

        //Blende den Prozess starten Button aus
        Button button_Prozess_starten = (Button)findViewById(R.id.button_Prozess_starten);
        button_Prozess_starten.setVisibility(View.INVISIBLE);

        //Blende Felder mit dem Infotext "Prozess xy laeuft" und das der Dauer ein
        button_Prozess_läuft.setVisibility(View.VISIBLE);
        timer.setVisibility(View.VISIBLE);
    }

    //startet einen Prozess mit currentProcessDuration
    //Erhaelt aus Performance Gruenden currentProcessDuration als Variable
    private void startDelayTimer(int delay) {
        TimerTask action = new TimerTask() {
            public void run() {
                Message msg = handlerDelayTimer.obtainMessage();
                handlerDelayTimer.sendMessage(msg);
            }
        };
        caretakerDelay = new Timer();

        //Delay
        caretakerDelay.schedule(action, delay);
    }

    //startet einen Prozess mit currentProcessDuration
    //Erhaelt aus Performance Gruenden currentProcessDuration als Variable
    private void startProcess(int currentProcessDuration) {
        TimerTask action = new TimerTask() {
            public void run() {
                Message msg = handlerProcessEnd.obtainMessage();
                handlerProcessEnd.sendMessage(msg);
            }
        };
        caretaker = new Timer();

        // Process i starten
        caretaker.schedule(action, currentProcessDuration);
    }

    //Timer bis zum Hinweis auf das Prozessende
    private void startProcesEndsInformation(int currentProcessDuration) {
        TimerTask action = new TimerTask() {
            public void run() {
                Message msg = handlerNotificationProcessEnd.obtainMessage();
                handlerNotificationProcessEnd.sendMessage(msg);
            }
        };
        caretakerProcessEnds = new Timer();

        //Timer zum Hinweis ueber das Prozess Ende
        caretakerProcessEnds.schedule(action, currentProcessDuration - hinweisZeit);
    }

    //Sekundentimer
    private void startSecond() {

        TimerTask action = new TimerTask() {
            public void run() {
                Message msg = handlerSecondOver.obtainMessage();
                handlerSecondOver.sendMessage(msg);
            }
        };
        caretakerSeconds = new Timer();
        //TODO: KLaeren ob eine Millisekunde Abzug fuer Programmabarbeitung sinnvoll ist
        caretakerSeconds.schedule(action, 59999);
    }

    //Hinweis auf Prozessende wird eingeblendet
    Handler handlerDelayTimer = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            blendInStartProcess();
        }
    };

    //Counter abgelaufen: Prozess  i läuft und Timerfeld werden invisible
    Handler handlerProcessEnd = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            caretakerSeconds.cancel();

            Button button = (Button) findViewById(R.id.button_Prozess_läuft);
            button.setVisibility(View.INVISIBLE);
            TextView timerFeld = (TextView) findViewById(R.id.textView_TimerAnzeige);
            timerFeld.setVisibility(View.INVISIBLE);

            if(use_watch)
                sendMessageToWear(PROCESS_ENDED);

            if((currentProcessId+1)<5)
                blendInStartProcess();
            else
                goToContinueActivity();
        }
    };

    //Aktualiere den Timerstring nach Ablauf einer Sekunde
    Handler handlerSecondOver = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            //Verbleibende Prozess Dauer um eine Sekunde verringern
            currentProcessDuration -=10000;

            //Prozessdauer im timer aktualisieren
            TextView timer = (TextView) findViewById(R.id.textView_TimerAnzeige);

            String timerHelper =
                    String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(currentProcessDuration),
                    TimeUnit.MILLISECONDS.toSeconds(currentProcessDuration) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentProcessDuration)));

            timer.setText(timerHelper);

            if(use_watch)
                sendMessageToWear("Prozess "+ (currentProcessId+1) +
                                " läuft. \n \n " + timerHelper
            );

            //countdown next second
            startSecond();
        }
    };

    //Hinweis auf Prozessende wird eingeblendet
    Handler handlerNotificationProcessEnd = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(use_watch)
                sendMessageToWear(PROCESS_CRITICAL);

            Button button = (Button) findViewById(R.id.button_Prozess_läuft);
            button.setBackgroundColor(Color.RED);
        }
    };

    //Passe das Feld Prozess starten an und blende es ein
    public void blendInStartProcess(){
        //Gehe zum naechsten Prozess ueber
        ui_Log.setProzess_id(Integer.toString(currentProcessId+1));
        ui_Log.setProcessBlendInTime(sdf.format(new Date()));

        //Blende das Feld mit "Prozess laeuft" und der Zeitanzeige zum ablaufenden Prozesses aus
        Button button_Prozess_läuft = (Button)findViewById(R.id.button_Prozess_läuft);
        button_Prozess_läuft.setVisibility(View.INVISIBLE);
        button_Prozess_läuft.setBackgroundColor(Color.WHITE);

        //Passe das Feld "Prozess starten" an und blende es ein
        Button button_Prozess_starten = (Button)findViewById(R.id.button_Prozess_starten);

        if(currentProcessId==0) {
            button_Prozess_starten.setText("Prozess" + (currentProcessId + 1) + " starten.");
        } else {
            button_Prozess_starten.setText("Prozess " + (currentProcessId) + " ist beendet. \n \n" +
                 "Prozess" + (currentProcessId + 1) + " starten.");
        }

        button_Prozess_starten.setVisibility(View.VISIBLE);

        if(use_watch)
            sendMessageToWear(PROCESS_NEW);

        //Prozess Id hochsetzen
        currentProcessId += 1;
    }

    //handler helper to call the get supervisor to continue screen
    private void goToContinueActivity(){
        dataSource.close();
        Intent continueIntent = new Intent(this, continueActivity.class);
        continueIntent.putExtra(".hmi.UserInputLog",  ui_Log);
        startActivityForResult(continueIntent, CONTINUE_REQUEST_CODE);
    }

    @Override
    public void onBackPressed() {
        //set cancel dialog behaviour
        new AlertDialog.Builder(this)
                .setTitle("Abbrechen")
                .setMessage("Die Eingaben dieses Teils des Versuchteils 2 wurden nicht gespeichert. Sind Sie sicher, dass Sie abbrechen möchten?")

                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        caretaker.cancel();
                        caretakerSeconds.cancel();
                        dataSource.deleteData(ui_Log.getUser_id(), ui_Log.getModalitaet(), databaseHelper.TABLENAME_V2);
                        dataSource.close();
                        //do not use onBackPressed, but use finish()
                        alarmActivity2.this.finish();
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

}
