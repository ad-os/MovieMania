package io.github.ad_os.moviemania.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import io.github.ad_os.moviemania.BuildConfig;
import io.github.ad_os.moviemania.R;
import io.github.ad_os.moviemania.service.VideosAndReviewsService;
import io.github.ad_os.moviemania.model.Movie;
import io.github.ad_os.moviemania.model.MoviesContract;

/**
 * Created by adhyan on 27/1/16.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the weather, in milliseconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private String[] mImageUrls;


    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync Called.");
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        SharedPreferences preferences = getContext().getSharedPreferences(String.valueOf(R.string.content_authority), Context.MODE_PRIVATE);
        String sortOrder = preferences.getString("view", "popularity.desc");
        final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
        final String SORT_PARAM = "sort_by";
        final String APP_ID_PARAM = "api_key";
        try {
            Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(APP_ID_PARAM, BuildConfig.MOVIES_DB_API_KEY)
                    .appendQueryParameter(SORT_PARAM, sortOrder)
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
            return;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            return;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {
                    Log.e("MainFragment", "Error closing stream", e);
                }
            }
        }
    }

    public void insertVideosOrReviews(String id, String type, String title) {
        Intent intent = new Intent(getContext(), VideosAndReviewsService.class);
        intent.putExtra("id", id);
        intent.putExtra("type", type);
        intent.putExtra("title", title);
        getContext().startService(intent);
    }

    public void insertMovies(String moviesData) {
        Movie[] mMovies;
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
                contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
                insertVideosOrReviews(movie.getId(), "videos", movie.getMovieTitle());
                insertVideosOrReviews(movie.getId(), "reviews", movie.getMovieTitle());
                cVVector.add(contentValues);
            }
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                Cursor c = getContext().getContentResolver().query(
                        MoviesContract.MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
                if (c != null) {
                    getContext().getContentResolver().delete(
                            MoviesContract.MovieEntry.CONTENT_URI,
                            null,
                            null
                    );
                    c.close();
                }
                getContext().getContentResolver().bulkInsert(
                        MoviesContract.MovieEntry.CONTENT_URI,
                        cvArray
                );
                Log.d(LOG_TAG, "Movies Inserted");
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
        final String MOVIE_ID = "id";
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
            movie.setId(movieObject.getString(MOVIE_ID));
            movies[i] = movie;
        }
        return movies;
    }


    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }


    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}
