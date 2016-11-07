package com.codepath.twitter.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.twitter.fragments.HomeTimelineFragment;
import com.codepath.twitter.fragments.MentionsTimelineFragment;

/**
 * Created by sdevired on 11/4/16.
 */
public class TweetsPagerAdapter extends FragmentPagerAdapter{

    private String tabTitles[] = new String[] { "Home", "Mentions"};

    public TweetsPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return new HomeTimelineFragment();
        }else if(position == 1){
            return new MentionsTimelineFragment();
        }else{
            return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

}
