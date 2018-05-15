package com.equip.equip.Fragments.EquipmentListFragments;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.equip.equip.Activities.EquipmentDetailActivity;
import com.equip.equip.DataStructures.Equipment;
import com.equip.equip.R;
import com.equip.equip.Util.location.LocationHelper;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class NearbyListFragment extends BaseEquipmentListFragment {


    LocationHelper mLocationHelper;
    int mFilterDistance = 50;
    final double KILOMETER_MULTIIPLIER_CONSTANT = 1.60934;


    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("equipment").orderByChild("available").equalTo(true);
    }



    @Override
    public Fragment getFragmentInstance() {
        return NearbyListFragment.this;
    }


    public void setLocationHelper(LocationHelper mLocationHelper) {
        this.mLocationHelper = mLocationHelper;
        mLocationHelper.buildGoogleApiClient();
        mLocationHelper.connectApiClient();
        mLocationHelper.getLocation();
    }

    public LocationHelper getLocationHelper() {
        return mLocationHelper;
    }

    public void setFilterDistance(int filterDistance) {
        this.mFilterDistance = filterDistance;
    }

    public int getFilterDistance() {
        return mFilterDistance;
    }

    public void setFilterLocation(String address){

    }

    public ArrayList<String> getFilteredIds() {
        final ArrayList<String> list = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        GeoFire geoFire = new GeoFire(databaseReference.child("geofire"));
        mLocationHelper.buildGoogleApiClient();
        mLocationHelper.connectApiClient();
        mLocationHelper.getLocation();
        Location location = mLocationHelper.getLastLocation();
        Double lat = location.getLatitude();
        Double lng = location.getLongitude();
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lat, lng),
                mFilterDistance * KILOMETER_MULTIIPLIER_CONSTANT);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                list.add(key);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

        return list;
    }

}

