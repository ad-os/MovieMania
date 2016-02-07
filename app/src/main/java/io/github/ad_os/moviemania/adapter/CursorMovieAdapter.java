package io.github.ad_os.moviemania.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.ad_os.moviemania.R;
import io.github.ad_os.moviemania.ui.MainActivity;
import io.github.ad_os.moviemania.ui.MainFragment;

/**
 * Created by adhyan on 13/1/16.
 */
public class CursorMovieAdapter extends CursorAdapter {
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";
    public static final String LOG_TAG = CursorMovieAdapter.class.getSimpleName();

    public CursorMovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (MainFragment.favorite_layout) {
            String[] urls = cursor.getString(MainFragment.COLUMN_LOCAL_URL).split(",", 2);
            String url =  "file://" + urls[0];
            Picasso.with(context)
                    .load(url)
                    .placeholder(R.mipmap.backgroundvetical).into(viewHolder.poster);
        } else {
            Picasso.with(context)
                    .load(IMAGE_BASE_URL + cursor.getString(MainFragment.COL_MOVIE_THUMBNAIL))
                    .placeholder(R.mipmap.backgroundvetical).into(viewHolder.poster);
        }
        viewHolder.rating
                .setText(getFloat(Float.parseFloat(cursor.getString(MainFragment.COL_MOVIE_RATING))/2) + "/5");
    }

    public double getFloat(float value) {
        double n = (double)Math.round(value * 10d) / 10d;
        return n;
    }

    /*
     * Cache the children values of grid_item.
     */
    public static class ViewHolder {
        @Bind(R.id.movie_image) ImageView poster;
        @Bind(R.id.rating) TextView rating;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
