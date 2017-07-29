package com.equip.equip.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.equip.equip.DataStructures.Equipment;
import com.equip.equip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by curtis on 7/19/17.
 */

public class CreateItemListingActivity extends Activity {

    private EditText mDescriptionText;
    private Spinner mCategorySpinner;
    private ImageButton mAddPhotoButton;
    private Button mCreateListingButton;

    private List<String> mSpinnerList;

    private FirebaseUser mUser;
    private DatabaseReference mDatabase;

    public CreateItemListingActivity(){}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item_listing);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mSpinnerList = new ArrayList<>();
        mDatabase.child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mSpinnerList.add(snapshot.getValue().toString());
                }
                mCategorySpinner = (Spinner) findViewById(R.id.category_spinner);
                ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<CharSequence>(CreateItemListingActivity.this, android.R.layout.simple_spinner_item);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mCategorySpinner.setAdapter(spinnerAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDescriptionText = (EditText) findViewById(R.id.equipment_description_entry);

        mAddPhotoButton = (ImageButton) findViewById(R.id.add_photo_button);
        mAddPhotoButton.setOnClickListener(new AddPhotoListener());

        mCreateListingButton = (Button) findViewById(R.id.create_listing_button);
        mCreateListingButton.setOnClickListener(new CreateListingListener());

//        mCategorySpinner = (Spinner) findViewById(R.id.category_spinner);
//        ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mCategorySpinner.setAdapter(spinnerAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class AddPhotoListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //TODO launch camera
        }
    }

    private class CreateListingListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //TODO upload new listing to db
            Equipment equipment = new Equipment(mDescriptionText.getText().toString(),
                    mUser.getUid(),
                    "",
                    mCategorySpinner.getSelectedItem().toString(),
                    null,
                    true);
            String key = mDatabase.child("equipment").push().getKey();
            Map<String, Object> equipmentValues = equipment.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/equipment/" + key, equipmentValues);
            childUpdates.put("/user-equipment/" + key, equipmentValues);
            mDatabase.updateChildren(childUpdates);


        }
    }


}
