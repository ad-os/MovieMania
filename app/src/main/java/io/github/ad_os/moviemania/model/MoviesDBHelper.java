package io.github.ad_os.moviemania.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import io.github.ad_os.moviemania.sync.MovieSyncAdapter;

/**
 * Created by adhyan on 12/1/16.
 */
public class MoviesDBHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = MoviesDBHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 16;

    public MoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MoviesContract.MovieEntry.TABLE_MOVIES + "( " +
                MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_THUMBNAIL + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_PLOT + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_RATING + " INTEGER NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_LOCAL_URL + " TEXT " +
                " );";

        final String CREATE_FAVORITE_TABLE = "CREATE TABLE " +
                MoviesContract.FavoriteMovieEntry.TABLE_FAVORITES + "( " +
                MoviesContract.FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.FavoriteMovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.FavoriteMovieEntry.COLUMN_THUMBNAIL + " TEXT NOT NULL, " +
                MoviesContract.FavoriteMovieEntry.COLUMN_PLOT + " TEXT NOT NULL, " +
                MoviesContract.FavoriteMovieEntry.COLUMN_RATING + " INTEGER NOT NULL, " +
                MoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesContract.FavoriteMovieEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                MoviesContract.FavoriteMovieEntry.COLUMN_LOCAL_URL + " TEXT " +
                " );";
        db.execSQL(CREATE_MOVIE_TABLE);
        db.execSQL(CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //deletes the whole table when new database version is created.
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_MOVIES);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.FavoriteMovieEntry.TABLE_FAVORITES);
        onCreate(db);
    }
}
