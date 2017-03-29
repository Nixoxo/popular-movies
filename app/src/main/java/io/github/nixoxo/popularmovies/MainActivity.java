package io.github.nixoxo.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.nixoxo.popularmovies.data.MovieContract;
import io.github.nixoxo.popularmovies.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {

    private GridLayoutManager gridLayoutManager;

    private RecyclerView mRecyclerView;

    private MovieAdapter mMovieAdapter;

    private Map<String, Movie> movieIdMap = new HashMap<>();

    private Set<String> favoriteMovies = new HashSet<>();

    private LastView lastView;


    private void initMovieData(boolean popular) {

        URL moviePoint = null;
        if (popular) {
            moviePoint = NetworkUtils.getPopularMoviePoint();
        } else {
            moviePoint = NetworkUtils.getTopRatedMoviePoint();
        }
        new MovieDBAsyncTask().execute(moviePoint);

    }

    private void switchView(){
        if (lastView.equals(LastView.FAVORITES)) {
            loadFavoriteMovieData();
        } else if (lastView.equals(LastView.POPULAR)) {
            initMovieData(true);
        }else{
            initMovieData(false);
        }
    }

    private void loadFavoriteMovieData() {
        if (favoriteMovies.size() == 0) {
            Toast.makeText(this, "There are no favorites at the moment.", Toast.LENGTH_SHORT).show();
            return;
        }
        List<Movie> favorites = new ArrayList<>();
        for (String moviesId : favoriteMovies) {
            if (movieIdMap.containsKey(moviesId)) {
                favorites.add(movieIdMap.get(moviesId));
            }
        }
        mMovieAdapter.setMovieList(favorites);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);

        gridLayoutManager = new GridLayoutManager(this, 2);


        mRecyclerView.setLayoutManager(gridLayoutManager);

        mMovieAdapter = new MovieAdapter(this);

        mRecyclerView.setAdapter(mMovieAdapter);
        getSupportLoaderManager().initLoader(0, null, this);

        Log.d("test", "here is the key");
        Log.d("test", BuildConfig.TMDB_API_KEY);
        lastView = LastView.POPULAR;
        initMovieData(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.top_rated) {
            lastView = LastView.TOPRATED;
            Toast.makeText(this, "Loading Top Rated movies", Toast.LENGTH_SHORT).show();
            initMovieData(false);
        } else if (item.getItemId() == R.id.popular) {
            lastView = LastView.POPULAR;
            Toast.makeText(this, "Loading Popular movies", Toast.LENGTH_SHORT).show();
            initMovieData(true);
        } else if (item.getItemId() == R.id.favorites) {
            lastView = LastView.FAVORITES;
            Toast.makeText(this, "Loading Favorite movies", Toast.LENGTH_SHORT).show();
            loadFavoriteMovieData();
        }

        Log.d("menu action", String.valueOf(item.getItemId()));
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(0, null, this);
    }


    @Override
    public void onClick(Movie movie) {
        Context context = this;
        Class destinationClass = MovieActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);

        intentToStartDetailActivity.putExtra("movie", movie);
        startActivity(intentToStartDetailActivity);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mMovieData = null;

            @Override
            protected void onStartLoading() {
                if (mMovieData != null) {
                    deliverResult(mMovieData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                Cursor query = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
                return query;
            }

            @Override
            public void deliverResult(Cursor data) {
                mMovieData = data;
                super.deliverResult(data);
                switchView();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int movieIdCol = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIEID);
        favoriteMovies = new HashSet<>();
        while (data.moveToNext()) {
            favoriteMovies.add(data.getString(movieIdCol));
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        favoriteMovies = new HashSet<>();

    }

    public class MovieDBAsyncTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            //mLoadingIndicator.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL movieUrl = urls[0];
            String movieResult = null;
            try {
                movieResult = NetworkUtils.getResponseFromHttpUrl(movieUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movieResult;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject result = new JSONObject(s);
                JSONArray results = result.getJSONArray("results");
                Log.d("json", String.valueOf(result));
                List<Movie> items = new ArrayList<>();
                for (int index = 0; index < results.length(); index++) {
                    JSONObject jsonObject = results.getJSONObject(index);
                    Movie movie = new Movie(jsonObject);

                    items.add(movie);
                    if (!movieIdMap.containsKey(movie.getId())) {
                        movieIdMap.put(movie.getId(), movie);
                    }
                }
                mMovieAdapter.setMovieList(items);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
