package com.example.android.maximmi;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;

/**
 * Description of the class
 */

public class databaseHelper extends SQLiteOpenHelper {

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class variables                                                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    //label fuer die Ausgabe
    String dbColumnLabel1 = "KEY_ROW_ID,Benutzer Id,Versuch,Kondition," +
            "Alarmtyp,Alarmklick,Anzeigezeitpunkt,Bestätigungszeitpunkt,Korrektur";

    String dbColumnLabel2 = "KEY_ROW_ID,Benutzer Id, Versuch, Kondition," +
            " Prozess Id, Anzeigezeit, Startzeit, Reaktionszeit";

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    //Database name
    public static final String DATABASE_NAME = "database.db";

    //Table names
    protected static final String TABLENAME_V1 = "db_table_name_V1";
    protected static final String TABLENAME_V2 = "db_table_name_V2";

    // Common column names
    protected static final String KEY_ID = "id";
    protected static final String USER_ID = "user_id";
    protected static final String VERSUCH = "versuch";
    protected static final String MODALITÄT = "modalitaet";

    // Column names Versuch 1
    protected static final String ALARMTYP = "alarmtype";
    protected static final String CLICKEDTYP = "clickedtype";
    protected static final String POPUPTIME = "popup_time";
    protected static final String CLICKTIME = "click_time";
    protected static final String CLEARING = "clearing";

    // Column names Versuch 2
    protected static final String PROZESS_ID = "prozess_Id";
    protected static final String PROCESS_BLENDIN = "prozess_anzeigeZeitpunkt";
    protected static final String CONFIRMATIONTIME = "prozess_startzeit";
    protected static final String DELAY = "Reaktionszeit";

    //Table create statement
    private static final String TABLE_CREATE_V1 =
            "CREATE TABLE " + TABLENAME_V1 +" ("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + USER_ID + "  INTEGER, "
                    + VERSUCH + "  INTEGER, "
                    + MODALITÄT + " TEXT, "
                    + ALARMTYP + " TEXT, "
                    + CLICKEDTYP + " TEXT, "
                    + POPUPTIME + " TEXT, "
                    + CLICKTIME + " TEXT, "
                    + CLEARING + " TEXT "
                    + ");";

    //Table create statement
    private static final String TABLE_CREATE_V2 =
            "CREATE TABLE " + TABLENAME_V2 +" ("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + USER_ID + "  INTEGER, "
                    + VERSUCH + "  INTEGER, "
                    + MODALITÄT + " TEXT, "
                    + PROZESS_ID + " TEXT, "
                    + PROCESS_BLENDIN + " TEXT, "
                    + CONFIRMATIONTIME + " TEXT, "
                    + DELAY + " TEXT"
                    + ");";

