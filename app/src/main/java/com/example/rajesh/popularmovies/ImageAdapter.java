package com.example.rajesh.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ImageView imageView;
    private String[] posterLinks = null;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

    }

    public void updateData(String[] strings){
        /*for (String posterLink: strings){
        Picasso.with(mContext).load(posterLink).into(imageView);
        }*/
        //Picasso.with(mContext).load(strings[0]).into(imageView);
        posterLinks=strings;
        notifyDataSetChanged();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        //ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            //imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.findViewById(R.id.grid_item);
        if (posterLinks == null){
        imageView.setImageResource(mThumbIds[position]);}
        else{
            Picasso.with(mContext).load(posterLinks[position]).fit().placeholder(R.drawable.no_image_available).into(imageView);
        }

        return imageView;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.no_image_available, R.drawable.no_image_available,
            R.drawable.no_image_available, R.drawable.no_image_available,
            R.drawable.no_image_available, R.drawable.no_image_available,
            R.drawable.no_image_available, R.drawable.no_image_available,
            R.drawable.no_image_available, R.drawable.no_image_available,
            R.drawable.no_image_available, R.drawable.no_image_available,
            R.drawable.no_image_available, R.drawable.no_image_available,
            R.drawable.no_image_available, R.drawable.no_image_available,
            R.drawable.no_image_available, R.drawable.no_image_available,
            R.drawable.no_image_available, R.drawable.no_image_available

    };
}