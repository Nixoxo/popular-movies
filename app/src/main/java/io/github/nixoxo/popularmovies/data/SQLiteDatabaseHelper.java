package io.github.nixoxo.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by nixoxo on 28/03/2017.
 */

public class SQLiteDatabaseHelper extends SQLiteOpenHelper

{

        /*
         * Instantiates an open helper for the provider's SQLite data repository
         * Do not do database creation and upgrade here.
         */

    public static final String TABLE_FAVMOVIE = "favmovie";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MOVIE = "movieid";
    public static final String COLUMN_TITLE = "title";

    private static final String DATABASE_NAME = "favmovie.db";
    private static final int DATABASE_VERSION = 2;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_FAVMOVIE + "( " + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_MOVIE
            + " text not null, " + COLUMN_TITLE
            +" text);";

    public SQLiteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
     * Creates the data repository. This is called when the provider attempts to open the
     * repository and SQLite reports that it doesn't exist.
     */
    public void onCreate(SQLiteDatabase db) {
        // Creates the main table
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteDatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVMOVIE);
        onCreate(db);
    }
}