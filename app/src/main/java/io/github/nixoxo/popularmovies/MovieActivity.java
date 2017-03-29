package io.github.nixoxo.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import io.github.nixoxo.popularmovies.data.MovieContract;
import io.github.nixoxo.popularmovies.utils.NetworkUtils;

public class MovieActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_review;
    private Button mTrailerButton;
    private String youtubeKey;
    private Button mFavoriteButton;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        movie = (Movie) getIntent().getParcelableExtra("movie");
        ImageView poster = (ImageView) findViewById(R.id.movie_poster);
        Picasso.with(this).load("https://image.tmdb.org/t/p/w500/" + movie.getPoster_path()).into(poster);

        TextView tv_title = (TextView) findViewById(R.id.movie_title);
        tv_title.setText(movie.getTitle());

        TextView tv_release_date = (TextView) findViewById(R.id.movie_release_date);
        tv_release_date.setText(movie.getRelease_date().substring(0, 4));

        TextView tv_vote_average = (TextView) findViewById(R.id.movie_vote_average);
        tv_vote_average.setText(movie.getVote_average());

        TextView tv_overview = (TextView) findViewById(R.id.movie_overview);
        tv_overview.setText(movie.getOverview());

        tv_review = (TextView) findViewById(R.id.movie_review);

        mTrailerButton = (Button) findViewById(R.id.trailer_button);
        mTrailerButton.setActivated(false);
        mTrailerButton.setOnClickListener(this);

        mFavoriteButton = (Button) findViewById(R.id.favorite_button);
        mFavoriteButton.setOnClickListener(this);

        checkFavoriteButton();

        loadReviews(Integer.valueOf(movie.getId()));
        loadVideos(Integer.valueOf(movie.getId()));
    }

    private void checkFavoriteButton() {

        Cursor query = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, MovieContract.MovieEntry.COLUMN_MOVIEID + "=?", new String[]{movie.getId()}, null);
        //String favoriteMovie_ = (String) query;
        //if (favoriteMovie != null) {

        if (query.moveToFirst()) {
            mFavoriteButton.setText("Remove from Favorites");
        } else {
            mFavoriteButton.setText("MARK AS FAVORITE");
        }

    }

    private void loadVideos(int id) {
        URL videoMoviePoint = NetworkUtils.getVideoMoviePoint(id);
        new MovieVideoTask().execute(videoMoviePoint);
    }

    private void loadReviews(int id) {
        URL reviewMoviePoint = NetworkUtils.getReviewMoviePoint(id);
        new MovieReviewTask().execute(reviewMoviePoint);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.trailer_button) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + youtubeKey)));
        } else if (view.getId() == R.id.favorite_button) {

            Cursor query = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, MovieContract.MovieEntry.COLUMN_MOVIEID + "=?", new String[]{movie.getId()}, null);
            if (query.moveToFirst()) {
                int idCol = query.getColumnIndex(MovieContract.MovieEntry._ID);
                String id = query.getString(idCol);
                Uri deleteUri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(id).build();
                Log.d("URI", "onClick: " + deleteUri);
                //int status = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, MovieContract.MovieEntry._ID + "=?", new String[]{id});
                int status = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, MovieContract.MovieEntry._ID + "=?", new String[]{id});
                if (status > 0) {
                    Toast.makeText(this, "Removed a movie from favorites", Toast.LENGTH_SHORT).show();
                }
                Log.d("Main Acitivity", "onClick: " + id + " status: " + status);
            }else{
                String id = movie.getId();
                String title = movie.getTitle();
                ContentValues values = new ContentValues();
                values.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
                values.put(MovieContract.MovieEntry.COLUMN_MOVIEID, id);
                Uri insert = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
                if (insert != null) {
                    Toast.makeText(this, "Added a new movie to favorites", Toast.LENGTH_SHORT).show();
                }
            }
            checkFavoriteButton();
        }
    }

    public class MovieReviewTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            //mLoadingIndicator.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL movieUrl = urls[0];
            String reviewResult = null;
            try {
                reviewResult = NetworkUtils.getResponseFromHttpUrl(movieUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return reviewResult;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject result = new JSONObject(s);
                JSONArray results = result.getJSONArray("results");

                String content = results.getJSONObject(0).get("content").toString();
                tv_review.setText(content);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class MovieVideoTask extends AsyncTask<URL, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL movieUrl = urls[0];
            String reviewResult = null;
            try {
                reviewResult = NetworkUtils.getResponseFromHttpUrl(movieUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return reviewResult;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject result = new JSONObject(s);
                JSONArray results = result.getJSONArray("results");
                JSONObject first = results.getJSONObject(0);
                youtubeKey = first.get("key").toString();
                mTrailerButton.setActivated(true);
                //


                Log.d("video", String.valueOf(results));
                //String content = results.getJSONObject(0).get("content").toString();
                //tv_review.setText(content);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
