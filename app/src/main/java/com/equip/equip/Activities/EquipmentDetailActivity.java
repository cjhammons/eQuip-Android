package com.equip.equip.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.equip.equip.DataStructures.Equipment;
import com.equip.equip.DataStructures.Reservation;
import com.equip.equip.DataStructures.User;
import com.equip.equip.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

/**
 * Date picker library found here https://github.com/borax12/MaterialDateRangePicker
 */
public class EquipmentDetailActivity extends AppCompatActivity {

    private String TAG = "EquipmentDetail";

    private Equipment mEquipment;
    private User mOwner;
    private boolean mIsOwner = false;

    Reservation mReservation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setTitle("");

        final String key = getIntent().getStringExtra("equipmentKey");
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("equipment/" + key);

        ValueEventListener equipmentListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEquipment = dataSnapshot.getValue(Equipment.class);
                if (mEquipment.getOwnerId()
                        .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    mIsOwner = true;
                }
                populateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(equipmentListener);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

//    @Override
//    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
//        String startDate = monthOfYear+1 + "/" + dayOfMonth + "/" + year;
//        String endDate = monthOfYearEnd+1 + "/" + dayOfMonthEnd + "/" + yearEnd;
//        reserve(startDate, endDate);
//    }

    void populateUI(){
        final ImageView image = (ImageView) findViewById(R.id.equipment_picture);
        TextView description = (TextView) findViewById(R.id.equipment_description);
        TextView name = (TextView) findViewById(R.id.equipment_title);
        TextView availableText = (TextView) findViewById(R.id.available_text);
        final TextView sellerName = (TextView) findViewById(R.id.seller_name);
        final TextView sellerAddress = (TextView) findViewById(R.id.seller_address);
        TextView mRateTextView = (TextView) findViewById(R.id.rate_amount);
        TextView mRateUnitTextView = (TextView) findViewById(R.id.rate_unit);


        FloatingTextButton reserveButton = (FloatingTextButton) findViewById(R.id.reserve_item_fab);
        if (mIsOwner) {
            reserveButton.setVisibility(View.GONE);
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String path = "equipment/" + mEquipment.getKey() + "/0.jpg";
        storageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                int dimensionx = 816;
                int dimensiony = 204;
                Picasso.with(EquipmentDetailActivity.this)
                        .load(uri)
//                        .resize(500, 204)
//                        .centerInside()
                        .fit()
                        .centerCrop()
                        .into(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG + "image download", e.getMessage());
            }
        });

        description.setText(mEquipment.getDescription());
        name.setText(mEquipment.getName());

        if (mEquipment.getRatePrice() < 0.0) {
           findViewById(R.id.rate_container).setVisibility(View.GONE);
        }
        mRateTextView.setText(String.valueOf(mEquipment.getRatePrice()));
        mRateUnitTextView.setText(String.valueOf(mEquipment.getRateUnit().getValue()));


        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("users/" + mEquipment.getOwnerId());

        ValueEventListener userEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mOwner = dataSnapshot.getValue(User.class);
                sellerName.setText(mOwner.getDisplayName());
                sellerAddress.setText(mOwner.getAddress());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(userEventListener);

        reserveButton.setOnClickListener(new ReserveButtonListener());
        if (!mEquipment.getAvailable()){
            availableText.setText(getString(R.string.not_available).toUpperCase());
            reserveButton.setEnabled(false);
            reserveButton.setVisibility(View.GONE);
            if (mIsOwner) {
                populateReservationInfo();
            }
        } else {
            availableText.setText(getString(R.string.available).toUpperCase());
        }

        findViewById(R.id.seller_container).setOnClickListener(new SellerDetailListener());
    }

    //Only called when the user owns the item and the item has been reserved
    void populateReservationInfo(){
        findViewById(R.id.reserved_container).setVisibility(View.VISIBLE);

        DatabaseReference reservationRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("reservations/" + mEquipment.getReservationId());

        ValueEventListener reservationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Reservation reservation = dataSnapshot.getValue(Reservation.class);
                mReservation = reservation;

                //date stuff
                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                df.applyPattern("MM/dd/yyyy");
                Date startDate = null;
                Date endDate = null;
                try {
                    startDate = df.parse(reservation.getReservedPeriodStartDate());
                    endDate = df.parse(reservation.getReservedPeriodEndDate());
                } catch (Exception e){
                    e.printStackTrace();
                }

                if (startDate!=null && endDate !=null) {
                    String combinedDateString = df.format(startDate) + " - " + df.format(endDate);
                    ((TextView)findViewById(R.id.date_range)).setText(combinedDateString);
                } else {
                    ((TextView)findViewById(R.id.date_range)).setText(df.format(startDate));
                }



                //get borrower information
                DatabaseReference borrowerRef = FirebaseDatabase.getInstance()
                        .getReference()
                        .child("users/" + reservation.getBorrowerId());
                ValueEventListener borrowerListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User borrower = dataSnapshot.getValue(User.class);
                        //todo use displayname instead of email
                        ((TextView)findViewById(R.id.borrower_name)).setText(borrower.getEmail());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                borrowerRef.addListenerForSingleValueEvent(borrowerListener);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        reservationRef.addListenerForSingleValueEvent(reservationListener);

        Button confirmReservationButton = (Button) findViewById(R.id.confirm_reservation_button);
        confirmReservationButton.setOnClickListener(new ConfirmReservationButtonListener());
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
            reserve(month+1 + "/" + dayOfMonth + "/" + year);
        }

        void reserve(String dateString){

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String dateTimeReserved = SimpleDateFormat.getDateTimeInstance().format(new Date()).toString();
            String reservationKey = databaseReference.child("reservations/").push().getKey();
            Reservation reservation = new Reservation(
                    equipment.getKey(),
                    equipment.getOwnerId(),
                    user.getUid(),
                    dateString,
                    dateString,
                    dateTimeReserved,
                    reservationKey
            );
            equipment.setAvailable(false);
            equipment.setReservationId(reservationKey);

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/reservations/" + reservationKey, reservation.toMap());
            childUpdates.put("/equipment/" + equipment.getKey(), equipment.toMap());

            databaseReference.updateChildren(childUpdates);
            Toast.makeText(getActivity(), R.string.reserved, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    private class ReserveButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
//            Calendar now = Calendar.getInstance();
//            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
//                    EquipmentDetailActivity.this,
//                    now.get(Calendar.YEAR),
//                    now.get(Calendar.MONTH),
//                    now.get(Calendar.DAY_OF_MONTH)
//                    //set the present date to be the minimum date that can be selected
//            );
//            datePickerDialog.setMinDate(now);
//            datePickerDialog.show(getFragmentManager(), "TimePickerDialog");

            DialogFragment datePickerFragment = new DatePickerFragment(mEquipment);
            datePickerFragment.show(getFragmentManager(), "datepicker");
        }
    }

    private class ConfirmReservationButtonListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            final Map<String, Object> childUpdates = new HashMap<>();

            String dateTimeConfirmed = SimpleDateFormat.getDateTimeInstance().format(new Date()).toString();
            mReservation.setConfirmed(dateTimeConfirmed);
            String reservationKey = "";
            if (mReservation.getKey().equals(null) || mReservation.getKey().equals("")){
                reservationKey = mEquipment.getReservationId();
            } else {
                reservationKey = mReservation.getKey();
            }
            childUpdates.put("/reservations/" + reservationKey, mReservation.toMap());

            //update due date in equipment
            mEquipment.setDueDate(mReservation.getReservedPeriodEndDate());
            childUpdates.put("/equipment/" + mEquipment.key, mEquipment.toMap());

//            DatabaseReference renterReference = databaseReference.child("/users/" + mReservation.getBorrowerId());
//            ValueEventListener renterValueListener = new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    User renter = dataSnapshot.getValue(User.class);
//                    renter.addRentalId(mReservation.getEquipmentId());
//
//                    childUpdates.put("/users/" + renter.getUserId(), renter.toMap());
//
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            };
//            renterReference.addListenerForSingleValueEvent(renterValueListener);

            databaseReference.updateChildren(childUpdates);
            Toast.makeText(EquipmentDetailActivity.this, getString(R.string.reservation_confirmed_toast), Toast.LENGTH_SHORT).show();
        }
    }

    private class SellerDetailListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(EquipmentDetailActivity.this, UserDetailActivity.class);
            intent.putExtra("userKey", mEquipment.getOwnerId());
            startActivity(intent);
        }
    }
}
