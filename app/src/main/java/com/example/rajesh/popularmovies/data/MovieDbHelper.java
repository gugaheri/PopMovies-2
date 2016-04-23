package com.example.rajesh.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.rajesh.popularmovies.data.MovieContract.FavMoviesEntry;
/**
 * Manages a local database for favorite movies data.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    // Marking DB VERSION as 2 as Backdrop Image Path is also stored in DB
    private static final int DATABASE_VERSION = 2;
    static final String DATABASE_NAME = "fav_movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold the information about favorite movies
        final String SQL_CREATE_FAV_MOVIE_TABLE = "CREATE TABLE " + FavMoviesEntry.TABLE_NAME + " (" +
                FavMoviesEntry._ID + " INTEGER PRIMARY KEY," +
                FavMoviesEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                FavMoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavMoviesEntry.COLUMN_POSTER_PATH + " TEXT, " +
                FavMoviesEntry.COLUMN_BACKDROP_PATH + " TEXT, " +
                FavMoviesEntry.COLUMN_OVERVIEW + " TEXT, " +
                FavMoviesEntry.COLUMN_USER_RATING + " TEXT, " +
                FavMoviesEntry.COLUMN_RELEASE_DATE + " TEXT " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_FAV_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavMoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
