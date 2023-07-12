package com.example.android.maximmi;

import com.example.android.bluetoothchat.MainActivity;
import com.example.android.bluetoothchat.R;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class alarmActivity2 extends Fragment {

    /**
    Der Versuch wird beendet und "Wenden Sie sich an
    die Versuchsleitung" wird aufgerufen bei Ablauf
    des vierten Prozesses.
    */

    //Connection to the Process Durations
    ProzessProvider processProvider;

    //currentProcessId is an array index variable
    //one needs to be added to write it to the database
    int currentProcessId = 0;

    //Variable fot delay after Process is started.
    //The Start command process and the duration of the process
    //are sent to the the watch in two separate packets, which
    //follow directly after each other.
    //The delay in between is necessary because else wise,
    //it happens that watch does not recognize that these are two
    //separate packets due to bluetooth
    int bluetoothMessagesDelay = 500;

    //Time when the user gets an information, that the process ends
    //Zeit des Hinweises vor Ende in ms
    private final int hinweisZeit = 10000;

    //Dauer des aktuellen Prozesses
    private int currentProcessDuration = 0;

    //private Objectsfrom the layout
    //Weisses bzw. rotes Feld mit Prozess laeuft
    private Button button_Prozess_läuft;

    //This variable sets how much time may be left to the next process,
    //if there are less than 15 sec left, the next process will get skipped (canceled)
    int timeWindowToSkipNextProzess;

    //Timeranzeige
    private TextView timer;

    //Button mit Prozess starten
    private Button button_Prozess_starten;

    //Enthaelt alle vier Prozessdauern in Millisekunden
    //mit Index 4 vier erhaelt man das Start Delay
    //This can be casted to integer
    int [] processDurations;

    // log data which is passed between activities
    UserInputLog ui_Log;

    //Timers
    Timer caretakerDelay, caretaker, caretakerProcessEnds, caretakerSeconds;

    //date for log of popup and click time
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'um' HH:mm:ss:SS");

    //processBlendInTime
    long processBlendInTime;

    //String Messages an die Watch
    final String PROCESS_NEW = "PROCESS_NEW";
    final String PROCESS_CONFIRMED = "PROCESS_CONFIRMED";
    final String PROCESS_CRITICAL = "PROCESS_CRITICAL";
    final String PROCESS_ENDED = "PROCESS_ENDED";

    //include wearable?
    boolean use_watch;
    boolean use_glasses;

    //String to hold the time
    String timerHelper;

    Handler handlerProcessEnd, handlerSecondOver,
            handlerDelayTimer, handlerNotificationProcessEnd;

    //variables to skip processes
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get the parcelable object to move around data
        ui_Log = getArguments().getParcelable("uiLog");

        use_watch   = ui_Log.getModalitaet().equals(ui_Log.WATCH);
        use_glasses = ui_Log.getModalitaet().equals(ui_Log.BRILLE);

        //calculate the Process Durations
        processProvider = new ProzessProvider();
        processDurations = new int[5];
        processDurations = processProvider.getProcesses();
        processProvider.printLog();
        Log.d("Process Durations", processDurations[0] + " " + processDurations[1]);
        Log.d("Process Durations", processDurations[2] + " " + processDurations[3]);
        Log.d("Process Durations", " " + processDurations[4]);
        //run delay until process 1 gets visible
        sendAlarmMessage(alarmConstants.START);

        caretakerDelay = new Timer();
        caretaker = new Timer();
        caretakerProcessEnds = new Timer();
        caretakerSeconds = new Timer();
    }

    //do this on start
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("A2", "On Create");
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_alarm2, container, false);

        //Belege die Klassenobjekte
        timer = (TextView) view.findViewById(R.id.textView_TimerAnzeige);
        button_Prozess_läuft = (Button) view.findViewById(R.id.button_Prozess_läuft);
        button_Prozess_starten = (Button) view.findViewById(R.id.button_Prozess_starten);
        button_Prozess_läuft.setVisibility(View.INVISIBLE);
        button_Prozess_starten.setVisibility(View.INVISIBLE);
        timer.setVisibility(View.INVISIBLE);

        //onClickListener
        button_Prozess_starten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButton_Prozess_starten();
            }
        });

        //Hinweis auf Prozessende wird eingeblendet
        handlerDelayTimer = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                blendInStartProcess();
            }
        };

        //Counter abgelaufen: Prozess  i läuft und Timerfeld werden invisible
        handlerProcessEnd = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                caretakerSeconds.cancel();

                button_Prozess_läuft.setVisibility(View.INVISIBLE);
                timer.setVisibility(View.INVISIBLE);

                if((currentProcessId+1)<5)
                    blendInStartProcess();
                else
                    goToContinueActivity();
            }
        };

        //Aktualiere den Timerstring nach Ablauf einer Sekunde
        handlerSecondOver = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                //Verbleibende Prozess Dauer um eine Sekunde verringern
                currentProcessDuration -=1000;

                setTimerHelper();

                Log.d("AlarmActivity2", "Second: " + currentProcessDuration);
                timer.setText(getTimerHelper());
                //TODO: Change if time should be displayed on watch include seconds
                //if(use_watch)
                //    sendAlarmMessage("Prozess " + (currentProcessId + 1) + " läuft.");

                //countdown next second
                if(currentProcessDuration >= 0)
                    startSecond();
            }
        };

        //Hinweis auf Prozessende wird eingeblendet
        handlerNotificationProcessEnd = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //TODO Check if this can be sent. it is not send because if so,
                //the display will reload and we do not want that to happen.
                //sendAlarmMessage(alarmConstants.P_CRITICAL);
                button_Prozess_läuft.setBackgroundColor(Color.RED);
                button_Prozess_läuft.setTextColor(Color.WHITE);
            }
        };

        startDelayTimer(processDurations[4]);
        return view;
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

    //Passe das Feld Prozess starten an und blende es ein
    public void blendInStartProcess(){
        //Gehe zum naechsten Prozess ueber
        ui_Log.setProzess_id(Integer.toString(currentProcessId + 1));
        ui_Log.setProcessBlendInTime(sdf.format(new Date()));
        processBlendInTime = System.currentTimeMillis();

        //Blende das Feld mit "Prozess laeuft" und der Zeitanzeige zum ablaufenden Prozesses aus
        button_Prozess_läuft.setVisibility(View.INVISIBLE);
        button_Prozess_läuft.setBackgroundColor(Color.WHITE);
        button_Prozess_läuft.setTextColor(Color.BLACK);
        timer.setText("Der Prozess ist abgeschlossen");

        //Passe das Feld "Prozess starten" an und blende es ein

        if(currentProcessId==0) {
            button_Prozess_starten.setText("Prozess " + (currentProcessId + 1) + " starten");
        } else {
            button_Prozess_starten.setText("Prozess " + (currentProcessId + 1) + " starten");
        }

        button_Prozess_starten.setVisibility(View.VISIBLE);

        switch(currentProcessId){
            case 0:
                sendAlarmMessage(alarmConstants.P_1_START);
                try {
                    Thread.sleep(bluetoothMessagesDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                sendAlarmMessage(alarmConstants.P_2_START);
                try {
                    Thread.sleep(bluetoothMessagesDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                sendAlarmMessage(alarmConstants.P_3_START);
                try {
                    Thread.sleep(bluetoothMessagesDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                sendAlarmMessage(alarmConstants.P_4_START);
                try {
                    Thread.sleep(bluetoothMessagesDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    //Prozess wird durch Userinteraktion gestartet
    public void onClickButton_Prozess_starten() {
        //track user interaction
        ui_Log.setConfirmationtime(sdf.format(new Date()));

        //write row to database, includes when process
        //blended in and when it was started by the user
        ((MainActivity)getActivity()).create(ui_Log);

        //Auf der watch Prozess Starten ausblenden
        //TODO: adapt if time should be displayed on watch
        if(use_watch)
            //sendAlarmMessage("Prozess " + (currentProcessId + 1) + " läuft. \n" + timerHelper);
            switch(currentProcessId){
                case 0:
                    sendAlarmMessage(alarmConstants.P_1_RUN);
                    break;
                case 1:
                    sendAlarmMessage(alarmConstants.P_2_RUN);
                    break;
                case 2:
                    sendAlarmMessage(alarmConstants.P_3_RUN);
                    break;
                case 3:
                    sendAlarmMessage(alarmConstants.P_4_RUN);
                    break;
                default:
                    break;
            }

        if(use_glasses)
            switch(currentProcessId){
                //TODO Show the string that the process is running
                case 0:
                    sendAlarmMessage(alarmConstants.P_1_RUN);
                    break;
                case 1:
                    sendAlarmMessage(alarmConstants.P_2_RUN);
                    break;
                case 2:
                    sendAlarmMessage(alarmConstants.P_3_RUN);
                    break;
                case 3:
                    sendAlarmMessage(alarmConstants.P_4_RUN);
                    break;
                default:
                    break;
            }

        //time that passed until the user confirmed
        int userDelay = (int) (System.currentTimeMillis() - processBlendInTime);

        //normally the while loop will not be called, as there is nothing to be skipped
        int prozessTimeSkipped = processDurations[currentProcessId];

        //If less than 15 sec are left in the next process, skip this process.
        //this should be tried for as many processes as possible.
        //So we loop over the processes durations.
        while( userDelay >= prozessTimeSkipped - timeWindowToSkipNextProzess ) {
            currentProcessId = + 1;
            prozessTimeSkipped = + processDurations[currentProcessId];
            //TODO: write skipped in die Datenbank
            //TODO: the process will just be skipped, optimization
            //TODO: is possible by renaming the processes
        }

        //Halte die Prozesszeit in einer schnellen Variablen fest
        currentProcessDuration = prozessTimeSkipped - userDelay;
        sendAlarmMessage(String.valueOf(currentProcessDuration));
        Log.d("ProcessIDDuration", "PID time "+ currentProcessDuration);

        //Lege die Anzeige fuer den Timer unter Prozess laeuft fest
        button_Prozess_läuft.setText("Prozess " + (currentProcessId + 1) + " läuft");

        setTimerHelper();

        timer.setText(getTimerHelper());

        Log.d("AlarmAct2", currentProcessDuration + "");

        //Starte den Prozess
        startProcess(currentProcessDuration);

        //Starte den Hinweistimer
        startProcesEndsInformation(currentProcessDuration, hinweisZeit);

        //Aktualisiere den Timerstring jede Sekunde
        startSecond();

        //Blende den Prozess starten Button aus
        button_Prozess_starten.setVisibility(View.INVISIBLE);

        //Blende Felder mit dem Infotext "Prozess xy laeuft" und das der Dauer ein
        button_Prozess_läuft.setVisibility(View.VISIBLE);
        timer.setVisibility(View.VISIBLE);

        //Prozess Id hochsetzen
        currentProcessId += 1;
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
    private void startProcesEndsInformation(int currentProcessDuration, int hinweisZeit) {
        TimerTask action = new TimerTask() {
            public void run() {
                Message msg = handlerNotificationProcessEnd.obtainMessage();
                handlerNotificationProcessEnd.sendMessage(msg);
            }
        };
        caretakerProcessEnds = new Timer();
        //Timer zum Hinweis ueber das Prozess Ende
        caretakerProcessEnds.schedule(action, currentProcessDuration-hinweisZeit);
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
        caretakerSeconds.schedule(action, 999);
    }

    //handler helper to call the get supervisor to continue screen
    private void goToContinueActivity(){

        //reset the wearables screen
        sendAlarmMessage(alarmConstants.DONE);

        Bundle b = new Bundle();
        b.putParcelable("uiLog", ui_Log);

        //add the bundle to the fragment
        continueActivity fragment = new  continueActivity();
        fragment.setArguments(b);

        //fragment transaction
        final FragmentTransaction ft = getFragmentManager().beginTransaction();

        //replace fragment and commit
        ft.replace(R.id.frame_user_screen, fragment).commit();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        goBackHelper();
        super.onStop();
    }

    public void goBackHelper(){
        Log.d("main", "go back helper zero");
        // continue with delete
        caretaker.cancel();
        caretakerSeconds.cancel();
        caretakerDelay.cancel();
        caretakerProcessEnds.cancel();
        caretaker.cancel();
        caretakerSeconds.cancel();
        caretakerDelay.cancel();
        caretakerProcessEnds.cancel();

        Log.d("main", "go back helper one");
    }

    public void goBack(){
        Bundle b = new Bundle();
        b.putParcelable("uiLog", ui_Log);

        //add the bundle to the fragment
        modification_select fragment = new modification_select();
        fragment.setArguments(b);

        //fragment transaction
        final FragmentTransaction ft = getFragmentManager().beginTransaction();

        Log.d("main", "go back helper two");
        //replace fragment and commit
        ft.replace(R.id.frame_user_screen, fragment).commit();
    }

    private void sendAlarmMessage(String message){
        if(use_watch)
            ((MainActivity)getActivity()).sendMessageToWear(message);

        if(use_glasses)
            ((MainActivity)getActivity()).sendMessageToVuzix(message);
    }

    private String getTimerHelper(){
        return timerHelper;
    }

    private void setTimerHelper(){
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
    }
}
