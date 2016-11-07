package com.codepath.twitter.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.twitter.R;
import com.codepath.twitter.adapters.MessageAdapter;
import com.codepath.twitter.application.TwitterApplication;
import com.codepath.twitter.databinding.FragmentMessageBinding;
import com.codepath.twitter.helpers.DividerItemDecoration;
import com.codepath.twitter.helpers.InternetCheck;
import com.codepath.twitter.helpers.SnackBar;
import com.codepath.twitter.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.twitter.models.Message;
import com.codepath.twitter.net.TwitterClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sdevired on 11/6/16.
 */
public class MessageFragment extends Fragment {

    ArrayList<Message> messages;
    RecyclerView rvMessages;
    FragmentMessageBinding binding;
    TwitterClient twitterClient;
    MessageAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        //data binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, parent, false);

        // Setup RecyclerView
        setupRecyclerView();

        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize users and adapter.
        messages = new ArrayList<>();
        adapter = new MessageAdapter(messages, getContext());
        //RestClient
        twitterClient = TwitterApplication.getRestClient();

         getMessages(0);

    }

    //setup Recyclerview
    private void setupRecyclerView(){
        //get the recyclerview
        rvMessages = binding.rvUsers;
        //set the adapter
        rvMessages.setAdapter(adapter);
        //set layout manager
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext());

        rvMessages.setLayoutManager(layoutManager);

        //set dividerItem decoration
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        rvMessages.addItemDecoration(itemDecoration);

        //infinite scrolling
        rvMessages.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                getMessages(messages.get(totalItemsCount-1).getId() -1);
            }
        });
    }

    protected void getMessages(long maxId) {
        if(InternetCheck.isOnline()) {
            twitterClient.getMessages(maxId, mGetMessageResponseHandler);
        }else{
            //display snackbar
            SnackBar.getSnackBar(InternetCheck.NO_INTERNET, this.getActivity()).show();
        }
    }


    //create getUsersResponseHandler
    public JsonHttpResponseHandler mGetMessageResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            Log.d("DEBUG", "Success -- " + response.toString());

            //prepare GSON handler
            Type listType = new TypeToken<ArrayList<Message>>(){}.getType();
            GsonBuilder gsonBuilder = new GsonBuilder();

            //register date adapter to convert string to date
            gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                SimpleDateFormat s = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
                @Override
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    try{
                        s.setTimeZone(TimeZone.getTimeZone("UTC"));
                        return s.parse(json.getAsString());

                    }
                    catch(ParseException ex){
                        return null;
                    }
                }
            });
            Gson dateGson = gsonBuilder.create();
            ArrayList<Message> newMessages = dateGson.fromJson(response.toString(), listType);   //notify adapter
            refreshAdapter(newMessages);
        }

        public void refreshAdapter(List<Message> newMessages){
            //notify adapter
            int curSize = adapter.getItemCount();
            messages.addAll(newMessages);
            adapter.notifyItemRangeInserted(curSize, newMessages.size());
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d("ERROR", errorResponse.toString());
            SnackBar.getSnackBar(getString(R.string.no_internet), getActivity()).show();
        }
    };

    public void newMessage(Message m){
        messages.add(0, m);
        adapter.notifyItemInserted(0);
   }








}
