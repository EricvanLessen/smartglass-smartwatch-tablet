package com.example.eric.application;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class modification_select extends AppCompatActivity {

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class variables                                                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    UserInputLog ui_Log;

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // activity methods, reaction on changes to the application                      //
    // the functions are self-explaining by their name                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onBackPressed() {
        //backpressing is deactivated
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modification_select);

        //do all the load work
        loadActivity();
    }

    //do the work to load the activity
    private void loadActivity() {
        //Get the parcelable object to move around data
        Bundle b = getIntent().getExtras();
        ui_Log = b.getParcelable(".hmi.UserInputLog");

        if(ui_Log.getVersuch()==1) {
            Button buttonGoToVersuch = (Button) findViewById(R.id.button_goToVersuch1);
            buttonGoToVersuch.setVisibility(View.INVISIBLE);
            setLabel("Versuchsteil 1");
            buttonGoToVersuch = (Button) findViewById(R.id.button_goToVersuch2);
            buttonGoToVersuch.setVisibility(View.VISIBLE);
        }

        if(ui_Log.getVersuch()==2) {
            Button buttonGoToVersuch = (Button) findViewById(R.id.button_goToVersuch2);
            buttonGoToVersuch.setVisibility(View.INVISIBLE);
            setLabel("Versuchsteil 2");
            buttonGoToVersuch = (Button) findViewById(R.id.button_goToVersuch1);
            buttonGoToVersuch.setVisibility(View.VISIBLE);
        }
    }

    private void setLabel(String labelText) {
        TextView label = (TextView) findViewById(R.id.text_Kondition);
        label.setText(labelText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modification_select, menu);
        return true;
    }

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

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class functions to provide the essential class functionality                  //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    public void goToMonitorVersuch(View view) {
        ui_Log.setModalitaet(ui_Log.MONITOR);
        goToConfirmationToStart();

    }

    public void goToWatchVersuch(View view) {
        ui_Log.setModalitaet(ui_Log.WATCH);
        goToConfirmationToStart();
    }

    private void goToConfirmationToStart() {
        Intent confirmationToStart = new Intent(this, com.example.eric.application.confirmationToStart.class);
        confirmationToStart.putExtra(".hmi.UserInputLog", ui_Log);
        startActivity(confirmationToStart);
    }

    public void showSettings() {
        Intent settings = new Intent(this, optionActivity.class);
        settings.putExtra(".hmi.UserInputLog", ui_Log);
        startActivity(settings);
    }

    public void goToVersuch2(View view){

        ui_Log.setVersuch(2);

        loadActivity();
    }

    public void goToVersuch1(View view){

        ui_Log.setVersuch(1);

        loadActivity();
    }
}

