package com.codepath.twitter.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.codepath.twitter.models.Tweet;
import com.codepath.twitter.service.TwitterDBSaveService;

import java.util.ArrayList;

/**
 * Created by sdevired on 10/29/16.
 */
public class TwitterDBUtil {

    public static void storeTweets(Context context, ArrayList<Tweet> tweets){
        Intent i = new Intent(context, TwitterDBSaveService.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("tweets", tweets);
        i.putExtra("tweetsBundle", bundle);
        context.startService(i);
    }

}
