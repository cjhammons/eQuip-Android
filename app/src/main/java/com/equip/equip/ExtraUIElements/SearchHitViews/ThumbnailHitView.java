package com.equip.equip.ExtraUIElements.SearchHitViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.algolia.instantsearch.ui.views.AlgoliaHitView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

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
                Picasso.with(mContext)
                        .load(uri)
                        .fit()
                        .into(ThumbnailHitView.this, new Callback() {
                            @Override
                            public void onSuccess() {
                                Bitmap imageBitmap = ((BitmapDrawable) ThumbnailHitView.this.getDrawable()).getBitmap();
                                RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                                imageDrawable.setCircular(true);
                                imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                                ThumbnailHitView.this.setImageDrawable(imageDrawable);
                            }

                            @Override
                            public void onError() {

                            }
                        });
            }
        });
    }
}
