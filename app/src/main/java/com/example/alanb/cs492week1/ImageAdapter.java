package com.example.alanb.cs492week1;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by shinjaemin on 2015. 12. 28..
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private String[] imageFetched;

    public ImageAdapter(Context c, String[] Fetched_Image) {
        mContext = c;
        imageFetched = Fetched_Image;
    }

    public int getCount() {
        return imageFetched.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        Picasso.with(this.mContext)
                .load(imageFetched[position])
                .into(imageView);
        return imageView;
    }

}