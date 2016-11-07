package com.codepath.twitter.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.codepath.twitter.R;
import com.codepath.twitter.databinding.ActivitySearchBinding;
import com.codepath.twitter.fragments.SearchFragment;

/**
 * Created by sdevired on 11/6/16.
 */
public class TweetSearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);

        setContentView(binding.getRoot());

        String query = getIntent().getStringExtra("query");
        //set toolbar
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(binding.tbInclude.toolbar);

        //to display home icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(query);

        //create FollowFragment
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            SearchFragment searchFragment = SearchFragment.newInstance(query);
            ft.replace(binding.flContainer.getId(), searchFragment);
            ft.commit();
        }
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
}
