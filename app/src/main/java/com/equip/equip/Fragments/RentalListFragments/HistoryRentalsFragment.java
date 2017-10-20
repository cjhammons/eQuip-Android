package com.equip.equip.Fragments.RentalListFragments;

import android.support.v4.app.Fragment;

import com.equip.equip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by Curtis on 9/23/2017.
 */

public class HistoryRentalsFragment extends BaseRentalFragment {

    @Override
    public Query getRentalQuery(DatabaseReference databaseReference) {
        //todo filter by correct thing
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return databaseReference.child("equipment").orderByChild("borrowerId").equalTo(uId);
    }

    @Override
    public Fragment getFragmentInstance() {
        return HistoryRentalsFragment.this;
    }
}
