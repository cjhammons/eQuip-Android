package com.equip.equip.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.equip.equip.DataStructures.Equipment;
import com.equip.equip.DataStructures.Reservation;
import com.equip.equip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RentalTimeFragment extends Fragment {

    Equipment mEquipment;
    static EditText mDate;
    static EditText mStartTime;
    static EditText mEndTime;

    Button mReserveButton;

    static Date startTimeDate;
    static Date endTimeDate;
    static Date reservationDate;

    public static RentalTimeFragment getInstance(Equipment equipment) {
        RentalTimeFragment rentalTimeFragment = new RentalTimeFragment();
        rentalTimeFragment.mEquipment = equipment;
        return rentalTimeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rental_time_fragment, container, false);

        mDate = view.findViewById(R.id.date_input);
        mStartTime = view.findViewById(R.id.start_time_input);
        mEndTime = view.findViewById(R.id.end_time_input);
        mReserveButton = view.findViewById(R.id.reserve_button);

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePickerFragment = new DatePickerFragment(mEquipment);
                datePickerFragment.show(getActivity().getFragmentManager(), "datepicker");
            }
        });
        mStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePickerFragment = new TimePickerFragment(TimePickerFragment.START_TIME);
                timePickerFragment.show(getActivity().getFragmentManager(), "timePickerStart");
            }
        });
        mEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePickerFragment = new TimePickerFragment(TimePickerFragment.END_TIME);
                timePickerFragment.show(getActivity().getFragmentManager(), "timePickerEnd");
            }
        });
        mReserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reserve();

            }
        });
        return view;
    }

    void reserve() {
        Calendar calStart = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        calStart.setTime(startTimeDate);
        calEnd.setTime(endTimeDate);

        if (calStart.after(calEnd)){
            Toast.makeText(getActivity(), "Start time cannot be after end time!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (reservationDate.after(new Date())){
            Toast.makeText(getActivity(), "Rental Date can't be in the past!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Double baseCost = calEnd.get(Calendar.HOUR) - calStart.get(Calendar.HOUR) + 0.0;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String dateTimeReserved = SimpleDateFormat.getDateTimeInstance().format(new Date()).toString();
        String reservationKey = databaseReference.child("reservations/").push().getKey();
        Reservation reservation = new Reservation(
                mEquipment.getKey(),
                mEquipment.getOwnerId(),
                user.getUid(),
                mDate.getText().toString(),
                mDate.getText().toString(),
                dateTimeReserved,
                reservationKey,
                mStartTime.getText().toString(),
                mEndTime.getText().toString(),
                baseCost
        );
        mEquipment.setAvailable(false);
        mEquipment.setReservationId(reservationKey);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/reservations/" + reservationKey, reservation.toMap());
        childUpdates.put("/mEquipment/" + mEquipment.getKey(), mEquipment.toMap());

        databaseReference.updateChildren(childUpdates);
        Toast.makeText(getActivity(), R.string.reserved, Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        Equipment equipment;

        public DatePickerFragment(Equipment equipment){
            super();
            this.equipment = equipment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }


        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String dateString = month+1 + "/" + dayOfMonth + "/" + year;
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            try {
                reservationDate = dateFormat.parse(dateString);
                mDate.setText(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }


    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        int whichTime;
        public static final int START_TIME = 0;
        public static final int END_TIME = 1;


        public TimePickerFragment(int whichTime){
            super();
            this.whichTime = whichTime;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute, false);
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormatter = new SimpleDateFormat("kk:mm");
            String minuteString = String.valueOf(minute);
            if (minute < 10) {
                minuteString = "0" + minuteString;
            }
            String ampm = "am";
            if (hourOfDay >= 12) ampm = "pm";


            String timeString = hourOfDay  + ":" + minuteString;
            String displayString = ((hourOfDay == 12) ? 12 : hourOfDay % 12)  + ":" + minuteString + " " + ampm;
            try {
                switch (whichTime){
                    case START_TIME:
                        startTimeDate = timeFormatter.parse(timeString);
                        mStartTime.setText(displayString);
                        break;
                    case END_TIME:
                    default:
                        endTimeDate = timeFormatter.parse(mStartTime.getText().toString());
                        mEndTime.setText(displayString);

                }
            } catch (ParseException e) {
                Log.e("Error parsing dates:", e.getMessage());
                Toast.makeText(getActivity(), "Error parsing dates", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
