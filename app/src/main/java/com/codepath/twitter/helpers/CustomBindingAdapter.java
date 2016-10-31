package com.codepath.twitter.helpers;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.twitter.R;

/**
 * Created by sdevired on 10/29/16.
 * Adapter to bind image
 */
public class CustomBindingAdapter {
    @BindingAdapter("bind:imageUrl")
    public static void loadImage(ImageView imageView, String url) {
        imageView.setBackgroundResource(android.R.color.transparent);
        Glide.with(imageView.getContext())
                .load(url)
                .placeholder(R.mipmap.default_profile)
                .into(imageView);
    }

}