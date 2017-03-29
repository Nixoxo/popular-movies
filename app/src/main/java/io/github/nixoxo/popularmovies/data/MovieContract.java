package io.github.nixoxo.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by nixoxo on 29/03/2017.
 */

public class MovieContract {

    public static final String AUTHORITY = "io.github.nixoxo.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        // Table Name
        public static final String TABLE_NAME = "movies";

        // Columns
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_MOVIEID = "movieid";
    }
}
