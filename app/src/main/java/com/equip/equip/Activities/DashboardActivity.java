package com.equip.equip.Activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.equip.equip.DataStructures.Equipment;
import com.equip.equip.DataStructures.User;
import com.equip.equip.ExtraUIElements.Drawer.DrawerHeader;
import com.equip.equip.ExtraUIElements.Drawer.DrawerMenuItem;
import com.equip.equip.Fragments.EquipmentListFragments.NearbyListFragment;
import com.equip.equip.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.mindorks.placeholderview.PlaceHolderView;

import java.util.HashMap;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity implements DrawerMenuItem.DrawerCallBack{

    public static final String TAG = "DashboardActivity";

    private PlaceHolderView mDrawerView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private PlaceHolderView mGalleryView;
    private FirebaseUser mUser;
    private FloatingActionButton mFab;
    //TODO finish tab thing?
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mDrawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerView = (PlaceHolderView)findViewById(R.id.drawerView);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab_add);
        mFab.setOnClickListener(new FabListener());

//        mViewPager = (ViewPager) findViewById(R.id.tab_pager);
//        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);

        mUser = FirebaseAuth.getInstance().getCurrentUser();


        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new NearbyListFragment())
                .commit();
        setupDrawer();
    }

    private void setupDrawer(){
        DrawerMenuItem search = new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_SEARCH);
        search.setDrawerCallBack(this);
        DrawerMenuItem rentals = new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_MY_RENTALS);
        rentals.setDrawerCallBack(this);
        DrawerMenuItem profile = new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_MY_PROFILE);
        profile.setDrawerCallBack(this);
        DrawerMenuItem messages = new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_MESSAGES);
        messages.setDrawerCallBack(this);
        DrawerMenuItem settings = new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_SETTINGS);
        settings.setDrawerCallBack(this);
        DrawerMenuItem logout = new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_LOGOUT);
        logout.setDrawerCallBack(this);
        mDrawerView
                .addView(new DrawerHeader(mUser.getDisplayName(), mUser.getEmail(), mUser.getPhotoUrl()))
                .addView(search)
                .addView(rentals)
                .addView(profile)
                .addView(messages)
                .addView(settings)
                .addView(logout);
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


    @Override
    public void onItemSearchSelected() {
        //todo
    }

    @Override
    public void onMyRentalsSelected() {
        //todo
    }

    @Override
    public void onMyProfileSelected() {
        //todo
    }

    @Override
    public void onMessagesSelected() {
        //todo
    }

    @Override
    public void onSettingsSelected() {
        //todo
    }

    @Override
    public void onLogoutSelected() {
        Intent intent = new Intent(this, LoginActivity.class);
        FirebaseAuth.getInstance().signOut();
        startActivity(intent);
    }

    private class FabListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(DashboardActivity.this, CreateItemListingActivity.class);
            startActivity(intent);

        }
    }
}
