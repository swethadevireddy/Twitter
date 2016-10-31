package com.codepath.twitter.databinding;

/**
 * Created by sdevired on 10/29/16.
 * class which can be used in ui to call the listeners directly.
 */
public class TweetDialogFragmentPresenter {


    private TweetDialogEvents tweetDialogEvents;

    public TweetDialogFragmentPresenter(TweetDialogEvents tweetDialogEvents) {
        this.tweetDialogEvents = tweetDialogEvents;
    }

    public void onCancel(){
        tweetDialogEvents.cancel();
    }


    public void onTweet(){
        tweetDialogEvents.tweet();
    }


    /**
     * interface to register the listeners
     */

    public interface TweetDialogEvents {
        void cancel() ;
        void tweet();
    }


}
