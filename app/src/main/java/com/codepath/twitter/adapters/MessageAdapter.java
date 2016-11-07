package com.codepath.twitter.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.twitter.R;
import com.codepath.twitter.activities.ProfileActivity;
import com.codepath.twitter.models.Message;
import com.codepath.twitter.models.User;
import com.codepath.twitter.utils.DateUtil;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by sdevired
 * RecyclerView Adapter to populate Tweets.
 */
public class MessageAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Store a member variable for the user
    private List<Message> messages;

    private Context mContext;

    // Pass in the contact array into the constructor
    public MessageAdapter(List<Message> messages, Context context) {
        this.messages = messages;
        mContext = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;

        // Inflate the custom layout based on the viewtype
        View articleView = inflater.inflate(R.layout.item_user, parent, false);
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
        Message message = messages.get(position);
        User user = message.getUser();
        // Set item views based on your views and data model
        viewHolder.tvUserName.setText(user.getName());
        viewHolder.tvScreenName.setText("@"+user.getScreenName());
        viewHolder.ivProfileImage.setImageResource(0);

        viewHolder.ivProfileImage.setTag(viewHolder.ivProfileImage.getId(), user.getScreenName());

        //Glide
        Glide.with(mContext)
                .load(user.getProfileImageUrl().replace("_normal", "_bigger"))
                .bitmapTransform(new RoundedCornersTransformation(mContext,10, 0))
                .into(viewHolder.ivProfileImage);

        viewHolder.ivProfileImage.setOnClickListener(mOnClickListener);

        viewHolder.tvText.setText(message.getMessage());

        viewHolder.tvRelativeTime.setText(DateUtil.getRelativeTime(message.getCreatedAt()));


    }

    //
    private final ImageView.OnClickListener mOnClickListener = v -> {
        Intent intent = new Intent(v.getContext(), ProfileActivity.class);
        intent.putExtra("screen_name", v.getTag(v.getId()).toString());
        v.getContext().startActivity(intent);
    };




    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * method to clear the adapter
     */
    public void clear(){
        final int size = getItemCount();
        messages.clear();
        notifyItemRangeRemoved(0, size);
    }

}
