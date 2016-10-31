package com.codepath.twitter.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.codepath.twitter.R;
import com.codepath.twitter.models.Tweet;
import com.codepath.twitter.utils.DateUtil;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by sdevired on 10/20/16.
 * RecyclerView Adapter to populate Article data
 */
public class TimeLineAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Store a member variable for the contacts
    private List<Tweet> tweets;

    // Pass in the contact array into the constructor
    public TimeLineAdapter(List<Tweet> tweets) {
        this.tweets = tweets;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;

        // Inflate the custom layout based on the viewtype
       View articleView = inflater.inflate(R.layout.item_tweet, parent, false);
       viewHolder = new TweetViewHolder(articleView);
       return viewHolder;
    }

    /**
     * method to load the data into the view
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Get the data model based on position
        TweetViewHolder viewHolder = (TweetViewHolder) holder;
        Tweet tweet = tweets.get(position);
            // Set item views based on your views and data model
        viewHolder.tvText.setText(tweet.getText());
        viewHolder.tvUserName.setText(tweet.getUser().getName());
        viewHolder.tvScreenName.setText(tweet.getUser().getScreenName());
        viewHolder.ivProfileImage.setImageResource(0);
        //viewHolder.tvRelativeTime.setText();
        viewHolder.tvRelativeTime.setText(DateUtil.getRelativeTime(tweet.getCreatedAt()));
        //Glide
        Glide.with(viewHolder.itemView.getContext())
                .load(tweet.getUser().getProfileImageUrl())
                .bitmapTransform(new RoundedCornersTransformation(viewHolder.itemView.getContext(),5, 5))
                .into(viewHolder.ivProfileImage);

    }




    @Override
    public int getItemCount() {
        return tweets.size();
    }

    /**
     * method to clear the adapter
     */
    public void clear(){
        final int size = getItemCount();
        tweets.clear();
        notifyItemRangeRemoved(0, size);
    }

}
