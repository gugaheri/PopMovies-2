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
    final String MOVIE_ID = "id";
    final String REVIEWER = "author";
    final String REVIEW = "content";
    final String TRAILER_KEY = "key";
    final String TRAILER_THUMBNAIL = "http://img.youtube.com/vi/";
    final String TRAILER_THUMBNAIL_EXT = "/mqdefault.jpg";

    /** Method for getting poster links for Main Activity */
    public String[] getPosterDataFromJson(String movieJsonStr)
            throws JSONException {

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(THDB_MOVIE);
        String[] resultStr = new String[movieArray.length()];

        for(int i = 0; i < movieArray.length(); i++) {
            // Get the JSON object representing the movie detail
            JSONObject movieDetail = movieArray.getJSONObject(i);
            String posterLink=movieDetail.getString(POSTER_IMG);
            //Building the movie poster URI
            resultStr[i] = BASE_URL + IMG_SIZE + posterLink;
        }

        return resultStr;
    }

    /** Method for getting movie details for Detail Activity */
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
        resultStr.add(movieDetail.getString(MOVIE_ID));

        return resultStr;
    }

    /** Method for getting movie reviews for Detail Activity */
    public String[] getReviewDataFromJson(String movieReviewStr)
            throws JSONException {

        JSONObject reviewJson = new JSONObject(movieReviewStr);
        JSONArray reviewArray = reviewJson.getJSONArray(THDB_MOVIE);
        String[] resultStr = new String[reviewArray.length()];

        for(int i = 0; i < reviewArray.length(); i++) {
            // Get the JSON object representing the movie detail
            JSONObject reviewDetail = reviewArray.getJSONObject(i);
            String reviewer = reviewDetail.getString(REVIEWER);
            String review = reviewDetail.getString(REVIEW);

            if (null == reviewer){
                reviewer = "Author: Anonymous";
            }else{
                reviewer = "Author: " + reviewer;
            }

            //Building the movie review
            resultStr[i] = reviewer + "\n" + review;
        }

        return resultStr;
    }

    /** Method for getting movie trailers for Detail Activity */
    public String[] getTrailerDataFromJson(String movieTrailerStr)
            throws JSONException {
        // getting trailer keys using method getTrailerKey(movieTrailerStr)
        String[] resultStrArray = getTrailerKey(movieTrailerStr);
        String[] resultStr = new String[resultStrArray.length];

        for(int i = 0; i < resultStrArray.length; i++) {
            //Building the movie trailer URI
            resultStr[i] = TRAILER_THUMBNAIL + resultStrArray[i] + TRAILER_THUMBNAIL_EXT;
        }

        return resultStr;
    }

    /** Method for getting movie trailers video ids for Detail Activity and Youtube */
    public String[] getTrailerKey(String movieTrailerStr)
            throws JSONException {

        JSONObject TrailerJson = new JSONObject(movieTrailerStr);
        JSONArray trailerArray = TrailerJson.getJSONArray(THDB_MOVIE);
        String[] resultStr = new String[trailerArray.length()];

        for(int i = 0; i < trailerArray.length(); i++) {
            // Get the JSON object representing the movie detail
            JSONObject reviewDetail = trailerArray.getJSONObject(i);
            String trailer_key = reviewDetail.getString(TRAILER_KEY);
            //Building the movie trailer video ids URI
            resultStr[i] = trailer_key;
        }

        return resultStr;
    }
}
