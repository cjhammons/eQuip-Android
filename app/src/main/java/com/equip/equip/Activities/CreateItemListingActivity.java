package com.equip.equip.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.equip.equip.DataStructures.Equipment;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

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
import java.util.Map;
import java.util.Objects;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;


/**
 * Created by curtis on 7/19/17.
 */
//TODO location tagging
public class CreateItemListingActivity extends Activity {

    public static final String TAG = "CreateListing";

    private EditText mDescriptionText;
    private Spinner mCategorySpinner;
    private EditText mNameText;
    private ImageView imageView;

    private ProgressDialog mProgressDialog;

    private List<String> mSpinnerList;

    private FirebaseUser mUser;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    private List<InputStream> mPhotoStreams = new ArrayList<>();

    private static final int REQUEST_IMAGE_FROM_PHONE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final int PHOTO_DISPLAY_DIMENSION = 400;

    private Bitmap mThumbnail;

    //I shouldn't have to use this but onActivityResult() is misbehaving to the extreme
    private Uri cameraUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item_listing);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        mSpinnerList = new ArrayList<>();
        mDatabase.child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mSpinnerList.add(snapshot.getValue().toString());
                }
                mCategorySpinner = (Spinner) findViewById(R.id.category_spinner);
                ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<CharSequence>(CreateItemListingActivity.this, android.R.layout.simple_spinner_item);
                spinnerAdapter.addAll(mSpinnerList);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mCategorySpinner.setAdapter(spinnerAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        mDescriptionText = (EditText) findViewById(R.id.equipment_description_entry);
        mNameText = (EditText) findViewById(R.id.equipment_name_entry);

        imageView = (ImageView) findViewById(R.id.image_view);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddPhotoDialogFragment().show(getFragmentManager(), "addPhotoDialog");
            }
        });

        FloatingTextButton createListingButton = (FloatingTextButton) findViewById(R.id.create_listing_button);
        createListingButton.setOnClickListener(new CreateListingListener());

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.uploading_photo));
        mProgressDialog.setIndeterminate(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        File thumbFile;
        switch (requestCode){

            case REQUEST_IMAGE_FROM_PHONE:
                try {
                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                mPhotoStreams.add(inputStream);
                Picasso.with(this)
                        .load(data.getData())
                        .resize(PHOTO_DISPLAY_DIMENSION, PHOTO_DISPLAY_DIMENSION)
                        .centerCrop()
                        .into(imageView);
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
                        .resize(PHOTO_DISPLAY_DIMENSION, PHOTO_DISPLAY_DIMENSION)
                        .centerCrop()
                        .into(imageView);
                break;
        }

    }

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
                        imageFile = File.createTempFile(
                                imageFileName,  /* prefix */
                                ".jpg",         /* suffix */
                                storageDir      /* directory */
                        );

                        // Save a file: path for use with ACTION_VIEW intents
                        mPhotoStreams.add(new FileInputStream(imageFile.getAbsolutePath()));
                    } catch (Exception e) {
                        Log.e("Photo capture error: ", e.getMessage());
                    }
                }

                if (imageFile != null){
                    Uri imageUri = FileProvider.getUriForFile(CreateItemListingActivity.this,
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
                            .resize(PHOTO_DISPLAY_DIMENSION, PHOTO_DISPLAY_DIMENSION)
                            .centerCrop()
                            .into(imageView);
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

    class AddPhotoDialogFragment extends DialogFragment{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateItemListingActivity.this);
            builder.setItems(R.array.add_photo_options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handlePhoto(which);
                }
            });

            return builder.create();
        }

    }

    private class CreateListingListener implements View.OnClickListener {

        private Equipment mEquipment;
        private String mKey;

        @Override
        public void onClick(View v) {
            if (mPhotoStreams.size() < 1){
                Toast.makeText(CreateItemListingActivity.this, getString(R.string.no_photos), Toast.LENGTH_SHORT).show();
                return;
            }

            mEquipment = new Equipment(mDescriptionText.getText().toString(),
                    mUser.getUid(),
                    "",
                    mCategorySpinner.getSelectedItem().toString(),
                    true,
                    mNameText.getText().toString());
            mKey = mDatabase.child("equipment").push().getKey();
            mEquipment.addKey(mKey);
            updateItemInDatabase();
            mProgressDialog.show();
            for (int i = 0; i < mPhotoStreams.size(); i++){
                StorageReference equipmentImageRef = mStorage.child("equipment/" + mKey +"/" + i + ".jpg");
                UploadTask uploadTask = equipmentImageRef.putStream(mPhotoStreams.get(i));
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mProgressDialog.dismiss();
                        CreateItemListingActivity.this.finish();
                    }
                });

            }


        }

        void updateItemInDatabase(){

            final DatabaseReference userReference = mDatabase.getDatabase().getReference()
                    .child("users/" + mUser.getUid());
            final ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Map<String, Object> childUpdates = new HashMap<>();

                    User user = dataSnapshot.getValue(User.class);
                    user.addEquipmentListing(mKey);
                    List<String> userEquipment = user.getEquipmentListings();
                    childUpdates.put("users/"
                            + user.getUserId() + "/equipmentListings",
                            userEquipment);

                    mEquipment.setGeolocation(user.getLat(), user.getLng());
                    Map<String, Object> equipmentValues = mEquipment.toMap();
                    childUpdates.put("/equipment/" + mKey, equipmentValues);
                    mDatabase.updateChildren(childUpdates);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, databaseError.getMessage());
                }
            };

            userReference.addListenerForSingleValueEvent(listener);
        }
    }


}
