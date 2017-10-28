package com.equip.equip.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.equip.equip.DataStructures.Equipment;
import com.equip.equip.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.ArrayList;

import static java.security.AccessController.getContext;

/**
 * Created by Curtis on 10/24/2017.
 */

public class UserDetailActivity extends Activity {

    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        Query query = mDatabaseReference.child("equipment").orderByChild("ownerId")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        EquipmentAdapter equipmentAdapter = new EquipmentAdapter(query);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.equipment_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(equipmentAdapter);
    }


    private class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.ViewHolder> {

        private static final String LOG_TAG = "EquipmentRecyclerAdapter";
        private Query mEquipmentQuery;
        private ArrayList<Equipment> mEquipmentList;

        private EquipmentAdapter(Query equipmentQuery){
            this.mEquipmentQuery = equipmentQuery;
            mEquipmentList = new ArrayList<>();
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final ArrayList<Integer> newElements = new ArrayList<>();
                    final ArrayList<Integer> removeElements = new ArrayList<>();
                    //TODO removing
                    for (DataSnapshot equipmentSnapshot: dataSnapshot.getChildren()) {
                        boolean add = true;
                        boolean remove = true;
                        Equipment equipment = equipmentSnapshot.getValue(Equipment.class);
                        if (equipment.getAvailable()) {
                            remove = false;
                        }
                        for (Equipment e : mEquipmentList){
                            if (e.getKey().equals(equipment.getKey())){
                                add = false;
                                break;
                            }
                        }
                        if (add) {
                            mEquipmentList.add(equipment);
                            newElements.add(mEquipmentList.indexOf(equipment));
                        } else if (remove){

                        }
                    }
                    for (Integer i : newElements){
                        notifyItemInserted(i);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mEquipmentQuery.addValueEventListener(valueEventListener);
        }


        @Override
        public EquipmentAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.equipment_list_item, viewGroup, false);
            return new EquipmentAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final EquipmentAdapter.ViewHolder holder, final int position) {
            Log.d(LOG_TAG, "onBindViewHolder (" + position + ")");
            final Equipment equipment = mEquipmentList.get(position);
            holder.mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserDetailActivity.this, EquipmentDetailActivity.class);
                    intent.putExtra("equipmentKey", equipment.getKey());
                    startActivity(intent);
                }
            });
            //Take first image's thumbnail. This can be changed
            String path = "equipment/" + equipment.getKey() + "/thumbnail_0.jpg";
            mStorageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    loadImage(holder, uri);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(LOG_TAG + ": load thumbnail", e.getMessage());
                }
            });


//            if (equipment.getImagePaths() == null) return;
//            int dimension = 350;
//            Picasso.with(getActivity())
//                    .load(equipment.getImagePaths().get(0))
//                    .resize(dimension, dimension)
//                    .centerCrop()
//                    .into(holder.mImage);
        }

        /**
         * Loads the image using picasso
         * @param holder
         * @param uri
         */
        void loadImage(EquipmentAdapter.ViewHolder holder, Uri uri){
            DisplayMetrics displayMetrics = new DisplayMetrics();
            UserDetailActivity.this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int dimension = (displayMetrics.widthPixels / 3);
            Picasso.with(UserDetailActivity.this)
                    .load(uri)
                    .resize(dimension, dimension)
                    .centerCrop()
                    .into(holder.mImage);
        }

        @Override
        public int getItemCount() {
            return mEquipmentList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            private ImageView mImage;

            private ViewHolder(View itemView) {
                super(itemView);
                mImage = (ImageView) itemView.findViewById(R.id.equipment_image);
            }
        }
    }
}
