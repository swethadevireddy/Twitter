package com.codepath.twitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.codepath.twitter.helpers.InternetCheck;
import com.codepath.twitter.helpers.SnackBar;

/**
 * Created by sdevired on 11/5/16.
 */
public class UserTimelineFragment extends TweetsFragment {

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fetch tweets
        populateTimeline(0);
    }

    /**
     * always userfragment should have screenname
     * @param screenName
     * @return
     */
    public static UserTimelineFragment newInstance(String screenName){
        UserTimelineFragment userFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screenName", screenName);
        userFragment.setArguments(args);
        return userFragment;
    }


    @Override
    protected void populateTimeline(long id) {
        if(InternetCheck.isOnline()) {
            String screenName = getArguments().getString("screenName");
            twitterClient.getUserTimeline(id, screenName, mGetTweetsResponseHandler);
        }else{
            //display snackbar
            SnackBar.getSnackBar(InternetCheck.NO_INTERNET, this.getActivity()).show();
        }
    }

    @Override
    protected String getType() {
        return null;
    }




}
