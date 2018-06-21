package com.equip.equip.FirebaseServices;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String LOG_TAG = "FirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(LOG_TAG, "from: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(LOG_TAG, "Message payload: " + remoteMessage.getData());
        }
        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
