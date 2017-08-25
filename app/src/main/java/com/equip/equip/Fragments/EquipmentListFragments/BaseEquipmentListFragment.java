package com.equip.equip.Fragments.EquipmentListFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.equip.equip.Activities.EquipmentDetailActivity;
import com.equip.equip.DataStructures.Equipment;
import com.equip.equip.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by curtis on 7/19/17.
 *
 * This Fragment handles displaying equipment information. The only method that needs to be
 * extended is is getQuery(), which specifies how to filter the equipment entries in the database.
 */

public abstract class BaseEquipmentListFragment extends Fragment {

    private RecyclerView mEquipmentRecyclerView;

    private DatabaseReference mDatabaseReference;
    private EquipmentAdapter mEquipmentAdapter;
    private StorageReference mStorageReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard_list, container, false);
        mEquipmentRecyclerView = (RecyclerView) view.findViewById(R.id.item_list);
        mEquipmentRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mEquipmentAdapter = new EquipmentAdapter(getQuery(mDatabaseReference));
        mEquipmentRecyclerView.setAdapter(mEquipmentAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

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
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.equipment_list_item, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            Log.d(LOG_TAG, "onBindViewHolder (" + position + ")");
            final Equipment equipment = mEquipmentList.get(position);
            //TODO load equipment detail screen
            holder.mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), EquipmentDetailActivity.class);
                    intent.putExtra("equipmentKey", equipment.getKey());
                    startActivity(intent);
                }
            });
            //Take first image's thumbnail. This can be changed
            String path = "equipment/" + equipment.getKey() + "/thumbnail_0.jpg";
            mStorageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    int dimension = 350;
                    Picasso.with(getActivity())
                            .load(uri)
                            .resize(dimension, dimension)
                            .centerCrop()
                            .into(holder.mImage);
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
