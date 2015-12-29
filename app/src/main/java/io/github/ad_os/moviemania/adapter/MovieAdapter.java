package io.github.ad_os.moviemania.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.ad_os.moviemania.R;
import io.github.ad_os.moviemania.model.Movie;
import io.github.ad_os.moviemania.ui.DetailActivity;

/**
 * Created by adhyan on 29/12/15.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private Context mContext;
    private Movie[] mMovies;

    public MovieAdapter(Context context, Movie[] movies) {
        super(context, 0 ,movies);
        mContext = context;
        mMovies = movies;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item, parent, false);
            viewHolder.poster = (ImageView) convertView.findViewById(R.id.movie_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Picasso.with(mContext).load(IMAGE_BASE_URL + movie.getPosterString()).into(viewHolder.poster);
        return convertView;
    }

    static class ViewHolder {
        ImageView poster;
    }
}
