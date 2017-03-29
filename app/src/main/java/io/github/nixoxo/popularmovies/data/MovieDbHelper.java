package io.github.nixoxo.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nixoxo on 29/03/2017.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "movieDb.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 1;

    MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create tasks table (careful to follow SQL formatting rules)
        final String CREATE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                MovieContract.MovieEntry.COLUMN_MOVIEID + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
