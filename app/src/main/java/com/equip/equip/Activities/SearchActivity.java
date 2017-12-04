package com.equip.equip.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.algolia.instantsearch.helpers.InstantSearch;
import com.algolia.instantsearch.helpers.Searcher;
import com.algolia.instantsearch.ui.views.Hits;
import com.algolia.instantsearch.utils.ItemClickSupport;
import com.equip.equip.EquipApplication;
import com.equip.equip.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by curtis on 12/4/17.
 */

public class SearchActivity extends Activity {

    static Searcher mSearcher;
    static InstantSearch mHelper;

    Hits mHitListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mSearcher = Searcher.create(EquipApplication.ALGOLIA_APP_ID,
                EquipApplication.ALGOLIA_SEARCH_API_KEY,
                EquipApplication.ALGOLIA_EQUIPMENT_INDEX_NAME);

        mHelper = new InstantSearch(this, mSearcher);

        mHitListView = findViewById(R.id.search_hits);
        mHitListView.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, int position, View v) {
                JSONObject hit = mHitListView.get(position);
                String key = "";
                try {
                    key = hit.getString("key");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (key.equals("")) {
                    return;
                }
                Intent intent = new Intent(SearchActivity.this, EquipmentDetailActivity.class);
                intent.putExtra("equipmentKey", key);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onDestroy() {
        mSearcher.destroy();
        super.onDestroy();
    }


}
