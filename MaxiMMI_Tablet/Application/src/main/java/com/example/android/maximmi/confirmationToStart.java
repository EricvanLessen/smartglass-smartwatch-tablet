package com.example.android.maximmi;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.bluetoothchat.R;

public class confirmationToStart extends Fragment {

    UserInputLog ui_Log;
    private TextView textView;

    //constructor since this a fragment
    public confirmationToStart(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get the parcelable object to move around data
        ui_Log = getArguments().getParcelable("uiLog");

        View view = inflater.inflate(R.layout.activity_confirmation_to_start, container, false);

        textView = (TextView) view.findViewById(R.id.textView_confirmToStart);
        //TODO set string button confirmation to start
        //set the text due to part of the experiment
        if(ui_Log.MONITOR.equals(ui_Log.getModalitaet())){
            textView.setText("Bitte bestätigen Sie um den Versuch zu beginnen.");
        }

        if(ui_Log.WATCH.equals(ui_Log.getModalitaet())){
            textView.setText("Bitte ziehen Sie die Uhr an und bestätigen Sie um den Versuch zu beginnen.");
        }

        if(ui_Log.BRILLE.equals(ui_Log.getModalitaet())){
            textView.setText("Bitte setzen Sie die Brille auf und bestätigen Sie um den Versuch zu beginnen.");
        }

        Button button_confirmToStart = (Button) view.findViewById(R.id.id_button_confirmToStart);
        //onClickListener
        button_confirmToStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButton_confirmToStart();
            }
        });

        return view;
    }

    public void onBackPressed() {
        //Send the bundle back to the caller
        Bundle b = new Bundle();

        //add the bundle to the fragment
        modification_select fragment = new modification_select();

        b.putParcelable("uiLog", ui_Log);
        fragment.setArguments(b);

        //fragment transaction
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //replace fragment and commit
        ft.replace(R.id.frame_user_screen, fragment).commit();
    }

    //go to one of the alarm activities, wrt. to the Versuchsteil
    public void onClickButton_confirmToStart() {

        //pass data
        Bundle b = new Bundle();
        b.putParcelable("uiLog", ui_Log);

        //fragment transaction
        final FragmentTransaction ft = getFragmentManager().beginTransaction();

        if(ui_Log.getVersuch()==1) {

            //add the bundle to the fragment
            alarmActivity fragment = new alarmActivity();
            fragment.setArguments(b);

            //replace fragment and commit
            ft.replace(R.id.frame_user_screen, fragment).commit();

        }else{

            //add the bundle to the fragment
            alarmActivity2 fragment = new  alarmActivity2();
            fragment.setArguments(b);

            //replace fragment and commit
            ft.replace(R.id.frame_user_screen, fragment).commit();
        }
    }
}
