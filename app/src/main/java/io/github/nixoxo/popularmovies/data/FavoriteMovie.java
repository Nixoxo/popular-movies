package io.github.nixoxo.popularmovies.data;

/**
 * Created by nixoxo on 28/03/2017.
 */

public class FavoriteMovie {
    private long _id;
    private String movieid;
    private String title;

    public FavoriteMovie() {
    }

    public long get_id() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getMovieid() {
        return movieid;
    }

    public void setMovieid(String movieid) {
        this.movieid = movieid;
    }

    @Override
    public String toString() {
        return String.valueOf(movieid);
    }
}
