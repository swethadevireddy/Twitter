package com.codepath.twitter.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.twitter.R;
import com.codepath.twitter.activities.TweetDetailActivity;
import com.codepath.twitter.adapters.TimeLineAdapter;
import com.codepath.twitter.application.TwitterApplication;
import com.codepath.twitter.databinding.FragmentTweetsBinding;
import com.codepath.twitter.helpers.DividerItemDecoration;
import com.codepath.twitter.helpers.InternetCheck;
import com.codepath.twitter.helpers.SnackBar;
import com.codepath.twitter.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.twitter.listeners.ItemClickSupport;
import com.codepath.twitter.listeners.OnProgressListener;
import com.codepath.twitter.models.Tweet;
import com.codepath.twitter.net.TwitterClient;
import com.codepath.twitter.utils.GSONBuilder;
import com.codepath.twitter.utils.TwitterDBUtil;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sdevired on 11/4/16.
 */
public abstract class TweetsFragment extends Fragment {
    ArrayList<Tweet> tweets;
    RecyclerView rvTimeline;
    TimeLineAdapter adapter;
    FragmentTweetsBinding binding;
    TwitterClient twitterClient;
    SwipeRefreshLayout swipeContainer;
    OnProgressListener onProgressListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onProgressListener = (OnProgressListener) context;
        }catch (ClassCastException e){
            Log.d("Error- ", "** ClassCastException for onProgressListener **");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        //data binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tweets, parent, false);

        // Setup RecyclerView
        setupRecyclerView();

        // Item click support
        setupItemClick();

        //Setup swipeContainer
        setupSwipeContainer();


        return binding.getRoot();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize tweets and adapter.
        tweets = new ArrayList<>();
        adapter = new TimeLineAdapter(tweets, getContext(), this);
        //RestClient
        twitterClient = TwitterApplication.getRestClient();

    }



    //define abstract methods to be implemented by the subclasses
    protected abstract void populateTimeline(long id);

    protected abstract String getType();


    //setup Recyclerview
    private void setupRecyclerView(){
        //get the recyclerview
        rvTimeline = binding.rvTimeline;
        //set the adapter
        rvTimeline.setAdapter(adapter);
        //set layout manager
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext());

        rvTimeline.setLayoutManager(layoutManager);

        //set dividerItem decoration
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        rvTimeline.addItemDecoration(itemDecoration);

        //infinite scrolling
        rvTimeline.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if(InternetCheck.isOnline()) {
                    populateTimeline(tweets.get(tweets.size() - 1).getId() - 1);
                }else {
                    //get from DB
                    getTweetsFromDb(page, getType());

                }
            }
        });
    }

    //setup ItemClickSupport
    private void setupItemClick(){
        ItemClickSupport.addTo(rvTimeline).setOnItemClickListener(mItemClickListener);
    }

    private void setupSwipeContainer(){

        // Lookup the swipe container view
        swipeContainer = binding.swipeContainer;

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(mRefreshListener);

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorAccent);

    }

    //define refreshListener
    private final SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            //check internet availability , if not display snackbar else call twitter api
            if (!InternetCheck.isOnline()) {
                swipeContainer.setRefreshing(false);
                SnackBar.getSnackBar("No Internet connection available", getActivity()).show();
            } else {
                adapter.clear();
                populateTimeline(0);
            }
        }
    };


    //define ItemClickListener
    private final ItemClickSupport.OnItemClickListener mItemClickListener = new ItemClickSupport.OnItemClickListener() {
        @Override
        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
            Tweet tweet = tweets.get(position);
            Intent i = new Intent(getContext(), TweetDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("tweet", tweet);
            i.putExtras(bundle);
            startActivity(i);
        }
    };




    /**
     * Get tweets from db
     * @param page
     */
    public void getTweetsFromDb(int page, String type){
        List<Tweet> tweetList = Tweet.getTweets(page, type);
        if(tweetList.size() > 0){
            refreshAdapter(tweetList);
        }else if(!InternetCheck.isOnline()){
            Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }


    //refresh adapter with newTweets
    public void refreshAdapter(List<Tweet> newTweets){
        //notify adapter
        int curSize = adapter.getItemCount();
        tweets.addAll(newTweets);
        adapter.notifyItemRangeInserted(curSize, newTweets.size());
        swipeContainer.setRefreshing(false);
    }

    //create getTweetsResponseHandler
    JsonHttpResponseHandler mGetTweetsResponseHandler = new JsonHttpResponseHandler() {


        @Override
        public void onStart() {
            super.onStart();
            // Show Progress Bar
            if(onProgressListener != null)
                onProgressListener.showProgressBar();
        }



        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            Log.d("DEBUG", "Success -- " + response.toString());

            //prepare GSON handler
            Type listType = new TypeToken<ArrayList<Tweet>>(){}.getType();
            ArrayList<Tweet> newTweets = GSONBuilder.getGsonWithDate().fromJson(response.toString(), listType);

            if(saveTweets()) {
                //start service to save data to db
                TwitterDBUtil.storeTweets(getContext(), newTweets, getType());
            }
            //notify adapter
            refreshAdapter(newTweets);
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



    public void newTweet(Tweet t){
        tweets.add(0, t);
        adapter.notifyItemInserted(0);
        //set scroll position to top.
        rvTimeline.smoothScrollToPosition(0);
    }

    public boolean saveTweets(){
        return false;
    }

    public void postFavorite(int position) {
        Tweet t = tweets.get(position);
        twitterClient.postFavorite(t.getId(), t.isFavorited(), new TweetResponseHandler(position, false , t));
    }

    public void postReTweet(int position) {
        Tweet t = tweets.get(position);
        twitterClient.postRetweetStatus(t.getId(), t.isRetweeted(), new TweetResponseHandler(position, true, t));
    }

    private class TweetResponseHandler extends JsonHttpResponseHandler{
        int position ;
        boolean retweet;
        Tweet originalTweet ;
        TweetResponseHandler(int position, boolean isRetweet, Tweet originalTweet){
            this.position = position;
            this.retweet = isRetweet;
            this.originalTweet = originalTweet;
        }
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d("DEBUG", "Success -- " + response.toString());
            Tweet tweet = GSONBuilder.getGsonWithDate().fromJson(response.toString(), com.codepath.twitter.models.Tweet.class);
            if(retweet){
                if(originalTweet.isRetweeted()){
                    //set untweet
                    originalTweet.setRetweeted(false);
                    originalTweet.setReTweetCount(originalTweet.getReTweetCount() - 1);
                }else{
                    originalTweet.setRetweeted(true);
                    originalTweet.setReTweetCount(originalTweet.getReTweetCount() +  1);
                }

            }else{
                if(originalTweet.isFavorited()){
                    //response for retweet is new tweet , so we reset the count of original tweet
                    originalTweet.setFavorited(false);
                    originalTweet.setFavoriteCount(originalTweet.getFavoriteCount() - 1);

                }else{
                    originalTweet.setFavorited(true);
                    originalTweet.setFavoriteCount(originalTweet.getFavoriteCount() + 1);
                }

            }
            tweets.set(position, originalTweet);
            adapter.notifyItemChanged(position);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d("ERROR", errorResponse.toString());
            SnackBar.getSnackBar(getString(R.string.no_internet), getActivity()).show();
        }
    }
}
