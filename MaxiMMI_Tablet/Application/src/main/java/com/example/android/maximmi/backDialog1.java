package com.example.android.maximmi;

/**
 * Created by Eric on 23.03.2016.
 */
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.android.bluetoothchat.MainActivity;
import com.example.android.bluetoothchat.R;
// ...

public class backDialog1 extends DialogFragment {

    private Button buttonCancel, buttonConfirm;

    public backDialog1() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_back_dialog_a1, container);
        buttonCancel = (Button) view.findViewById(R.id.button_alertDialog1_Cancel);
        buttonConfirm = (Button) view.findViewById(R.id.button_alertDialog1_Confirm);
        getDialog().setTitle("Abbrechen");

        //onClickListener
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        //onClickListener
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).goBackFromActivity1();
                getDialog().dismiss();
            }
        });

        return view;
    }
}