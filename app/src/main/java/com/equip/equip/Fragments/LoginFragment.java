package com.equip.equip.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.equip.equip.Activities.LoginActivity;
import com.equip.equip.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by curtis on 5/24/17.
 */

public class LoginFragment extends Fragment {

    Button loginButton;
    EditText emailText;
    EditText passwordText;
    TextView createAccountText;
    TextView recoverPasswordText;

    public static final String TAG = "LoginFragment";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static LoginFragment getInstance(FirebaseAuth auth) {
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.mAuth = auth;
        return loginFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

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
        recoverPasswordText = (TextView) view.findViewById(R.id.recover_password_text);

        loginButton.setOnClickListener(new LoginListener(mAuth));
        createAccountText.setOnClickListener(new CreateAccountListener());
        recoverPasswordText.setOnClickListener(new RecoverPasswordListener());

        return view;
    }



    public void goToDashboard(){
        ((LoginActivity)getActivity()).goToDashboard();
    }

    /**
     * This listener will execute the login procedure
     */
    private class LoginListener implements View.OnClickListener {

        private FirebaseAuth mLoginAuth;

        public LoginListener(FirebaseAuth auth){
            super();
            mLoginAuth = auth;
        }

        @Override
        public void onClick(View v) {
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();
            if (email.equals("") || password.equals("")){
                Toast.makeText(getContext(), getString(R.string.error_blank_password_email), Toast.LENGTH_SHORT).show();
                return;
            }
            mLoginAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete" + task.isSuccessful());

                            if (!task.isSuccessful()){
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(getContext(), R.string.login_failed,
                                        Toast.LENGTH_SHORT).show();
                                //todo specific errors
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
            CreateAccountFragment createAccountFragment = CreateAccountFragment.getInstance(mAuth, LoginFragment.this);
            LoginFragment.this.getFragmentManager().beginTransaction()
                    .replace(R.id.login_fragment_container, createAccountFragment, CreateAccountFragment.TAG)
                    .addToBackStack(null)
                    .commit();
        }
    }

    //todo
    private class RecoverPasswordListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {

        }
    }

}
