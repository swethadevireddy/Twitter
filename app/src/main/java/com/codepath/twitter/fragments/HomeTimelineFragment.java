package com.codepath.twitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.codepath.twitter.helpers.InternetCheck;
import com.codepath.twitter.models.Tweet;

/**
 * Created by sdevired on 11/4/16.
 */
public class HomeTimelineFragment extends TweetsFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //populate the timeline
        fetchTweets();
    }


    public void fetchTweets(){
        if(InternetCheck.isOnline()) {
            populateTimeline(0);
        }else{
            //get from db
            getTweetsFromDb(0, getType());
        }
    }


    /**
     * method to populate timeline,
     */
    @Override
    public void populateTimeline(long maxId){
       // onProgressListener.showProgressBar();
        twitterClient.getHomeTimeLine(maxId, mGetTweetsResponseHandler);

    }

    @Override
    public String getType(){
        return Tweet.HOME_TIMELINE;
    }

    @Override
    public boolean saveTweets(){
        return true;
    }







}
