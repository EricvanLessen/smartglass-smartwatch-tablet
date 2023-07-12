package com.example.android.maximmi;

import android.support.v4.app.Fragment;
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

import com.example.android.bluetoothchat.MainActivity;
import com.example.android.bluetoothchat.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Description of the class:
 * This class provides the functionality during the first part of the experiment.
 * The
 */

public class alarmActivity extends Fragment {

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class variables                                                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////


    // variables for the alarm
    private AlarmProvider alarm = new AlarmProvider();
    private int [] type = alarm.getAlarmType();
    private int [] delay = alarm.getDelay();
    private boolean alarmOn;

    // log data which is passed between activities
    UserInputLog ui_Log;

    //helper variables to
    private final String ALARM_A = "A";
    private final String ALARM_B = "B";
    private final String ALARM_C = "C";
    private final String ALARM_D = "D";

    //Timer für den Alarm
    Timer caretaker, caretaker1, caretaker2, caretaker3, caretaker4;

    //date for log of popup and click time
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'um' HH:mm:ss:SS");

    private Button id_button_alarm, id_button_alarm_a_beheben, id_button_alarm_b_beheben,
                                    id_button_alarm_c_beheben, id_button_alarm_d_beheben;
    private TextView textView_fehlerbehoben, alarm_beheben;

    //include wearable? This variable determines where to send the alarm message
    boolean use_watch;
    boolean use_glasses;


    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // handler on behaviour of functions                                             //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    //constructor
    public alarmActivity(){

    }

    //Handler for the Timer, action on Alarm
    Handler handlerAlarmOn = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //
            if(getAlarmOn())
                return;

            //TextView textView = (TextView) findViewById(R.id.textView2);
            //String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            // textView is the TextView view that should display it
            //textView.setText( textView.getText() + currentDateTimeString +" "+ msg.arg1 + "\n");
            Button button = id_button_alarm;
            button.setBackgroundColor(0xffff0000);

            switch(msg.arg1){
                case 0:{
                    ui_Log.setAlarmtyp(ALARM_A);
                    //textView.setText(textView.getText() + " " + get_alarmType() + "\n");
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


            //Main Activity calls the Method from Bluetooth
            switch(ui_Log.getAlarmtyp()){
                case "A":
                    sendAlarmMessage(alarmConstants.ALARM_A);
                    break;
                case "B":
                    sendAlarmMessage(alarmConstants.ALARM_B);
                    break;
                case "C":
                    sendAlarmMessage(alarmConstants.ALARM_C);
                    break;
                case "D":
                    sendAlarmMessage(alarmConstants.ALARM_D);
                    break;
                default:
                    break;
            }

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
            TextView tv = textView_fehlerbehoben;
            if(getAlarmOn()){
                tv.setText(R.string.fehler_nicht_behoben);
            }else {
                tv.setText(R.string.fehler_behoben);
                sendAlarmMessage(alarmConstants.ALARM_OFF);
            }
            tv.setVisibility(View.VISIBLE);

            alarm_beheben.setVisibility(View.INVISIBLE);
            id_button_alarm.setVisibility(View.INVISIBLE);
            id_button_alarm_a_beheben.setVisibility(View.INVISIBLE);
            id_button_alarm_b_beheben.setVisibility(View.INVISIBLE);
            id_button_alarm_c_beheben.setVisibility(View.INVISIBLE);
            id_button_alarm_d_beheben.setVisibility(View.INVISIBLE);
        }
    };

    //Handler fades out the feedback about the alarm correction
    Handler handlerCorrectionFadeOut = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            TextView tv = textView_fehlerbehoben;
            tv.setVisibility(View.INVISIBLE);
            alarm_beheben.setVisibility(View.VISIBLE);
            id_button_alarm_a_beheben.setVisibility(View.VISIBLE);
            id_button_alarm_b_beheben.setVisibility(View.VISIBLE);
            id_button_alarm_c_beheben.setVisibility(View.VISIBLE);
            id_button_alarm_d_beheben.setVisibility(View.VISIBLE);

            if (getAlarmOn()){
                Button button = id_button_alarm;
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
        super.onStop();
    }

