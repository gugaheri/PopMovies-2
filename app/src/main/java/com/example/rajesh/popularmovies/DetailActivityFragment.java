package com.example.rajesh.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rajesh.popularmovies.data.MovieContract.FavMoviesEntry;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    // Butterknife Library used instead of findViewById
    @Bind(R.id.scroll_view) ScrollView scrollView;
    @Bind(R.id.movie_backdrop) ImageView movieBackdrop;
    @Bind(R.id.movie_title) TextView movieTitle;
    @Bind(R.id.movie_poster) ImageView moviePoster;
    @Bind(R.id.release_date) TextView releaseDate;
    @Bind(R.id.vote_average) TextView voteAverage;
    @Bind(R.id.movie_overview) TextView movieOverview;
    @Bind(R.id.trailer_grid_view) GridView gridView;
    @Bind(R.id.listview_reviews) ListView listView;
    @Bind(R.id.fav_button) ImageButton favButton;

    public final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    private ArrayList<String> mMovieDetail = new ArrayList<String>();
    private final String VOTE_MAX = "/10";
    private ShareActionProvider mShareActionProvider;
    private boolean mIsFavorite;// = false;
    private Cursor mMovieCursor;
    private Uri mInsertedMovie;
    private int mDeletedRows;

    static final String MOVIE_DETAIL = "MOVIE_DETAIL";

    public ArrayList<String> reviews;
    public String[] trailers;
    public ImageAdapter trailerAdapter;
    public ArrayAdapter<String> reviewAdapter;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("reviews", reviews);
        outState.putStringArray("trailerLinks", trailers);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail_fragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
