package com.equip.equip.ExtraUIElements.SearchHitViews;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.algolia.instantsearch.ui.views.AlgoliaHitView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by curtis on 12/6/17.
 */

public class LocationHitView extends AppCompatTextView implements AlgoliaHitView{

    Context mContext;

    public LocationHitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public void onUpdateView(JSONObject result) {
        double lat = -999;
        double lng = -999;
        try {
            JSONObject geoloc = (JSONObject) result.get("geoloc");
            lat = geoloc.getDouble("lat");
            lng = geoloc.getDouble("lng");
        }catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        if (lat == -999 || lng == -999)
            return;

        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (addresses == null)
            return;

        Address addr = addresses.get(0);
        String displayText = addr.getLocality() + ", " + addr.getAdminArea();

        this.setText(displayText);
    }
}