package com.codepath.twitter.helpers;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

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
                .bitmapTransform(new RoundedCornersTransformation(imageView.getContext(), 10, 0,
                RoundedCornersTransformation.CornerType.ALL)).into(imageView);

    }

}