package io.github.ad_os.moviemania.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.ad_os.moviemania.R;
import io.github.ad_os.moviemania.adapter.MovieAdapter;
import io.github.ad_os.moviemania.model.Movie;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    @Bind(R.id.movies_grid_view) GridView gridView;
    public static final String TAG = MainActivityFragment.class.getSimpleName();
    public static final String MOVIE_DETAILS = "MOVIE_DETAILS";
    private Movie[] mMovies;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        if (isNetworkAvailable()) {
            if (savedInstanceState == null || !savedInstanceState.containsKey("Movies")) {
                FetchMovies fetchMovies = new FetchMovies();
                fetchMovies.execute("popularity.desc");
            } else {
                mMovies = (Movie[]) savedInstanceState.getParcelableArray("Movies");
                initializeAdapter(mMovies);
            }
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray("Movies", mMovies);
        super.onSaveInstanceState(outState);
    }

    public class FetchMovies extends AsyncTask<String, Void, String> {

        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;

        @Override
        protected String doInBackground(String... params) {
            final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String APP_ID_PARAM = "api_key";
            final String APP_ID = "15ae93992b3faf50d91572189a738361";
            try {
                Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(APP_ID_PARAM, APP_ID)
                        .appendQueryParameter(SORT_PARAM, params[0])
                        .build();
                URL url = new URL(buildUri.toString());
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                StringBuilder stringBuilder = new StringBuilder();
                String line, moviesData;
                InputStream in = connection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(in));
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                moviesData = stringBuilder.toString();
                return moviesData;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
                        Log.e("MainActivityFragment", "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String moviesData) {
            try {
                mMovies = parseMovieDetails(moviesData);
                initializeAdapter(mMovies);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void initializeAdapter(Movie[] movies) {
        final Movie[] moviesData = movies;
        final MovieAdapter movieAdapter = new MovieAdapter(getActivity(), moviesData);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(MOVIE_DETAILS, moviesData);
                startActivity(intent);
            }
        });
        gridView.setAdapter(movieAdapter);
    }

    public Movie[] parseMovieDetails(String movieData) throws JSONException {
        final String MOVIES_ARRAY = "results";
        final String MOVIE_TITLE = "original_title";
        final String MOVIE_POSTER = "poster_path";
        final String MOVIE_PLOT = "overview";
        final String MOVIE_RATING = "vote_average";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_BACK_POSTER = "backdrop_path";
        JSONObject movieDataJson = new JSONObject(movieData);
        JSONArray moviesArray = movieDataJson.getJSONArray(MOVIES_ARRAY);
        Movie[] movies = new Movie[moviesArray.length()];
        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movieObject = moviesArray.getJSONObject(i);
            Movie movie = new Movie();
            movie.setMovieTitle(movieObject.getString(MOVIE_TITLE));
            movie.setPosterString(movieObject.getString(MOVIE_POSTER));
            movie.setPlotSynopsis(movieObject.getString(MOVIE_PLOT));
            movie.setUserRating(movieObject.getString(MOVIE_RATING));
            movie.setReleaseDate(movieObject.getString(MOVIE_RELEASE_DATE));
            movie.setBackPosterString(movieObject.getString(MOVIE_BACK_POSTER));
            movies[i] = movie;
        }
        return movies;
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
