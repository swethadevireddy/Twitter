package com.codepath.twitter.databinding;

/**
 * Created by sdevired on 10/29/16.
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



    public interface TweetDialogEvents {
        void cancel() ;
        void tweet();
    }


}
