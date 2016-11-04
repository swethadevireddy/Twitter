package com.codepath.twitter.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.codepath.twitter.R;
import com.codepath.twitter.models.Media;
import com.codepath.twitter.models.Tweet;
import com.codepath.twitter.utils.DateUtil;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by sdevired
 * RecyclerView Adapter to populate Tweets.
 */
public class TimeLineAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Store a member variable for the tweets
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
                .load(tweet.getUser().getProfileImageUrl().replace("_normal", "_bigger"))
                .bitmapTransform(new RoundedCornersTransformation(viewHolder.itemView.getContext(),10, 0))
                .into(viewHolder.ivProfileImage);
        viewHolder.ivMediaImage.setImageResource(0);


      if(tweet.getExtendedEntities() != null){
             List<Media> mediaList = tweet.getExtendedEntities().getMedia();
          for (Media m : mediaList)
             if("photo".equals(m.getType())){
                    //Glide
                    Glide.with(viewHolder.ivMediaImage.getContext()).load(m.getMediaUrl()).bitmapTransform(new RoundedCornersTransformation(viewHolder.ivMediaImage.getContext(), 10, 0,
                            RoundedCornersTransformation.CornerType.ALL)).into(viewHolder.ivMediaImage);
                 break;
             }

        }

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
