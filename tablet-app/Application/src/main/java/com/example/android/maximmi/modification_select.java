package com.example.android.maximmi;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.bluetoothchat.MainActivity;
import com.example.android.bluetoothchat.R;


public class modification_select extends Fragment {

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class variables                                                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    UserInputLog ui_Log;
    private Button buttonGoToVersuch1, buttonGoToVersuch2;
    private TextView label;

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // activity methods, reaction on changes to the application                      //
    // the functions are self-explaining by their name                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    //constructor since this a fragment
    public modification_select(){

    }

    public void onBackPressed() {
            //No Back press here, go over settings
            /**
            //Neustarten?

            //fragment transaction
            final FragmentTransaction ft = getFragmentManager().beginTransaction();

            //add the bundle to the fragment
            StartActivity fragment = new StartActivity();

            //replace fragment and commit
            ft.replace(R.id.frame_user_screen, fragment).commit();
            */
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui_Log = getArguments().getParcelable("uiLog");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_modification_select, container, false);

        //set Button
        buttonGoToVersuch1 = (Button) view.findViewById(R.id.button_goToVersuch1);
        buttonGoToVersuch2 = (Button) view.findViewById(R.id.button_goToVersuch1);

        //setTExtView
        label = (TextView) view.findViewById(R.id.text_Kondition);

        //Listener for the buttons and initialize buttons to change visibility later on
        Button button_monitor = (Button) view.findViewById(R.id.button_monitor);
        button_monitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMonitorVersuch();
            }
        });

        Button button_smartwatch = (Button) view.findViewById(R.id.button_smartwatch);
        button_smartwatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToWatchVersuch();
            }
        });

        Button button_datenbrille = (Button) view.findViewById(R.id.button_datenbrille);
        button_datenbrille.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGlassesVersuch();
            }
        });

        Button button_goToVersuch1 = (Button) view.findViewById(R.id.button_goToVersuch1);
        button_goToVersuch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToVersuch1();
            }
        });
        this.buttonGoToVersuch1 = button_goToVersuch1;

        Button button_goToVersuch2 = (Button) view.findViewById(R.id.button_goToVersuch2);
        button_goToVersuch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToVersuch2();
            }
        });
        this.buttonGoToVersuch2 = button_goToVersuch2;

        //Watch out, all private variables need to be initialised before loadActivity gets called
        loadActivity();

        return view;
    }

    //do the work to load the activity
    private void loadActivity() {

        if(ui_Log.getVersuch()==1) {
            buttonGoToVersuch1.setVisibility(View.INVISIBLE);
            setLabel("Versuchsteil 1");
            buttonGoToVersuch2.setVisibility(View.VISIBLE);
            return;
        }

        if(ui_Log.getVersuch()==2) {
            buttonGoToVersuch2.setVisibility(View.INVISIBLE);
            setLabel("Versuchsteil 2");
            buttonGoToVersuch1.setVisibility(View.VISIBLE);
        }
    }

    private void setLabel(String labelText) {
        label.setText(labelText);
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class functions to provide the essential class functionality                  //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    public void goToMonitorVersuch() {
        ui_Log.setModalitaet(ui_Log.MONITOR);
        goToConfirmationToStart();
    }

    public void goToWatchVersuch() {
        ui_Log.setModalitaet(ui_Log.WATCH);
        goToConfirmationToStart();
    }

    public void goToGlassesVersuch() {
        ui_Log.setModalitaet(ui_Log.BRILLE);
        goToConfirmationToStart();
    }

    private void goToConfirmationToStart() {
        Bundle b = new Bundle();
        b.putParcelable("uiLog", ui_Log);

        //fragment transaction
        final FragmentTransaction ft = getFragmentManager().beginTransaction();

        //add the bundle to the fragment
        confirmationToStart fragment = new confirmationToStart();
        fragment.setArguments(b);

        //replace fragment and commit
        ft.replace(R.id.frame_user_screen, fragment).commit();
    }

    public void goToVersuch2(){

        ui_Log.setVersuch(2);

        loadActivity();
    }

    public void goToVersuch1(){

        ui_Log.setVersuch(1);

        loadActivity();
    }

    public UserInputLog getUiLog(){
        return ui_Log;
    }
}

