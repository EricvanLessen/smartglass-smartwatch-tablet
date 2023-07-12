package com.example.android.maximmi;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.android.bluetoothchat.R;

/**
 * Description of the class:
 */

public class continueActivity extends Fragment {

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class variables                                                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    UserInputLog ui_Log;
    Button buttonContinue;

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // activity methods, reaction on changes to the application                      //
    // the functions are self-explaining by their name                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    //do this on start
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ui_Log = getArguments().getParcelable("uiLog");
        View view = inflater.inflate(R.layout.activity_continue, container, false);

        buttonContinue = (Button) view.findViewById(R.id.id_button_fortfahren);

        //onClickListener
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               button_continue_ClickHandler();
            }
        });
        return view;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // click handlers for the buttons in this activity                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    public void button_continue_ClickHandler() {
        Bundle b = new Bundle();
        b.putParcelable("uiLog", ui_Log);

        //add the bundle to the fragment
        modification_select fragment = new modification_select();
        fragment.setArguments(b);

        //fragment transaction
        final FragmentTransaction ft = getFragmentManager().beginTransaction();

        //replace fragment and commit
        ft.replace(R.id.frame_user_screen, fragment).commit();
    }
}
