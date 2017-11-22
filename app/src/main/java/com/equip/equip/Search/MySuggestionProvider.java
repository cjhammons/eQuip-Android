package com.equip.equip.Search;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by Curtis on 11/20/2017.
 */

public class MySuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.equip.equip.Search.MySuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public MySuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
