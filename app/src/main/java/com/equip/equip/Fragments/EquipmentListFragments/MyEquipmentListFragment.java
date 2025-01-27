package com.equip.equip.Fragments.EquipmentListFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;

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
        return databaseReference.child("equipment").orderByChild("ownerId").equalTo(uId);
    }

    @Override
    public ArrayList<String> getFilteredIds() {
        return null;
    }

    @Override
    public Fragment getFragmentInstance() {
        return MyEquipmentListFragment.this;
    }
}
