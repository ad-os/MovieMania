package io.github.ad_os.moviemania.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import io.github.ad_os.moviemania.model.MovieImageUrl;
import io.github.ad_os.moviemania.model.MoviesContract;

/**
 * Created by adhyan on 30/1/16.
 */
public class FetchImages extends AsyncTask<Void, Void, Bitmap[]> {

    private Context mContext;
    private MovieImageUrl mMovieImageUrl;
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";
    public static final String LOG_TAG = FetchImages.class.getSimpleName();

    public FetchImages(Context context, MovieImageUrl movieImageUrl) {
        mContext = context;
        mMovieImageUrl = movieImageUrl;
    }

    @Override
    protected void onPostExecute(Bitmap[] bitmaps) {
        super.onPostExecute(bitmaps);
        ContentValues values = new ContentValues();
        ContextWrapper cw = new ContextWrapper(mContext.getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        String urls = "";
        for (Bitmap bitmap : bitmaps) {
            Date d = new Date();
            CharSequence s  = DateFormat.format("MM-dd-yy hh-mm-ss", d.getTime());
            File myPath = new File(directory, s + ".jpg");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(myPath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (null != fos) {
                    try {
                        urls += myPath;
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        values.put(MoviesContract.FavoriteMovieEntry.COLUMN_LOCAL_URL, urls);
        mContext.getContentResolver().update(
                MoviesContract.FavoriteMovieEntry.CONTENT_URI,
                values,
                MoviesContract.FavoriteMovieEntry.COLUMN_THUMBNAIL + " = ? ",
                new String[]{mMovieImageUrl.getThumbnailImageUrl()}
        );
    }

    @Override
    protected Bitmap[] doInBackground(Void...params) {
        String[] urls = new String[]{IMAGE_BASE_URL + mMovieImageUrl.getThumbnailImageUrl(),
                IMAGE_BASE_URL + mMovieImageUrl.getBackPosterImageUrl()};
        Bitmap[] bitmaps = new Bitmap[urls.length];
        for (int i = 0; i < urls.length; i++) {
            Bitmap bitmap;
            InputStream in = null;
            try {
                URL u = new URL(urls[i]);
                HttpURLConnection connection = (HttpURLConnection) u.openConnection();
                connection.connect();
                in = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(in);
                bitmaps[i] = bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return bitmaps;
    }
}
