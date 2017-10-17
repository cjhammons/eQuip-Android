package com.equip.equip.Fragments.RentalListFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.equip.equip.DataStructures.Equipment;
import com.equip.equip.Fragments.EquipmentListFragments.BaseEquipmentListFragment;
import com.equip.equip.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rental_list_item,  parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Log.d(LOG_TAG, "onBindViewHolder (" + position +")");

        }

        @Override
        public int getItemCount() {
            return mEquipmentList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            private ImageView mImage;

            private ViewHolder(View itemView) {
                super(itemView);
//                mImage = (ImageView) itemView.findViewById(R.id.equipment_image);
            }
        }
    }

}
