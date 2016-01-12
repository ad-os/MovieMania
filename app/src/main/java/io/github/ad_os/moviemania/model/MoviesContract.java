package io.github.ad_os.moviemania.model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by adhyan on 11/1/16.
 */
public class MoviesContract {
    public static final String CONTENT_AUTHORITY = "io.github.ad_os.moviesmania";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class MovieEntry implements BaseColumns {
        //table name
        public static final String TABLE_MOVIES = "movie";
        //columns
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_THUMBNAIL = "thumbnail_string";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER = "poster_string";
        public static final String COLUMN_FAVORITE = "favorite";

        //create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(TABLE_MOVIES)
                .build();
        // create cursor of base type directory for multiple items
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIES;
        // create cursor of base time item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIES;

        //for building Uris on insertion
        public static Uri buildMovieUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
