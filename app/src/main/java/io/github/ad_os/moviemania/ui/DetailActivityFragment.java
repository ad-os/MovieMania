package io.github.ad_os.moviemania.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.ad_os.moviemania.R;
import io.github.ad_os.moviemania.model.Movie;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIE_DETAIL_LOADER = 0;
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500/";
    public static final String TAG = DetailActivity.class.getSimpleName();
    @Bind(R.id.movie_release_date) TextView mReleaseDate;
    @Bind(R.id.ratingBar) RatingBar mRatingBar;
    @Bind(R.id.movie_synopsis) TextView mSynopsis;
    @Bind(R.id.movie_image) ImageView mImageView;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.fab) FloatingActionButton mFab;

    public DetailActivityFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Movie saved to favorites", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                MainActivityFragment.MOVIE_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {return; }
        String releaseDate = data.getString(MainActivityFragment.COL_RELEASE_DATE);
        mReleaseDate.setText(releaseDate);

        String rating = data.getString(MainActivityFragment.COL_MOVIE_RATING);
        mRatingBar.setRating(Float.parseFloat(rating)/2);

        mCollapsingToolbarLayout.setTitle(data.getString(MainActivityFragment.COL_MOVIE_TITLE));

        String posterString = data.getString(MainActivityFragment.COL_POSTER);
        Picasso.with(getActivity())
                .load(IMAGE_BASE_URL + posterString)
                .placeholder(R.mipmap.background)
                .into(mImageView);

        String synopsis = data.getString(MainActivityFragment.COL_MOVIE_PLOT);
        mSynopsis.setText(synopsis);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
