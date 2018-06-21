package com.equip.equip.Fragments;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.equip.equip.Activities.DashboardActivity;
import com.equip.equip.Fragments.EquipmentListFragments.FilterListFragment;
import com.equip.equip.R;

public class FilterFragment extends DialogFragment{
    int rawSelected = 5;
    DashboardActivity dashboardActivity;

    @Override
    public void onAttach(Context context) {
        dashboardActivity = (DashboardActivity) context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_filter_dialog, container, false);


        final LinearLayout locationLayout = v.findViewById(R.id.location_container);
        final LinearLayout priceLayout = v.findViewById(R.id.price_container);
        locationLayout.setVisibility(View.GONE);
        priceLayout.setVisibility(View.GONE);

        final CheckBox locationCheckbox = v.findViewById(R.id.button_filter_location);
        final CheckBox priceCheckBox =  v.findViewById(R.id.button_filter_price);

        final EditText minPriceText = v.findViewById(R.id.price_min);
        final EditText maxPriceText = v.findViewById(R.id.price_max);

        locationCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    locationLayout.setVisibility(View.VISIBLE);
                else
                    locationLayout.setVisibility(View.GONE);
            }
        });

        priceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    priceLayout.setVisibility(View.VISIBLE);
                else
                    priceLayout.setVisibility(View.GONE);
            }
        });

        final TextView distanceText = v.findViewById(R.id.miles_text);

        SeekBar distanceBar = v.findViewById(R.id.distance_seek_bar);
        distanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rawSelected = progress;
                distanceText.setText(rawSelected * 25 + " miles");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final EditText addressText = v.findViewById(R.id.filter_custom_address);
        addressText.setVisibility(View.GONE);

        final RadioGroup radioGroup = v.findViewById(R.id.filter_radio);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.current_location:
                        addressText.setVisibility(View.GONE);
                        break;
                    case R.id.other_location:
                        addressText.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        v.findViewById(R.id.filter_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!priceCheckBox.isChecked() && !locationCheckbox.isChecked()){
                    Toast.makeText(getActivity(), "No filter parameters selected", Toast.LENGTH_LONG).show();
                    return;
                }
                FilterListFragment filterListFragment = new FilterListFragment();
                if (locationCheckbox.isChecked()){
                    filterListFragment.setFilterDistance(rawSelected * 25);
                    if (radioGroup.getCheckedRadioButtonId() == R.id.other_location){
                        filterListFragment.setCustomAddr(addressText.getText().toString());
                    }
                }
                if (priceCheckBox.isChecked()){
                    if (minPriceText.getText().toString().equals("") || maxPriceText.getText().toString().equals("") ){
                        Toast.makeText(getActivity(), "Enter valid price range", Toast.LENGTH_LONG).show();
                        return;
                    }
                    filterListFragment.setPriceFilter(true);
                    Double min = Double.parseDouble(minPriceText.getText().toString());
                    Double max = Double.parseDouble(maxPriceText.getText().toString());
                    filterListFragment.setPriceRange(min, max);
                }

                dashboardActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, filterListFragment).addToBackStack("Dashboard").commit();
//                dashboardActivity.getActionBar().setTitle("Nearby Equipment");
                FilterFragment.this.dismiss();
            }
        });

        v.findViewById(R.id.filter_cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterFragment.this.dismiss();
            }
        });

        return v;
    }
}