    //do this on start
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.activity_alarm, container, false);

        //initialise the private objects from the layout
        id_button_alarm = (Button) view.findViewById(R.id.id_button_alarm);
        textView_fehlerbehoben = (TextView) view.findViewById(R.id.id_fehler_behoben);
        alarm_beheben = (TextView) view.findViewById(R.id.textView_Alarm_beheben);
        id_button_alarm = (Button) view.findViewById(R.id.id_button_alarm);
        id_button_alarm_a_beheben = (Button) view.findViewById(R.id.id_button_alarm_a_beheben);
        id_button_alarm_b_beheben = (Button) view.findViewById(R.id.id_button_alarm_b_beheben);
        id_button_alarm_c_beheben = (Button) view.findViewById(R.id.id_button_alarm_c_beheben);
        id_button_alarm_d_beheben = (Button) view.findViewById(R.id.id_button_alarm_d_beheben);


        //Get the parcelable object to move around the input data
        ui_Log = getArguments().getParcelable("uiLog");

        use_watch   = ui_Log.getModalitaet().equals(ui_Log.WATCH);
        use_glasses = ui_Log.getModalitaet().equals(ui_Log.BRILLE);

        //this is the first part of the lab
        setTimers();
        setAlarmOn(false);

        //onClickListener
        id_button_alarm_a_beheben.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_alarm_AClickHandler();
            }
        });

        //onClickListener
        id_button_alarm_b_beheben.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_alarm_BClickHandler();
            }
        });

        //onClickListener
        id_button_alarm_c_beheben.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_alarm_CClickHandler();
            }
        });

        //onClickListener
        id_button_alarm_d_beheben.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_alarm_DClickHandler();
            }
        });

        sendAlarmMessage("START");

        return view;
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // click handlers for the buttons in this activity                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    //Click Handler for the Buttons
    public void button_alarm_AClickHandler() {
        ui_Log.setClickedButtonType(ALARM_A);
        button_alarm_ClickHandler_Helper();
    }

    //Click Handler for the Button
    public void button_alarm_BClickHandler() {
        ui_Log.setClickedButtonType(ALARM_B);
        button_alarm_ClickHandler_Helper();
    }

    //Click Handler for the Button
    public void button_alarm_CClickHandler() {
        ui_Log.setClickedButtonType(ALARM_C);
        button_alarm_ClickHandler_Helper();
    }

    //Click Handler for the Button
    public void button_alarm_DClickHandler() {
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
    private void addClickData() {
        ui_Log.setClicktime(sdf.format(new Date()));

        ((MainActivity) getActivity()).create(ui_Log);
    }

    //Helping Method for the Button Click Handler
    private void button_alarm_ClickHandler_Helper(){
        if(getAlarmOn()) {
            if (ui_Log.getAlarmtyp() == ui_Log.getClickedButtonType()) {
                setAlarmOn(false);
                id_button_alarm.setVisibility(View.INVISIBLE);
            }
            addClickData();
            showCorrection();
        }
    }

    //This is for on Back pressed
    public void cancelTimers(){
        caretaker.cancel();
        caretaker1.cancel();
        caretaker2.cancel();
        caretaker3.cancel();
        caretaker4.cancel();
        Log.i("Activity", "Timers canceled");
    }

    //handler helper to call the get supervisor to continue screen
    private void goToContinueActivity(){
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

    private void sendAlarmMessage(String message){
        if(use_watch)
            ((MainActivity)getActivity()).sendMessageToWear(message);

        if(use_glasses)
            ((MainActivity)getActivity()).sendMessageToVuzix(message);
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

    public void goBack(){
        Bundle b = new Bundle();
        b.putParcelable("uiLog", ui_Log);

        //add the bundle to the fragment
        modification_select fragment = new modification_select();
        fragment.setArguments(b);

        Log.d("main", "go back helper one");
        //fragment transaction
        final FragmentTransaction ft = getFragmentManager().beginTransaction();

        //replace fragment and commit
        ft.replace(R.id.frame_user_screen, fragment).commit();
    }
}

