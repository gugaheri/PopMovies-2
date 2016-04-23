package com.example.rajesh.popularmovies;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.FileOutputStream;

/**
 * General Purpose Utility Class.
 * Targets for poster and backdrop are seperately created as member variables and set seperately to avoid Garbage Collection.
 * If its not done so, then one or both of the images wouldn't be saved to device due to Garbage Collection acting on them
 * before saving images to device.
 */
public class Utility {

    public static final String LOG_TAG = Utility.class.getSimpleName();
    public static Target sTargetPoster;
    public static Target sTargetBackdrop;

    // Method to check network availability
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    // Method to get the preferred setting
    public static String getPreferredSortBy(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(context.getString(R.string.pref_sort), context.getString(R.string.pref_default));
    }


    // Target setter for poster image
    public static Target setTargetPoster(final String imagePath) {
        sTargetPoster = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        FileOutputStream outStream = new FileOutputStream(imagePath);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        outStream.close();
                        Log.v(LOG_TAG, "Image saved at:" + imagePath);
                    } catch (Exception e) {
                        Log.v(LOG_TAG, "Image not saved. Some exception occured");
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.v(LOG_TAG, "Image saving failed");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            if (placeHolderDrawable != null) {
            }
        }

    };

        return sTargetPoster;
    }


    // Target setter for backdrop image
    public static Target setTargetBackdrop(final String imagePath) {
        sTargetBackdrop = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FileOutputStream outStream = new FileOutputStream(imagePath);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                            outStream.close();
                            Log.v(LOG_TAG, "Image saved at:" + imagePath);
                        } catch (Exception e) {
                            Log.v(LOG_TAG, "Image not saved. Some exception occured");
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.v(LOG_TAG, "Image saving failed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {
                }
            }


        };

        return sTargetBackdrop;
    }
}
