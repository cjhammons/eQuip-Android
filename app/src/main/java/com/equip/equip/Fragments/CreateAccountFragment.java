package com.equip.equip.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.mobileconnectors.cognito.internal.util.StringUtils;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentityprovider.model.CodeDeliveryFailureException;
import com.amazonaws.services.cognitoidentityprovider.model.InternalErrorException;
import com.amazonaws.services.cognitoidentityprovider.model.InvalidPasswordException;
import com.amazonaws.services.cognitoidentityprovider.model.UsernameExistsException;
import com.equip.equip.EquipApplication;
import com.equip.equip.R;


public class CreateAccountFragment extends Fragment {

    public static final String TAG_FRAGMENT = "CreateAccountFragment";

    EditText emailText;
    EditText firstNameText;
    EditText lastNameText;
    EditText phoneNumberText;
    EditText passwordText;
    EditText confirmPasswordText;
    Button createAccountButton;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);

        emailText           = (EditText) view.findViewById(R.id.email_entry);
        firstNameText       = (EditText) view.findViewById(R.id.first_name_entry);
        lastNameText        = (EditText) view.findViewById(R.id.last_name_entry);
        phoneNumberText     = (EditText) view.findViewById(R.id.phone_entry);
        passwordText        = (EditText) view.findViewById(R.id.password_entry);
        confirmPasswordText = (EditText) view.findViewById(R.id.confirm_password_entry);
        createAccountButton = (Button)   view.findViewById(R.id.create_account_button);

        createAccountButton.setOnClickListener(new CreateAccountListener());

        return view;
    }

    /**
     * Listener for the create account button. Triggers the create account
     * logic with AWS.
     */
    class CreateAccountListener implements View.OnClickListener{
        public static final String ATTRIBUTE_FIRST_NAME = "given_name";
        public static final String ATTRIBUTE_LAST_NAME = "family_name";
        public static final String ATTRIBUTE_PHONE_NUMBER = "phone_number";
        public static final String ATTRIBUTE_EMAIL = "email";

        @Override
        public void onClick(View v) {

            if (!passwordText.getText().toString().equals(confirmPasswordText.getText().toString())){
                Toast.makeText(CreateAccountFragment.this.getContext(),
                        R.string.toast_passwords_do_not_match,
                        Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            final CognitoUserPool userPool = EquipApplication.getUserPool(CreateAccountFragment.this.getContext());


            // Create a CognitoUserAttributes object and add user attributes
            CognitoUserAttributes userAttributes = new CognitoUserAttributes();
            // Add the user attributes. Attributes are added as key-value pairs
            userAttributes.addAttribute(ATTRIBUTE_FIRST_NAME, firstNameText.getText().toString());
            userAttributes.addAttribute(ATTRIBUTE_LAST_NAME, lastNameText.getText().toString());
            // Adding user's phone number

            String phoneString = phoneNumberText.getText().toString();
            phoneString = "+" + phoneString.replaceAll("[^\\d.]", "");
            userAttributes.addAttribute(ATTRIBUTE_PHONE_NUMBER, phoneString);
            // Adding user's email address
            userAttributes.addAttribute(ATTRIBUTE_EMAIL, emailText.getText().toString());

            SignUpHandler signUpHandler = new SignUpHandler() {
                @Override
                public void onSuccess(CognitoUser user, boolean signUpConfirmationState, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
                    if (signUpConfirmationState){
                        return;
                    }
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.addToBackStack(null);
                    DialogFragment confirmUserFragment = ConfirmUserDialogFragment.newInstance(user, emailText.getText().toString(),
                                                                                                userPool, CreateAccountFragment.this);
                    confirmUserFragment.show(ft, "confirmDialog");
                }

                @Override
                public void onFailure(Exception exception) {
                    //TODO failure messages
                    String toastMessage = getString(R.string.an_error_occured);
                    if (exception instanceof UsernameExistsException){
                        toastMessage  = getString(R.string.email_already_in_use);
                    } else if (exception instanceof InternalErrorException){
                        toastMessage = getString(R.string.an_error_occured);
                    } else if (exception instanceof InvalidPasswordException) {
                        toastMessage = getString(R.string.invalid_password);
                    }

                    Log.e(TAG_FRAGMENT, exception.getMessage());
                    Toast.makeText(getContext(), toastMessage, Toast.LENGTH_LONG).show();
                }
            };

            userPool.signUpInBackground(emailText.getText().toString(),
                    passwordText.getText().toString(),
                    userAttributes,
                    null,
                    signUpHandler);

        }
    }

}
