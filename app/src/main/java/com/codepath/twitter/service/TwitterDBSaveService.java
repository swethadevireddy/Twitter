package com.codepath.twitter.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.codepath.twitter.models.Tweet;
import com.codepath.twitter.models.User;

import java.util.ArrayList;

/**
 * Created by sdevired on 10/29/16.
 * Backend service to save the tweets to db.
 */
public class TwitterDBSaveService extends IntentService {
    // Must create a default constructor
    public TwitterDBSaveService() {
        // Used to name the worker thread, important only for debugging.
        super("twitterdbsave");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // This describes what will happen when service is triggered
        // Get Data
        Bundle bundle = intent.getBundleExtra("tweetsBundle");

        ArrayList<Tweet> tweetArrayList = bundle.getParcelableArrayList("tweets");
        String tweetType = bundle.getParcelable("type");
        if(tweetArrayList != null && tweetArrayList.size() > 0){
            for (Tweet tweet: tweetArrayList) {
                User user = tweet.getUser();
                user.save();
                tweet.setUser(user);
                tweet.setType(tweetType);
                tweet.save();
            }
        }

        Log.d("DEBUG", "TwitterDBSaveService Completed - ");
    }
}


