package com.equip.equip.Activities;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.equip.equip.DataStructures.Equipment;
import com.equip.equip.DataStructures.Reservation;
import com.equip.equip.DataStructures.User;
import com.equip.equip.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class EquipmentDetailActivity extends AppCompatActivity {

    private String TAG = "EquipmentDetail";

    private Equipment mEquipment;
    private User mOwner;
    private boolean mIsOwner = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        final String key = getIntent().getStringExtra("equipmentKey");
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("equipment/" + key);

        ValueEventListener equipmentListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEquipment = dataSnapshot.getValue(Equipment.class);
                if (mEquipment.getOwnerId()
                        .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    mIsOwner = true;
                }
                populateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(equipmentListener);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    void populateUI(){
        final ImageView image = (ImageView) findViewById(R.id.equipment_picture);
        TextView description = (TextView) findViewById(R.id.equipment_description);
        TextView title = (TextView) findViewById(R.id.equipment_title);
        TextView availableText = (TextView) findViewById(R.id.available_text);

        //todo
        title.setText("Equipment Title goes here");
//        if (mEquipment.getAvailable())
//            availableText.setText(getString(R.string.available).toUpperCase());
//        else
//            availableText.setText(getString(R.string.not_available).toUpperCase());

//        Button reserveButton = (Button) findViewById(R.id.reserve_button);
        FloatingTextButton reserveButton = (FloatingTextButton) findViewById(R.id.reserve_item_fab);
        if (mIsOwner) {
            reserveButton.setVisibility(View.GONE);
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String path = "equipment/" + mEquipment.getKey() + "/0.jpg";
        storageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                int dimensionx = 816;
                int dimensiony = 204;
                Picasso.with(EquipmentDetailActivity.this)
                        .load(uri)
//                        .resize(500, 204)
//                        .centerInside()
                        .fit()
                        .centerCrop()
                        .into(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG + "image download", e.getMessage());
            }
        });

        description.setText(mEquipment.getDescription());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("users/" + mEquipment.getOwnerId());

        ValueEventListener userEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mOwner = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(userEventListener);

        reserveButton.setOnClickListener(new ReserveButtonListener());
        if (!mEquipment.getAvailable()){
            availableText.setText(getString(R.string.not_available).toUpperCase());
            reserveButton.setEnabled(false);
            reserveButton.setVisibility(View.GONE);
            if (mIsOwner) {
//                findViewById(R.id.reserved_layout).setVisibility(View.VISIBLE);
//                DatabaseReference reserverRef = FirebaseDatabase.getInstance()
//                        .getReference()
//                        .child("users/" + mEquipment.getBorrowerId());
//
//                ValueEventListener listener = new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        User borrower = dataSnapshot.getValue(User.class);
//                        ((TextView)findViewById(R.id.reserved_by)).setText(" " + borrower.getEmail());
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                };
//                reserverRef.addListenerForSingleValueEvent(listener);
            }
        } else {
            availableText.setText(getString(R.string.available).toUpperCase());
        }
    }
    //TODO go to screen with custom fields, move this there
    private class ReserveButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Reservation reservation = new Reservation(mEquipment.getKey(), mEquipment.getOwnerId(), user.getUid());

            String dateTime = SimpleDateFormat.getDateTimeInstance().format(new Date()).toString();
            reservation.reserve(dateTime);

//            mEquipment.setBorrowerId(user.getUid());
            mEquipment.setAvailable(false);
            String key = databaseReference.child("reservations/").push().getKey();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/reservations/" + key, reservation.toMap());
            childUpdates.put("/equipment/" + mEquipment.getKey(), mEquipment.toMap());

            databaseReference.updateChildren(childUpdates);
            Toast.makeText(EquipmentDetailActivity.this, R.string.reserved, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}
