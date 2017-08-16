package com.equip.equip.Activities;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.equip.equip.DataStructures.Equipment;
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





    }

}
