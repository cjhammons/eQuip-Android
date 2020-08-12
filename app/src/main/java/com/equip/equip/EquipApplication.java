package com.equip.equip;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


import com.equip.equip.Activities.CreateItemListingActivity;
import com.equip.equip.Fragments.CreateAccountFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stripe.android.PaymentConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by curtis on 5/24/17.
 */

public class EquipApplication extends Application {
    private final static String LOG_TAG = Application.class.getSimpleName();
    public static List<String> EQUIPMENT_CATEGORIES = new ArrayList<>();

    public static String ALGOLIA_APP_ID = "<appid>";
    public static String ALGOLIA_SEARCH_API_KEY = "<apikey>";
    public static String ALGOLIA_EQUIPMENT_INDEX_NAME = "EQUIPMENT";

    public static String STRIPE_PUBLISHABLE_KEY = "<stripekey>";

    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    EQUIPMENT_CATEGORIES.add(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
