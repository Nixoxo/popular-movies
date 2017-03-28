package io.github.nixoxo.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nixoxo on 28/03/2017.
 */

public class MovieProvider {

    private String[] allColumns = {SQLiteDatabaseHelper.COLUMN_ID, SQLiteDatabaseHelper.COLUMN_MOVIE, SQLiteDatabaseHelper.COLUMN_TITLE};
    private SQLiteDatabaseHelper dbHelper;
    private SQLiteDatabase db;


    public MovieProvider(Context context) {
        dbHelper = new SQLiteDatabaseHelper(context);
        //open();
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public FavoriteMovie createFavoriteMove(String movieid, String title) {
        ContentValues values = new ContentValues();
        values.put(SQLiteDatabaseHelper.COLUMN_MOVIE, movieid);
        values.put(SQLiteDatabaseHelper.COLUMN_TITLE, title);
        long insertId = db.insert(SQLiteDatabaseHelper.TABLE_FAVMOVIE, null, values);

        Cursor cursor = db.query(SQLiteDatabaseHelper.TABLE_FAVMOVIE, allColumns, SQLiteDatabaseHelper.COLUMN_ID + "=" + insertId, null, null, null, null);
        cursor.moveToFirst();
        FavoriteMovie favoriteMovie = cursorToFavoriteMovie(cursor);
        cursor.close();

        return favoriteMovie;
    }

    public FavoriteMovie getFavoriteMovie(String movieid){
        Cursor cursor = db.query(SQLiteDatabaseHelper.TABLE_FAVMOVIE, allColumns, SQLiteDatabaseHelper.COLUMN_MOVIE + "=" + movieid, null, null, null, null);
        if (cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToFirst();
        FavoriteMovie favoriteMovie = cursorToFavoriteMovie(cursor);
        cursor.close();
        return favoriteMovie;
    }

    public void removeFavoriteMovie(long _id){
        db.delete(SQLiteDatabaseHelper.TABLE_FAVMOVIE, SQLiteDatabaseHelper.COLUMN_ID + "=" + _id, null);
    }

    public List<FavoriteMovie> getAllFavoriteMovies() {
        List<FavoriteMovie> favoriteMovies = new ArrayList<>();

        Cursor cursor = db.query(SQLiteDatabaseHelper.TABLE_FAVMOVIE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            FavoriteMovie favoriteMovie = cursorToFavoriteMovie(cursor);
            favoriteMovies.add(favoriteMovie);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return favoriteMovies;
    }


    private FavoriteMovie cursorToFavoriteMovie(Cursor cursor) {
        FavoriteMovie favoriteMovie = new FavoriteMovie();
        favoriteMovie.set_id(cursor.getLong(0));
        favoriteMovie.setMovieid(cursor.getString(1));
        return favoriteMovie;
    }
}
