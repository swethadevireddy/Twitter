package com.codepath.twitter.fragments;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.twitter.R;
import com.codepath.twitter.application.TwitterApplication;
import com.codepath.twitter.databinding.FragmentComposeTweetBinding;
import com.codepath.twitter.databinding.TweetDialogFragmentPresenter;
import com.codepath.twitter.models.Message;
import com.codepath.twitter.models.User;
import com.codepath.twitter.net.TwitterClient;
import com.codepath.twitter.utils.GSONBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


/**
 * Created by sdevired on 10/21/16.
 * Dialog Fragment for Composing Tweet
 */
public class ComposeMessageFragment extends DialogFragment  implements TweetDialogFragmentPresenter.TweetDialogEvents{

    private ComposeMessageListener listener;
    private FragmentComposeTweetBinding binding;
    private TwitterClient client;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //setup binding object
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_compose_tweet, container, false);
        //populate profile
        client = TwitterApplication.getRestClient();
        getProfileInfo();
        return  binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set the presenter
        TweetDialogFragmentPresenter presenter = new TweetDialogFragmentPresenter(this);
        binding.setPresenter(presenter);
        binding.etMessageTo.setVisibility(View.VISIBLE);
        binding.btnTweet.setText(getString(R.string.message));
        binding.btnTweet.setEnabled(true);
        binding.tvTweetCharCount.setVisibility(View.GONE);
    }

    /**
     * attach the activity
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (ComposeMessageListener) context;
    }

    // Defines the listener interface with a method passing back data result.
    public interface ComposeMessageListener{
        void onMessageSubmit(Message m);
    }

    /**
     * to retrieve the profile
     */
    private void getProfileInfo(){
        client.getProfileInfo(null, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                User loggedInUser = User.fromJSONObject(response);
                binding.setScreenName("@"+loggedInUser.getScreenName());
                //user model is used in the view directly
                binding.setUser(loggedInUser);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });

    }

    /**
     *This method is called on click of close button
     * check if text is present, if present show alert dialog else close the fragment.
     */
    @Override
    public void cancel() {
       // Toast.makeText(this.getContext(),"on cancel", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    /**
     * method is called on click of tweet button, call the api, remove the draft and dismiss
     */
    @Override
    public void tweet() {
        client.postMessage(binding.etMessageTo.getText().toString(),  binding.etTweet.getText().toString(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Message m = GSONBuilder.getGsonWithDate().fromJson(response.toString(), Message.class);
                listener.onMessageSubmit(m);
                dismiss();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });

    }

}