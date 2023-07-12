/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.example.android.bluetoothchat;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.example.android.common.activities.SampleActivityBase;
import com.example.android.common.logger.Log;
import com.example.android.common.logger.LogFragment;
import com.example.android.common.logger.LogWrapper;
import com.example.android.common.logger.MessageOnlyLogFilter;
import com.example.android.maximmi.StartActivity;
import com.example.android.maximmi.UserInputLog;
import com.example.android.maximmi.alarmActivity;
import com.example.android.maximmi.alarmActivity2;
import com.example.android.maximmi.backDialog1;
import com.example.android.maximmi.backDialog2;
import com.example.android.maximmi.confirmationToStart;
import com.example.android.maximmi.databaseHelper;
import com.example.android.maximmi.databaseSource;
import com.example.android.maximmi.emptyDBWarningDialog;
import com.example.android.maximmi.idEingabe;
import com.example.android.maximmi.modification_select;
import com.example.android.maximmi.optionActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class MainActivity extends SampleActivityBase
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    ////////////////////////////////////////////////////////////////////////////////
    //                                                                            //
    //       Google Play Part                                                     //
    //                                                                            //
    ////////////////////////////////////////////////////////////////////////////////

    // for the connection to the Watch
    private com.google.android.gms.wearable.Node mNode;
    private GoogleApiClient mApiClient;
    private static final String DEVICE_MAIN = "DeviceMain";
    private static final String WEAR_PATH = "/message";
    public static final String optionCallerStart = "OC0";
    public static final String optionCallerIdEingabe = "OC1";
    public static final String optionCallerModificationSelect = "OC2";

    private void showBackDialogA1() {
        FragmentManager fm = getSupportFragmentManager();
        backDialog1 backdialog = new backDialog1();
        backdialog.show(fm, "backDialog1");
    }

    private void showBackDialogA2() {
        FragmentManager fm = getSupportFragmentManager();
        backDialog2 backdialog = new backDialog2();
        backdialog.show(fm, "backDialog2");
    }

    public void showEmptyDBWarningHelper(){
        FragmentManager fm = getSupportFragmentManager();
        emptyDBWarningDialog emptywarn = new emptyDBWarningDialog();
        emptywarn.show(fm, "emptyWarn");
    }

    //on connection to the wearable
    @Override
    public void onConnected(Bundle bundle) {
        Wearable.NodeApi.getConnectedNodes(mApiClient)
                .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                        for (com.google.android.gms.wearable.Node node : nodes.getNodes()) {

                            if (node != null && node.isNearby()) {
                                mNode = node;
                                Toast toast = Toast.makeText(getApplicationContext(),"Connected to" + node.getDisplayName(), Toast.LENGTH_SHORT);
                                toast.show();
                                //android.util.Log.d(DEVICE_MAIN, "Connected to" + node.getDisplayName());
                            }

                            if (mNode == null) {

                                Toast toast = Toast.makeText(getApplicationContext(), "Not connected!", Toast.LENGTH_SHORT);
                                toast.show();
                                //android.util.Log.d(DEVICE_MAIN, "Not connected!");
                            }
                        }
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    ////////////////////////////////////////////////////////////////////////////////
    //                                                                            //
    //       End Google Play Part                                                 //
    //                                                                            //
    ////////////////////////////////////////////////////////////////////////////////


    //Database
    databaseSource dataSource;
    databaseHelper dbHelper;

    public static final String TAG = "MainActivity";

    // Whether the Log Fragment is currently shown
    private boolean mLogShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            BluetoothChatFragment fragment = new BluetoothChatFragment();
            transaction.replace(R.id.sample_content_fragment, fragment, "BluetoothChatFragment");
            transaction.commit();
        }

        //Initialize mGoolgeAPIClient
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
            mApiClient.connect();

        //initialize database - this is done in the activity via interface
        dataSource = new databaseSource(this);
        dbHelper = new databaseHelper(this);

        sendMessageToWear("start");
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }
    @Override

    protected void onResume() {
        super.onResume();
        dataSource.open();
    }

    @Override
    protected void onStart() {
        super.onStart();
        dataSource.open();
        mApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dataSource.close();
        mApiClient.disconnect();
    }

    @Override
    public void onBackPressed()
    {
        Fragment currentFragment = this.getSupportFragmentManager().
                                            findFragmentById(R.id.frame_user_screen);

        if(currentFragment instanceof idEingabe)
            ((idEingabe) currentFragment).onBackPressed();

        if(currentFragment instanceof modification_select)
            ((modification_select) currentFragment).onBackPressed();

        if(currentFragment instanceof confirmationToStart)
        ((confirmationToStart) currentFragment).onBackPressed();

        if(currentFragment instanceof alarmActivity)
            showBackDialogA1();

        if(currentFragment instanceof alarmActivity2)
            showBackDialogA2();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //TODO: Turn off show settings
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
        logToggle.setVisible(findViewById(R.id.sample_output) instanceof ViewAnimator);
        logToggle.setTitle(mLogShown ? R.string.sample_hide_log : R.string.sample_show_log);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_toggle_log:
                mLogShown = !mLogShown;
                ViewAnimator output = (ViewAnimator) findViewById(R.id.sample_output);
                if (mLogShown) {
                    output.setDisplayedChild(1);
                } else {
                    output.setDisplayedChild(0);
                }
                supportInvalidateOptionsMenu();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Create a chain of targets that will receive log data */
    @Override
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());

        Log.i(TAG, "Ready");
    }

    public void viewSettings(){
        Fragment currentFragment = this.getSupportFragmentManager().
                findFragmentById(R.id.frame_user_screen);
        /**if(currentFragment instanceof StartActivity){
            goToOptions(optionCallerStart, null);
            return;
        }*/
        if(currentFragment instanceof idEingabe){
            goToOptions(optionCallerIdEingabe, ((idEingabe) currentFragment).getUiLog());
            return;
        }
        if(currentFragment instanceof modification_select){
            goToOptions(optionCallerModificationSelect, ((modification_select) currentFragment).getUiLog());
            return;
        }
        CharSequence text = "Nicht verfuegbar!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();
    }

    private void goToOptions(String backTag, UserInputLog uiLog){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle b = new Bundle();
        optionActivity fragment = new optionActivity();
        b.putString("backTag", backTag);
        if(uiLog!=null)
            b.putParcelable("uiLog", uiLog);
        fragment.setArguments(b);
        transaction.replace(R.id.frame_user_screen, fragment);
        transaction.commit();
    }

    public void emptyDatabase(String userID){
        String emptyResult;
        if(userID!=null)
            emptyResult =  dbHelper.emptyDatabase(userID);
        else
            emptyResult = "Löschen fehlgeschlagen, falsche userID.";
        //set duration of toast
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(this, emptyResult, duration);
        toast.show();
    }

    public void emptyDatabase(){
        String emptyResult = dbHelper.emptyDatabase();
        //set duration of toast
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(this, emptyResult, duration);
        toast.show();
    }

    public void exportDatabase(boolean backup){
        String exportResult = dbHelper.exportDatabase(this, backup);
        //set duration of toast
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(this, exportResult, duration);
        toast.show();
    }

    public void create(UserInputLog ui_Log){
        dataSource.create(ui_Log);
    }

    public void deleteData(long user, String modalitaet, String Tablename) {
        dataSource.deleteData(user, modalitaet, Tablename);
    }

    //connect to the watch
    public void watch_connect(){
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mApiClient.connect();
    }

    //sends a message to the wearable
    public void sendMessageToWear(String alarm){
        Log.d(WEAR_PATH, "in send");
        if(mNode != null && mApiClient != null){
            Wearable.MessageApi.sendMessage(mApiClient,
                mNode.getId(), WEAR_PATH, alarm.getBytes())
                    .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                                           @Override
                                           public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                               if (!sendMessageResult.getStatus().isSuccess()) {
                                                   Log.d(WEAR_PATH, "Failed message "
                                                           + sendMessageResult.getStatus().getStatusCode());
                                               } else {
                                                   Log.d(WEAR_PATH, "Message succeeded");
                                               }
                                           }
                                       }
                    );
        }
    }

    public void sendMessageToVuzix(String message){
        FragmentManager fm = getSupportFragmentManager();
        BluetoothChatFragment bt = (BluetoothChatFragment)fm.findFragmentByTag("BluetoothChatFragment");
        bt.sendMessage(message);
    }

    public void goBackFromActivity1(){
        Fragment currentFragment = this.getSupportFragmentManager().
                findFragmentById(R.id.frame_user_screen);
        ((alarmActivity)currentFragment).cancelTimers();
        ((alarmActivity)currentFragment).goBack();
    }

    public void goBackFromActivity2(){
        Fragment currentFragment = this.getSupportFragmentManager().
                findFragmentById(R.id.frame_user_screen);
        ((alarmActivity2)currentFragment).goBackHelper();
        ((alarmActivity2)currentFragment).goBack();
    }

    public boolean checkIfUserExist(int userID){
        return dbHelper.checkIfUserIdExist(userID);
    }
}
