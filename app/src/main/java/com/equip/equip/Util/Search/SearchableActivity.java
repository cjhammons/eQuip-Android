package com.equip.equip.Util.Search;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Curtis on 11/20/2017.
 */
@Deprecated
public class SearchableActivity extends Activity {

    String LOG_TAG = "SearchableActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "In search activity");

        Intent intent = getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            Log.d(LOG_TAG, "doing search");
            doSearch(query);
        }
    }

    void doSearch(String query){

    }

}
