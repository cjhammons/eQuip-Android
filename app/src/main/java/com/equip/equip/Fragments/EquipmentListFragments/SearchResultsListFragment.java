package com.equip.equip.Fragments.EquipmentListFragments;

import android.support.v4.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;

/**
 * Created by Curtis on 11/20/2017.
 */

public class SearchResultsListFragment extends BaseEquipmentListFragment {

    private Query mQuery;

    public void setQuery(Query query){
        this.mQuery = query;
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        if (mQuery == null) {
            return databaseReference.child("equipment");
        }
        return mQuery;
    }

    @Override
    public ArrayList<String> getFilteredIds() {
        return null;
    }

    @Override
    public Fragment getFragmentInstance() {
        return this;
    }
}
