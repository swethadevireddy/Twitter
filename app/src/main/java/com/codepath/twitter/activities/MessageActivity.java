package com.codepath.twitter.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.twitter.R;
import com.codepath.twitter.databinding.ActivityMessageBinding;
import com.codepath.twitter.fragments.ComposeMessageFragment;
import com.codepath.twitter.fragments.MessageFragment;
import com.codepath.twitter.fragments.TweetsFragment;
import com.codepath.twitter.helpers.InternetCheck;
import com.codepath.twitter.helpers.SnackBar;
import com.codepath.twitter.models.Message;
import com.codepath.twitter.models.Tweet;

/**
 * Created by sdevired on 11/6/16.
 */
public class MessageActivity extends AppCompatActivity implements ComposeMessageFragment.ComposeMessageListener {

    ActivityMessageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_message);

        setContentView(binding.getRoot());

        //set toolbar
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(binding.tbInclude.toolbar);

        //to display home icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(getString(R.string.message));

        //create FollowFragment
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            MessageFragment messageFragment = new MessageFragment();
            ft.replace(binding.flContainer.getId(), messageFragment);
            ft.commit();
        }
    }


    public void showTweetDialog(String message){
        FragmentManager fm = getSupportFragmentManager();
        ComposeMessageFragment settingsDialog = new ComposeMessageFragment();
        settingsDialog.show(fm, "message");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (!InternetCheck.isOnline()){
            SnackBar.getSnackBar("No Internet connection available", getParent()).show();
            return true;
        }
        switch (id){
            case R.id.miMesssage:
                //open tweet dialog
                showTweetDialog(null);
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onMessageSubmit(Message m) {

        MessageFragment fragment = (MessageFragment) getSupportFragmentManager().getFragments().get(0);
        fragment.newMessage(m);

    }
}
