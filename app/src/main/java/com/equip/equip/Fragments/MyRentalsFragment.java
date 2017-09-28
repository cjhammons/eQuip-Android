package com.equip.equip.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.equip.equip.Fragments.RentalListFragments.BaseRentalFragment;
import com.equip.equip.Fragments.RentalListFragments.CurrentRentalsFragment;
import com.equip.equip.Fragments.RentalListFragments.HistoryRentalsFragment;
import com.equip.equip.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Curtis on 9/23/2017.
 */

public class MyRentalsFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_my_rentals, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager = setupViewPager(viewPager);

        TabLayout tabs = (TabLayout) view.findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);

        return view;
    }


    ViewPager setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new CurrentRentalsFragment(), getString(R.string.current_rentals_title));
        adapter.addFragment(new HistoryRentalsFragment(), getString(R.string.history_rentals_title));
        viewPager.setAdapter(adapter);
        return viewPager;
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter{

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String fragmentTitle){
            if (fragment instanceof BaseRentalFragment) {
                mFragmentList.add(fragment);
                mFragmentTitleList.add(fragmentTitle);
            }


        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
