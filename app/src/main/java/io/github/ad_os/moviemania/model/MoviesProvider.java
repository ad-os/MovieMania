package io.github.ad_os.moviemania.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import io.github.ad_os.moviemania.ui.MainActivityFragment;

/**
 * Created by adhyan on 12/1/16.
 */
public class MoviesProvider extends ContentProvider{
    private static final String LOG_TAG = MoviesProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = getBuildUriMatcher();
    private MoviesDBHelper mOpenHelper;

    //Integer constants for the Uri
    private static final int MOVIE = 100;
    private static final int MOVIE_WITH_ID = 200;

    private  static UriMatcher getBuildUriMatcher() {
        // Build a Urimatcher by adding a specific code the return on a match
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;
        //add a code for each of uri you want
        matcher.addURI(authority, MoviesContract.MovieEntry.TABLE_MOVIES, MOVIE);
        matcher.addURI(authority, MoviesContract.MovieEntry.TABLE_MOVIES + "/#", MOVIE_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            //All movies selected
            case MOVIE: {
                retCursor = db.query(
                        MoviesContract.MovieEntry.TABLE_MOVIES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // Individual Movie based on Id
            case MOVIE_WITH_ID: {
                retCursor = db.query(
                        MoviesContract.MovieEntry.TABLE_MOVIES,
                        projection,
                        MoviesContract.MovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE: {
                return MoviesContract.MovieEntry.CONTENT_DIR_TYPE;
            }
            case MOVIE_WITH_ID : {
                return MoviesContract.MovieEntry.CONTENT_ITEM_TYPE;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri retUri;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:{
                //returns the row id of the newly inserted row.
                Cursor cursor = db.query(
                        MoviesContract.MovieEntry.TABLE_MOVIES,
                        null,
                        MoviesContract.MovieEntry._ID  + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        null
                );
                if (cursor == null) {
                    long _id = db.insert(MoviesContract.MovieEntry.TABLE_MOVIES, null, values);
                    //insert unless it is already contained in the database.
                    if (_id > 0) {
                        retUri = MoviesContract.MovieEntry.buildMovieUriWithId(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into: " + uri);
                    }
                } else {
                    retUri = MoviesContract.MovieEntry
                            .buildMovieUriWithId(cursor.getLong(MainActivityFragment.COL_MOVIE_ID));
                }
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        //By default cursor adapter objects will get notifications from notifyChange()
        getContext().getContentResolver().notifyChange(uri, null);
        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numDeleted;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:{
                //returns the number of rows deleted
                numDeleted = db.delete(
                        MoviesContract.MovieEntry.TABLE_MOVIES,
                        selection,
                        selectionArgs
                );
                break;
            }
            case MOVIE_WITH_ID:{
                numDeleted = db.delete(
                        MoviesContract.MovieEntry.TABLE_MOVIES,
                        MoviesContract.MovieEntry._ID + " = ? ",
                        new String[] {String.valueOf(ContentUris.parseId(uri))}
                );
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        if (numDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numUpdated;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:{
                numUpdated = db.update(
                        MoviesContract.MovieEntry.TABLE_MOVIES,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }
            case MOVIE_WITH_ID: {
                numUpdated = db.update(
                        MoviesContract.MovieEntry.TABLE_MOVIES,
                        values,
                        MoviesContract.MovieEntry._ID + " = ? ",
                        new String[] {String.valueOf(ContentUris.parseId(uri))}
                );
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }

        }
        if (numUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numInserted = 0;
        switch (sUriMatcher.match(uri)) {
            case MOVIE: {
                //allows multiple transactions
                db.beginTransaction();
                try {
                    for (ContentValues value: values) {
                        long _id = db.insert(
                                MoviesContract.MovieEntry.TABLE_MOVIES,
                                null,
                                value
                        );
                        if (_id != -1) {
                            numInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            }
            default:{
                return super.bulkInsert(uri, values);
            }
        }
        return numInserted;
    }
}
