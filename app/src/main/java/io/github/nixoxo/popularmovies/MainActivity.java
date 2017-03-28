package io.github.nixoxo.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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

import io.github.nixoxo.popularmovies.data.FavoriteMovie;
import io.github.nixoxo.popularmovies.data.MovieProvider;
import io.github.nixoxo.popularmovies.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private GridLayoutManager gridLayoutManager;

    private RecyclerView mRecyclerView;

    private MovieAdapter mMovieAdapter;

    private MovieProvider movieProvider;

    private Map<String, Movie> movieIdMap = new HashMap<>();


    private void loadMovieData(boolean popular) {

        URL moviePoint = null;

        if (popular) {
            moviePoint = NetworkUtils.getPopularMoviePoint();
        } else {
            moviePoint = NetworkUtils.getTopRatedMoviePoint();
        }
        new MovieDBAsyncTask().execute(moviePoint);

    }

    private void loadFavoriteMovieData() {

        List<FavoriteMovie> allFavoriteMovies = movieProvider.getAllFavoriteMovies();
        if (allFavoriteMovies.size() == 0) {
            Toast.makeText(this, "There are no favorites at the moment", Toast.LENGTH_SHORT).show();
            mMovieAdapter.setMovieList(new ArrayList<Movie>());
            return;
        }

        Set<String> favoriteMoviesIds = new HashSet<>();

        for (FavoriteMovie favoriteMovie : allFavoriteMovies) {
            favoriteMoviesIds.add(favoriteMovie.getMovieid());
        }

        List<Movie> favorites = new ArrayList<>();

        for (String moviesId : favoriteMoviesIds) {
            if (movieIdMap.containsKey(moviesId)) {
                favorites.add(movieIdMap.get(moviesId));
            }
        }
        mMovieAdapter.setMovieList(favorites);



        Log.d("favorite", String.valueOf(allFavoriteMovies.size()));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        movieProvider = new MovieProvider(this);
        movieProvider.open();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);

        gridLayoutManager = new GridLayoutManager(this, 2);


        mRecyclerView.setLayoutManager(gridLayoutManager);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);


        mMovieAdapter = new MovieAdapter(this);

        mRecyclerView.setAdapter(mMovieAdapter);

        loadMovieData(false);
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
            Toast.makeText(this, "Loading Top Rated movies", Toast.LENGTH_SHORT).show();
            loadMovieData(false);
        } else if (item.getItemId() == R.id.popular) {
            Toast.makeText(this, "Loading Popular movies", Toast.LENGTH_SHORT).show();
            loadMovieData(true);
        } else if (item.getItemId() == R.id.favorites) {
            Toast.makeText(this, "Loading Favorite movies", Toast.LENGTH_SHORT).show();
            loadFavoriteMovieData();
        }

        Log.d("menu action", String.valueOf(item.getItemId()));
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(Movie movie) {
        Context context = this;
        Class destinationClass = MovieActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);

        intentToStartDetailActivity.putExtra("movie", movie);
        startActivity(intentToStartDetailActivity);

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
