package com.codepath.twitter.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sdevired on 10/26/16.
 * model for UserFollowResponse
 */
public class SearchResponse {


    public ArrayList<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(ArrayList<Tweet> tweets) {
        this.tweets = tweets;
    }

    @SerializedName("statuses")
    @Expose
    ArrayList<Tweet> tweets = new ArrayList<Tweet>();

    public SearchMetaData getSearchMetaData() {
        return searchMetaData;
    }

    public void setSearchMetaData(SearchMetaData searchMetaData) {
        this.searchMetaData = searchMetaData;
    }

    @SerializedName("search_metadata")
    @Expose
    private SearchMetaData searchMetaData;



}
