package com.example.rajesh.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.rajesh.popularmovies.data.MovieContract;
import com.example.rajesh.popularmovies.data.MovieContract.FavMoviesEntry;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a grid view for main activity.
 */
public class MainActivityFragment extends Fragment{

    /**
     * A callback interface that all activities containing this fragment must implement.
     * This mechanism allows activities to be notified of item selections.
     */
    public interface Callback {
        /**
         * DetailActivityFragment Callback for when an item has been selected.
         */
        public void onItemSelected(ArrayList<ArrayList<String>> movieList, String movieJsonStr, int position);
    }

    public MainActivityFragment() {
    }

    public final String LOG_TAG_FRAGMENT = MainActivityFragment.class.getSimpleName();
//    public final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private ImageAdapter mImageAdapter;
    private String[] mPosterLinks;
    // String to contain the raw JSON response as a string.
    public String movieJsonStr;
    // ArrayList to contain favorite movies info as an ArrayList of String
    public ArrayList<ArrayList<String>> movieList = new ArrayList<ArrayList<String>>();
    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    // Specify the columns we need.
    private static final String[] FAV_MOVIES_COLUMNS = {
            FavMoviesEntry.COLUMN_TITLE,
            FavMoviesEntry.COLUMN_POSTER_PATH,
            FavMoviesEntry.COLUMN_RELEASE_DATE,
            FavMoviesEntry.COLUMN_USER_RATING,
            FavMoviesEntry.COLUMN_OVERVIEW,
            FavMoviesEntry.COLUMN_MOVIE_ID,
            FavMoviesEntry.COLUMN_BACKDROP_PATH
    };
    // These indices are tied to FAV_MOVIES_COLUMNS. If FAV_MOVIES_COLUMNS changes, these must change.
    public static final int COL_MOVIE_TITLE = 0;
    public static final int COL_MOVIE_POSTER_PATH = 1;
    public static final int COL_MOVIE_RELEASE_DATE = 2;
    public static final int COL_MOVIE_USER_RATING = 3;
    public static final int COL_MOVIE_OVERVIEW = 4;
    public static final int COL_MOVIE_ID = 5;
    public static final int COL_MOVIE_BACKDROP_PATH = 6;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.movie_fragment, menu);
    }

    /** Method for calling a background thread with parameter from Settings Menu */
    public void updateMovieData(){
        String sortBy = Utility.getPreferredSortBy(getActivity());

        if (sortBy.equals(getString(R.string.pref_favorites))){
            getFavoriteMovies();
        }else {
            if(Utility.isNetworkAvailable(getContext())) {
                new FetchMovieTask().execute(sortBy);
            } else {
                mPosterLinks=new String[0];
                mImageAdapter.updateData(mPosterLinks);
                Toast.makeText(getActivity(), "No Network Access!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks
        // on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArray("posterLinks", mPosterLinks);
        outState.putString("movieJsonStr", movieJsonStr);
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }

        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


//        Log.v(LOG_TAG_FRAGMENT, "onCreateView of MainActivityFragment");

        //savedInstanceState used to restore data on rotation of device
        if(savedInstanceState == null || !savedInstanceState.containsKey("posterLinks")) {
            mPosterLinks=new String[0];
            movieJsonStr = null;
            mImageAdapter = new ImageAdapter(getActivity(), mPosterLinks);
            updateMovieData();
        }
        else {
            mPosterLinks = savedInstanceState.getStringArray("posterLinks");
            movieJsonStr = savedInstanceState.getString("movieJsonStr");
            mImageAdapter = new ImageAdapter(getActivity(), mPosterLinks);

            // To get Favorite Movie Details in case of rotation of device
            String sortBy = Utility.getPreferredSortBy(getActivity());
            if (sortBy.equals(getString(R.string.pref_favorites))) {
                getFavoriteMovies();
            }
        }

//        mImageAdapter = new ImageAdapter(getActivity(), mPosterLinks);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView)rootView.findViewById(R.id.grid_view);
        gridView.setAdapter(mImageAdapter);

        // Notify MainActivity via Callback on clicking the poster thumbnail
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((Callback) getActivity()).onItemSelected(movieList, movieJsonStr, position);
                mPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        if (mPosition != GridView.INVALID_POSITION) {
            gridView.smoothScrollToPosition(mPosition);
        }

        return rootView;
    }

    // Method to get Favorite Movies from database and update the image adapter accordingly
    public void getFavoriteMovies(){
        Cursor movieCursor = getActivity().getContentResolver().query(
                MovieContract.FavMoviesEntry.CONTENT_URI,
                FAV_MOVIES_COLUMNS,
                null,
                null,
                null
        );

        Log.v(LOG_TAG_FRAGMENT, "Number of Favorite Movies: " + movieCursor.getCount());
        ArrayList<String> posterLink = new ArrayList<String>();

        while (movieCursor.moveToNext()){
//            Log.v(LOG_TAG_FRAGMENT, "Movie Title from DB: " + movieCursor.getString(COL_MOVIE_TITLE));
//            Log.v(LOG_TAG_FRAGMENT, "Poster Link from DB: " + movieCursor.getString(COL_MOVIE_POSTER_PATH));
//            Log.v(LOG_TAG_FRAGMENT, "Backdrop Link from DB: " + movieCursor.getString(COL_MOVIE_BACKDROP_PATH));

            posterLink.add(movieCursor.getString(COL_MOVIE_POSTER_PATH));

            ArrayList<String> movieInfo = new ArrayList<String>();

            movieInfo.add(movieCursor.getString(COL_MOVIE_TITLE));
            movieInfo.add(movieCursor.getString(COL_MOVIE_POSTER_PATH));
            movieInfo.add(movieCursor.getString(COL_MOVIE_RELEASE_DATE));
            movieInfo.add(movieCursor.getString(COL_MOVIE_USER_RATING));
            movieInfo.add(movieCursor.getString(COL_MOVIE_OVERVIEW));
            movieInfo.add(String.valueOf(movieCursor.getInt(COL_MOVIE_ID)));
            movieInfo.add(movieCursor.getString(COL_MOVIE_BACKDROP_PATH));

            movieList.add(movieInfo);
        }

        mPosterLinks = posterLink.toArray(new String[posterLink.size()]);
        mImageAdapter.updateData(mPosterLinks);

        movieCursor.close();
    }


    /** Background Thread for getting Movie Details through the network call to an API */
    public class FetchMovieTask extends AsyncTask<String, Void, String[]>{

        public final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params){
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                final String SCHEME="http";
                final String FORECAST_BASE_URL="//api.themoviedb.org/3/movie/";
                final String APIKEY_PARAM="api_key";

                Uri.Builder builtUri=new Uri.Builder();
                builtUri.scheme(SCHEME);
                builtUri.path(FORECAST_BASE_URL);
                builtUri.appendPath(params[0]);
                builtUri.appendQueryParameter(APIKEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY);
                Log.v(LOG_TAG, "Build URI:" + builtUri.toString());

                URL url=new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    //movieJsonStr = null;
                    return null;
                }
                movieJsonStr = buffer.toString();
                //Log.v(LOG_TAG, "Movie JSON String:" + movieJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attempting
                // to parse it.
                //movieJsonStr = null;
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try{

                String[] moviePosters = new JSONParser().getPosterDataFromJson(movieJsonStr);
//                Log.v(LOG_TAG, "Movie poster link:" + moviePosters[0]);

                return moviePosters;
            }
            catch (JSONException e) {
                Log.e(LOG_TAG, "JSON Related Error", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            mPosterLinks = strings;
            mImageAdapter.updateData(mPosterLinks);
        }
    }
}


