package com.equip.equip.Fragments.EquipmentListFragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;


public class NearbyListFragment extends BaseEquipmentListFragment {
    //todo order by location or something idk
    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("equipment").orderByChild("available").equalTo(true);
    }
}
