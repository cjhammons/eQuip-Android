package com.equip.equip.ExtraUIElements.Drawer;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import com.equip.equip.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.squareup.picasso.Picasso;

/**
 * Created by curtis on 7/4/17.
 */


@NonReusable
@Layout(R.layout.drawer_header)
public class DrawerHeader {

    @View(R.id.profileImageView)
    private ImageView profileImage;
    private Uri mProfileImageUri;

    @View(R.id.nameTxt)
    private TextView nameTxt;
    private String mName;


    @View(R.id.emailTxt)
    private TextView emailTxt;
    private String mEmail;

    android.view.View.OnClickListener profileClickListener;
    Context context;

    public DrawerHeader(){}

    public DrawerHeader(Context context, String name, String email, Uri imageUri, android.view.View.OnClickListener profileClickListener){
        this.context = context;
        this.mEmail = email;
        this.mName = name;
        this.mProfileImageUri = imageUri;
        this.profileClickListener = profileClickListener;
    }



    @Resolve
    private void onResolved() {
        nameTxt.setText(this.mName);
        emailTxt.setText(this.mEmail);
        profileImage.setOnClickListener(profileClickListener);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("users/" + user.getUid() + "/thumbnail_profilePicture.jpg")
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(context)
                                .load(uri)
                                .fit()
                                .into(profileImage);
                    }
                });

    }



}
