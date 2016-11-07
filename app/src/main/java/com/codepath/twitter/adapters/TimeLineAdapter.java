package com.codepath.twitter.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.twitter.R;
import com.codepath.twitter.activities.ProfileActivity;
import com.codepath.twitter.activities.TweetSearchActivity;
import com.codepath.twitter.fragments.TweetDialogFragment;
import com.codepath.twitter.fragments.TweetsFragment;
import com.codepath.twitter.models.Media;
import com.codepath.twitter.models.Tweet;
import com.codepath.twitter.utils.DateUtil;
import com.codepath.twitter.utils.PatternEditableBuilder;

import java.util.List;
import java.util.regex.Pattern;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by sdevired
 * RecyclerView Adapter to populate Tweets.
 */
public class TimeLineAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Store a member variable for the tweets
    private List<Tweet> tweets;

    private Context mcontext;

    private TweetsFragment mFragment;

    // Pass in the contact array into the constructor
    public TimeLineAdapter(List<Tweet> tweets, Context context, TweetsFragment fragmentManager) {
        this.tweets = tweets;
        mcontext = context;
        mFragment = fragmentManager;
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
        //add clickable for screen name
        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\#(\\w+)"),  new PatternEditableBuilder.SpannableStyleListener(R.color.transparent) {
                            @Override
                            public void onSpanStyled(TextPaint ds) {
                                ds.linkColor = mcontext.getResources().getColor(R.color.colorAccent);
                            }
                        },
                        text -> {
                            startSearchactivity(text);
                        }).
                addPattern(Pattern.compile("\\@(\\w+)"),  new PatternEditableBuilder.SpannableStyleListener(R.color.transparent) {
                           @Override
                            public void onSpanStyled(TextPaint ds) {
                                ds.linkColor = mcontext.getResources().getColor(R.color.colorAccent);
                            }
                        },
                        text -> {
                            startProfileActivity(text.substring(1));
                        }).
              into(viewHolder.tvText);
        viewHolder.tvUserName.setText(tweet.getUser().getName());
        viewHolder.tvScreenName.setText("@"+tweet.getUser().getScreenName());
        viewHolder.ivProfileImage.setImageResource(0);

        viewHolder.tvRelativeTime.setText(DateUtil.getRelativeTime(tweet.getCreatedAt()));

        viewHolder.ivProfileImage.setTag(viewHolder.ivProfileImage.getId(), tweet.getUser().getScreenName());
        if(tweet.getFavoriteCount() != null) {
            viewHolder.tvFavoriteCount.setText(tweet.getFavoriteCount().toString());
        }
        if("0".equals(viewHolder.tvFavoriteCount.getText().toString())){
            viewHolder.tvFavoriteCount.setText("");
        }
        if(tweet.getReTweetCount() != null) {
            viewHolder.tvRetweetCount.setText(tweet.getReTweetCount().toString());
        }

        if("0".equals(viewHolder.tvRetweetCount.getText().toString())){
            viewHolder.tvRetweetCount.setText("");
        }

        if(tweet.isFavorited()){
            viewHolder.ibFavorited.setImageResource(R.drawable.ic_favorite_on);
            viewHolder.tvFavoriteCount.setTextColor(ContextCompat.getColor(viewHolder.tvFavoriteCount.getContext(), R.color.colorPink));
        }else{
            viewHolder.ibFavorited.setImageResource(R.drawable.ic_favorite);
            viewHolder.tvFavoriteCount.setTextColor(ContextCompat.getColor(viewHolder.tvFavoriteCount.getContext(), R.color.defaultText));
        }

        if(tweet.isRetweeted()){
            viewHolder.ibRetweeted.setImageResource(R.drawable.ic_retweet_on);
            viewHolder.tvRetweetCount.setTextColor(ContextCompat.getColor(viewHolder.tvRetweetCount.getContext(), R.color.colorGreen));
        }else{
            viewHolder.ibRetweeted.setImageResource(R.drawable.ic_retweet);
            viewHolder.tvRetweetCount.setTextColor(ContextCompat.getColor(viewHolder.tvRetweetCount.getContext(), R.color.defaultText));
        }

        if(tweet.getUser().getProfileImageUrl() != null) {
            //Glide
            Glide.with(mcontext)
                    .load(tweet.getUser().getProfileImageUrl().replace("_normal", "_bigger"))
                    .bitmapTransform(new RoundedCornersTransformation(mcontext, 10, 0))
                    .into(viewHolder.ivProfileImage);
            viewHolder.ivMediaImage.setImageResource(0);

        }

      if(tweet.getExtendedEntities() != null){
             List<Media> mediaList = tweet.getExtendedEntities().getMedia();
          for (Media m : mediaList)
             if("photo".equals(m.getType())){
                    //Glide
                    Glide.with(mcontext).load(m.getMediaUrl()).bitmapTransform(new RoundedCornersTransformation(mcontext, 10, 0,
                            RoundedCornersTransformation.CornerType.ALL)).into(viewHolder.ivMediaImage);
                 break;
             }

        }
        //set listener on click of image for the profile view
        viewHolder.ivProfileImage.setOnClickListener(mOnClickListener);

        //set listener on click of replybutton to open tweet dialog fragment
        viewHolder.ibReply.setTag(tweet);
        viewHolder.ibReply.setOnClickListener(mReplyOnClickListener);

        //setup listener for favorites button
        viewHolder.ibFavorited.setTag(position);
        viewHolder.ibFavorited.setOnClickListener(mFavOnClickListener);


        //setup listener for retweet button
        viewHolder.ibRetweeted.setTag(position);
        viewHolder.ibRetweeted.setOnClickListener(mRetweetOnClickListener);


    }


    private final ImageView.OnClickListener mOnClickListener = v -> {
        startProfileActivity(v.getTag(v.getId()).toString());
    };

    private final ImageButton.OnClickListener mReplyOnClickListener = v -> {
        Tweet t = (Tweet)v.getTag();
        showTweetDialog(t.getUser().getScreenName(), t.getId(), t.getUser().getName());
    };

    private final ImageButton.OnClickListener mFavOnClickListener = v -> {
        Integer position = (Integer)v.getTag();
        mFragment.postFavorite(position);
    };

    private final ImageButton.OnClickListener mRetweetOnClickListener = v -> {
        int position = (Integer)v.getTag();
        mFragment.postReTweet(position);
    };

    private void startProfileActivity(String screenName){
        Intent intent = new Intent(mcontext, ProfileActivity.class);
        intent.putExtra("screen_name", screenName);
        mcontext.startActivity(intent);
    }

    private void startSearchactivity(String query){
        Intent i = new Intent(mcontext, TweetSearchActivity.class);
        i.putExtra("query", query);
        mcontext.startActivity(i);
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

    /**
     * to open Tweet compose dialog
     */
    public void showTweetDialog(String screenName, Long id, String name){
        TweetDialogFragment settingsDialog =  TweetDialogFragment.newInstance(screenName, id, name);
        settingsDialog.show(mFragment.getFragmentManager(), "tweet");
    }

}
