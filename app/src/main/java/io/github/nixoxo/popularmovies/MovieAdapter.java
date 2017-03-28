package io.github.nixoxo.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by nixoxo on 28/03/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder>{

    private final MovieAdapterOnClickHandler mClickHandler;

    private List<Movie> movieList;

    private Context context;


    public MovieAdapter(MovieAdapterOnClickHandler mClickHandler){

        this.mClickHandler = mClickHandler;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {

        Movie movie = movieList.get(position);
        Picasso.with(context).load("https://image.tmdb.org/t/p/w500/" + movie.getPoster_path()).into(holder.imageView);
        //Picasso.with(MainActivity.this).load("https://image.tmdb.org/t/p/w500/" + poster_path).into(child);



    }

    @Override
    public int getItemCount() {
        if (movieList == null) {
            return 0;
        }
        return movieList.size();
    }

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie id);
    }



    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;

        public MovieAdapterViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.movie_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie movie = movieList.get(adapterPosition);
            mClickHandler.onClick(movie);

        }
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
        notifyDataSetChanged();
    }
}
