package com.example.android.maximmi;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Methods which use SQLiteOpenHelper dbHelper Object e.g. delete.
 * Methods which change values and work on values; e.g. use put ...
 * */

public class databaseSource{

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'um' HH:mm:ss:SS");
    java.util.Date date1 = new java.util.Date();
    java.util.Date date2 = new java.util.Date();

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class variables                                                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    // Logcat tag
    private static final String LOG = "DatabaseSource";

    //Database is used by databaseHelper methods
    SQLiteDatabase database;

    //The Helper provides all methods which are not directly on the database but use SQLiteOpenHelper
    SQLiteOpenHelper dbHelper;

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // constructors, getters, setters                                                //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    public databaseSource(Context context){
        //create database
        dbHelper = new databaseHelper(context);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    //                 activity methods, reaction on changes to the application                      //
    //            the functions are self-explaining by their name                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    //Add user data by use of modified UI_Log
    public void create(UserInputLog ui_Log){

        ContentValues values = new ContentValues();

        if(ui_Log.getVersuch()==1) {
            values.put(databaseHelper.USER_ID, ui_Log.getUser_id());
            values.put(databaseHelper.VERSUCH, ui_Log.getVersuch());
            values.put(databaseHelper.MODALITÄT, ui_Log.getModalitaet());
            values.put(databaseHelper.ALARMTYP, ui_Log.getAlarmtyp());
            values.put(databaseHelper.CLICKEDTYP, ui_Log.getClickedButtonType());
            values.put(databaseHelper.POPUPTIME, ui_Log.getPopuptime());
            values.put(databaseHelper.CLICKTIME, ui_Log.getClicktime());
            values.put(databaseHelper.CLEARING, ui_Log.getClearing());
            database.insert(databaseHelper.TABLENAME_V1, null, values);
        }
        if(ui_Log.getVersuch()==2){
            values.put(databaseHelper.USER_ID, ui_Log.getUser_id());
            values.put(databaseHelper.VERSUCH, ui_Log.getVersuch());
            values.put(databaseHelper.MODALITÄT, ui_Log.getModalitaet());
            values.put(databaseHelper.PROZESS_ID, ui_Log.getProzess_id());
            values.put(databaseHelper.PROCESS_BLENDIN, ui_Log.getProcessBlendInTime());
            values.put(databaseHelper.CONFIRMATIONTIME, ui_Log.getConfirmationtime());
            values.put(databaseHelper.DELAY, ui_Log.getDelay());
            Log.d("created", "wrote get delay");
            database.insert(databaseHelper.TABLENAME_V2, null, values);
        }
        Log.e("Database Source", "ONE ROW INSERTED...");
    }

    //close database, here for logcat
    public void close(){
        if (database != null) {
            dbHelper.close();
        }
        Log.e(LOG, "Database closed");
    }

    //deletes data of a user
    public void deleteData(long User, String modalitaet, String Tablename) {

       String whereClause = databaseHelper.USER_ID + " = " + User
               + " AND " + databaseHelper.MODALITÄT + " = " + modalitaet;

        if(Tablename==databaseHelper.TABLENAME_V1) {
            database.delete(databaseHelper.TABLENAME_V1, whereClause, null);
        }

        if(Tablename==databaseHelper.TABLENAME_V2) {
            database.delete(databaseHelper.TABLENAME_V2, whereClause, null);
        }
    }

    //open database
    public void open(){
        database = dbHelper.getWritableDatabase();
        Log.e(LOG, "Database opened");
    }
}
