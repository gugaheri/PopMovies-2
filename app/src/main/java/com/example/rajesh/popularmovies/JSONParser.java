package com.example.rajesh.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONParser {

    // These are the names of the JSON objects that need to be extracted.
    final String THDB_MOVIE = "results";
    final String POSTER_IMG = "poster_path";
    final String BACKDROP_IMG = "backdrop_path";
    final String BASE_URL = "http://image.tmdb.org/t/p/";
    final String POSTER_SIZE = "w185";
    final String BACKDROP_SIZE = "w500";
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
    final String YOUTUBE_PREFIX = "https://www.youtube.com/watch?v=";

    /** Method for getting poster links for Main Activity/Fragment */
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
            resultStr[i] = BASE_URL + POSTER_SIZE + posterLink;
        }

        return resultStr;
    }

    /** Method for getting movie details for Detail Activity/Fragment */
    public ArrayList<String> getMovieDataFromJson(String movieJsonStr, int position )
            throws JSONException {

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(THDB_MOVIE);
        ArrayList<String> resultStr = new ArrayList<String>();

        JSONObject movieDetail = movieArray.getJSONObject(position);
        resultStr.add(movieDetail.getString(ORIGINAL_TITLE));
        resultStr.add(BASE_URL + POSTER_SIZE + movieDetail.getString(POSTER_IMG));
        resultStr.add(movieDetail.getString(RELEASE_DATE));
        resultStr.add(movieDetail.getString(VOTE_AVERAGE));
        resultStr.add(movieDetail.getString(OVERVIEW));
        resultStr.add(movieDetail.getString(MOVIE_ID));
        resultStr.add(BASE_URL + BACKDROP_SIZE + movieDetail.getString(BACKDROP_IMG));

        return resultStr;
    }

    /** Method for getting movie reviews for Detail Activity/Fragment */
    public String[] getReviewDataFromJson(String movieReviewStr)
            throws JSONException {

        JSONObject reviewJson = new JSONObject(movieReviewStr);
        JSONArray reviewArray = reviewJson.getJSONArray(THDB_MOVIE);
        String[] resultStr = new String[reviewArray.length()];

        for(int i = 0; i < reviewArray.length(); i++) {
            // Get the JSON object representing the reviews
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

    /** Method for getting movie trailers for Detail Activity/Fragment */
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

    /** Method for getting movie trailers video ids for Detail Activity/Fragment and Youtube */
    public String[] getTrailerKey(String movieTrailerStr)
            throws JSONException {

        JSONObject TrailerJson = new JSONObject(movieTrailerStr);
        JSONArray trailerArray = TrailerJson.getJSONArray(THDB_MOVIE);
        String[] resultStr = new String[trailerArray.length()];

        for(int i = 0; i < trailerArray.length(); i++) {
            // Get the JSON object representing the trailer detail
            JSONObject reviewDetail = trailerArray.getJSONObject(i);
            String trailer_key = reviewDetail.getString(TRAILER_KEY);
            resultStr[i] = trailer_key;
        }

        return resultStr;
    }

    /** Method for generating Youtube Links*/
    public String getYoutubeUrl(String[] trailers, int position){
        if (trailers.length>0) {
            String trailerImageLink = trailers[position];
            String[] trailerImageLinkArray = trailerImageLink.split("/");
            final int trailerKeyIndex = trailerImageLinkArray.length - 2;
            String trailerKey = trailerImageLinkArray[trailerKeyIndex];
            return YOUTUBE_PREFIX + trailerKey;
        }else{
            return YOUTUBE_PREFIX;
        }
    }
}
