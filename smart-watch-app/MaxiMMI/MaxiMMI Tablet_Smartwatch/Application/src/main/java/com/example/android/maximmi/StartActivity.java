package com.example.android.maximmi;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.android.bluetoothchat.R;

public class StartActivity extends Fragment{


    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // activity methods, reaction on changes to the application                      //
    // the functions are self-explaining by their name                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    public StartActivity(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_start, container, false);

        //Listener for the button
        Button button = (Button) view.findViewById(R.id.id_button_start);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                button_startClickHandler();
            }
        });

        return view;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // click handlers for the buttons in this activity                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    //No special go back for this class as no data is passed
    public void button_startClickHandler() {

        //fragment transaction
        final FragmentTransaction ft = getFragmentManager().beginTransaction();

        //replace fragment and commit
        ft.replace(R.id.frame_user_screen, new idEingabe()).commit();
    }
}

