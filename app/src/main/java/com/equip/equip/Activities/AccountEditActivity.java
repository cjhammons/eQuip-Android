package com.equip.equip.Activities;

import android.Manifest;
import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.equip.equip.DataStructures.User;
import com.equip.equip.R;
import com.equip.equip.Util.stripe.MyEphemeralKeyProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.stripe.android.CustomerSession;
import com.stripe.android.EphemeralKeyProvider;
import com.stripe.android.EphemeralKeyUpdateListener;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Customer;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;
import com.stripe.android.view.PaymentMethodsActivity;
import java.nio.file.Files;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Created by Curtis on 10/27/2017.
 */

public class AccountEditActivity extends Activity  {

    private EditText mDisplayNameView;
    private EditText mAddressView;
    private EditText mEmailView;
    private EditText mPhoneView;
    private ImageView mProfilePicture;
    private Button mConfirmButton;
    private Button mManagePaymentMethodsButton;

    CardInputWidget mCardInputWidget;

    private ProgressDialog mProgressDialog;

    private User mUser;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mUserDatabaseReference;
    private StorageReference mStorageReference;

    private String mProfilePicURL = "";

    private static final int REQUEST_IMAGE_FROM_PHONE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_LOCATION_PERMISION = 10;

    private InputStream mPhotoStream;

    private Uri cameraUri;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_edit);
        mDisplayNameView = (EditText) findViewById(R.id.account_display_name);
        mAddressView = (EditText) findViewById(R.id.account_address);
        mEmailView = (EditText) findViewById(R.id.account_email);
        mPhoneView = (EditText) findViewById(R.id.account_phone);
        mProfilePicture = (ImageView) findViewById(R.id.account_profile_pic);
        mConfirmButton = (Button) findViewById(R.id.confirm_changes);
//        mCardInputWidget = findViewById(R.id.card_input_widget);
        mManagePaymentMethodsButton = findViewById(R.id.manage_payments);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mStorageReference = FirebaseStorage.getInstance().getReference().child("users/" + mFirebaseUser.getUid());
        mUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users/" + mFirebaseUser.getUid());
        mUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                populateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        CustomerSession.initCustomerSession(new MyEphemeralKeyProvider(new MyEphemeralKeyProvider.ProgressListener() {
            @Override
            public void onStringResponse(String string) {
                if (string.startsWith("Error:")) {
                    Toast.makeText(AccountEditActivity.this, "Could not get payment information", Toast.LENGTH_SHORT).show();
                }
            }
        }));

        CustomerSession.getInstance().retrieveCurrentCustomer(new CustomerSession.CustomerRetrievalListener() {
            @Override
            public void onCustomerRetrieved(@NonNull Customer customer) {
                mManagePaymentMethodsButton.setEnabled(true);
            }

            @Override
            public void onError(int errorCode, @Nullable String errorMessage) {
                mManagePaymentMethodsButton.setEnabled(false);
            }
        });
    }

    void populateUI() {
        mDisplayNameView.setText(mUser.getDisplayName());
        mAddressView.setText(mUser.getAddress());
        mPhoneView.setText(mUser.getPhoneNumber());
        mEmailView.setText(mUser.getEmail());
        mConfirmButton.setOnClickListener(new confirmChangesListener());
        mProfilePicture.setOnClickListener(new changeProfilePicListener());
        mManagePaymentMethodsButton.setOnClickListener(new managePaymentMethodsListener());

//        FirebaseDatabase.getInstance().getReference().child("stripe_customers/" + mUser.getUserId() + "/sources/").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot sourcesSnapshot) {
////                for (DataSnapshot sourceSnapshot: sourcesSnapshot.getChildren()) {
//                    Gson gson = new GsonBuilder().create();
//                    Token token = gson.fromJson(sourcesSnapshot.getValue().toString(), Token.class);
//
//                    mCardInputWidget.setCardNumber(token.getCard().getLast4());
//                    mCardInputWidget.setExpiryDate(token.getCard().getExpMonth(), token.getCard().getExpYear());
////                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });




        mStorageReference.child("/profilePicture.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(AccountEditActivity.this)
                        .load(uri)
                        .fit()
                        .into(mProfilePicture);
                mProfilePicURL = mUser.getPicUrl();
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.uploading_photo));
        mProgressDialog.setIndeterminate(true);

    }

    void updateUser(){
        boolean doingPhotoUpload = false;
        if (mPhotoStream != null) {
            mProgressDialog.show();
            doingPhotoUpload = true;
            UploadTask uploadTask = mStorageReference
                    .child("/profilePicture.jpg")
                    .putStream(mPhotoStream);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mUser.setPicUrl(mProfilePicURL);

                    //upload
                    Map<String, Object> userValues = mUser.toMap();
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("users/" + mUser.getUserId(), userValues);
                    FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
                    mProgressDialog.dismiss();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressDialog.dismiss();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    System.out.println("Upload is " + progress + "% done");
                    int currentprogress = (int) progress;
                    mProgressDialog.setProgress(currentprogress);
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    mProgressDialog.dismiss();
                }
            });
        }



        mUser.setDisplayName(mDisplayNameView.getText().toString());
        mUser.setAddress(mAddressView.getText().toString());
        mUser.setEmail(mEmailView.getText().toString());
        mUser.setPhoneNumber(mPhoneView.getText().toString());

