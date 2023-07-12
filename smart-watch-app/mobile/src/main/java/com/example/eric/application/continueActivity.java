package com.example.eric.application;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Description of the class:
 */

public class continueActivity extends AppCompatActivity {

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
    protected void onCreate(Bundle savedInstanceState) {
        Bundle b = getIntent().getExtras();
        ui_Log = b.getParcelable(".hmi.UserInputLog");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_continue, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // click handlers for the buttons in this activity                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    public void button_continue_ClickHandler(View view) {
        Intent modificationSelect = new Intent(this, modification_select.class);
        modificationSelect.putExtra(".hmi.UserInputLog", ui_Log);
        startActivity(modificationSelect);
    }
}
