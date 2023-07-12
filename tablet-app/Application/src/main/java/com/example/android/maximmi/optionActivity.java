package com.example.android.maximmi;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bluetoothchat.MainActivity;
import com.example.android.bluetoothchat.R;

/**
 imports unused:
 import java.io.File;
 import java.io.FileWriter;
 import java.io.PrintWriter;
 import android.widget.Button;
 import android.database.Cursor;
 import android.database.sqlite.SQLiteDatabase;
 import android.graphics.Color;
 import java.text.DateFormat;
 import java.util.Date;
 import java.util.Locale;
 import android.database.Cursor;
 import android.os.Environment;
 */

public class optionActivity extends Fragment{

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class variables                                                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    UserInputLog ui_Log;
    String strID;
    String backTag;

    //TODO: Passwort festlegen
    final String PASSWORT = "iaw123";

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // activity methods, reaction on changes to the application                      //
    // the functions are self-explaining by their name                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    private EditText id_shower;
    private EditText editTextPasswort;
    private Button button_goBack, buttonNeustart, buttonEmptyDB, buttonEmptyDBAll ,buttonExportDB, buttonExportDBBackup;

    //do this on start
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get the parcelable object to move around data
        //ui_Log = getArguments().getParcelable("uiLog");
        //dbHelper = new databaseHelper(this);
        backTag = getArguments().getString("backTag");

        ui_Log = getArguments().getParcelable("uiLog");

        View view = inflater.inflate(R.layout.activity_option, container, false);

        id_shower = (EditText) view.findViewById(R.id.editText_options_id_show);

        editTextPasswort = (EditText) view.findViewById(R.id.editText_passwort);

        strID = Long.toString(ui_Log.getUser_id());

        id_shower.setText(strID);

        button_goBack = (Button) view.findViewById(R.id.button_goBack);

        //onClickListener
        button_goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        buttonNeustart = (Button) view.findViewById(R.id.buttonNeustart);

        //onClickListener
        buttonNeustart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                neuStart();
            }
        });

        buttonEmptyDB = (Button) view.findViewById(R.id.buttonEmptyDB);

        //onClickListener
        buttonEmptyDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyDatabase();
            }
        });

        buttonExportDB = (Button) view.findViewById(R.id.buttonExportDB);

        //onClickListener
        buttonExportDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportDatabase();
            }
        });

        buttonEmptyDBAll = (Button) view.findViewById(R.id.buttonEmptyDBAll);

        //onClickListener
        buttonEmptyDBAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyDatabaseAll();
            }
        });

        buttonExportDBBackup = (Button) view.findViewById(R.id.buttonExportDBBackup);

        //onClickListener
        buttonExportDBBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportDatabaseBackup();
            }
        });

        return view;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class functions to provide the essential class functionality                  //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    //exports the data base
    public void exportDatabase() {
        if(askForPasswort())
            //Parameter for backup or no backup in export database
            ((MainActivity)getActivity()).exportDatabase(false);
    }

    //exports the data base
    public void exportDatabaseBackup() {
        if(askForPasswort())
            //Parameter for backup or no backup in export database
            ((MainActivity)getActivity()).exportDatabase(true);
    }

    public void emptyDatabase() {
        if(askForPasswort()) {
            ((MainActivity)getActivity()).emptyDatabase(id_shower.getText().toString());
        }
    }

    public void emptyDatabaseAll(){
        if(askForPasswort())
            ((MainActivity)getActivity()).showEmptyDBWarningHelper();
    }

    private boolean askForPasswort(){
        if(editTextPasswort.getText().toString().equals(PASSWORT))
            return true;

        //set duration of toast
        Toast toast = Toast.makeText(getActivity(), "Falsches Passwort.", Toast.LENGTH_LONG);
        toast.show();
        return false;
    }

    public void goBack(){
        Fragment fragment = detectFragment();
        Bundle modificationSelectUiLogBundle = new Bundle();

        if(backTag.equals(MainActivity.optionCallerModificationSelect)){
            modificationSelectUiLogBundle.putParcelable("uiLog", ui_Log);
            fragment.setArguments(modificationSelectUiLogBundle);
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_user_screen, fragment);
        transaction.commit();

    }

    public Fragment detectFragment(){
        if(backTag.equals(MainActivity.optionCallerIdEingabe)) {
            idEingabe fragment = new idEingabe();
            return fragment;
        }
        if(backTag.equals(MainActivity.optionCallerModificationSelect)) {
            modification_select fragment = new modification_select();
            return fragment;
        }else{
            StartActivity fragment = new StartActivity();
            return fragment;
        }
    }

    public void neuStart(){
        if(askForPasswort()) {
            StartActivity fragment = new StartActivity();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_user_screen, fragment);
            transaction.commit();
        }
    }
}
