package com.example.rajesh.popularmovies;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Rajesh on 17-Apr-16.
 */
public class Utility {

    public static final String LOG_TAG = Utility.class.getSimpleName();
    public static Target target;
    public static Target target1;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static Target setTarget(String imagePath) {
        final String path = imagePath;
        target = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File file = new File(path);
                    try {
                        file.createNewFile();
                        FileOutputStream outStream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        outStream.close();
                        Log.v(LOG_TAG, "Image saved:" + path);
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

        return target;
    }

    public static Target setTarget1(String imagePath) {
        final String path = imagePath;
        target1 = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File file = new File(path);
                        try {
                            file.createNewFile();
                            FileOutputStream outStream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                            outStream.close();
                            Log.v(LOG_TAG, "Image saved:" + path);
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

        return target1;
    }
}
