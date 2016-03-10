package com.example.rajesh.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Rajesh on 05-Mar-16.
 */
public class JSONParser {

    // These are the names of the JSON objects that need to be extracted.
    final String THDB_MOVIE = "results";
    final String POSTER_IMG = "poster_path";
    final String BASE_URL = "http://image.tmdb.org/t/p/";
    final String IMG_SIZE = "w185";
    final String ORIGINAL_TITLE = "original_title";
    final String OVERVIEW = "overview";
    final String VOTE_AVERAGE = "vote_average";
    final String RELEASE_DATE = "release_date";

    public String[] getPosterDataFromJson(String movieJsonStr)
            throws JSONException {

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(THDB_MOVIE);
        String[] resultStr = new String[movieArray.length()];

        for(int i = 0; i < movieArray.length(); i++) {
            // Get the JSON object representing the movie detail
            JSONObject movieDetail = movieArray.getJSONObject(i);

            //JSONObject posterObject = movieDetail.getJSONObject(POSTER_IMG);
            String posterLink=movieDetail.getString(POSTER_IMG);
            //Building the movie poster URI
            resultStr[i] = BASE_URL + IMG_SIZE + posterLink;
        }

        return resultStr;
    }

    public ArrayList<String> getMovieDataFromJson(String movieJsonStr, int position )
            throws JSONException {

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(THDB_MOVIE);
        ArrayList<String> resultStr = new ArrayList<String>();

        JSONObject movieDetail = movieArray.getJSONObject(position);
        resultStr.add(movieDetail.getString(ORIGINAL_TITLE));
        resultStr.add(BASE_URL + IMG_SIZE + movieDetail.getString(POSTER_IMG));
        resultStr.add(movieDetail.getString(RELEASE_DATE));
        resultStr.add(movieDetail.getString(VOTE_AVERAGE));
        resultStr.add(movieDetail.getString(OVERVIEW));

        return resultStr;
    }

}
