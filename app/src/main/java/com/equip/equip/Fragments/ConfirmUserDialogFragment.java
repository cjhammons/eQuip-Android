package com.equip.equip.Fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;

import com.amazonaws.services.cognitoidentityprovider.model.CodeMismatchException;
import com.amazonaws.services.cognitoidentityprovider.model.ExpiredCodeException;
import com.equip.equip.R;



/**
 * Created by curtis on 5/28/17.
 */

public class ConfirmUserDialogFragment extends DialogFragment {


    private CognitoUserPool cognitoUserPool;

    private Button confirmButton;
    private EditText codeText;
    private TextView enterEmailText;
    private Button resendButton;

    private String email;
    private Fragment parentFragment;
    private CognitoUser user;


    static ConfirmUserDialogFragment newInstance(CognitoUser user, String email,
                                                 CognitoUserPool cognitoUserPool, Fragment parentFragment){
        ConfirmUserDialogFragment fragment = new ConfirmUserDialogFragment();
        fragment.user = user;
        fragment.email = email;
        fragment.cognitoUserPool = cognitoUserPool;
        fragment.parentFragment = parentFragment;
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
        resendButton = (Button) view.findViewById(R.id.resend_email_button);
        enterEmailText = (TextView) view.findViewById(R.id.enter_email_text);

        enterEmailText.setText(getString(R.string.enter_confirmation_code) + " " + email);

        confirmButton.setOnClickListener(new ConfirmListener());
        resendButton.setOnClickListener(new ResendListener());


        return view;
    }

    void resendCode(){
        VerificationHandler verificationHandler = new VerificationHandler() {
            @Override
            public void onSuccess(CognitoUserCodeDeliveryDetails verificationCodeDeliveryMedium) {
                Toast.makeText(getContext(), getString(R.string.confirmation_resent), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception exception) {
                String s = "";
                Log.e("Confirm resend error: ", exception.getMessage());
            }
        };

        user.resendConfirmationCodeInBackground(verificationHandler);

    }

    private class ConfirmListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // Callback handler for confirmSignUp API
            GenericHandler confirmationCallback = new GenericHandler() {

                @Override
                public void onSuccess() {
                    // User was successfully confirmed
                    ConfirmUserDialogFragment.this.dismiss();
                    if (parentFragment instanceof LoginFragment){
                        ((LoginFragment) parentFragment).goToDashboard();
                    } else {
                        Toast.makeText(ConfirmUserDialogFragment.this.getContext(), R.string.confirmation_success, Toast.LENGTH_LONG).show();
                        parentFragment.getFragmentManager().popBackStack();
                    }
                }

                @Override
                public void onFailure(Exception exception) {
                    // User confirmation failed. Check exception for the cause.
                    String toastMessage = getString(R.string.confirmation_failure);

                    if (exception instanceof CodeMismatchException){
                        toastMessage = getString(R.string.wrong_code_failure);
                    } else if (exception instanceof ExpiredCodeException){
                        toastMessage = getString(R.string.code_expired_exception);
                        //TODO give option to resend
                    }

                    Toast.makeText(ConfirmUserDialogFragment.this.getContext(), toastMessage, Toast.LENGTH_SHORT).show();
                }
            };

//            CognitoUser user = cognitoUserPool.getUser(email);
            user.confirmSignUpInBackground(codeText.getText().toString(), false, confirmationCallback);

        }
    }

    private class ResendListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            resendCode();
        }
    }


}