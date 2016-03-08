package com.example.rajesh.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rajesh on 05-Mar-16.
 */
public class JSONParser {


    public String[] getPosterDataFromJson(String movieJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String THDB_MOVIE = "results";
        final String POSTER_IMG = "poster_path";
        final String BASE_URL = "http://image.tmdb.org/t/p/";
        final String IMG_SIZE = "w185";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(THDB_MOVIE);

        String[] resultStrs = new String[movieArray.length()];



        for(int i = 0; i < movieArray.length(); i++) {

            // Get the JSON object representing the day
            JSONObject movieDetail = movieArray.getJSONObject(i);

            //JSONObject posterObject = movieDetail.getJSONObject(POSTER_IMG);
            String posterLink=movieDetail.getString(POSTER_IMG);

            resultStrs[i] = BASE_URL + IMG_SIZE + posterLink;
        }

        return resultStrs;

    }
}
