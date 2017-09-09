package com.equip.equip.FirebaseServices;

import android.util.Log;

import com.equip.equip.DataStructures.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Curtis on 8/24/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIDService";
    private static String mToken = "";
    private static String mOldToken = "";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mOldToken = mToken;
        mToken = refreshedToken;
        if (user != null) {
            updateUserTokens();
        }

    }

    /**
     * Adds the current token to the current user's list of tokens.
     * Also looks for the old token in the user's list and removes it.
     */
    public static void updateUserTokens() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users/" +
                FirebaseAuth.getInstance().getCurrentUser().getUid());
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                //We remove the old tokens which have been refreshed
                user.removeToken(mOldToken);
                //Then add the new ones :D
                user.addToken(mToken);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("users/"+ user.getUserId(), user.toMap());
                ref.updateChildren(childUpdates);
                Log.d(TAG, "Token " + mToken + " added to " + user.userId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        userRef.addValueEventListener(valueEventListener);
    }
}
