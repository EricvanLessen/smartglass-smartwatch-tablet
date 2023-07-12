package com.example.eric.application;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

public class optionActivity extends AppCompatActivity{

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class variables                                                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    databaseHelper dbHelper;
    UserInputLog ui_Log;
    String strID;

    //TODO: Passwort festlegen
    String PASSWORT = "IAW12345";

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // activity methods, reaction on changes to the application                      //
    // the functions are self-explaining by their name                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get the parcelable object to move around data
        Bundle b = getIntent().getExtras();
        ui_Log = b.getParcelable(".hmi.UserInputLog");
        dbHelper = new databaseHelper(this);
        setContentView(R.layout.activity_option);
        EditText id_shower = (EditText) findViewById(R.id.editText_options_id_show);
        strID = Long.toString(ui_Log.getUser_id());
        id_shower.setText(strID);
    }

    public void OnClickConfirm1(View view) {

    }

    public void OnClickConfirm2(View view) {

    }

    public void OnClickConfirm3(View view) {

    }

    public void OnClickDatabase(View view) {

    }

    public void OnClickEmpty(View view) {
        if(askForPasswort())
            emptyDatabase(this);
    }

    public void OnClickExport(View view) {
        if(askForPasswort())
            exportDatabase(this);
    }

    public void OnClickGlasses(View view) {

    }

    public void OnClickHMI(View view) {

    }

    //TODO: Nice to have, function as parameter to confirm the delete, abstract or interface
    public void OnClickPart1(View view) {

    }

    public void OnClickPart2(View view) {

    }

    public void OnClickWatch(View view) {

    }

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class functions to provide the essential class functionality                  //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    private long getEditTextID(){
        EditText id_shower = (EditText) findViewById(R.id.editText_options_id_show);
        id_shower.getText();
        long result=0;
        try{
            result = (long) Integer.parseInt(id_shower.getText().toString());
        }catch(Exception e) {
            Log.e("Options", "Cant parse ID");
        }
        return result;
    }

    //exports the data base
    public void exportDatabase(Context context) {

        //Context context = getApplicationContext();
        String exportResult = dbHelper.exportDatabase(context, databaseHelper.TABLENAME);
        exportResult = exportResult +"\n"+ dbHelper.exportDatabase(context, databaseHelper.TABLENAME_V2);

        //set duration of toast
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, exportResult, duration);
        toast.show();
    }

    public void emptyDatabase(Context context) {

        EditText id_shower = (EditText) findViewById(R.id.editText_options_id_show);
        String emptyResult = dbHelper.emptyDatabase(id_shower.getText().toString(), databaseHelper.TABLENAME);
        emptyResult = emptyResult + "\n" + dbHelper.emptyDatabase(id_shower.getText().toString(), databaseHelper.TABLENAME_V2);

        //set duration of toast
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, emptyResult, duration);
        toast.show();
    }

    private boolean askForPasswort(){
        EditText editTextPasswort = (EditText) findViewById(R.id.editText_passwort);
        if(editTextPasswort.getText().toString() == PASSWORT)
            return true;

        //set duration of toast
        Toast toast = Toast.makeText(this, "Falsches Passwort.", Toast.LENGTH_LONG);
        toast.show();
        return false;
    }
}
