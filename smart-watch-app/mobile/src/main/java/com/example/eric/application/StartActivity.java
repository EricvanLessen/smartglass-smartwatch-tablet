package com.example.eric.application;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class StartActivity extends AppCompatActivity{

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // activity methods, reaction on changes to the application                      //
    // the functions are self-explaining by their name                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // click handlers for the buttons in this activity                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    public void button_startClickHandler(View view) {
        Intent idEingabe = new Intent(this, idEingabe.class);
        startActivity(idEingabe);
    }
}

