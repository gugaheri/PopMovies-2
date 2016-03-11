package com.example.rajesh.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    private ArrayList<String> mMovieDetail  = new ArrayList<String>();
    private final String VOTE_MAX = "/10";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent showDetail = getActivity().getIntent();
        mMovieDetail = showDetail.getStringArrayListExtra(Intent.EXTRA_TEXT);

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

        return rootView;
    }
}
