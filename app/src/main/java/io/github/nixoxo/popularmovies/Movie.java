package io.github.nixoxo.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by nixoxo on 27/03/2017.
 */

public class Movie implements Parcelable{

    private String id;
    private String poster_path;
    private String vote_average;
    private String overview;
    private String release_date;
    private String title;



    public Movie(JSONObject object) throws JSONException {
        id = String.valueOf(object.get("id"));
        poster_path = String.valueOf(object.get("poster_path"));
        vote_average = String.valueOf(object.get("vote_average")) + " of 10";
        overview= String.valueOf(object.get("overview"));
        release_date = String.valueOf(object.get("release_date"));
        title = String.valueOf(object.get("title"));
    }

    protected Movie(Parcel in) {
        String[] data = new String[6];
        in.readStringArray(data);

        id = data[0];
        poster_path = data[1];
        vote_average = data[2];
        overview = data[3];
        release_date = data[4];
        title = data[5];
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeStringArray(new String[]{
                this.id,
                this.poster_path,
                this.vote_average,
                this.overview,
                this.release_date,
                this.title});

    }
}
