package com.example.rajesh.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        /*ArrayList<String> weekForecast=new ArrayList<String>();
        weekForecast.add("Today - Sunny - 88/63");
        weekForecast.add("Tomorrow - Foggy - 70/46");
        weekForecast.add("Weds - Cloudy - 72/63");
        weekForecast.add("Thurs - Rainy - 64/51");
        weekForecast.add("Fri - Foggy - 70/46");
        weekForecast.add("Sat - Sunny - 76/68");

        ArrayAdapter<String> mForecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.grid_item, weekForecast);*/

        /*ArrayList<ImageView> weekForecast=new ArrayList<ImageView>();
        ImageView droid = new ImageView(getContext());
        droid.setImageResource(R.drawable.ic_launcher);
        weekForecast.add(droid);
        weekForecast.add(droid);
        weekForecast.add(droid);
        weekForecast.add(droid);
        ArrayAdapter<ImageView> mForecastAdapter = new ArrayAdapter<ImageView>(getActivity(), R.layout.grid_item, weekForecast);*/


        /*GridView gridView = (GridView)rootView.findViewById(R.id.grid_view);
        gridView.setAdapter(mForecastAdapter);*/

        GridView gridView = (GridView)rootView.findViewById(R.id.grid_view);
        gridView.setAdapter(new ImageAdapter(getActivity()));

        return rootView;

    }
}
