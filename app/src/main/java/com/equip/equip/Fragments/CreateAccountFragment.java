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
import android.widget.TextView;
import android.widget.Toast;


import com.equip.equip.DataStructures.User;
import com.equip.equip.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountFragment extends Fragment {

    public static final String TAG = "CreateAccountFragment";

    EditText emailText;
    EditText passwordText;
    EditText confirmPasswordText;
    Button createAccountButton;
    TextView returnToLogin;

    FirebaseAuth mAuth;

    LoginFragment loginFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static CreateAccountFragment getInstance(FirebaseAuth auth, LoginFragment loginFragment){
        CreateAccountFragment createAccountFragment = new CreateAccountFragment();
        createAccountFragment.mAuth = auth;
        createAccountFragment.loginFragment = loginFragment;
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

        returnToLogin       = (TextView) view.findViewById(R.id.return_to_login);
        returnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        createAccountButton.setOnClickListener(new CreateAccountListener());

        return view;
    }

    /**
     * Listener for the create account button. Triggers the create account
     * logic with Firebase.
     */
    private class CreateAccountListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            final String email = emailText.getText().toString();
            String password = passwordText.getText().toString();
            String passwordConfirm = passwordText.getText().toString();

            if (!password.equals(passwordConfirm)) {
                Toast.makeText(getContext(), getString(R.string.toast_passwords_do_not_match), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!email.matches(emailRegex)){
                Toast.makeText(getContext(), getString(R.string.invalid_email_format), Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                User user = new User(currentUser.getUid(),
                                        email,
                                        "");
                                Map<String, Object> userValues = user.toMap();
                                final Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put("/users/" + currentUser.getUid(), userValues);

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.updateChildren(childUpdates);
                                loginFragment.goToDashboard();

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

    //God forgive me
    final String emailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
}
