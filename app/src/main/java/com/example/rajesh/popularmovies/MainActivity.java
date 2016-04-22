package com.example.rajesh.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;
    private String mSortBy;
    private ArrayList<String> mMovieDetail  = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSortBy = Utility.getPreferredSortBy(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Setting default preference
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        if (findViewById(R.id.fragment_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_detail_container, new DetailActivityFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "onResume() of Main Activity");
        String sortBy = Utility.getPreferredSortBy(this);
        // update the SortBy in our second pane using the fragment manager
        if (sortBy != null && !sortBy.equals(mSortBy)) {
            MainActivityFragment ff = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            if (null != ff) {
                ff.updateMovieData();
            }
            // Below lines are giving exception for setting args to null.
            DetailActivityFragment df = (DetailActivityFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if ( null != df ) {
//                Bundle args = new Bundle();
//                df.setArguments(null);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_detail_container, new DetailActivityFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }

            mSortBy = sortBy;

        }
    }

    @Override
    public void onItemSelected(ArrayList<ArrayList<String>> movieList, String movieJsonStr, int position) {

        if (mSortBy.equals(getString(R.string.pref_favorites))){
             mMovieDetail = movieList.get(position);
        }else {
            try {
                mMovieDetail = new JSONParser().getMovieDataFromJson(movieJsonStr, position);
            } catch (JSONException e) {
                    Log.e(LOG_TAG, "JSON Error", e);
                }
        }

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putStringArrayList(DetailActivityFragment.MOVIE_DETAIL, mMovieDetail);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent showDetail = new Intent(this, DetailActivity.class);
            showDetail.putStringArrayListExtra(Intent.EXTRA_TEXT, mMovieDetail);
            startActivity(showDetail);
        }
    }
}


