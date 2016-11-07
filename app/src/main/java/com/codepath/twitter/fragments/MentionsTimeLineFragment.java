package com.codepath.twitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.codepath.twitter.helpers.InternetCheck;
import com.codepath.twitter.models.Tweet;

/**
 * Created by sdevired on 11/4/16.
 */
public class MentionsTimelineFragment extends TweetsFragment{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //populate mentions
        fetchMentions();
    }

    /**
     * method to populate timeline,
     */
    @Override
    public void populateTimeline(long maxId) {
//        onProgressListener.showProgressBar();
        twitterClient.getMentionsTimeline(maxId, mGetTweetsResponseHandler);
    }

    public void fetchMentions(){
        if(InternetCheck.isOnline()) {
            populateTimeline(0);
        }else{
            //get from db
            getTweetsFromDb(0, getType());
        }
    }

    @Override
    public String getType(){
        return Tweet.MENTION_TIMELINE;
    }

    @Override
    public boolean saveTweets(){
        return true;
    }


}
