package com.codepath.twitter.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.twitter.R;

/**
 *
 * ViewHolder
 */
public class TweetViewHolder extends RecyclerView.ViewHolder {
    // for any view that will be set as you render a row
    public ImageView ivProfileImage;
    public TextView tvUserName;
    public TextView tvText;
    public TextView tvScreenName;
    public TextView tvRelativeTime;

    // We also create a constructor that accepts the entire item row
    // and does the view lookups to find each subview
    public TweetViewHolder(View itemView) {
        // Stores the itemView in a public final member variable that can be used
        // to access the context from any ViewHolder instance.
        super(itemView);

        tvText = (TextView) itemView.findViewById(R.id.tvText);
        ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
        tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
        tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
        tvRelativeTime = (TextView) itemView.findViewById(R.id.tvRelativeTime);
    }
}
