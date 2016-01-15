package io.github.ad_os.moviemania.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import io.github.ad_os.moviemania.R;
import io.github.ad_os.moviemania.ui.MainActivityFragment;

/**
 * Created by adhyan on 13/1/16.
 */
public class MovieAdapter extends CursorAdapter {
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView poster = (ImageView) view.findViewById(R.id.movie_image);
        TextView rating = (TextView) view.findViewById(R.id.rating);
        Picasso.with(context)
                .load(IMAGE_BASE_URL + cursor.getString(MainActivityFragment.COL_MOVIE_THUMBNAIL))
                .placeholder(R.mipmap.backgroundvetical).into(poster);
        rating.setText(getFloat(Float.parseFloat(cursor.getString(MainActivityFragment.COL_MOVIE_RATING))/2) + "/5");
    }

    public double getFloat(float value) {
        double n = (double)Math.round(value * 10d) / 10d;
        return n;
    }

}
