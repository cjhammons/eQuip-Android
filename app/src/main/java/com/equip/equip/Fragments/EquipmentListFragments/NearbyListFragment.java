package com.equip.equip.Fragments.EquipmentListFragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.equip.equip.Activities.CreateItemListingActivity;
import com.equip.equip.Activities.EquipmentDetailActivity;
import com.equip.equip.DataStructures.Equipment;
import com.equip.equip.R;
import com.equip.equip.Util.location.LocationHelper;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class NearbyListFragment extends Fragment {

    int mFilterDistance = 50;
    static final double KILOMETER_MULTIIPLIER_CONSTANT = 1.6;
    boolean useCurrentLocation = true;
    String customAddr;

    private RecyclerView mEquipmentRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private DatabaseReference mDatabaseReference;
    private EquipmentAdapter mEquipmentAdapter;
    private StorageReference mStorageReference;
    private Query mQuery;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard_list, container, false);
        mEquipmentRecyclerView = (RecyclerView) view.findViewById(R.id.item_list);
        mEquipmentRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mEquipmentAdapter = new EquipmentAdapter();
        mEquipmentRecyclerView.setAdapter(mEquipmentAdapter);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mEquipmentAdapter.refresh();
                //todo
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;
    }

    public void setFilterDistance(int mFilterDistance) {
        this.mFilterDistance = mFilterDistance;
    }

    public void setCustomAddr(String customAddr) {
        this.customAddr = customAddr;
        useCurrentLocation = false;
    }

    public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.ViewHolder> {

        private static final String LOG_TAG = "EquipmentRecyclerAdapter";
        private Query mEquipmentQuery;
        private ArrayList<Equipment> mEquipmentList;
        DatabaseReference databaseReference;
        private EquipmentAdapter(){
            mEquipmentList = new ArrayList<>();
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));


            databaseReference = FirebaseDatabase.getInstance().getReference();
            if (useCurrentLocation) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Log.d("EquipmentAdapter:", "No permission to use location");

                } else {
                    Log.d("EquipmentAdapter:", "Has location permisssion");
                    fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            doGeoQuery(location.getLatitude(), location.getLongitude());
                        }
                    });
                }
            } else {
                List<Address> geocodes = new ArrayList<Address>();
                Geocoder geocoder = new Geocoder(getActivity());
                try {
                    geocodes = geocoder.getFromLocationName(customAddr, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address addr;
                if (geocodes != null) {
                    addr = geocodes.get(0);
                } else {
                    Log.e("Geotagging:", "geocodes is null");
                    addr = new Address(Locale.getDefault());
                }
                doGeoQuery(addr.getLatitude(), addr.getLongitude());
            }

        }

        void doGeoQuery(double lat, double lng) {
            final GeoFire geoFire = new GeoFire(databaseReference.child("geofire"));

            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lat, lng),
                    mFilterDistance * KILOMETER_MULTIIPLIER_CONSTANT);

            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    databaseReference.child("equipment/" + key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Equipment e = dataSnapshot.getValue(Equipment.class);
                            if (!mEquipmentList.contains(e))
                                mEquipmentList.add(e);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onKeyExited(String key) {
                    databaseReference.child("equipment/" + key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Equipment e = dataSnapshot.getValue(Equipment.class);
                            if (mEquipmentList.contains(e))
                                mEquipmentList.remove(e);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {

                }

                @Override
                public void onGeoQueryReady() {

                }

                @Override
                public void onGeoQueryError(DatabaseError error) {
                    Log.e("GeoQuery: ", error.getMessage());
                }
            });
        }

        public void refresh(){
            notifyDataSetChanged();
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.equipment_list_item, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            Log.d(LOG_TAG, "onBindViewHolder (" + position + ")");
            final Equipment equipment = mEquipmentList.get(position);
            holder.mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), EquipmentDetailActivity.class);
                    intent.putExtra("equipmentKey", equipment.getKey());
                    startActivity(intent);
                }
            });
            //Take first image's thumbnail. This can be changed
            String path = "equipment/" + equipment.getKey() + "/thumbnail_0.jpg";
            mStorageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    loadImage(holder, uri);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(LOG_TAG + ": load thumbnail", e.getMessage());
                }
            });


        }

        /**
         * Loads the image using picasso
         * @param holder
         * @param uri
         */
        void loadImage(ViewHolder holder, Uri uri){
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int dimension = (displayMetrics.widthPixels / 3);
            Picasso.with(getActivity())
                    .load(uri)
                    .resize(dimension, dimension)
                    .centerCrop()
                    .into(holder.mImage);
        }

        @Override
        public int getItemCount() {
            return mEquipmentList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            private ImageView mImage;
            private ImageView mReserveNotification;

            private ViewHolder(View itemView) {
                super(itemView);
                mImage = (ImageView) itemView.findViewById(R.id.equipment_image);
                mReserveNotification = (ImageView) itemView.findViewById(R.id.reserve_notification);
            }
        }
    }

}

