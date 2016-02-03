package io.github.ad_os.moviemania.ui;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.ad_os.moviemania.R;
import io.github.ad_os.moviemania.Utility;
import io.github.ad_os.moviemania.adapter.VideoThumbnailRecyclerViewAdapter;
import io.github.ad_os.moviemania.model.MovieImageUrl;
import io.github.ad_os.moviemania.model.MoviesContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIE_DETAIL_LOADER = 0;
    VideoThumbnailRecyclerViewAdapter mAdapter;
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500/";
    public static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private ArrayList<String>  mkeysList;
    @Bind(R.id.movie_release_date) TextView mReleaseDate;
    @Bind(R.id.ratingBar) RatingBar mRatingBar;
    @Bind(R.id.movie_synopsis) TextView mSynopsis;
    @Bind(R.id.movie_image) ImageView mImageView;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.fab) FloatingActionButton mFab;
    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;

    public static final int COL_MOVIE_TITLE = 1;
    public static final int COL_MOVIE_THUMBNAIL = 2;
    public static final int COL_MOVIE_PLOT = 3;
    public static final int COL_MOVIE_RATING = 4;
    public static final int COL_RELEASE_DATE = 5;
    public static final int COL_POSTER = 6;



    public DetailFragment() {
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
        final MovieImageUrl movieImageUrl = new MovieImageUrl();
        ButterKnife.bind(this, rootView);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mAdapter = new VideoThumbnailRecyclerViewAdapter(getActivity(), new ArrayList<String>());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.HORIZONTAL, false));
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getActivity().getIntent();
                ContentValues value = new ContentValues();
                String snackBar;
                Cursor cursorMovie = getActivity().getContentResolver().query(
                        intent.getData(),
                        MainFragment.MOVIE_COLUMNS,
                        MoviesContract.MovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(intent.getData()))},
                        null
                );
                if (cursorMovie != null) {
                    cursorMovie.moveToFirst();
                }
                Cursor cursorFavMovie = getActivity().getContentResolver().query(
                        MoviesContract.FavoriteMovieEntry.CONTENT_URI,
                        null,
                        MoviesContract.FavoriteMovieEntry.COLUMN_TITLE + " =  ? ",
                        new String[]{cursorMovie.getString(COL_MOVIE_TITLE)},
                        null
                );
                if (!cursorFavMovie.moveToFirst()) {
                    ContentValues[] values = new ContentValues[cursorMovie.getCount()];
                    value.put(MoviesContract.FavoriteMovieEntry.COLUMN_TITLE, cursorMovie.getString(COL_MOVIE_TITLE));
                    value.put(MoviesContract.FavoriteMovieEntry.COLUMN_THUMBNAIL, cursorMovie.getString(COL_MOVIE_THUMBNAIL));
                    value.put(MoviesContract.FavoriteMovieEntry.COLUMN_PLOT, cursorMovie.getString(COL_MOVIE_PLOT));
                    value.put(MoviesContract.FavoriteMovieEntry.COLUMN_POSTER, cursorMovie.getString(COL_POSTER));
                    value.put(MoviesContract.FavoriteMovieEntry.COLUMN_RATING, cursorMovie.getString(COL_MOVIE_RATING));
                    value.put(MoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, cursorMovie.getString(COL_RELEASE_DATE));
                    movieImageUrl.setThumbnailImageUrl(cursorMovie.getString(COL_MOVIE_THUMBNAIL));
                    movieImageUrl.setBackPosterImageUrl(cursorMovie.getString(COL_POSTER));
                    FetchImages fetchImages = new FetchImages(getActivity(), movieImageUrl);
                    fetchImages.execute();
                    values[0] = value;
                    getActivity().getContentResolver().bulkInsert(
                            MoviesContract.FavoriteMovieEntry.CONTENT_URI,
                            values
                    );
                    snackBar = getActivity().getString(R.string.snackbar_new_notification);
                } else {
                    snackBar = getActivity().getString(R.string.snackBar_notification_already);
                }
                Snackbar.make(view,snackBar, Snackbar.LENGTH_LONG)
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
                MainFragment.MOVIE_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {return; }
        String releaseDate = data.getString(MainFragment.COL_RELEASE_DATE);
        mReleaseDate.setText(releaseDate);
        String rating = data.getString(MainFragment.COL_MOVIE_RATING);
        mRatingBar.setRating(Float.parseFloat(rating)/2);
        mCollapsingToolbarLayout.setTitle(data.getString(MainFragment.COL_MOVIE_TITLE));
        String posterString = data.getString(MainFragment.COL_POSTER);
        String[] videoKeys = data.getString(MainFragment.COLUMN_VIDEOS_URL).split(",");
        mkeysList = new ArrayList<>(Arrays.asList(videoKeys));
        if (Utility.isNetworkAvailable(getActivity())) {
            Utility.setImage(getActivity(), mImageView, IMAGE_BASE_URL + posterString);
        } else {
            String[] urls = data.getString(MainFragment.COLUMN_LOCAL_URL).split(",", 2);
            String url =  "file://" + urls[1];
            Utility.setImage(getActivity(), mImageView, url);
        }
        mAdapter.addData(mkeysList);
        String synopsis = data.getString(MainFragment.COL_MOVIE_PLOT);
        mSynopsis.setText(synopsis);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
