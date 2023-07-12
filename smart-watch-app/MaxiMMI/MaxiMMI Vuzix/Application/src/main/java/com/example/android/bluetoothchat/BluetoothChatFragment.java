/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bluetoothchat;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.common.logger.Log;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class BluetoothChatFragment extends Fragment {

    private static final String TAG = "BluetoothChatFragment";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;
    private Button mScreenButton;
    private String messageHolder;
    boolean isRunning = false;
    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothChatService mChatService = null;

    //Variables for the timer screen
    int currentProcessDuration;
    int synchDuration;
    String timerHelper;
    Handler handlerSecondOver;
    Timer caretakerSeconds;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }

        caretakerSeconds = new Timer();
    }


    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //Aktualiere den Timerstring nach Ablauf einer Sekunde
        handlerSecondOver = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                //Verbleibende Prozess Dauer um eine Sekunde verringern
                currentProcessDuration -= 1000;

                if(currentProcessDuration < 11000) {
                    mScreenButton.setBackgroundColor(Color.RED);
                    mScreenButton.setTextColor(Color.WHITE);
                }

                setTimerHelper();
                mScreenButton.setText(timerHelper);
                android.util.Log.d("AlarmActivity2", "Second: " + currentProcessDuration);

                if (currentProcessDuration >= 1000) {
                    startSecond();
                }
            }
        };

        return inflater.inflate(R.layout.fragment_bluetooth_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mConversationView = (ListView) view.findViewById(R.id.in);
        mOutEditText = (EditText) view.findViewById(R.id.edit_text_out);
        mSendButton = (Button) view.findViewById(R.id.button_send);
        mScreenButton = (Button) view.findViewById(R.id.screenButton);
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);

        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                View view = getView();
                if (null != view) {
                    TextView textView = (TextView) view.findViewById(R.id.edit_text_out);
                    String message = textView.getText().toString();
                    sendMessage(message);
                }
            }
        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(getActivity(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * Makes this device discoverable.
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }

    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();

            switch (msg.what) {

                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //mConversationArrayAdapter.add("                 "+writeMessage+"\n"+"\n"+"\n"+"\n");
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    Log.d("Message", readMessage);
                    setScreen(readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    /**
     * Establish connection with other divice
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bluetooth_chat, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.insecure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case R.id.discoverable: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }
        }
        return false;
    }

    //The screen is set if a message is received from the watch
    public void setScreen(String message) {
        switch (message) {

            //Versuch 1
            case alarmConstants.START:
                mScreenButton.setText("");
                mScreenButton.setBackgroundColor(Color.WHITE);
                caretakerSeconds.cancel();
                break;

            case alarmConstants.ALARM_A:
                mScreenButton.setText(message);
                mScreenButton.setBackgroundColor(Color.RED);
                mScreenButton.setTextColor(Color.WHITE);
                break;

            case alarmConstants.ALARM_B:
                mScreenButton.setText(message);
                mScreenButton.setBackgroundColor(Color.RED);
                mScreenButton.setTextColor(Color.WHITE);
                break;

            case alarmConstants.ALARM_C:
                mScreenButton.setText(message);
                mScreenButton.setBackgroundColor(Color.RED);
                mScreenButton.setTextColor(Color.WHITE);
                break;

            case alarmConstants.ALARM_D:
                mScreenButton.setText(message);
                mScreenButton.setBackgroundColor(Color.RED);
                mScreenButton.setTextColor(Color.WHITE);
                break;

            case alarmConstants.ALARM_OFF:
                mScreenButton.setText("");
                mScreenButton.setBackgroundColor(Color.WHITE);
                mScreenButton.setTextColor(Color.BLACK);
                break;

            //Versuch 2
            case alarmConstants.P_1_RUN:
                //mScreenButton.setText(message);
                messageHolder = message;
                mScreenButton.setBackgroundColor(Color.WHITE);
                mScreenButton.setTextColor(Color.BLACK);
                break;

            case alarmConstants.P_2_RUN:
                //mScreenButton.setText(message);
                messageHolder = message;
                mScreenButton.setBackgroundColor(Color.WHITE);
                mScreenButton.setTextColor(Color.BLACK);
                break;

            case alarmConstants.P_3_RUN:
                //mScreenButton.setText(message);
                messageHolder = message;
                mScreenButton.setBackgroundColor(Color.WHITE);
                mScreenButton.setTextColor(Color.BLACK);
                break;

            case alarmConstants.P_4_RUN:
                //mScreenButton.setText(message);
                messageHolder = message;
                mScreenButton.setBackgroundColor(Color.WHITE);
                mScreenButton.setTextColor(Color.BLACK);
                break;

            case alarmConstants.P_1_START:
                //if(isRunning) break;
                mScreenButton.setText(message);
                messageHolder = message;
                mScreenButton.setBackgroundColor(Color.WHITE);
                mScreenButton.setTextColor(Color.BLACK);
                break;

            case alarmConstants.P_2_START:
                //if(isRunning) break;
                mScreenButton.setText(message);
                messageHolder = message;
                mScreenButton.setBackgroundColor(Color.WHITE);
                mScreenButton.setTextColor(Color.BLACK);
                break;

            case alarmConstants.P_3_START:
                //if(isRunning) break;
                mScreenButton.setText(message);
                messageHolder = message;
                mScreenButton.setBackgroundColor(Color.WHITE);
                mScreenButton.setTextColor(Color.BLACK);
                break;

            case alarmConstants.P_4_START:
                //if(isRunning) break;
                mScreenButton.setText(message);
                messageHolder = message;
                mScreenButton.setBackgroundColor(Color.WHITE);
                mScreenButton.setTextColor(Color.BLACK);
                break;

            case alarmConstants.DONE:
                //if(isRunning) break;
                mScreenButton.setText("Bitte an die Versuchsleitung wenden.");
                messageHolder = message;
                mScreenButton.setBackgroundColor(Color.WHITE);
                mScreenButton.setTextColor(Color.BLACK);
                break;

            case alarmConstants.P_CRITICAL:
                //mScreenButton.setText(messageHolder);
                mScreenButton.setBackgroundColor(Color.RED);
                mScreenButton.setTextColor(Color.WHITE);
                break;

            default:
                String regexStr = "^[0-9]*$";
                if(message.matches(regexStr))
                {
                    //sets the process Duration if it is a numeric value
                    currentProcessDuration = Integer.parseInt(message)-synchDuration;
                    mScreenButton.setText("");

                    //and starts the countdown timer
                    startSecond();
                }
                else {
                    //mScreenButton.setText("_"+message);
                    //mScreenButton.setBackgroundColor(Color.WHITE);
                }
        }
    }

    //Sekundentimer
    private void startSecond() {
        TimerTask action = new TimerTask() {
            public void run() {
                Message msg = handlerSecondOver.obtainMessage();
                handlerSecondOver.sendMessage(msg);
            }
        };
        //if (currentProcessDuration <= 9000) isRunning = false;
        if(currentProcessDuration >= 0) {
            caretakerSeconds = new Timer();
            caretakerSeconds.schedule(action, 999);
        }
    }

    private void setTimerHelper(){
        //Prozessdauer im timer aktualisieren
        long seconds = TimeUnit.MILLISECONDS.toSeconds(currentProcessDuration) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentProcessDuration));

        if(TimeUnit.MILLISECONDS.toMinutes(currentProcessDuration)==0) {
            if ( seconds > 9 )
                timerHelper = String.format("00:%d", seconds);
            else
                timerHelper = String.format("00:0%d", seconds);
        }else{
            if ( seconds > 9 )
                timerHelper =
                        String.format("0%d:%d",
                                TimeUnit.MILLISECONDS.toMinutes(currentProcessDuration), seconds);
            else
                timerHelper =
                        String.format("0%d:0%d",
                                TimeUnit.MILLISECONDS.toMinutes(currentProcessDuration), seconds);
        }
    }
}
