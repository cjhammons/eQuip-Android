package com.equip.equip.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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


import com.equip.equip.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateAccountFragment extends Fragment {

    public static final String TAG = "CreateAccountFragment";

    EditText emailText;
    EditText passwordText;
    EditText confirmPasswordText;
    Button createAccountButton;

    FirebaseAuth mAuth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static CreateAccountFragment getInstance(FirebaseAuth auth){
        CreateAccountFragment createAccountFragment = new CreateAccountFragment();
        createAccountFragment.mAuth = auth;
        return createAccountFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);

        emailText           = (EditText) view.findViewById(R.id.email_entry);

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
    private class CreateAccountListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }

}
