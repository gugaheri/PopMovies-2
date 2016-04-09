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
    public static ImageAdapter sImageAdapter;
    public static ArrayAdapter<String> sReviewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent showDetail = getActivity().getIntent();
        mMovieDetail = showDetail.getStringArrayListExtra(Intent.EXTRA_TEXT);

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

//        Intent showDetail = getActivity().getIntent();
//        mMovieDetail = showDetail.getStringArrayListExtra(Intent.EXTRA_TEXT);

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

        sImageAdapter = new ImageAdapter(getActivity(), new String[0]);
        new FetchTrailerTask().execute(mMovieDetail.get(5));
        GridView gridView = (GridView)rootView.findViewById(R.id.trailer_grid_view);
        gridView.setAdapter(sImageAdapter);


//        new FetchReviewTask().execute(mMovieDetail.get(5));
//        Log.v(LOG_TAG, "reviews" + reviews[0]);
//        mReviewAdapter = new ArrayAdapter<String>(getActivity(), R.layout.review_item, R.id.review_item_textview, new ArrayList<String>(Arrays.asList(reviews)));
        sReviewAdapter = new ArrayAdapter<String>(getActivity(), R.layout.review_item, R.id.review_item_textview, new ArrayList<String>());
        new FetchReviewTask().execute(mMovieDetail.get(5));
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
