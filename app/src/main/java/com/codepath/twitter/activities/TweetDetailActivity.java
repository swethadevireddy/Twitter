package com.codepath.twitter.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.codepath.twitter.R;
import com.codepath.twitter.application.TwitterApplication;
import com.codepath.twitter.databinding.ActivityTweetDetailBinding;
import com.codepath.twitter.databinding.TweetDialogFragmentPresenter;
import com.codepath.twitter.helpers.InternetCheck;
import com.codepath.twitter.helpers.SnackBar;
import com.codepath.twitter.models.Tweet;
import com.codepath.twitter.net.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sdevired on 10/22/16.
 * Activity to sow the embedded webview
 */
public class TweetDetailActivity extends AppCompatActivity implements TweetDialogFragmentPresenter.TweetDialogEvents{

    ActivityTweetDetailBinding binding;
    private static final  int MAX_TWEET_CHAR_COUNT = 140;
    Tweet tweet;
    private TwitterClient client;
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_tweet_detail);

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(binding.toolbar);

        //to display home icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        tweet = intent.getParcelableExtra("tweet");
        ActionBar actionBar = getSupportActionBar();
        //set actionbar title
        if(actionBar != null) {
            actionBar.setTitle("Tweet");
        }
        binding.setTweet(tweet);
        binding.setReplyTo("Reply to " + tweet.getUser().getName());
        String screenName = tweet.getUser().getScreenName();
        binding.etTweetReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.linearLayoutTweetButton.setVisibility(View.VISIBLE);
                binding.etTweetReply.setText(screenName);
                binding.etTweetReply.setSelection(screenName.length());
            }
        });

        //register on text change listener
        binding.etTweetReply.addTextChangedListener(new TextWatcher() {

            ColorStateList oldColors;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //default color
                oldColors = binding.etTweetReply.getTextColors(); //save original colors
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int remaningCharCount = MAX_TWEET_CHAR_COUNT  - s.length();
                binding.tvCharCount.setText(String.valueOf(remaningCharCount));
                binding.btnTweet.setEnabled(true);
                //set back the default color
                binding.tvCharCount.setTextColor(oldColors);
                if(remaningCharCount < 0){
                    binding.btnTweet.setEnabled(false);
                    binding.tvCharCount.setTextColor(ContextCompat.getColor(binding.tvCharCount.getContext(), R.color.colorError));
                }else{
                    if(remaningCharCount == MAX_TWEET_CHAR_COUNT){
                        binding.btnTweet.setEnabled(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE );
        activity = this;
        TweetDialogFragmentPresenter presenter = new TweetDialogFragmentPresenter(this);
        binding.setPresenter(presenter);

        client = TwitterApplication.getRestClient();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    public void cancel() {

    }



    @Override
    public void tweet() {
        if(InternetCheck.isOnline("api.twitter.com")) {
            client.postTweet(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Tweet t = Tweet.fromJSONObject(response);
                    //save to db
                    t.save();
                    binding.linearLayoutTweetButton.setVisibility(View.GONE);
                    binding.etTweetReply.setText("");
                    SnackBar.getSnackBarForSuccess("Reply Tweet Posted Successfully !!", activity).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

            }, binding.etTweetReply.getText().toString(), tweet.getId());

        }else{
            //display snackbar
            SnackBar.getSnackBar("No Internet connection available", this).show();
        }

    }
}
