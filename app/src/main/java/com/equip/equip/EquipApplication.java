package com.equip.equip;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.regions.Regions;
import com.equip.equip.Fragments.CreateAccountFragment;

/**
 * Created by curtis on 5/24/17.
 */

public class EquipApplication extends Application {
    private final static String LOG_TAG = Application.class.getSimpleName();

    public final static String COGNITO_POOL_ID       = "us-east-1_t00ZNIKzL";
    public final static String COGNITO_CLIENT_ID     = "6nohlnto0n1429snesnmpt2fr5";
    public final static String COGNITO_CLIENT_SECRET = "aeg03mqeaot3h8j29lt59qcut95qi5gj218401btj55p5ndl7td";

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "Application.onCreate - Initializing application...");
        super.onCreate();
        initializeApplication();
        Log.d(LOG_TAG, "Application.onCreate - Application initialized OK");
    }

    private void initializeApplication() {

        // Initialize the AWS Mobile Client
        AWSMobileClient.initializeMobileClientIfNecessary(getApplicationContext());

    }

    public static CognitoUserPool getUserPool(Context context) {

        ClientConfiguration clientConfiguration = new ClientConfiguration();

        return new CognitoUserPool(context,
                EquipApplication.COGNITO_POOL_ID,
                EquipApplication.COGNITO_CLIENT_ID,
                EquipApplication.COGNITO_CLIENT_SECRET,
                clientConfiguration,
                Regions.US_EAST_1);
    }
}
