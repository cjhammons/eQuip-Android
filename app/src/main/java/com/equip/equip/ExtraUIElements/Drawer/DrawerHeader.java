package com.equip.equip.ExtraUIElements.Drawer;

import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import com.equip.equip.R;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

/**
 * Created by curtis on 7/4/17.
 */


@NonReusable
@Layout(R.layout.drawer_header)
public class DrawerHeader {

    public DrawerHeader(){}

    public DrawerHeader(String name, String email, Uri imageUri){
        this.mEmail = email;
        this.mName = name;
        this.mProfileImageUri = imageUri;
    }

    @View(R.id.profileImageView)
    private ImageView profileImage;
    private Uri mProfileImageUri;

    @View(R.id.nameTxt)
    private TextView nameTxt;
    private String mName;


    @View(R.id.emailTxt)
    private TextView emailTxt;
    private String mEmail;

    @Resolve
    private void onResolved() {
        nameTxt.setText(this.mName);
        emailTxt.setText(this.mEmail);
        //TODO image load
    }

}
