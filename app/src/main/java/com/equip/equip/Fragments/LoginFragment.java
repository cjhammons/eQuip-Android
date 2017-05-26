package com.equip.equip.Fragments;

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

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
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
