package com.equip.equip.Fragments.EquipmentListFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.equip.equip.Activities.EquipmentDetailActivity;
import com.equip.equip.DataStructures.Equipment;
import com.equip.equip.R;
import com.equip.equip.Util.location.LocationHelper;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
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
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private DatabaseReference mDatabaseReference;
    private EquipmentAdapter mEquipmentAdapter;
    private StorageReference mStorageReference;
    private Query mQuery;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mQuery = getQuery(mDatabaseReference);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard_list, container, false);
        mEquipmentRecyclerView = (RecyclerView) view.findViewById(R.id.item_list);
        mEquipmentRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mEquipmentAdapter = new EquipmentAdapter(mQuery);
        mEquipmentRecyclerView.setAdapter(mEquipmentAdapter);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mEquipmentAdapter.refresh();
                //todo
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;
    }

    public abstract Fragment getFragmentInstance();

    public abstract Query getQuery(DatabaseReference databaseReference);

    public abstract ArrayList<String> getFilteredIds();


    public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.ViewHolder> {

        private static final String LOG_TAG = "EquipmentRecyclerAdapter";
        private Query mEquipmentQuery;
        private ArrayList<Equipment> mEquipmentList;

        private EquipmentAdapter(Query equipmentQuery){
            mEquipmentList = new ArrayList<>();
            this.mEquipmentQuery = equipmentQuery;
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
                        } else {
                            add = false;
                        }

                        if (BaseEquipmentListFragment.this instanceof NearbyListFragment){
                            List filterIds = getFilteredIds();
                            if (filterIds.contains(equipment.key))
                                add = true;
                            else
                                remove = true;
                        } else {
                            if (equipment.getAvailable()) {
                                remove = false;
                            } else {
                                add = false;
                            }
                        }
                        if (add && !mEquipmentList.contains(equipment)) {
                            mEquipmentList.add(equipment);
                            newElements.add(mEquipmentList.indexOf(equipment));
                            Log.d("Equipment list", "Adding " + dataSnapshot.getKey());
                        } else if (remove && mEquipmentList.contains(equipment)){
                            removeElements.add(mEquipmentList.indexOf(equipment));
                            mEquipmentList.remove(equipment);
                            Log.d("Equipment list", "Removing " + dataSnapshot.getKey());

                        }
                    }
                    for (Integer i : newElements){
                        notifyItemInserted(i);
                    }
                    for (Integer i : removeElements){
                        notifyItemRemoved(i);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mEquipmentQuery.addValueEventListener(valueEventListener);
        }

        public void refresh(){
//            mEquipmentList = BaseEquipmentListFragment.this.getEquipment();
            notifyDataSetChanged();
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
                    loadImage(holder, uri);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(LOG_TAG + ": load thumbnail", e.getMessage());
                }
            });

            if (!equipment.getAvailable() && getFragmentInstance() instanceof MyEquipmentListFragment){
                holder.mReserveNotification.setVisibility(View.VISIBLE);
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
            private ImageView mImage;
            private ImageView mReserveNotification;

            private ViewHolder(View itemView) {
                super(itemView);
                mImage = (ImageView) itemView.findViewById(R.id.equipment_image);
                mReserveNotification = (ImageView) itemView.findViewById(R.id.reserve_notification);
            }
        }
    }

}
