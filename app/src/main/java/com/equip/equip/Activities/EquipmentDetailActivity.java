package com.equip.equip.Activities;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class EquipmentDetailActivity extends AppCompatActivity {

    private String TAG = "EquipmentDetail";

    private Equipment mEquipment;
    private User mOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_detail);

        final String key = getIntent().getStringExtra("equipmentKey");
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("equipment/" + key);

        ValueEventListener equipmentListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEquipment = dataSnapshot.getValue(Equipment.class);
                populateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(equipmentListener);
    }

    void populateUI(){
        final ImageView image = (ImageView) findViewById(R.id.equipment_picture);
        TextView description = (TextView) findViewById(R.id.equipment_description);
        final TextView email = (TextView) findViewById(R.id.owner_email);
        Button reserveButton = (Button) findViewById(R.id.reserve_button);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String path = "equipment/" + mEquipment.getKey() + "/0.jpg";
        storageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                int dimension = 350;
                Picasso.with(EquipmentDetailActivity.this)
                        .load(uri)
                        .resize(dimension, dimension)
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
                email.setText(mOwner.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(userEventListener);

        reserveButton.setOnClickListener(new ReserveButtonListener());


    }

    private class ReserveButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Reservation reservation = new Reservation(mEquipment.getKey(), mEquipment.getOwnerId(), user.getUid());

            String dateTime = SimpleDateFormat.getDateTimeInstance().format(new Date()).toString();
            reservation.reserve(dateTime);

            mEquipment.setBorrowerId(user.getUid());
            mEquipment.setAvailable(false);

            String key = databaseReference.child("reservations").push().getKey();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/reservations/" + key, reservation.toMap());
            childUpdates.put("/equipment/" + mEquipment.getKey(), mEquipment.toMap());
            childUpdates.put("/user-equipment/"
                            + user.getUid()
                            + mEquipment.getKey(),
                            mEquipment.toMap());
            databaseReference.updateChildren(childUpdates);
            Toast.makeText(EquipmentDetailActivity.this, R.string.reserved, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}
