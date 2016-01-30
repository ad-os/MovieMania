package io.github.ad_os.moviemania.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.ad_os.moviemania.R;
import io.github.ad_os.moviemania.adapter.OfflineMovieAdapter;
import io.github.ad_os.moviemania.model.MoviesContract;


public class FavoriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    @Bind(R.id.movies_grid_view)
    GridView gridView;
    private static final int FAVORITE_MOVIE_LOADER = 1;
    private int mPosition = GridView.INVALID_POSITION;
    public static final String LOG_TAG = FavoriteFragment.class.getSimpleName();
    private OfflineMovieAdapter mOfflineMovieAdapter;
    public static final String[] MOVIE_COLUMNS = {
            MoviesContract.FavoriteMovieEntry._ID,
            MoviesContract.FavoriteMovieEntry.COLUMN_TITLE,
            MoviesContract.FavoriteMovieEntry.COLUMN_THUMBNAIL,
            MoviesContract.FavoriteMovieEntry.COLUMN_PLOT,
            MoviesContract.FavoriteMovieEntry.COLUMN_RATING,
            MoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE,
            MoviesContract.FavoriteMovieEntry.COLUMN_POSTER,
            MoviesContract.FavoriteMovieEntry.COLUMN_LOCAL_URL
    };

    public static final int COLUMN_FAVORITE__MOVIE_ID = 0;
    public  FavoriteFragment() {}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(FAVORITE_MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);
        ButterKnife.bind(this, rootView);
        mOfflineMovieAdapter = new OfflineMovieAdapter(getActivity(), null, FAVORITE_MOVIE_LOADER);
        gridView = (GridView) rootView.findViewById(R.id.movies_grid_view);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(MoviesContract.FavoriteMovieEntry.buildFavoriteMovieUriWithId(
                                    cursor.getLong(COLUMN_FAVORITE__MOVIE_ID)
                            ));
                    startActivity(intent);
                }
            }
        });
        gridView.setAdapter(mOfflineMovieAdapter);
        return  rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MoviesContract.FavoriteMovieEntry.CONTENT_URI;
        return new CursorLoader(
                getActivity(),
                uri,
                MOVIE_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mOfflineMovieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mOfflineMovieAdapter.swapCursor(null);
    }
}
