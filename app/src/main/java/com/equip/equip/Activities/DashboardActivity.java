package com.equip.equip.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;

import com.equip.equip.ExtraUIElements.Drawer.DrawerHeader;
import com.equip.equip.ExtraUIElements.Drawer.DrawerMenuItem;
import com.equip.equip.Fragments.EquipmentListFragments.MyEquipmentListFragment;
import com.equip.equip.Fragments.EquipmentListFragments.NearbyListFragment;
import com.equip.equip.Fragments.MyRentalsFragment;
import com.equip.equip.R;
import com.equip.equip.Search.MySuggestionProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mindorks.placeholderview.PlaceHolderView;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class DashboardActivity extends AppCompatActivity implements DrawerMenuItem.DrawerCallBack{

    public static final String TAG = "DashboardActivity";

    private PlaceHolderView mDrawerView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private PlaceHolderView mGalleryView;
    private FirebaseUser mUser;
//    private FloatingActionButton mFab;
    private FloatingTextButton mFab;

    //TODO finish tab thing?
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Intent intent = getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            Log.d(TAG, "doing search");
            doSearch(query);
        }

        mDrawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerView = (PlaceHolderView)findViewById(R.id.drawerView);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(mToolbar);
        mFab = (FloatingTextButton) findViewById(R.id.fab_add);
        mFab.setOnClickListener(new FabListener());



        mUser = FirebaseAuth.getInstance().getCurrentUser();


        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new NearbyListFragment())
                .addToBackStack("Nearby")
                .commit();
        setupDrawer();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        return true;
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
        DrawerMenuItem myEquipment = new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_MY_EQUIPMENT);
        myEquipment.setDrawerCallBack(this);
        DrawerMenuItem dashboard = new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_DASHBOARD);
        dashboard.setDrawerCallBack(this);

        DrawerHeader drawerHeader = new DrawerHeader(this, mUser.getDisplayName(), mUser.getEmail(), mUser.getPhotoUrl(), new DrawerHeaderListener());
        mDrawerView
                .addView(drawerHeader)
//                .addView(search)
//                .addView(profile)
//                .addView(messages)
                .addView(dashboard)
                .addView(rentals)
                .addView(myEquipment)
//                .addView(settings)
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
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new MyRentalsFragment())
                .addToBackStack("MyRentals")
                .commit();
        getSupportActionBar().setTitle(getString(R.string.drawer_my_rentals));
        mDrawer.closeDrawers();
        mFab.setVisibility(View.GONE);

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
    public void onDashboardSelected() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new NearbyListFragment())
                .addToBackStack("Dashboard")
                .commit();
        getSupportActionBar().setTitle(getString(R.string.app_name));
        mDrawer.closeDrawers();
        mFab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMyEquipmentSelected() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new MyEquipmentListFragment())
                .addToBackStack("MyEquipment")
                .commit();
        getSupportActionBar().setTitle(getString(R.string.my_equipment));
        mDrawer.closeDrawers();
        mFab.setVisibility(View.VISIBLE);
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

    void doSearch(String query){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("equipment");
        DatabaseReference categoryReference = (DatabaseReference) databaseReference.orderByChild(query);


    }

    private class DrawerHeaderListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(DashboardActivity.this, AccountEditActivity.class);
            startActivity(intent);
        }
    }
}
