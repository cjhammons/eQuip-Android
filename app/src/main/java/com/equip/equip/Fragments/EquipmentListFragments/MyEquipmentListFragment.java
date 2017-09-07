package com.equip.equip.Fragments.EquipmentListFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by Curtis on 8/21/2017.
 *
 * Implementation of BaseEquipmentListFragment filtering for the current user's
 * listed equipment.
 */

public class MyEquipmentListFragment extends BaseEquipmentListFragment {

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String query = "user-equipment/" + uId;
        return databaseReference.child(query);
    }

}