//        Log.v(LOG_TAG, "onCreateOptionsMenu()");

        if( ! mMovieDetail.isEmpty()) {
            setShareIntent();
        } else{
            mShareActionProvider.setShareIntent(null);
        }
    }

    public Intent createShareTrailerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Check out the trailer of movie " + mMovieDetail.get(0) + " at Youtube link: " + new JSONParser().getYoutubeUrl(trailers, 0));
        return shareIntent;
    }

    // Method to update the share intent
    public void setShareIntent() {
        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
//            Log.d(LOG_TAG, "Share Action Provider is not null.");
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView");

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovieDetail = arguments.getStringArrayList(MOVIE_DETAIL);
        } else {
            return null;
        }

        //savedInstanceState used to restore data on rotation of phone
        if(savedInstanceState == null || !savedInstanceState.containsKey("reviews") || !savedInstanceState.containsKey("trailerLinks")) {
            reviews = new ArrayList<String>();
            trailers = new String[0];

            if(Utility.isNetworkAvailable(getContext())) {
                FetchTask fetchTrailerTask = new FetchTask(this);
                fetchTrailerTask.setFetch("TRAILERS");
                fetchTrailerTask.execute(mMovieDetail.get(5));
                FetchTask fetchReviewTask = new FetchTask(this);
                fetchReviewTask.setFetch("REVIEWS");
                fetchReviewTask.execute(mMovieDetail.get(5));
            } else {
                Toast.makeText(getActivity(), "No Network Access!", Toast.LENGTH_SHORT).show();
            }

        }
        else {
            reviews = savedInstanceState.getStringArrayList("reviews");
            trailers = savedInstanceState.getStringArray("trailerLinks");
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        Picasso.with(getActivity()).load(mMovieDetail.get(6)).fit().placeholder(R.drawable.no_image_available).into(movieBackdrop);
        movieTitle.setText(mMovieDetail.get(0));
        Picasso.with(getActivity()).load(mMovieDetail.get(1)).fit().placeholder(R.drawable.no_image_available).into(moviePoster);
        releaseDate.setText(mMovieDetail.get(2));
        voteAverage.setText(mMovieDetail.get(3) + VOTE_MAX);
        movieOverview.setText(mMovieDetail.get(4));
        trailerAdapter = new ImageAdapter(getActivity(), trailers);
        reviewAdapter = new ArrayAdapter<String>(getActivity(), R.layout.review_item, R.id.review_item_textview, reviews);
        gridView.setAdapter(trailerAdapter);
        listView.setAdapter(reviewAdapter);

        // Listener for handling click on Trailers and accordingly launching Implicit Intent to watch Trailers
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String youtubeUrl = new JSONParser().getYoutubeUrl(trailers, position);
                Uri youtubeUri = Uri.parse(youtubeUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, youtubeUri);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
//                    Log.d(LOG_TAG, "Couldn't play trailer, no receiving apps installed!");
                    Toast.makeText(getActivity(), "Couldn't play trailer, no receiving apps installed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Cursor to check if movie already marked as Favorite
        mMovieCursor = getActivity().getContentResolver().query(
                FavMoviesEntry.buildMovieUri(mMovieDetail.get(5)),
                new String[]{FavMoviesEntry.COLUMN_MOVIE_ID},
                null,
                null,
                null
        );

//        Log.v(LOG_TAG, "Movie already in DB: " + mMovieCursor.getCount());

        if (mMovieCursor.getCount() > 0){
            mIsFavorite = true;
        }else{
            mIsFavorite = false;
        }

        if (mIsFavorite){
            favButton.setImageResource(R.drawable.favorite);
        }else{
            favButton.setImageResource(R.drawable.unfavorite);
        }

        favButton.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             if (mIsFavorite) {
                                                 // Unfavorite the movie
                                                 favButton.setImageResource(R.drawable.unfavorite);

                                                 mDeletedRows = getActivity().getContentResolver().delete(
                                                         FavMoviesEntry.CONTENT_URI,
                                                         FavMoviesEntry.COLUMN_MOVIE_ID + " = ?",
                                                         new String[]{mMovieDetail.get(5)}
                                                 );
                                                 Log.v(LOG_TAG, "Number of Movie deleted: " + mDeletedRows);
                                                 // Deleting the saved poster from device storage has been commented out
                                                 // so that setting/unsetting favorite can work offline in Detail Activity/Fragment for Favorite Movie
                                                 // Delete the saved poster image from internal storage
                                                 //getActivity().deleteFile(mMovieDetail.get(5) + "_poster.jpg");
                                                 //getActivity().deleteFile(mMovieDetail.get(5) + "_backdrop.jpg");

//                                                 Log.v(LOG_TAG, "Poster file deleted: " + getActivity().deleteFile(mMovieDetail.get(5) + ".jpg"));

                                                 mIsFavorite = false;
                                                 Toast.makeText(getActivity(),
                                                         "Removed from Favorite", Toast.LENGTH_SHORT).show();

                                             } else {
                                                 // Marking the movie as Favorite
                                                 favButton.setImageResource(R.drawable.favorite);

                                                 // Target Name to save the poster image on device storage
                                                 final String posterPath = getActivity().getExternalFilesDir(null) + "/" + mMovieDetail.get(5) + "_poster.jpg";
                                                 final String backdropPath = getActivity().getExternalFilesDir(null) + "/" + mMovieDetail.get(5) + "_backdrop.jpg";
//                                                 Log.v(LOG_TAG, "Local Dir poster path: " + posterPath);
//                                                 Log.v(LOG_TAG, "Local Dir backdrop path: " + backdropPath);

                                                 Picasso.with(getActivity())
                                                         .load(mMovieDetail.get(1))
                                                         .into(Utility.setTargetPoster(posterPath));

                                                 Picasso.with(getActivity())
                                                         .load(mMovieDetail.get(6))
                                                         .into(Utility.setTargetBackdrop(backdropPath));

                                                 ContentValues contentValues = new ContentValues();
                                                 contentValues.put(FavMoviesEntry.COLUMN_MOVIE_ID, mMovieDetail.get(5));
                                                 contentValues.put(FavMoviesEntry.COLUMN_TITLE, mMovieDetail.get(0));
                                                 contentValues.put(FavMoviesEntry.COLUMN_POSTER_PATH, "file://" + posterPath);
                                                 contentValues.put(FavMoviesEntry.COLUMN_BACKDROP_PATH, "file://" + backdropPath);
                                                 contentValues.put(FavMoviesEntry.COLUMN_RELEASE_DATE, mMovieDetail.get(2));
                                                 contentValues.put(FavMoviesEntry.COLUMN_USER_RATING, mMovieDetail.get(3));
                                                 contentValues.put(FavMoviesEntry.COLUMN_OVERVIEW, mMovieDetail.get(4));

                                                 mInsertedMovie = getActivity().getContentResolver().insert(
                                                         FavMoviesEntry.CONTENT_URI,
                                                         contentValues
                                                 );
                                                 Log.v(LOG_TAG, "Inserted Movie Uri: " + mInsertedMovie);

                                                 mIsFavorite = true;
                                                 Toast.makeText(getActivity(),
                                                         "Marked as Favorite", Toast.LENGTH_SHORT).show();
                                             }
                                         }
                                     }
        );

        // Code to make GridView scrollable in ScrollView
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

        // Code to make ListView scrollable in ScrollView
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


        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "onDestroy");
        if ( mMovieCursor != null) {
            mMovieCursor.close();
        }
    }

}
