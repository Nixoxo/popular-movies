package io.github.nixoxo.popularmovies;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by nixoxo on 27/03/2017.
 */

public class Movie implements Serializable{

    private String poster_path;
    private boolean adult;
    private String overview;
    private String release_date;
    private String id;
    private String original_title;
    private String vote_average;
    private String title;



    public Movie(JSONObject object) throws JSONException {
        id = String.valueOf(object.get("id"));
        poster_path = String.valueOf(object.get("poster_path"));
        vote_average = String.valueOf(object.get("vote_average")) + " of 10";
        overview= String.valueOf(object.get("overview"));
        release_date = String.valueOf(object.get("release_date"));
        title = String.valueOf(object.get("title"));
    }

    public String getTitle() {
        return title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getId(){
        return id;
    }

    public String getVote_average() {
        return vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getOverview() {
        return overview;
    }
}
