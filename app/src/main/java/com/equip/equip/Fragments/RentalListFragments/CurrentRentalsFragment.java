package com.equip.equip.Fragments.RentalListFragments;

import android.support.v4.app.Fragment;

import com.equip.equip.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by Curtis on 9/23/2017.
 */

public class CurrentRentalsFragment extends BaseRentalFragment {

    @Override
    public Query getRentalQuery(DatabaseReference databaseReference) {
        return null;
    }

}
