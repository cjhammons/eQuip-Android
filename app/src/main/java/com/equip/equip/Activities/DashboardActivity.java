package com.equip.equip.Activities;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.equip.equip.DataStructures.Equipment;
import com.equip.equip.ExtraUIElements.Drawer.DrawerHeader;
import com.equip.equip.ExtraUIElements.Drawer.DrawerMenuItem;
import com.equip.equip.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.mindorks.placeholderview.PlaceHolderView;

import java.util.HashMap;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    public static final String TAG = "DashboardActivity";


    private PlaceHolderView mDrawerView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private PlaceHolderView mGalleryView;
    private FirebaseUser mUser;
    private FloatingActionButton mFab;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mDrawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerView = (PlaceHolderView)findViewById(R.id.drawerView);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mFab = (FloatingActionButton) findViewById(R.id.fab_add);
        mFab.setOnClickListener(new FabListener());
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        setupDrawer();
    }

    public FirebaseUser getUser() {
        return mUser;
    }

    private void setupDrawer(){
        mDrawerView
                .addView(new DrawerHeader(mUser.getDisplayName(), mUser.getEmail(), mUser.getPhotoUrl()))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_SEARCH))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_MY_RENTALS))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_MY_PROFILE))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_MESSAGES))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_SETTINGS))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_LOGOUT));

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.open_drawer, R.string.close_drawer){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        mDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private class FabListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(DashboardActivity.this, CreateItemListingActivity.class);
            startActivity(intent);

        }
    }
}
