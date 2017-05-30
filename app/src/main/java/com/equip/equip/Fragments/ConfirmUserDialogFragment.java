package com.equip.equip.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.regions.Regions;

import com.equip.equip.EquipApplication;
import com.equip.equip.R;



/**
 * Created by curtis on 5/28/17.
 */

public class ConfirmUserDialogFragment extends DialogFragment {


    CognitoUser user;

    Button confirmButton;
    EditText codeText;
    TextView confirmationEmail;

    String email;

    static ConfirmUserDialogFragment newInstance(CognitoUser user, String email){
        ConfirmUserDialogFragment fragment = new ConfirmUserDialogFragment();
        fragment.user = user;
        fragment.email = email;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm_user, container, false);

        confirmButton = (Button) view.findViewById(R.id.confirm_user_button);
        codeText = (EditText) view.findViewById(R.id.confirm_user_code);
        confirmationEmail = (TextView) view.findViewById(R.id.confirmation_email);
        confirmationEmail.setText(email);

        confirmButton.setOnClickListener(new ConfirmListener());
        return view;
    }

    class ConfirmListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // Callback handler for confirmSignUp API
            GenericHandler confirmationCallback = new GenericHandler() {

                @Override
                public void onSuccess() {
                    // User was successfully confirmed
                    ConfirmUserDialogFragment.this.dismiss();
                    Toast.makeText(ConfirmUserDialogFragment.this.getContext(), R.string.confirmation_failure, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception exception) {
                    // User confirmation failed. Check exception for the cause.
                    Toast.makeText(ConfirmUserDialogFragment.this.getContext(), R.string.confirmation_failure, Toast.LENGTH_SHORT).show();
                }
            };

            ClientConfiguration clientConfiguration = new ClientConfiguration();

            // Create a CognitoUserPool object to refer to your user pool
            CognitoUserPool userPool = new CognitoUserPool(ConfirmUserDialogFragment.this.getContext(),
                    EquipApplication.COGNITO_POOL_ID,
                    EquipApplication.COGNITO_CLIENT_ID,
                    EquipApplication.COGNITO_CLIENT_SECRET,
                    clientConfiguration,
                    Regions.US_EAST_1);
             userPool.getUser(email).confirmSignUpInBackground(codeText.getText().toString(), false, confirmationCallback);

        }
    }


}