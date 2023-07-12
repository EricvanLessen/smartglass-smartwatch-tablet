package com.example.eric.application;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class confirmationToStart extends AppCompatActivity {

    UserInputLog ui_Log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get the parcelable object to move around data
        Bundle b = getIntent().getExtras();
        ui_Log = b.getParcelable(".hmi.UserInputLog");

        TextView textView = (TextView) findViewById(R.id.textView_confirmToStart);

        //set the text due to part of the experiment
        if(ui_Log.MONITOR==ui_Log.getModalitaet()){
            textView.setText("Bitte bestätigen Sie um den Versuch zu beginnen.");
        }

        if(ui_Log.WATCH==ui_Log.getModalitaet()){
            textView.setText("Bitte ziehen Sie die Uhr an und bestätigen Sie um den Versuch zu beginnen.");
        }

        if(ui_Log.BRILLE==ui_Log.getModalitaet()){
            textView.setText("Bitte setzen Sie die Brille auf und bestätigen Sie um den Versuch zu beginnen.");
        }
        setContentView(R.layout.activity_confirmation_to_start);
    }

    //go to one of the alarm activities, wrt. to the Versuchsteil
    public void onClickButton_confirmToStart(View view) {
        Intent goToalarmActivity;
        if(ui_Log.getVersuch()==1) {
            goToalarmActivity = new Intent(this, alarmActivity.class);
        }else{
            goToalarmActivity = new Intent(this, alarmActivity2.class);
        }
        goToalarmActivity.putExtra(".hmi.UserInputLog", ui_Log);
        startActivity(goToalarmActivity);
    }
}