    String currentDateTimeString;

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // constructors, getters, setters                                                //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    public databaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e(LOG, "Database created opened.");
    }

    // creating required tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_V1);
        db.execSQL(TABLE_CREATE_V2);
        Log.e(LOG, "Database finished.");
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // activity methods, reaction on changes to the application                      //
    // the functions are self-explaining by their name                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    //TODO: CHECK THIS USAGE?
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_V1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_V2);
        onCreate(db);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class functions to provide the essential class functionality                  //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    public String emptyDatabase(){
        String res = emptyDatabase(null, TABLENAME_V1);
        res += "\n" + emptyDatabase(null, databaseHelper.TABLENAME_V2);
        return res;
    }

    public String emptyDatabase(String userID){
        String res = emptyDatabase(userID, TABLENAME_V1);
        res += "\n" + emptyDatabase(userID, databaseHelper.TABLENAME_V2);
        return res;
    }

    //empties the data base
    public String emptyDatabase(String userID, String Tablename) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(Tablename, USER_ID + " = " + userID, null);

        } catch (Exception e) {
            //if there are any exceptions, return false
            return "Leeren der Tabelle fehlgeschlagen.";
        } finally {

        }

        if(userID != null)
            return "Löschen des Datensatzes mit ID " + userID + " erfolgreich.";
        //If there are no errors, return true.
        return "Leeren der Tabelle erfolgreich.";
    }

    public String  exportDatabase(Context context, boolean backup){
        String res = exportDatabase(context, this.TABLENAME_V1, backup);
        res += ", " + exportDatabase(context, this.TABLENAME_V2, backup);
        return res;
    }

    //exports the data base
    private String exportDatabase(Context context, String Tablename, boolean backup) {

        if(!(Tablename== TABLENAME_V1 || Tablename==TABLENAME_V2)){
            Log.e(LOG, "Tabelle exisitert nicht");
            return "Export fehlgeschlagen";
        }
        //DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        /**First of all we check if the external storage of the device is available for writing.
         * Remember that the external storage is not necessarily the sd card. Very often it is
         * the device storage.
         */

        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Log.e(LOG, "External storage unavailable");
            return "Export fehlgeschlagen";
        } else {
            //We use the Download directory for saving our .csv file.
            File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
                Log.e(LOG, "Ordner \" Download \" erstellt");
            }

            File file;
            PrintWriter printWriter = null;

            try {
                if (Tablename == TABLENAME_V1) {
                    if(backup) {
                        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                        file = new File(exportDir, currentDateTimeString + "_MaxiMMI_db_v1_backup.csv");
                    }
                    else
                        file = new File(exportDir, "MaxiMMI_db_v1.csv");
                } else {
                    if(backup) {
                        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                        file = new File(exportDir, currentDateTimeString + "_MaxiMMI_db_v2_backup.csv");
                    }
                    else
                        file = new File(exportDir, "MaxiMMI_db_v2.csv");
                }
                file.createNewFile();
                printWriter = new PrintWriter(new FileWriter(file));

                /**This is our database connector class that reads the data from the database.
                 * The code of this class is omitted for brevity.
                 */

                databaseSource dbcOurDatabaseConnector = new databaseSource(context);
                dbcOurDatabaseConnector.open(); //open the database for reading
                /**Let's read the first table of the database.
                 * getFirstTable() is a method in our DBCOurDatabaseConnector class which retrieves a Cursor
                 * containing all records of the table (all fields).
                 * The code of this class is omitted for brevity.
                 */

                Cursor curCSV = this.getCursor(Tablename);
                //Write the name of the table and the name of the columns (comma separated values) in the .csv file.
                printWriter = printLines(Tablename, curCSV, printWriter);

                curCSV.close();
                dbcOurDatabaseConnector.close();
            } catch (Exception e) {
                //if there are any exceptions, return false
                return "Export fehlgeschlagen. ";
            } finally {
                if (printWriter != null) printWriter.close();
            }
            //If there are no errors, return true.
            if (Tablename == TABLENAME_V1) {
                return "Datenbank zu Teil 1 exportiert";
            } else {
                return "Datenbank zu Teil 2 exportiert";
            }
        }
    }

    //Get Cursor to the database
    public Cursor getCursor(String Tablename){
        String query = "SELECT * FROM "+Tablename;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    //TODO: Wie sollen die Label heißen
    private PrintWriter printLines(String Tablename, Cursor curCSV, PrintWriter printWriter) {
        if (Tablename == TABLENAME_V1) {
            printWriter.println("Tabelle zu Versuch 1");
            printWriter.println(dbColumnLabel1);

            while (curCSV.moveToNext()) {
                Long key_rowid = curCSV.getLong(curCSV.getColumnIndex(KEY_ID));
                String user_id = curCSV.getString(curCSV.getColumnIndex(USER_ID));
                String versuch = curCSV.getString(curCSV.getColumnIndex(VERSUCH));
                String modalitaet = curCSV.getString(curCSV.getColumnIndex(MODALITÄT));
                String alarmtype = curCSV.getString(curCSV.getColumnIndex(ALARMTYP));
                String clickedtype = curCSV.getString(curCSV.getColumnIndex(CLICKEDTYP));
                String popup_time = curCSV.getString(curCSV.getColumnIndex(POPUPTIME));
                String click_time = curCSV.getString(curCSV.getColumnIndex(CLICKTIME));
                String clearing = curCSV.getString(curCSV.getColumnIndex(CLEARING));

                /**Create the line to write in the .csv file.
                 * We need a String where values are comma separated.
                 * The field date (Long) is formatted in a readable text.
                 */

                String record = key_rowid + "," + user_id + "," + versuch + "," + modalitaet + "," + alarmtype
                        + "," + clickedtype + "," + popup_time + "," + click_time + "," + clearing;
                printWriter.println(record); //write the record in the .csv file
            }
            //Tabelle 2 soll exportiert werden
        } else {
            printWriter.println("Tabelle zu Versuch 2");
            printWriter.println(dbColumnLabel2);

            while (curCSV.moveToNext()) {
                Long key_rowid_V2 = curCSV.getLong(curCSV.getColumnIndex(KEY_ID));
                String user_id = curCSV.getString(curCSV.getColumnIndex(USER_ID));
                String versuch_V2 = curCSV.getString(curCSV.getColumnIndex(VERSUCH));
                String modalitaet_V2 = curCSV.getString(curCSV.getColumnIndex(MODALITÄT));
                String process_Id_V2 = curCSV.getString(curCSV.getColumnIndex(PROZESS_ID));
                String process_blendIn_V2 = curCSV.getString(curCSV.getColumnIndex(PROCESS_BLENDIN));
                String confirmationtime_V2 = curCSV.getString(curCSV.getColumnIndex(CONFIRMATIONTIME));
                String delay_V2 = curCSV.getString(curCSV.getColumnIndex(DELAY));

                /**Create the line to write in the .csv file.
                 * We need a String where values are comma separated.
                 * The field date (Long) is formatted in a readable text.
                 */

                String record = key_rowid_V2 + "," + user_id + "," + versuch_V2 + "," + modalitaet_V2 + ","
                        + process_Id_V2 + "," + process_blendIn_V2 + "," + confirmationtime_V2
                        + "," + delay_V2;
                printWriter.println(record); //write the record in the .csv file
            }
        }
        return printWriter;
    }

    public boolean checkIfUserIdExist(int userId){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLENAME_V1 + " WHERE " + USER_ID + "= '" + userId + "'", null);
        if ( c==null )
            return false;
        else
            return true;
    }
}
