package com.equip.equip.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
import com.amazonaws.services.cognitoidentityprovider.model.UserNotConfirmedException;
import com.equip.equip.Activities.DashboardActivity;
import com.equip.equip.EquipApplication;
import com.equip.equip.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

/**
 * Created by curtis on 5/24/17.
 */

public class LoginFragment extends Fragment {

    Button loginButton;
    EditText emailText;
    EditText passwordText;
    TextView createAccountText;

    public static final String TAG = "LoginFragment";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

    }

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

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void goToDashboard(){
        getActivity().startActivity(new Intent(getActivity(), DashboardActivity.class));
    }

    /**
     * This listener will execute the login procedure
     */
    private class LoginListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mAuth.signInWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "sighInWithEmail:onComplete" + task.isSuccessful());

                            if (!task.isSuccessful()){
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(getContext(), R.string.login_failed,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                goToDashboard();
                            }

                        }
                    });
        }

    }

    /**
     * This listener will launch the signup fragment
     */
    private class CreateAccountListener implements View.OnClickListener{

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