//        Card card = mCardInputWidget.getCard();
//        if (card != null){
//            Stripe stripe = new Stripe(this, "pk_test_SeqezrLyAgz4eVotKj6icafX"); //TODO REPLACE WITH REAL KEY
//            stripe.createToken(card, new TokenCallback() {
//                @Override
//                public void onError(Exception error) {
//                    Toast.makeText(AccountEditActivity.this, "Invalid Card information", Toast.LENGTH_SHORT).show();
//
//                }
//
//                @Override
//                public void onSuccess(Token token) {
//                    GsonBuilder builder = new GsonBuilder();
//                    Gson gson = builder.create();
//                    String tokenJson = gson.toJson(token);
//
//                    Map<String, Object> childUpdates = new HashMap<>();
//                    childUpdates.put("stripe_customers/" + mUser.getUserId() + "/sources", tokenJson);
//                    FirebaseDatabase.getInstance().getReference().child("stripe_customers/" + mUser.getUserId() + "/sources").setValue(tokenJson);
//                }
//            });
//        }

        if (ContextCompat.checkSelfPermission(AccountEditActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(AccountEditActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(AccountEditActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        List<Address> geocodes = new ArrayList<Address>();
        Geocoder geocoder = new Geocoder(AccountEditActivity.this);
        try {
            geocodes = geocoder.getFromLocationName(mAddressView.getText().toString(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address addr = new Address(Locale.getDefault());
        if (geocodes != null && geocodes.size() > 0) {
            addr = geocodes.get(0);
            double lat = addr.getLatitude();
            double lng = addr.getLongitude();
            mUser.setGeolocation(addr.getLatitude(), addr.getLongitude());
        }
        Map<String, Object> userValues = mUser.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("users/" + mUser.getUserId(), userValues);
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
        if (!doingPhotoUpload)
            finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
        }
    }

    //copied form CreateItemListingActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        File thumbFile;
        switch (requestCode){

            case REQUEST_IMAGE_FROM_PHONE:
                try {
                    mPhotoStream = this.getContentResolver().openInputStream(data.getData());
                    Picasso.with(this)
                            .load(data.getData())
                            .fit()
                            .into(mProfilePicture);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
//                if (data.getExtras() == null) return;
//                Uri uri = (Uri) data.getExtras().get(MediaStore.EXTRA_OUTPUT);
                thumbFile = new File(String.valueOf(cameraUri));

                Picasso.with(this)
                        .load(cameraUri)
                        .fit()
                        .into(mProfilePicture);
                break;


        }

    }

    //copied from CreateItemListingActivity
    void handlePhoto(int selection){
        Intent intent;
        int requestCode;
        switch (selection){
            //Take Photo
            case REQUEST_IMAGE_CAPTURE:
                intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                requestCode = REQUEST_IMAGE_CAPTURE;

                File imageFile = null;
                if (intent.resolveActivity(getPackageManager()) != null) {
                    try {
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String imageFileName = "JPEG_" + timeStamp + "_";
                        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        imageFile = Files.createTempFile(storageDir.toPath(), imageFileName, ".jpg").toFile();

                        // Save a file: path for use with ACTION_VIEW intents
                        mPhotoStream = new FileInputStream(imageFile.getAbsolutePath());
                    } catch (Exception e) {
                        Log.e("Photo capture error: ", e.getMessage());
                    }
                }

                if (imageFile != null){
                    Uri imageUri = FileProvider.getUriForFile(AccountEditActivity.this,
                            "com.example.android.fileprovider",
                            imageFile);
                    imageFile.getParentFile().mkdirs();
                    try {
                        imageFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    cameraUri = imageUri;
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    Picasso.with(this)
                            .load(imageUri)
                            .fit()
                            .into(mProfilePicture);
                }
                break;
            //Add from phone
            case REQUEST_IMAGE_FROM_PHONE:
            default:
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                requestCode = REQUEST_IMAGE_FROM_PHONE;
                intent.setType("image/*");
        }
        startActivityForResult(intent, requestCode);
    }


    //copied from CreateItemListingActivity
    class AddPhotoDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            AlertDialog.Builder builder = new AlertDialog.Builder(AccountEditActivity.this);
            builder.setItems(R.array.add_photo_options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handlePhoto(which);
                }
            });

            return builder.create();
        }

    }

    private class changeProfilePicListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            new AddPhotoDialogFragment().show(getFragmentManager(), "addPhotoDialog");
        }
    }

    private class confirmChangesListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            updateUser();
        }
    }

    private class managePaymentMethodsListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = PaymentMethodsActivity.newIntent(AccountEditActivity.this);
            startActivity(intent);
        }
    }
}
