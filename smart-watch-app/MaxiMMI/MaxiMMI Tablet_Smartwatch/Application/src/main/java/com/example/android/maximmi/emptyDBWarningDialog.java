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

public class emptyDBWarningDialog extends DialogFragment {

    private Button buttonCancel, buttonConfirm;

    public emptyDBWarningDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empty_warning, container);
        buttonCancel = (Button) view.findViewById(R.id.button_emptyWarning_Cancel);
        buttonConfirm = (Button) view.findViewById(R.id.button_emptyWarning_Confirm);
        getDialog().setTitle("Tabelle leeren");

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
                ((MainActivity)getActivity()).emptyDatabase();
                getDialog().dismiss();
            }
        });

        return view;
    }
}