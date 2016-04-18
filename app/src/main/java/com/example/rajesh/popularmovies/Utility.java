package com.example.rajesh.popularmovies;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.FileOutputStream;

/**
 * Targets for poster and backdrop are seperately created as member variables and set seperately to avoid Garbage Collection.
 * If its not done so, then one or both of the images wouldn't be saved to device due to Garbage Collection.
 */
public class Utility {

    public static final String LOG_TAG = Utility.class.getSimpleName();
    public static Target targetPoster;
    public static Target targetBackdrop;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static Target setTargetPoster(final String imagePath) {
//        final String path = imagePath;
        targetPoster = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
//                    File file = new File(imagePath);
                    try {
//                        file.createNewFile();
                        FileOutputStream outStream = new FileOutputStream(imagePath);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        outStream.close();
                        Log.v(LOG_TAG, "Image saved:" + imagePath);
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

        return targetPoster;
    }


    public static Target setTargetBackdrop(final String imagePath) {
//        final String path = imagePath;
        targetBackdrop = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        File file = new File(imagePath);
                        try {
//                            file.createNewFile();
                            FileOutputStream outStream = new FileOutputStream(imagePath);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                            outStream.close();
                            Log.v(LOG_TAG, "Image saved:" + imagePath);
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

        return targetBackdrop;
    }
}
