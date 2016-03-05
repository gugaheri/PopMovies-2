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
import android.widget.GridView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        /*ArrayList<String> weekForecast=new ArrayList<String>();
        weekForecast.add("Today - Sunny - 88/63");
        weekForecast.add("Tomorrow - Foggy - 70/46");
        weekForecast.add("Weds - Cloudy - 72/63");
        weekForecast.add("Thurs - Rainy - 64/51");
        weekForecast.add("Fri - Foggy - 70/46");
        weekForecast.add("Sat - Sunny - 76/68");

        ArrayAdapter<String> mForecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.grid_item, weekForecast);*/

        /*ArrayList<ImageView> weekForecast=new ArrayList<ImageView>();
        ImageView droid = new ImageView(getContext());
        droid.setImageResource(R.drawable.ic_launcher);
        weekForecast.add(droid);
        weekForecast.add(droid);
        weekForecast.add(droid);
        weekForecast.add(droid);
        ArrayAdapter<ImageView> mForecastAdapter = new ArrayAdapter<ImageView>(getActivity(), R.layout.grid_item, weekForecast);*/


        /*GridView gridView = (GridView)rootView.findViewById(R.id.grid_view);
        gridView.setAdapter(mForecastAdapter);*/

        GridView gridView = (GridView)rootView.findViewById(R.id.grid_view);
        gridView.setAdapter(new ImageAdapter(getActivity()));


        return rootView;

    }

    public class FetchMovieTask extends AsyncTask<String, Void, Void>{

        public final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        @Override
        protected Void doInBackground(String... params){
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //URL url = new URL("http://api.themoviedb.org/3/discover/movie/?sort_by=popular.desc&api_key=cbb2a0a4759143e9eb5a8fbaa69eac66");
                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7&APPID=44db6a862fba0b067b1930da0d769e98");
                // Create the request to OpenWeatherMap, and open the connection


                final String SCHEME="http";
                final String FORECAST_BASE_URL="//api.themoviedb.org/3/discover/movie/";
                final String QUERY_PARAM="sort_by";
                final String APIKEY_PARAM="api_key";

                Uri.Builder builtUri=new Uri.Builder();
                builtUri.scheme(SCHEME);
                builtUri.path(FORECAST_BASE_URL);
                builtUri.appendQueryParameter(QUERY_PARAM, params[0]);
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
                    // Nothing to do.
                    //forecastJsonStr = null;
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
                    //forecastJsonStr = null;
                    return null;
                }
                forecastJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Movie JSON String:" + forecastJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                //forecastJsonStr = null;
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

            return null;
        }

    }
}


