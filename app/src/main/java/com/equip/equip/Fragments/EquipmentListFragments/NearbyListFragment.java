package com.equip.equip.Fragments.EquipmentListFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.equip.equip.Activities.DashboardActivity;
import com.equip.equip.DataStructures.Equipment;
import com.equip.equip.Fragments.EquipmentListFragments.BaseEquipmentListFragment;
import com.equip.equip.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NearbyListFragment extends BaseEquipmentListFragment {

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        //TODO filter by location
        Query query = databaseReference.child("equipment");
        return query;
    }


//    private class FabListener implements View.OnClickListener {
//
//        @Override
//        public void onClick(View v) {
//        //tODO go to another screen and junk
//            for (int i = 0; i < 10; i++){
//                Equipment equipment = new Equipment(i + "",
//                        "Item " + i,
//                        ((DashboardActivity) getActivity()).getmUser().getUid(),
//                        "",
//                        "Bike",
//                        null,
//                        true);
////                mDatabase.child("Equipment").child(i + "").setValue(equipment);
//                String key = mDatabase.child("equipment").push().getKey();
//                Map<String, Object> equipmentValues = equipment.toMap();
//
//                Map<String, Object> childUpdates = new HashMap<>();
//                childUpdates.put("/equipment/" + key, equipmentValues);
//                childUpdates.put("/user-equipment/" + equipment.uId + "/" + key, equipmentValues);
//                mDatabase.updateChildren(childUpdates);
//            }
//        }
//    }

//    private class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.ViewHolder> {
//
//        private Query mEquipmentQuery;
//        private Context mContext;
//        private ArrayList<Equipment> mEquipmentList;
//
//        public EquipmentAdapter(Query equipmentQuery, Context context){
//            this.mEquipmentQuery = equipmentQuery;
//            this.mContext = context;
//            mEquipmentList = new ArrayList<Equipment>();
//            ValueEventListener valueEventListener = new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    Equipment equipment = dataSnapshot.getValue(Equipment.class);
//                    mEquipmentList.add(equipment);
//                    notifyDataSetChanged();
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            };
//            mEquipmentReference.addValueEventListener(valueEventListener);
//        }
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.equipment_list_item, viewGroup, false);
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(ViewHolder holder, int position) {
//            //TODO use picasso to load image
//            Equipment equipment = mEquipmentList.get(position);
//        }
//
//        @Override
//        public int getItemCount() {
//            return mEquipmentList.size();
//        }
//
//        class ViewHolder extends RecyclerView.ViewHolder{
//            private ImageView mImage;
//
//            public ViewHolder(View itemView) {
//                super(itemView);
//                mImage = (ImageView) itemView.findViewById(R.id.equipment_image);
//            }
//        }
//    }

}
