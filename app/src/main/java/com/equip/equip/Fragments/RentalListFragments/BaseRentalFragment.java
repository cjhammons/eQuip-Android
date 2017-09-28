package com.equip.equip.Fragments.RentalListFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.equip.equip.R;
import com.google.firebase.database.Query;

/**
 * Created by Curtis on 9/23/2017.
 */

public abstract class BaseRentalFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        return view;
    }

    public abstract Query getRentalQuery();

}
