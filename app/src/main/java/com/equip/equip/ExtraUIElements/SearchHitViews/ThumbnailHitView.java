package com.equip.equip.ExtraUIElements.SearchHitViews;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.algolia.instantsearch.ui.views.AlgoliaHitView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by curtis on 12/4/17.
 */

public class ThumbnailHitView extends AppCompatImageView implements AlgoliaHitView {

    Context mContext;

    public ThumbnailHitView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public void onUpdateView(JSONObject result) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String path = "";
        try {
            path = "equipment/" + result.getString("key") + "/thumbnail_0.jpg";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (path.equals("")) {
            return;
        }
        storageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                int dimension = 128;
                Picasso.with(mContext)
                        .load(uri)
                        .resize(dimension, dimension)
                        .centerCrop()
                        .into(ThumbnailHitView.this);
            }
        });
    }
}
