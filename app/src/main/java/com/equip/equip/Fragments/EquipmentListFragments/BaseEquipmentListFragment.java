package com.equip.equip.Fragments.EquipmentListFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.equip.equip.DataStructures.Equipment;
import com.equip.equip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by curtis on 7/19/17.
 */

public abstract class BaseEquipmentListFragment extends Fragment {

    private RecyclerView mEquipmentList;

    private DatabaseReference mDatabase;
    private DatabaseReference mEquipmentReference;


    //TODO all this shit

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mEquipmentReference = mDatabase.child("equipment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard_list, container, false);
        mEquipmentList = (RecyclerView) view.findViewById(R.id.item_list);
        mEquipmentList.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mEquipmentList.setAdapter(new EquipmentAdapter(getQuery(mEquipmentReference), getContext()));
        return view;
    }

    private class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.ViewHolder> {

        private Query mEquipmentQuery;
        private Context mContext;
        private ArrayList<Equipment> mEquipmentList;
        private DatabaseReference mDatabase;
        private DatabaseReference mEquipmentReference;

        public EquipmentAdapter(Query equipmentQuery, Context context){
            this.mEquipmentQuery = equipmentQuery;
            this.mContext = context;
            mEquipmentList = new ArrayList<Equipment>();
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Equipment equipment = dataSnapshot.getValue(Equipment.class);
                    mEquipmentList.add(equipment);
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mEquipmentReference.addValueEventListener(valueEventListener);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.equipment_list_item, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //TODO use picasso to load image
            Equipment equipment = mEquipmentList.get(position);
        }

        @Override
        public int getItemCount() {
            return mEquipmentList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            private ImageView mImage;

            public ViewHolder(View itemView) {
                super(itemView);
                mImage = (ImageView) itemView.findViewById(R.id.equipment_image);
            }
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);
}
