package com.codepath.twitter.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.codepath.twitter.R;
import com.codepath.twitter.application.TwitterApplication;
import com.codepath.twitter.databinding.ActivityProfileBinding;
import com.codepath.twitter.fragments.UserTimelineFragment;
import com.codepath.twitter.helpers.SnackBar;
import com.codepath.twitter.listeners.OnProgressListener;
import com.codepath.twitter.models.User;
import com.codepath.twitter.net.TwitterClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sdevired on 11/4/16.
 */
public class ProfileActivity extends AppCompatActivity implements OnProgressListener {

    ActivityProfileBinding binding;
    Activity activity;
    String screenName;
    MenuItem miActionProgressItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        setContentView(binding.getRoot());

        screenName = getIntent().getStringExtra("screen_name");

        //call profile
        TwitterClient client = TwitterApplication.getRestClient();

        //call api
        client.getProfileInfo(screenName, mGetProfileResponseHandler);

        //create userTimelinefragment
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);
            ft.replace(binding.flContainer.getId(), userTimelineFragment);
            ft.commit();
        }

        activity = this;

        //setup listeners for followers and following
        binding.tvFriends.setOnClickListener(mOnClickListener);
        binding.tvFriends.setTag("Following");
        binding.tvFollowers.setOnClickListener(mOnClickListener);
        binding.tvFollowers.setTag("Followers");


    }

    //create getTweetsResponseHandler
    JsonHttpResponseHandler mGetProfileResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d("DEBUG", "Success -- " + response.toString());
            //prepare GSON handler
            Gson gson = new GsonBuilder().create();
            User user = gson.fromJson(response.toString(), User.class);
            binding.setUser(user);
            setHeaders(user);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d("ERROR", errorResponse.toString());
            SnackBar.getSnackBar("Api call failed", activity).show();
        }

    };

    //customize the properties for ui display
    private void setHeaders(User user){
        binding.tvScreenName.setText("@"+ user.getScreenName());
        binding.tvFollowers.setText(Html.fromHtml(String.format("<b>%,d %s" , Integer.valueOf(user.getFollowersCount()), getString(R.string.followers))));
        binding.tvFriends.setText(Html.fromHtml(String.format("<b>%,d %s" , Integer.valueOf(user.getFriendsCount()) , getString(R.string.following))));
    }

    private final TextView.OnClickListener mOnClickListener = view -> {

        Intent intent = new Intent(view.getContext(), FollowActivity.class);
        intent.putExtra("screen_name", screenName);
        intent.putExtra("follow_type", view.getTag().toString());
        view.getContext().startActivity(intent);
    };


    @Override
    public void showProgressBar() {
        // Show progressbar
        binding.pbTweets.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        //hide progressbar
        binding.pbTweets.setVisibility(View.GONE);
    }

}