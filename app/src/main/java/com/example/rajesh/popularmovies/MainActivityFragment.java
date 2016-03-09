package com.example.rajesh.popularmovies;

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

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view for main activity.
 */
public class MainActivityFragment extends Fragment {


    public MainActivityFragment() {
    }

    ImageAdapter mImageAdapter;
    String[] mPosterLinks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        if(savedInstanceState == null || !savedInstanceState.containsKey("posterLinks")) {
            mPosterLinks=new String[0];
        }
        else {
            mPosterLinks = savedInstanceState.getStringArray("posterLinks");
            /*for (String s : mPosterLinks) {
                Log.v("ROTATION-GET", "Movie Poster Link: " + s);
            }*/
        }
        /*if(savedInstanceState != null) {
            mPosterLinks = savedInstanceState.getStringArray("posterLinks");
            for (String s : mPosterLinks) {
                Log.v("ROTATION-GET", "Movie Poster Link: " + s);
            }
        }*/

    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.movie_fragment, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            new FetchMovieTask().execute("popular.desc"); //Network call
            //new FetchMovieTask().execute("vote_average.desc"); //Network call
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArray("posterLinks", mPosterLinks);
        /*for (String s : mPosterLinks) {
            Log.v("ROTATION-PUT", "Movie Poster Link: " + s);
        }*/
        super.onSaveInstanceState(outState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mImageAdapter = new ImageAdapter(getActivity(), mPosterLinks);
        GridView gridView = (GridView)rootView.findViewById(R.id.grid_view);
        gridView.setAdapter(mImageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), mImageAdapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    public class FetchMovieTask extends AsyncTask<String, Void, String[]>{

        public final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params){
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try {
                final String SCHEME="http";
                final String FORECAST_BASE_URL="//api.themoviedb.org/3/discover/movie/";
                final String QUERY_PARAM="sort_by";
                final String APIKEY_PARAM="api_key";

                Uri.Builder builtUri=new Uri.Builder();
                builtUri.scheme(SCHEME);
                builtUri.path(FORECAST_BASE_URL);
                builtUri.appendQueryParameter(QUERY_PARAM, params[0]);
                builtUri.appendQueryParameter(APIKEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY);

                //Log.v(LOG_TAG, "Build URI:" + builtUri.toString());

                URL url=new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    //movieJsonStr = null;
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
                //for (String s : moviePosters) {
                //    Log.v(LOG_TAG, "Movie Poster Link: " + s);
                //}
                //return null;
                return moviePosters;
            }
            catch (JSONException e) {
                Log.e(LOG_TAG, "JSON Related Error ", e);
                // JSONException handler
                //movieJsonStr = null;
                //return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            //mImageAdapter.clear();
            //mImageAdapter.addAll(Arrays.asList(strings));
            mPosterLinks = strings;
            mImageAdapter.updateData(mPosterLinks);
            //mImageAdapter.notifyDataSetChanged();

        }
    }
}


