package com.codepath.twitter.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.twitter.R;
import com.codepath.twitter.application.TwitterApplication;
import com.codepath.twitter.databinding.FragmentComposeTweetBinding;
import com.codepath.twitter.databinding.TweetDialogFragmentPresenter;
import com.codepath.twitter.models.Tweet;
import com.codepath.twitter.models.User;
import com.codepath.twitter.net.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


/**
 * Created by sdevired on 10/21/16.
 * Diaglog Fragment for settings
 */
public class TweetDialogFragment extends DialogFragment  implements TweetDialogFragmentPresenter.TweetDialogEvents{


    private TweetDialogListener listener;
    private FragmentComposeTweetBinding binding;
    private TwitterClient client;
    private static final  int MAX_TWEET_CHAR_COUNT = 140;
    String sharedTweet;
    AlertDialog alertDialog;
    SharedPreferences pref;
    private static final String SAVE_TWEET_NAME = "savedTweet";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //setup binding object
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_compose_tweet, container, false);


        //populate profile
        client = TwitterApplication.getRestClient();
        getProfileInfo();

        createPopupWindow();



        return  binding.getRoot();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String savedTweet = pref.getString(SAVE_TWEET_NAME, "");
        sharedTweet = (sharedTweet == null)?savedTweet:sharedTweet;

        if(sharedTweet != null && sharedTweet.length() > 0){
            binding.etTweet.setText(sharedTweet);
            binding.etTweet.setSelection(sharedTweet.length());
            setTweetAttributes(sharedTweet);
        }
        //register on text change listener
        binding.etTweet.addTextChangedListener(new TextWatcher() {
            ColorStateList oldColors;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //default color
                oldColors = binding.etTweet.getTextColors(); //save original colors

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //set back the default color
                binding.tvTweetCharCount.setTextColor(oldColors);
                setTweetAttributes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        TweetDialogFragmentPresenter presenter = new TweetDialogFragmentPresenter(this);
        binding.setPresenter(presenter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (TweetDialogListener) context;

    }

    // Defines the listener interface with a method passing back data result.
    public interface TweetDialogListener{
        void onSubmitTweet(Tweet t);
    }


    private void getProfileInfo(){
        client.getProfileInfo(new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                User loggedInUser = User.fromJSONObject(response);
                binding.setUser(loggedInUser);
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });

    }


    @Override
    public void cancel() {
       // Toast.makeText(this.getContext(),"on cancel", Toast.LENGTH_SHORT).show();
        String tweet = binding.etTweet.getText()+"";
        if(binding.etTweet.getText() != null && tweet.length() > 0){
            //show save alert
            alertDialog.show();

        }else{
            //remove saved draft
            SharedPreferences.Editor edit = pref.edit();
            edit.remove(SAVE_TWEET_NAME);
            edit.commit();
            dismiss();
        }

    }

    @Override
    public void tweet() {
        client.postTweet(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //remove saved draft
                SharedPreferences.Editor edit = pref.edit();
                edit.remove(SAVE_TWEET_NAME);
                edit.commit();
                Tweet t =  Tweet.fromJSONObject(response);
                listener.onSubmitTweet(t);
                dismiss();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        }, binding.etTweet.getText().toString(), null);


        //Toast.makeText(this.getContext(),"on Submit", Toast.LENGTH_SHORT).show();
    }

    public void setTweet(String tweet){
        this.sharedTweet = tweet;
    }

    private void setTweetAttributes(String s){
        int remaningCharCount = MAX_TWEET_CHAR_COUNT  - s.length();
        binding.tvTweetCharCount.setText(String.valueOf(remaningCharCount));
        binding.btnTweet.setEnabled(true);
        if(remaningCharCount < 0){
            binding.btnTweet.setEnabled(false);
            binding.tvTweetCharCount.setTextColor(ContextCompat.getColor(binding.etTweet.getContext(), R.color.colorError));
        }else{
            if(remaningCharCount == MAX_TWEET_CHAR_COUNT){
                binding.btnTweet.setEnabled(false);
            }
        }
    }

    private void createPopupWindow(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        //TODO change the button color to default color
        // set dialog message
        alertDialogBuilder
                .setMessage("Save Draft? ")
                .setCancelable(false)
                .setPositiveButton("SAVE",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        SharedPreferences.Editor edit = pref.edit();
                        edit.putString(SAVE_TWEET_NAME, binding.etTweet.getText()+"");
                        edit.commit();
                        dismiss();
                        dialog.cancel();

                    }
                })
                .setNegativeButton("DELETE",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                        dismiss();
                    }
                });

        // create alert dialog
         alertDialog = alertDialogBuilder.create();

        // show it
       // alertDialog.show();
    }






}