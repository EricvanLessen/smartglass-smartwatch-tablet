package com.example.eric.application;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

//TODO Make just one UI_Log class

public class idEingabe extends AppCompatActivity {

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class variables                                                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////


    //user data is saved here
    UserInputLog ui_Log = new UserInputLog();
    //UserInputLog2 ui_Log2 = new UserInputLog2();

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // activity methods, reaction on changes to the application                      //
    // the functions are self-explaining by their name                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_eingabe);
        //dataSource = new databaseSource(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_id_eingabe, menu);
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
            Log.e("IDEingabe", "Error in Settings");
        }
        return super.onOptionsItemSelected(item);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class functions to provide the essential class functionality                  //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    public void clickOnIDButton(View view){
        ClickOnIDButton_Helper();
    }

    private void ClickOnIDButton_Helper(){
        EditText userID = (EditText) findViewById(R.id.id_Eingabe_EditText);
        //Record user ID
        try{
            ui_Log.setUser_id((long) Integer.parseInt(userID.getText().toString()));

            //could be set in the class, done this way for further development options
            ui_Log.setVersuch(1);


            if(isNewID(ui_Log.getUser_id())){
                //parcelable data
                Intent modificationSelect = new Intent(this, modification_select.class);
                modificationSelect.putExtra(".hmi.UserInputLog",  ui_Log);
                startActivity(modificationSelect);
            }
        }catch(Exception e) {
            Log.e("IDEingabe", "Cant parse ID");
        }
    }

    private boolean isNewID(long ID){
        //TODO: Check if ID is unused
        //dont overwrite former data, check if this a an unused ID

        return true;
    }

    private void showSettings() {
        ClickOnIDButton_Helper();
        Intent settings = new Intent(this, optionActivity.class);
        settings.putExtra(".hmi.UserInputLog",  ui_Log);
        startActivity(settings);
    }
}
