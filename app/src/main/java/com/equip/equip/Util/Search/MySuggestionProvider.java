package com.equip.equip.Util.Search;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by Curtis on 11/20/2017.
 */

public class MySuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.equip.equip.UserSearch.MySuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public MySuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
