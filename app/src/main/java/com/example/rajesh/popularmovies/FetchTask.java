package com.example.rajesh.popularmovies;


import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/** Background Thread for getting Trailer and/or Reviews through the network call to an API.
 * A single class to fetch Trailers or Reviews Info based on the type of FetchTask object created.
 */
public class FetchTask extends AsyncTask<String, Void, String[]> {

    public final String LOG_TAG = FetchTask.class.getSimpleName();
    private String mFetch;
    private DetailActivityFragment mDetailActivityFragment;

    public FetchTask(DetailActivityFragment detailActivityFragment){
        mDetailActivityFragment = detailActivityFragment;
    }

    public String getFetch() {
        return mFetch;
    }

    public void setFetch(String mFetch) {
        this.mFetch = mFetch;
    }

    @Override
    protected String[] doInBackground(String... params){
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        String movieReviewStr;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            final String SCHEME="http";
            final String FORECAST_BASE_URL="//api.themoviedb.org/3/movie/";
            String REVIEWS_TRAILERS = null;
            final String APIKEY_PARAM="api_key";

            switch (getFetch()){
                case "TRAILERS":
                    REVIEWS_TRAILERS="videos";
                    break;
                case "REVIEWS":
                    REVIEWS_TRAILERS="reviews";
                    break;
            }

            Uri.Builder builtUri=new Uri.Builder();
            builtUri.scheme(SCHEME);
            builtUri.path(FORECAST_BASE_URL);
            builtUri.appendPath(params[0]);
            builtUri.appendPath(REVIEWS_TRAILERS);
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
                return null;
            }
            movieReviewStr = buffer.toString();
            //Log.v(LOG_TAG, "Movie JSON String:" + movieJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attempting to parse it.
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
            String[] movieReviewsTrailers = new String[0];
            switch (getFetch()){
                case "TRAILERS":
                    movieReviewsTrailers = new JSONParser().getTrailerDataFromJson(movieReviewStr);
                    break;
                case "REVIEWS":
                    movieReviewsTrailers = new JSONParser().getReviewDataFromJson(movieReviewStr);
                    break;
            }

            return movieReviewsTrailers;
        }
        catch (JSONException e) {
            Log.e(LOG_TAG, "JSON Related Error", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(String[] strings) {

        switch (getFetch()){
            case "TRAILERS":
                mDetailActivityFragment.trailers = strings;
                mDetailActivityFragment.trailerAdapter.updateData(mDetailActivityFragment.trailers);
                // Setting Share Intent as now we have Trailer info
                mDetailActivityFragment.setShareIntent();
                break;
            case "REVIEWS":
                mDetailActivityFragment.reviews = new ArrayList<String>(Arrays.asList(strings));
                mDetailActivityFragment.reviewAdapter.clear();
                mDetailActivityFragment.reviewAdapter.addAll(mDetailActivityFragment.reviews);
                mDetailActivityFragment.reviewAdapter.notifyDataSetChanged();
                break;
            }
    }
}