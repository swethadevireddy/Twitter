package com.codepath.twitter.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.twitter.R;
import com.codepath.twitter.adapters.TweetsPagerAdapter;
import com.codepath.twitter.databinding.ActivityTimeLineBinding;
import com.codepath.twitter.fragments.TweetDialogFragment;
import com.codepath.twitter.fragments.TweetsFragment;
import com.codepath.twitter.helpers.InternetCheck;
import com.codepath.twitter.helpers.SnackBar;
import com.codepath.twitter.listeners.OnProgressListener;
import com.codepath.twitter.models.Tweet;

//Activity to display timeline.
public class TimeLineActivity extends AppCompatActivity implements TweetDialogFragment.TweetDialogListener, OnProgressListener{

    private ActivityTimeLineBinding binding;
    private TweetsPagerAdapter pagerAdapter;
    MenuItem miActionProgressItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_time_line);

        setContentView(binding.getRoot());

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(binding.tbInclude.toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);


        //setup floating button
        setupFloatingActionButton();

        pagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager());

        binding.contentInclude.viewpager.setAdapter(pagerAdapter);
        binding.contentInclude.tabs.setViewPager(binding.contentInclude.viewpager);
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

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);

        MenuItem searchItem = menu.findItem(R.id.miSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //open intent
                Intent i = new Intent(getApplicationContext(), TweetSearchActivity.class);
                i.putExtra("query", query);
                startActivity(i);
                searchView.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
             // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (!InternetCheck.isOnline()){
            SnackBar.getSnackBar("No Internet connection available", getParent()).show();
            return true;
        }

        switch (id){
            case R.id.miTweet:
                //open tweet dialog
                showTweetDialog(null);
                return true;
            case R.id.miProfile:
                //open tweet dialog
                showProfile();
                return true;
            case R.id.miMesssage:
                //show Message activity
                showMessageActivity();
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
     * to open Tweet compose dialog
     */
    public void showProfile(){
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }




    private void setupFloatingActionButton() {
        binding.fab.setOnClickListener(view -> showTweetDialog(null));
    }

    /**
     * listener method from TweetDialogFragment
     * @param t(Tweet)
     */
    @Override
    public void onSubmitTweet(Tweet t) {
        //TODO check flow from browser sharing
        binding.contentInclude.viewpager.setCurrentItem(0);
        TweetsFragment fragment = (TweetsFragment) getSupportFragmentManager().getFragments().get(0);
        fragment.newTweet(t);
    }

    @Override
    public void showProgressBar() {
        // Show progress item
        if(miActionProgressItem != null) {
            miActionProgressItem.setVisible(true);
        }
    }

    @Override
    public void hideProgressBar() {
        if(miActionProgressItem != null) {
            // Hide progress item
            miActionProgressItem.setVisible(false);
        }
    }

    private void showMessageActivity(){
        Intent i = new Intent(getApplicationContext(),MessageActivity.class);
        startActivity(i);
    }


}


