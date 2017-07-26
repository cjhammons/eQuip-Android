package com.equip.equip.Fragments.EquipmentListFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.equip.equip.Fragments.NearbyListFragment;
import com.equip.equip.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by curtis on 7/19/17.
 */

public abstract class BaseEquipmentListFragment extends Fragment {

    private RecyclerView mItemList;

    private DatabaseReference mDatabase;

    //TODO all this shit

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_dashboard_list, container, false);
//        mItemList = (RecyclerView) view.findViewById(R.id.item_list);
//        mItemList.setLayoutManager(new GridLayoutManager(getContext(), 3));
//        mItemList.setAdapter(new NearbyListFragment.EquipmentAdapter(getEquipmentList(), getContext()));
//
//        mFab = (FloatingActionButton) view.findViewById(R.id.fab_add);
//        mFab.setOnClickListener(new NearbyListFragment.FabListener());
//
//        return view;
//    }

    public abstract Query getQuery(DatabaseReference databaseReference);
}
