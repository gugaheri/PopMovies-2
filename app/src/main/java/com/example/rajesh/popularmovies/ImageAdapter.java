package com.example.rajesh.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/** Custom Image Adapter */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ImageView mImageView;
    private String[] mPosterLinks;

    public ImageAdapter(Context c, String[] posterLinks) {
        mContext = c;
        mPosterLinks = posterLinks;
    }

    public int getCount() {
        return mPosterLinks.length;
    }

    public Object getItem(int position) {
        return mPosterLinks[position];
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

    }

    public void updateData(String[] strings){
        mPosterLinks =strings;
        notifyDataSetChanged();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            mImageView = new ImageView(mContext);
        } else {
            mImageView = (ImageView) convertView;
        }

        mImageView.findViewById(R.id.grid_item);
        Picasso.with(mContext).load(mPosterLinks[position]).fit().placeholder(R.drawable.no_image_available).into(mImageView);

        return mImageView;
    }

}