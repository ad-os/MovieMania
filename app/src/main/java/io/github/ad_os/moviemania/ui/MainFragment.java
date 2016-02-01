package io.github.ad_os.moviemania.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.ad_os.moviemania.R;
import io.github.ad_os.moviemania.Utility;
import io.github.ad_os.moviemania.adapter.OnlineMovieAdapter;
import io.github.ad_os.moviemania.model.MoviesContract;
import io.github.ad_os.moviemania.sync.MovieSyncAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.movies_grid_view) GridView gridView;
    private static final int MOVIE_LOADER = 0;
    private int mPosition = GridView.INVALID_POSITION;
    public static final String LOG_TAG = MainFragment.class.getSimpleName();
    private static final String SELECTED_KEY = "selected_position";
    private OnlineMovieAdapter mOnlineMovieAdapter;
    public static final String[] MOVIE_COLUMNS = {
            MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_THUMBNAIL,
            MoviesContract.MovieEntry.COLUMN_PLOT,
            MoviesContract.MovieEntry.COLUMN_RATING,
            MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_POSTER,
            MoviesContract.MovieEntry.COLUMN_LOCAL_URL,
            MoviesContract.MovieEntry.COLUMN_VIDEOS_URL
    };

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_TITLE = 1;
    public static final int COL_MOVIE_THUMBNAIL = 2;
    public static final int COL_MOVIE_PLOT = 3;
    public static final int COL_MOVIE_RATING = 4;
    public static final int COL_RELEASE_DATE = 5;
    public static final int COL_POSTER = 6;
    public static final int COLUMN_LOCAL_URL = 7;
    public static final int COLUMN_VIDEOS_URL = 8;

    public MainFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        mOnlineMovieAdapter = new OnlineMovieAdapter(getActivity(), null, MOVIE_LOADER);
        gridView = (GridView) rootView.findViewById(R.id.movies_grid_view);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                Intent intent;
                if (cursor != null) {
                    intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(MoviesContract.MovieEntry.buildMovieUriWithId(
                                    cursor.getLong(MainFragment.COL_MOVIE_ID)
                            ));
                    startActivity(intent);
                }
                mPosition = position;
            }
        });
        gridView.setAdapter(mOnlineMovieAdapter);
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mainmenufragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String choice = "";
        mPosition = GridView.INVALID_POSITION;
        gridView.smoothScrollToPosition(0);
        if (id == R.id.popular_label) {
            choice = "popularity.desc";
            MovieSyncAdapter.syncImmediately(getActivity());
        } else if(id == R.id.highest_rated) {
            choice = "vote_count.desc";
            MovieSyncAdapter.syncImmediately(getActivity());
        } else if (id == R.id.favorite) {
            Intent intent = new Intent(getActivity(), FavoriteActivity.class);
            startActivity(intent);
        }
        Utility.userChoice(getActivity(), choice);
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieUri;
        movieUri = MoviesContract.MovieEntry.CONTENT_URI;
        return new CursorLoader(
                getActivity(),
                movieUri,
                MOVIE_COLUMNS,
                null,
                null,
                null
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mOnlineMovieAdapter.swapCursor(data);
        if (mPosition != GridView.INVALID_POSITION) {
            gridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mOnlineMovieAdapter.swapCursor(null);
    }

}
