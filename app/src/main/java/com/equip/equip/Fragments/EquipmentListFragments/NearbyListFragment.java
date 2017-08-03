package com.equip.equip.Fragments.EquipmentListFragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;


public class NearbyListFragment extends BaseEquipmentListFragment {

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        //TODO filter by location or something
        return databaseReference.child("equipment");
    }
}
