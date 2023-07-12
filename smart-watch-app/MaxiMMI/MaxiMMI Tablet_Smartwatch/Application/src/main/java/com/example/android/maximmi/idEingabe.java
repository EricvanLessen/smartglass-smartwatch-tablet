package com.example.android.maximmi;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bluetoothchat.MainActivity;
import com.example.android.bluetoothchat.R;

//TODO Make just one UI_Log class

public class idEingabe extends Fragment {

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class variables                                                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////



    //user data is saved here
    private UserInputLog ui_Log = new UserInputLog();

    private EditText userIDEdit;

    public idEingabe(){

    }

    public void onBackPressed(){
        //fragment transaction
        final FragmentTransaction ft = getFragmentManager().beginTransaction();

        //add the bundle to the fragment
        StartActivity fragment = new  StartActivity();

        //replace fragment and commit
        ft.replace(R.id.frame_user_screen, fragment).commit();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // activity methods, reaction on changes to the application                      //
    // the functions are self-explaining by their name                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_id_eingabe, container, false);


        //Listener for the button
        Button button = (Button) view.findViewById(R.id.id_Eingabe_Button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ClickOnIDButton();
            }
        });

        userIDEdit = (EditText) view.findViewById(R.id.id_Eingabe_EditText);

        return view;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class functions to provide the essential class functionality                  //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    private void ClickOnIDButton(){

        //Record user ID
        try{
            ui_Log.setUser_id((long) Integer.parseInt(userIDEdit.getText().toString()));

            //could be set in the class, done this way for further development options
            ui_Log.setVersuch(1);

            //TODO Check user ID
            if(!isNewID()) {
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getContext(), "Die ID existiert bereits.", duration);
                toast.show();
                return;
            }

            //create bundle to pass data
            Bundle b = new Bundle();
            b.putParcelable("uiLog", ui_Log);

            //fragment transaction
            final FragmentTransaction ft = getFragmentManager().beginTransaction();

            //add the bundle to the fragment
            modification_select fragment = new modification_select();
            fragment.setArguments(b);

            //replace fragment and commit
            ft.replace(R.id.frame_user_screen, fragment).commit();

        }catch(Exception e) {
            //set duration of toast
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getContext(), "Keine gueltige ID", duration);
            toast.show();
        }
    }

    private boolean isNewID(){
        return ( (MainActivity) getActivity() ).checkIfUserExist( (int)ui_Log.getUser_id() );
    }

    public UserInputLog getUiLog(){
        return ui_Log;
    }
}
