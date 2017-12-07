package com.equip.equip.ExtraUIElements.SearchHitViews;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.algolia.instantsearch.ui.views.AlgoliaHitView;
import com.equip.equip.DataStructures.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by curtis on 12/5/17.
 */

public class OwnerNameHitView extends AppCompatTextView implements AlgoliaHitView {

    Context mContext;

    public OwnerNameHitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public void onUpdateView(JSONObject result) {
        String ownerId = "";

        try {
            ownerId = result.getString("ownerId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (ownerId.equals(""))
            return;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(ownerId);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String ownerName = user.getDisplayName();
                OwnerNameHitView.this.setText(ownerName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(valueEventListener);




    }
}
