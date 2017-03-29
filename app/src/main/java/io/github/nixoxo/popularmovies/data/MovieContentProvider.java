package io.github.nixoxo.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by nixoxo on 29/03/2017.
 */

public class MovieContentProvider extends ContentProvider{

    public static final int MOVIES = 100;
    public static final int MOVIE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIE_WITH_ID);
        return uriMatcher;
    }

    private MovieDbHelper mMovieDbHelper;

    @Override
    public boolean onCreate() {

        Context context = getContext();
        mMovieDbHelper = new MovieDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        Cursor retCursor;
        switch (match) {
            case MOVIES:
                retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknow uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri returnUri;
        switch (match) {
            case MOVIES:
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                }else{
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknow uri: " + uri);
        }

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereargs) {

        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int deleted = 0;
        switch (match) {
            case MOVIES:
                deleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, where, whereargs);
                break;
            case MOVIE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                deleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, MovieContract.MovieEntry.COLUMN_MOVIEID + "=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (deleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        return 0;
    }
}
