package io.github.nixoxo.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import io.github.nixoxo.popularmovies.data.FavoriteMovie;
import io.github.nixoxo.popularmovies.data.MovieProvider;
import io.github.nixoxo.popularmovies.utils.NetworkUtils;

public class MovieActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_review;
    private Button mTrailerButton;
    private String youtubeKey;
    private Button mFavoriteButton;
    private Movie movie;
    private MovieProvider movieProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        movie = (Movie) getIntent().getSerializableExtra("movie");
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

        movieProvider = new MovieProvider(this);
        checkFavoriteButton();

        loadReviews(Integer.valueOf(movie.getId()));
        loadVideos(Integer.valueOf(movie.getId()));
    }

    private void checkFavoriteButton() {
        movieProvider.open();
        FavoriteMovie favoriteMovie = movieProvider.getFavoriteMovie(movie.getId());
        if (favoriteMovie != null) {
            mFavoriteButton.setText("Remove from Favorites");
        }else{
            mFavoriteButton.setText("MARK AS FAVORITE");
        }
        movieProvider.close();
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

            movieProvider.open();
            FavoriteMovie favoriteMovie = movieProvider.getFavoriteMovie(movie.getId());
            if (favoriteMovie == null) {
                movieProvider.createFavoriteMove(movie.getId(), movie.getTitle());
            } else {
                movieProvider.removeFavoriteMovie(favoriteMovie.get_id());
            }
            movieProvider.close();
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
