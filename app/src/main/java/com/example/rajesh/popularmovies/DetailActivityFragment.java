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

//    private String[] reviews = {""};
//    private ArrayAdapter<String> reviewAdapter;
//    public static ArrayList<String> sReviews;
//    public static String[] sTrailers;
//    public static ImageAdapter sTrailerAdapter;
//    public static ArrayAdapter<String> sReviewAdapter;
    public ArrayList<String> reviews;
    public String[] trailers;
    public ImageAdapter trailerAdapter;
    public ArrayAdapter<String> reviewAdapter;
//    public ScrollView scrollView;


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
        Log.v(LOG_TAG, "onCreateOptionsMenu()");

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
//        if (mShareActionProvider != null ) {
//            mShareActionProvider.setShareIntent(createShareTrailerIntent());
//        } else {
//            Log.d(LOG_TAG, "Share Action Provider is null?");
//        }
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
        Log.v(LOG_TAG, "Share Intent:" + "Check out the trailer for movie: " + mMovieDetail.get(0));
        return shareIntent;
    }

    // Call to update the share intent
    public void setShareIntent() {
        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
            Log.d(LOG_TAG, "Share Action Provider is not null.");
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

//  Tried for BUG of Share Intent - ShareActionProvider refresh text. But its not fixing the bug
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_share) {
//            setShareIntent();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView");
//        Intent showDetail = getActivity().getIntent();
//
//        if (showDetail == null || showDetail.getData() == null) {
//            return null;
//        } else {
//            mMovieDetail = showDetail.getStringArrayListExtra(Intent.EXTRA_TEXT);
//        }

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
//            new FetchTrailerTask().execute(mMovieDetail.get(5));
//            new FetchReviewTask().execute(mMovieDetail.get(5));

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
        // Updating share intent as Trailers got fetched from background thread
//        mShareActionProvider.setShareIntent(createShareTrailerIntent());
//        setShareIntent();
//        mShareActionProvider.setShareIntent(createShareTrailerIntent());

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        ButterKnife.bind(this, rootView);

//        scrollView = (ScrollView)rootView.findViewById(R.id.scroll_view);

//        ImageView movieBackdrop = (ImageView)rootView.findViewById(R.id.movie_backdrop);
        Picasso.with(getActivity()).load(mMovieDetail.get(6)).fit().placeholder(R.drawable.no_image_available).into(movieBackdrop);

//        TextView movieTitle = (TextView)rootView.findViewById(R.id.movie_title);
        movieTitle.setText(mMovieDetail.get(0));

//        ImageView moviePoster = (ImageView)rootView.findViewById(R.id.movie_poster);
        Picasso.with(getActivity()).load(mMovieDetail.get(1)).fit().placeholder(R.drawable.no_image_available).into(moviePoster);

//        TextView releaseDate = (TextView)rootView.findViewById(R.id.release_date);
        releaseDate.setText(mMovieDetail.get(2));

//        TextView voteAverage = (TextView)rootView.findViewById(R.id.vote_average);
        voteAverage.setText(mMovieDetail.get(3) + VOTE_MAX);

//        TextView movieOverview = (TextView)rootView.findViewById(R.id.movie_overview);
        movieOverview.setText(mMovieDetail.get(4));

        trailerAdapter = new ImageAdapter(getActivity(), trailers);
        reviewAdapter = new ArrayAdapter<String>(getActivity(), R.layout.review_item, R.id.review_item_textview, reviews);
//        trailerAdapter = new ImageAdapter(getActivity(), new String[0]);
//        reviewAdapter = new ArrayAdapter<String>(getActivity(), R.layout.review_item, R.id.review_item_textview, new ArrayList<String>());

//        new FetchTrailerTask().execute(mMovieDetail.get(5));
//        new FetchReviewTask().execute(mMovieDetail.get(5));

//        GridView gridView = (GridView)rootView.findViewById(R.id.trailer_grid_view);
        gridView.setAdapter(trailerAdapter);

        //Log.v(LOG_TAG, "reviews:" + reviews[0]);
//        ListView listView = (ListView) rootView.findViewById(R.id.listview_reviews);
//        reviewAdapter.notifyDataSetChanged();
        listView.setAdapter(reviewAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String youtubeUrl = new JSONParser().getYoutubeUrl(trailers, position);
                Uri youtubeUri = Uri.parse(youtubeUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, youtubeUri);
//                Log.v(LOG_TAG, "sTrailers:" + sTrailers[position] );
//                Log.v(LOG_TAG, "Youtube Url:" + youtubeUrl );
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Log.d(LOG_TAG, "Couldn't play trailer, no receiving apps installed!");
                }
            }
        });

