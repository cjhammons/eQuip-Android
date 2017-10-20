package com.equip.equip.Fragments.RentalListFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.equip.equip.Activities.EquipmentDetailActivity;
import com.equip.equip.DataStructures.Equipment;
import com.equip.equip.DataStructures.User;
import com.equip.equip.Fragments.EquipmentListFragments.BaseEquipmentListFragment;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Curtis on 9/23/2017.
 *
 * This abstract Fragment handles displaying a user's rental information. getQuery() is implemented
 * in the extending classes to determine how the rentals are filtered. Similar to BaseEquipmentListFragment
 */

public abstract class BaseRentalFragment extends Fragment {

    private RecyclerView mRentalRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private DatabaseReference mDatabaseReference;
    private RentalAdapter mRentalAdapter;
    private StorageReference mStorageReference;
    private Query mQuery;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mQuery = getRentalQuery(mDatabaseReference);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmenet_rental_list, container, false);
        mRentalRecyclerView = (RecyclerView) view.findViewById(R.id.rental_list);
        mRentalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRentalAdapter = new RentalAdapter(mQuery);
        mRentalRecyclerView.setAdapter(mRentalAdapter);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout_rental);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRentalAdapter.notifyDataSetChanged();
                //todo
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;
    }

    public abstract Query getRentalQuery(DatabaseReference databaseReference);

    public abstract Fragment getFragmentInstance();

    private class RentalAdapter extends RecyclerView.Adapter<RentalAdapter.ViewHolder> {

        private static final String LOG_TAG = "RentalRecyclerAdapter";
        private Query mRentalQuery;
        private ArrayList<Equipment> mEquipmentList;

        private RentalAdapter(Query rentalQuery){
            this.mRentalQuery = rentalQuery;
            mEquipmentList = new ArrayList<>();
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final ArrayList<Integer> newElements = new ArrayList<>();
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
            mRentalQuery.addValueEventListener(valueEventListener);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rental_list_item,  parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Log.d(LOG_TAG, "onBindViewHolder (" + position +")");
            final Equipment equipment = mEquipmentList.get(position);
            holder.mRoot.setOnClickListener(new View.OnClickListener() {
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
                    loadImage(holder, uri);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(LOG_TAG + ": load thumbnail", e.getMessage());
                }
            });

            holder.mEquipmentTitle.setText(equipment.getName());
            holder.mDueDate.setText(equipment.getDueDate());

            DatabaseReference ownerRef = FirebaseDatabase.getInstance().getReference().child("users/" + equipment.ownerId);
            ownerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User owner = dataSnapshot.getValue(User.class);
                    holder.mRentedFrom.setText(owner.muhDisplayName());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if (getFragmentInstance() instanceof CurrentRentalsFragment) {
                holder.mReturnButton.setVisibility(View.VISIBLE);
                holder.mReturnButton.setOnClickListener(new ReturnButtonListener());
            } else {
                holder.mReturnButton.setVisibility(View.GONE);
            }

        }


        /**
         * Loads the image using picasso
         * @param holder
         * @param uri
         */
        void loadImage(ViewHolder holder, Uri uri){
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int dimension = (displayMetrics.widthPixels / 3);
            Picasso.with(getActivity())
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
            private RelativeLayout mRoot;
            private ImageView mImage;
            private TextView mEquipmentTitle;
            private TextView mRentedFrom;
            private TextView mDueDate;
            private Button mReturnButton;

            private ViewHolder(View itemView) {
                super(itemView);
                mRoot = (RelativeLayout) itemView.findViewById(R.id.root);
                mImage = (ImageView) itemView.findViewById(R.id.equipment_image);
                mEquipmentTitle = (TextView) itemView.findViewById(R.id.equipment_title);
                mRentedFrom = (TextView) itemView.findViewById(R.id.rented_from_text);
                mDueDate = (TextView) itemView.findViewById(R.id.due_text);
                mReturnButton = (Button) itemView.findViewById(R.id.return_button);
            }
        }
    }

    private class ReturnButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

        }
    }

}
