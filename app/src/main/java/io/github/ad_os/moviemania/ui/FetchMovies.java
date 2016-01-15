package io.github.ad_os.moviemania.ui;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
import java.util.Vector;

import io.github.ad_os.moviemania.model.Movie;
import io.github.ad_os.moviemania.model.MoviesContract;

/**
 * Created by adhyan on 14/1/16.
 */
public class FetchMovies extends AsyncTask<String, Void, Void> {

    public final String LOG_TAG = FetchMovies.class.getSimpleName();
    HttpURLConnection connection = null;
    BufferedReader bufferedReader = null;
    private Context mContext;
    private Movie[] mMovies;

    public FetchMovies(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {
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
            insertMovies(moviesData);
            return null;
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

    public void insertMovies(String moviesData) {
        try {
            mMovies = parseMovieDetails(moviesData);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(mMovies.length);
            for (Movie movie : mMovies) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, movie.getMovieTitle());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_THUMBNAIL, movie.getPosterString());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_PLOT, movie.getPlotSynopsis());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_RATING, movie.getUserRating());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_POSTER, movie.getBackPosterString());
                cVVector.add(contentValues);
            }
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                mContext.getContentResolver().bulkInsert(
                        MoviesContract.MovieEntry.CONTENT_URI,
                        cvArray
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

}

