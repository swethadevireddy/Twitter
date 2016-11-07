package com.codepath.twitter.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.codepath.twitter.R;
import com.codepath.twitter.application.TwitterApplication;
import com.codepath.twitter.databinding.ActivityFollowBinding;
import com.codepath.twitter.fragments.FollowFragment;
import com.codepath.twitter.net.TwitterClient;

/**
 * Created by sdevired on 11/5/16.
 */
public class FollowActivity extends AppCompatActivity {

    ActivityFollowBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_follow);

        setContentView(binding.getRoot());


        String screenName = getIntent().getStringExtra("screen_name");
        String followType = getIntent().getStringExtra("follow_type");

        //set toolbar
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(binding.tbInclude.toolbar);

        //to display home icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(followType);



        //call profile
        TwitterClient client = TwitterApplication.getRestClient();

           //create FollowFragment
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            FollowFragment followingFragment = FollowFragment.newInstance(screenName, followType);
            ft.replace(binding.flContainer.getId(), followingFragment);
            ft.commit();
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
