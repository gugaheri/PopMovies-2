package com.example.rajesh.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Rajesh on 15-Apr-16.
 */
public class MovieProvider extends ContentProvider{

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int FAV_MOVIES = 100;
    static final int MOVIE = 101;

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case FAV_MOVIES:
                return MovieContract.FavMoviesEntry.CONTENT_TYPE;
            case MOVIE:
                return MovieContract.FavMoviesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;


        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.
        matcher.addURI(authority, MovieContract.PATH_MOVIE, FAV_MOVIES);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*", MOVIE);

        // 3) Return the new matcher!
        //return null;
        return matcher;
    }


    //fav_movies.movie_id = ?
    private static final String sMovieIdSelection =
            MovieContract.FavMoviesEntry.TABLE_NAME+
                    "." + MovieContract.FavMoviesEntry.COLUMN_MOVIE_ID + " = ? ";

    private Cursor getMovieById(Uri uri, String[] projection, String sortOrder) {
        String MovieId = MovieContract.FavMoviesEntry.getMovieIdFromUri(uri);

        String[] selectionArgs = new String[] {MovieId};
        String selection = sMovieIdSelection;

        return mOpenHelper.getReadableDatabase().query(
                MovieContract.FavMoviesEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case FAV_MOVIES:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavMoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MOVIE: {
                retCursor = getMovieById(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAV_MOVIES: {
                long _id = db.insert(MovieContract.FavMoviesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.FavMoviesEntry.buildMovieUri(String.valueOf(_id));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowDeleted;

        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";

        // Use the uriMatcher to match the MOVIE we are going to handle.
        // If it doesn't match these, throw an UnsupportedOperationException.
        final int match = sUriMatcher.match(uri);
        // A null value deletes all rows.  In my implementation of this, I only notified
        // the uri listeners (using the content resolver) if the rowsDeleted != 0 or the selection
        // is null.
        switch (match) {
//            case MOVIE: {
            case FAV_MOVIES:{
                rowDeleted = db.delete(MovieContract.FavMoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Oh, and you should notify the listeners here.
        //getContext().getContentResolver().notifyChange(uri, null);
        // return the actual rows deleted
        //return 0;
        if (rowDeleted > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // This is a lot like the delete function.  We return the number of rows impacted by the update.
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowUpdated;

        // Use the uriMatcher to match the MOVIE we are going to handle.
        // If it doesn't match these, throw an UnsupportedOperationException.
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE: {
                rowUpdated = db.update(MovieContract.FavMoviesEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Oh, and you should notify the listeners here.
        //getContext().getContentResolver().notifyChange(uri, null);
        // Student: return the actual rows deleted
        //return 0;
        if (rowUpdated > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowUpdated;
    }
}
