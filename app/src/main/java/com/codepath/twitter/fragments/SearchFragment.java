package com.codepath.twitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.twitter.R;
import com.codepath.twitter.helpers.InternetCheck;
import com.codepath.twitter.helpers.SnackBar;
import com.codepath.twitter.models.SearchResponse;
import com.codepath.twitter.models.Tweet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sdevired on 11/6/16.
 */
public class SearchFragment extends TweetsFragment {

    String query ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        query = getArguments().getString("query");
        //populate the timeline
        fetchTweets();
    }


    public void fetchTweets(){
        if(InternetCheck.isOnline()) {
            populateTimeline(0);
        }else{
            //get from db
            getTweetsFromDb(0, Tweet.HOME_TIMELINE);
        }
    }

    public static SearchFragment newInstance(String query){
        SearchFragment searchFragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
        searchFragment.setArguments(args);
        return searchFragment;
    }


    @Override
    protected void populateTimeline(long maxId) {
        twitterClient.getTweetsOnSearch(maxId, query,  mSearchResponseHandler);
    }

    @Override
    protected String getType() {
        return null;
    }

    //create getTweetsResponseHandler
    JsonHttpResponseHandler mSearchResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            super.onStart();
            // Show Progress Bar
            if(onProgressListener != null)
                onProgressListener.showProgressBar();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d("DEBUG", "Success -- " + response.toString());
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                SimpleDateFormat s = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
                @Override
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    try{
                        s.setTimeZone(TimeZone.getTimeZone("UTC"));
                        return s.parse(json.getAsString());

                    }
                    catch(ParseException ex){
                        return null;
                    }
                }
            });
            Gson gson = gsonBuilder.create();
            SearchResponse searchResponse = gson.fromJson(response.toString(), com.codepath.twitter.models.SearchResponse.class);
            //notify adapter
            refreshAdapter(searchResponse.getTweets());
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d("ERROR", errorResponse.toString());
            SnackBar.getSnackBar(getString(R.string.no_internet), getActivity()).show();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            //hide Progress Bar
            if(onProgressListener != null)
                onProgressListener.hideProgressBar();
        }

    };

}
