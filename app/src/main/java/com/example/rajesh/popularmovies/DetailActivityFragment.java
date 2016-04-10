package com.example.rajesh.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    public final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private ArrayList<String> mMovieDetail  = new ArrayList<String>();
    private final String VOTE_MAX = "/10";

//    private String[] mReviews = {""};
//    private ArrayAdapter<String> mReviewAdapter;
    public static ArrayList<String> sReviews;
    public static String[] sTrailers;
    public static ImageAdapter sTrailerAdapter;
    public static ArrayAdapter<String> sReviewAdapter;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("reviews", sReviews);
        outState.putStringArray("trailerLinks", sTrailers);

        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent showDetail = getActivity().getIntent();
        mMovieDetail = showDetail.getStringArrayListExtra(Intent.EXTRA_TEXT);

        //savedInstanceState used to restore data on rotation of phone
        if(savedInstanceState == null || !savedInstanceState.containsKey("reviews") || !savedInstanceState.containsKey("trailerLinks")) {
            sReviews = new ArrayList<String>();
            sTrailers = new String[0];
//            new FetchTrailerTask().execute(mMovieDetail.get(5));
//            new FetchReviewTask().execute(mMovieDetail.get(5));
            FetchTask fetchTrailerTask = new FetchTask();
            fetchTrailerTask.setFetch("TRAILERS");
            fetchTrailerTask.execute(mMovieDetail.get(5));
            FetchTask fetchReviewTask = new FetchTask();
            fetchReviewTask.setFetch("REVIEWS");
            fetchReviewTask.execute(mMovieDetail.get(5));

        }
        else {
            sReviews = savedInstanceState.getStringArrayList("reviews");
            sTrailers = savedInstanceState.getStringArray("trailerLinks");
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView movieTitle = (TextView)rootView.findViewById(R.id.movie_title);
        movieTitle.setText(mMovieDetail.get(0));

        ImageView moviePoster = (ImageView)rootView.findViewById(R.id.movie_poster);
        Picasso.with(getActivity()).load(mMovieDetail.get(1)).fit().placeholder(R.drawable.no_image_available).into(moviePoster);

        TextView releaseDate = (TextView)rootView.findViewById(R.id.release_date);
        releaseDate.setText(mMovieDetail.get(2));

        TextView voteAverage = (TextView)rootView.findViewById(R.id.vote_average);
        voteAverage.setText(mMovieDetail.get(3) + VOTE_MAX);

        TextView movieOverview = (TextView)rootView.findViewById(R.id.movie_overview);
        movieOverview.setText(mMovieDetail.get(4));

        sTrailerAdapter = new ImageAdapter(getActivity(), sTrailers);
        sReviewAdapter = new ArrayAdapter<String>(getActivity(), R.layout.review_item, R.id.review_item_textview, sReviews);
//        trailerAdapter = new ImageAdapter(getActivity(), new String[0]);
//        reviewAdapter = new ArrayAdapter<String>(getActivity(), R.layout.review_item, R.id.review_item_textview, new ArrayList<String>());

//        new FetchTrailerTask().execute(mMovieDetail.get(5));
//        new FetchReviewTask().execute(mMovieDetail.get(5));

        GridView gridView = (GridView)rootView.findViewById(R.id.trailer_grid_view);
        gridView.setAdapter(sTrailerAdapter);

        //Log.v(LOG_TAG, "reviews:" + reviews[0]);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_reviews);
//        mReviewAdapter.notifyDataSetChanged();
        listView.setAdapter(sReviewAdapter);

        // Code to make ListView and GridView scrollable in ScrollView
        gridView.setOnTouchListener(new GridView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        listView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });



        return rootView;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        new FetchReviewTask().execute(mMovieDetail.get(5));
//    }
}
