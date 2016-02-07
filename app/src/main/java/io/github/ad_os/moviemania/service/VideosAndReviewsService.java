package io.github.ad_os.moviemania.service;

    import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
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

import io.github.ad_os.moviemania.BuildConfig;
import io.github.ad_os.moviemania.model.MoviesContract;

/**
 * Created by adhyan on 7/2/16.
 */
public class VideosAndReviewsService extends IntentService {


    private String mMovieTile;
    public static final String LOG_TAG = VideosAndReviewsService.class.getSimpleName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public VideosAndReviewsService() {
        super("VideosAndReviewsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String movieId = intent.getStringExtra("id");
        String movieType = intent.getStringExtra("type");
        mMovieTile = intent.getStringExtra("title");
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        final String APP_ID_PARAM = "api_key";
        String base_url;
        if (movieType.equals("videos")) {
            base_url = "http://api.themoviedb.org/3/movie/" + movieId + "/videos?";
        } else {
            base_url = "http://api.themoviedb.org/3/movie/" + movieId + "/reviews?";
        }
        Uri buildUri = Uri.parse(base_url).buildUpon()
                .appendQueryParameter(APP_ID_PARAM, BuildConfig.MOVIES_DB_API_KEY)
                .build();
        try {
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
            if (movieType.equals("videos")) {
                insertMovieVideos(moviesData);
            } else {
                insertMovieReviews(moviesData);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void insertMovieVideos(String moviesData) throws JSONException {
        String videoKeys = parseMovieVideos(moviesData);
        ContentValues values = new ContentValues();
        values.put(MoviesContract.MovieEntry.COLUMN_VIDEOS_URL, videoKeys);
        getContentResolver().update(
                MoviesContract.MovieEntry.CONTENT_URI,
                values,
                MoviesContract.MovieEntry.COLUMN_TITLE + " = ? ",
                new String[]{mMovieTile}
        );
    }

    public void insertMovieReviews(String moviesData) throws JSONException {
        String content = parseMovieReviews(moviesData);
        ContentValues values = new ContentValues();
        values.put(MoviesContract.MovieEntry.COLUMN_REVIEWS, content);
        getContentResolver().update(
                MoviesContract.MovieEntry.CONTENT_URI,
                values,
                MoviesContract.MovieEntry.COLUMN_TITLE + " = ? ",
                new String[]{mMovieTile}
        );
    }

    public String parseMovieVideos(String moviesData) throws JSONException {
        final String MOVIES_ARRAY = "results";
        final String MOVIE_VIDEO_KEY = "key";
        String videoKeys = "";
        JSONObject moviesDataJson = new JSONObject(moviesData);
        JSONArray moviesArray = moviesDataJson.getJSONArray(MOVIES_ARRAY);
        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movieObject = moviesArray.getJSONObject(i);
            if (videoKeys.equals("")) {
                videoKeys += movieObject.get(MOVIE_VIDEO_KEY);
            } else {
                videoKeys += "," + movieObject.get(MOVIE_VIDEO_KEY);
            }
        }
        return videoKeys;
    }

    public String parseMovieReviews(String moviesData) throws JSONException {
        final String REVIEWS_ARRAY = "results";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";
        String content = "";
        String authors = "";
        JSONObject reviewsDataJson = new JSONObject(moviesData);
        JSONArray reviewsArray = reviewsDataJson.getJSONArray(REVIEWS_ARRAY);
        for (int i = 0; i < reviewsArray.length(); i++) {
            JSONObject reviewObject = reviewsArray.getJSONObject(i);
            if (content.equals("")) {
                authors += reviewObject.getString(REVIEW_AUTHOR);
                content += reviewObject.getString(REVIEW_CONTENT);
            } else {
                authors += "|" + reviewObject.getString(REVIEW_AUTHOR);
                content += "|" + reviewObject.getString(REVIEW_CONTENT);
            }
        }
        return authors+ "||" + content;
    }
}
