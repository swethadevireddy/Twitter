package com.codepath.twitter.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.twitter.R;
import com.codepath.twitter.adapters.TimeLineAdapter;
import com.codepath.twitter.application.TwitterApplication;
import com.codepath.twitter.fragments.TweetDialogFragment;
import com.codepath.twitter.helpers.DividerItemDecoration;
import com.codepath.twitter.helpers.InternetCheck;
import com.codepath.twitter.helpers.SnackBar;
import com.codepath.twitter.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.twitter.listeners.ItemClickSupport;
import com.codepath.twitter.models.Tweet;
import com.codepath.twitter.net.TwitterClient;
import com.codepath.twitter.utils.TwitterDBUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

//Activity to display timeline.
public class TimeLineActivity extends AppCompatActivity implements TweetDialogFragment.TweetDialogListener {

    private TwitterClient twitterClient;
    private ArrayList<Tweet> tweets;
    private RecyclerView rvTimeline;
    private TimeLineAdapter adapter;
    private Activity activity;
    private SwipeRefreshLayout swipeContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(toolbar);


        activity = this;

        twitterClient = TwitterApplication.getRestClient();
        tweets = new ArrayList<>();
        adapter = new TimeLineAdapter(tweets);
        rvTimeline = (RecyclerView)findViewById(R.id.rvTimeline);
        rvTimeline.setAdapter(adapter);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvTimeline.setLayoutManager(layoutManager);

        //set dividerItem decoration
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        rvTimeline.addItemDecoration(itemDecoration);

        //set item click support for detail activity
        ItemClickSupport.addTo(rvTimeline).setOnItemClickListener((recyclerView, position, v) -> {
            Tweet tweet = tweets.get(position);
            //code to start a Detail activity
            Intent i = new Intent(getApplicationContext(), TweetDetailActivity.class);
            i.putExtra("tweet", tweet);
            startActivity(i);
        });

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //check internet availability , if not display snackbar else call twitter api
                if (!InternetCheck.isNetworkAvailable(activity) || !InternetCheck.isOnline("api.twitter.com")) {
                    swipeContainer.setRefreshing(false);
                    SnackBar.getSnackBar("No Internet connection available", activity).show();

                } else {
                    populateTimeline(0, 0, true);
                }
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorAccent);

        //infinite scrolling
        rvTimeline.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                populateTimeline(totalItemsCount, page, InternetCheck.isNetworkAvailable(activity));
            }
        });

        //fetch timeline
        populateTimeline(0, 0, InternetCheck.isNetworkAvailable(activity));

        //check if it is Implicit intent
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        // Get intent, action and MIME type
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            String titleOfPage = intent.getStringExtra(Intent.EXTRA_SUBJECT);
            String urlOfPage = intent.getStringExtra(Intent.EXTRA_TEXT);
            //show tweet dialog
            showTweetDialog(titleOfPage + " " + urlOfPage);
        }
    }

    /**
     * method to populate timeline, will check for internet availability
     * populate from db if internet is not available , else calls twitter api
     * @param totalItemsCount
     * @param page
     * @param internetAvailable
     */
    private void populateTimeline(int totalItemsCount, int page, boolean internetAvailable){
        if(internetAvailable) {
            final long id = (totalItemsCount == 0) ? 0 : tweets.get(totalItemsCount - 1).getId();
            twitterClient.getHomeTimeLine(id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    if (id == 0) {
                        //clear the adapter
                        adapter.clear();
                    }
                    Log.d("DEBUG", "getHomeTimeLine Success -- " + response.toString());
                    ArrayList<Tweet> newTweets = Tweet.fromJsonArray(response);
                    //start service to save data to db
                    TwitterDBUtil.storeTweets(activity, newTweets);
                    updateView(newTweets);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", "getHomeTimeLine Failed -- " + errorResponse);
                }

            });
        }else{
            //get from DB
            ArrayList<Tweet> newTweets = Tweet.getTweets(page);
            //Display snackbar if the tweets from db is empty
            if(newTweets == null || newTweets.size() == 0 ){
                SnackBar.getSnackBar("No Internet connection available", activity).show();
            }else{
                if (page == 0) {
                    //clear the adapter
                    adapter.clear();
                }
                updateView(newTweets);
            }
        }
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.miTweet:
                //hide the compose window if internet is not available
                if (!InternetCheck.isNetworkAvailable(activity)){
                    SnackBar.getSnackBar("No Internet connection available", activity).show();
                }else {
                    //open tweet dialog
                    showTweetDialog(null);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * to open Tweet compose dialog
     */
    public void showTweetDialog(String message){
        FragmentManager fm = getSupportFragmentManager();
        TweetDialogFragment settingsDialog = new TweetDialogFragment();
        if(message != null){
            settingsDialog.setTweet(message);
        }
        settingsDialog.show(fm, "tweet");
    }

    /**
     * listener method from TweetDialogFragment
     * @param tweet
     */
    @Override
    public void onSubmitTweet(Tweet t) {
        tweets.add(0, t);
        adapter.notifyItemInserted(0);
        //set scroll position to top.
        rvTimeline.smoothScrollToPosition(0);
    }

    /**
     * notify adapter
     * @param newTweets
     */
    private void updateView(ArrayList<Tweet> newTweets){
        int curSize = adapter.getItemCount();
        tweets.addAll(newTweets);
        adapter.notifyItemRangeInserted(curSize, newTweets.size());
        swipeContainer.setRefreshing(false);
    }
}


