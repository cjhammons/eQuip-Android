package com.equip.equip.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.equip.equip.Activities.DashboardActivity;
import com.equip.equip.EquipApplication;
import com.equip.equip.R;

/**
 * Created by curtis on 5/24/17.
 */

public class LoginFragment extends Fragment {

    Button loginButton;
    EditText emailText;
    EditText passwordText;
    TextView createAccountText;

    public static final String TAG_FRAGMENT = "LoginFragment";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        loginButton         = (Button)   view.findViewById(R.id.login_button);
        emailText           = (EditText) view.findViewById(R.id.email_entry);
        passwordText        = (EditText) view.findViewById(R.id.password_entry);
        createAccountText   = (TextView) view.findViewById(R.id.create_account_text);

        loginButton.setOnClickListener(new LoginListener());
        createAccountText.setOnClickListener(new CreateAccountListener());

        return view;
    }

    /**
     * This listener will execute the login procedure
     */
    class LoginListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            final String email = emailText.getText().toString();
            final String password = passwordText.getText().toString();

            CognitoUserPool cognitoUserPool = EquipApplication.getUserPool(LoginFragment.this.getContext());
            CognitoUser cognitoUser = cognitoUserPool.getUser(email);
            // Callback handler for the sign-in process
            AuthenticationHandler authenticationHandler = new AuthenticationHandler() {

                @Override
                public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice)                    // Sign-in was successful, cognitoUserSession will contain tokens for the user
                {
                    getActivity().startActivity(new Intent(getActivity(), DashboardActivity.class));
                }

                @Override
                public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                    // The API needs user sign-in credentials to continue
                    AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, password, null);

                    // Pass the user sign-in credentials to the continuation
                    authenticationContinuation.setAuthenticationDetails(authenticationDetails);

                    // Allow the sign-in to continue
                    authenticationContinuation.continueTask();
                }

                @Override
                public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
                    // Multi-factor authentication is required; get the verification code from user
//                    multiFactorAuthenticationContinuation.setMfaCode(mfaVerificationCode);
                    // Allow the sign-in process to continue
//                    multiFactorAuthenticationContinuation.continueTask();
                }

                @Override
                public void authenticationChallenge(ChallengeContinuation continuation) {

                }

                @Override
                public void onFailure(Exception exception) {
                    // Sign-in failed, check exception for the cause
                    //tODO exceptions and such
                    Toast.makeText(LoginFragment.this.getContext(), getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                }
            };

            // Sign in the user
            cognitoUser.getSessionInBackground(authenticationHandler);
        }
    }

    /**
     * This listener will launch the signup fragment
     */
    class CreateAccountListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            CreateAccountFragment createAccountFragment = new CreateAccountFragment();
            LoginFragment.this.getFragmentManager().beginTransaction()
                    .replace(R.id.login_fragment_container, createAccountFragment, CreateAccountFragment.TAG_FRAGMENT)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
