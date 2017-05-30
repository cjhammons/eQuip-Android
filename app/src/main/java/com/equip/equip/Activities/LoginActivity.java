package com.equip.equip.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobilehelper.auth.DefaultSignInResultHandler;
import com.amazonaws.mobilehelper.auth.IdentityManager;
import com.amazonaws.mobilehelper.auth.IdentityProvider;
import com.amazonaws.mobilehelper.auth.StartupAuthErrorDetails;
import com.amazonaws.mobilehelper.auth.StartupAuthResult;
import com.amazonaws.mobilehelper.auth.StartupAuthResultHandler;
import com.amazonaws.mobilehelper.auth.signin.CognitoUserPoolsSignInProvider;
import com.equip.equip.Fragments.LoginFragment;
import com.equip.equip.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.login_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            LoginFragment loginFragment = new LoginFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            loginFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.login_fragment_container, loginFragment).commit();
        }


        AWSMobileClient.initializeMobileClientIfNecessary(getApplicationContext());
        final IdentityManager identityManager =
                AWSMobileClient.defaultMobileClient().getIdentityManager();

        identityManager.addIdentityProvider(CognitoUserPoolsSignInProvider.class);

//        identityManager.doStartupAuth(this,
//                new StartupAuthResultHandler() {
//                    @Override
//                    public void onComplete(final StartupAuthResult authResults) {
//                        if (authResults.isUserSignedIn()) {
//                            final IdentityProvider provider = identityManager.getCurrentIdentityProvider();
//                            // If we were signed in previously with a provider indicate that to the user with a toast.
//                            Toast.makeText(LoginActivity.this, String.format("Signed in with %s",
//                                    provider.getDisplayName()), Toast.LENGTH_LONG).show();
//                        } else {
//                            // Either the user has never signed in with a provider before or refresh failed with a previously
//                            // signed in provider.
//
//                            // Optionally, you may want to check if refresh failed for the previously signed in provider.
//                            final StartupAuthErrorDetails errors = authResults.getErrorDetails();
//                            if (errors.didErrorOccurRefreshingProvider()) {
////                                Log.w(LOG_TAG, String.format(
////                                        "Credentials for Previously signed-in provider %s could not be refreshed.",
////                                        errors.getErrorProvider().getDisplayName()), errors.getProviderErrorException());
//                            }
//
//                            doMandatorySignIn(identityManager);
//                            return;
//                        }
//
//                        // Go to your main activity and finish your splash activity here
//                        goMain(LoginActivity.this);
//                    }
//                }, 2000);


    }

    private void doMandatorySignIn(final IdentityManager identityManager) {
        identityManager.signInOrSignUp(LoginActivity.this, new DefaultSignInResultHandler() {
            private final String LOG_TAG = "SignInResultsHandler";

            @Override
            public void onSuccess(final Activity callingActivity, final IdentityProvider provider) {
                if (provider != null) {
                    Log.d(LOG_TAG, String.format("User sign-in with %s provider succeeded",
                            provider.getDisplayName()));
                    Toast.makeText(callingActivity, String.format("Sign-in with %s succeeded.",
                            provider.getDisplayName()), Toast.LENGTH_LONG).show();
                }

                goMain(callingActivity);
            }

            @Override
            public boolean onCancel(final Activity callingActivity) {
                return true;
            }

//            /** Go to the main activity. */
//            private void goMain(final Activity callingActivity) {
//                /
//                callingActivity.startActivity(new Intent(callingActivity, MainActivity.class)
//                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//            }
        });
        LoginActivity.this.finish();
    }

    /** Go to the main activity. */
    private void goMain(final Activity callingActivity) {
        //TODO Go to dashboard on login
        callingActivity.startActivity(new Intent(callingActivity, DashboardActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        callingActivity.finish();
    }
}