//        final ImageButton favButton = (ImageButton) rootView.findViewById(R.id.fav_button);

        mMovieCursor = getActivity().getContentResolver().query(
                FavMoviesEntry.buildMovieUri(mMovieDetail.get(5)),
                new String[]{FavMoviesEntry.COLUMN_MOVIE_ID},
                null,
                null,
                null
        );

        Log.v(LOG_TAG, "Movie already in DB: " + mMovieCursor.getCount());

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
                                                 favButton.setImageResource(R.drawable.unfavorite);

                                                 mDeletedRows = getActivity().getContentResolver().delete(
                                                         FavMoviesEntry.CONTENT_URI,
                                                         FavMoviesEntry.COLUMN_MOVIE_ID + " = ?",
                                                         new String[]{mMovieDetail.get(5)}
                                                 );
                                                 Log.v(LOG_TAG, "No. of Movie deleted: " + mDeletedRows);
                                                 // Deleting the saved poster from device storage has been commented out
                                                 // so that setting/unsetting favorite can work offline in Detail Activity for Favorite Movie
                                                 // Delete the saved poster image from internal storage
                                                 //getActivity().deleteFile(mMovieDetail.get(5) + "_poster.jpg");
//                                                 getActivity().deleteFile(mMovieDetail.get(5) + "_backdrop.jpg");

//                                                 Log.v(LOG_TAG, "Poster file deleted: " + getActivity().deleteFile(mMovieDetail.get(5) + ".jpg"));

                                                 mIsFavorite = false;
                                                 Toast.makeText(getActivity(),
                                                         "Removed from Favorite", Toast.LENGTH_SHORT).show();

                                             } else {
                                                 favButton.setImageResource(R.drawable.favorite);

                                                 // Target to save the poster image on device storage
//                                                 final String posterPath = "file://" + getActivity().getFilesDir() + "/" + mMovieDetail.get(5) + ".jpg";
//                                                 final String posterPath = getActivity().getFilesDir() + "/" + mMovieDetail.get(5) + "_poster.jpg";
//                                                 final String backdropPath = getActivity().getFilesDir() + "/" + mMovieDetail.get(5) + "_backdrop.jpg";
                                                 final String posterPath = getActivity().getExternalFilesDir(null) + "/" + mMovieDetail.get(5) + "_poster.jpg";
                                                 final String backdropPath = getActivity().getExternalFilesDir(null) + "/" + mMovieDetail.get(5) + "_backdrop.jpg";
                                                 Log.v(LOG_TAG, "Local Dir poster path: " + posterPath);
                                                 Log.v(LOG_TAG, "Local Dir backdrop path: " + backdropPath);

                                                 Picasso.with(getActivity())
                                                         .load(mMovieDetail.get(1))
                                                         .into(Utility.setTargetPoster(posterPath));
//                                                         .into(target);

                                                 Picasso.with(getActivity())
                                                         .load(mMovieDetail.get(6))
                                                         .into(Utility.setTargetBackdrop(backdropPath));
//                                                         .into(target1);

                                                 ContentValues contentValues = new ContentValues();
                                                 contentValues.put(FavMoviesEntry.COLUMN_MOVIE_ID, mMovieDetail.get(5));
                                                 contentValues.put(FavMoviesEntry.COLUMN_TITLE, mMovieDetail.get(0));
//                                                 contentValues.put(FavMoviesEntry.COLUMN_POSTER_PATH,mMovieDetail.get(1));
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

//        Log.v(LOG_TAG, "Just before returning rootView");
//        setShareIntent();

//        ScrollView scrollView = (ScrollView)rootView.findViewById(R.id.scroll_view);
//        scrollView.setSmoothScrollingEnabled(true);
//        scrollView.smoothScrollTo(0, 0);
//        scrollView.fullScroll(ScrollView.FOCUS_UP);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
//                scrollView.pageScroll(ScrollView.FOCUS_UP);
//                scrollView.smoothScrollTo(0, 0);
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

//    @Override
//    public void onStart() {
//        super.onStart();
//        new FetchReviewTask().execute(mMovieDetail.get(5));
//    }
}
