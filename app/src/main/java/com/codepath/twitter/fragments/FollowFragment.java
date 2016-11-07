package com.codepath.twitter.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.twitter.R;
import com.codepath.twitter.adapters.UsersAdapter;
import com.codepath.twitter.application.TwitterApplication;
import com.codepath.twitter.databinding.FragmentFollowBinding;
import com.codepath.twitter.helpers.DividerItemDecoration;
import com.codepath.twitter.helpers.InternetCheck;
import com.codepath.twitter.helpers.SnackBar;
import com.codepath.twitter.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.twitter.models.User;
import com.codepath.twitter.models.UserFollowResponse;
import com.codepath.twitter.net.TwitterClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sdevired on 11/5/16.
 */
public class FollowFragment extends Fragment {

    ArrayList<User> users;
    RecyclerView rvUsers;
    FragmentFollowBinding binding;
    TwitterClient twitterClient;
    SwipeRefreshLayout swipeContainer;
    UsersAdapter adapter;
    String screenName;
    String followType;
    Long nextCursor;
    public static final String FOLLOWING_FRAGMENT_TYPE = "following";
    public static final String FOLLOWERS_FRAGMENT_TYPE = "followers";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        //data binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_follow, parent, false);

        // Setup RecyclerView
        setupRecyclerView();
        //Setup swipeContainer
        setupSwipeContainer();
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize users and adapter.
        users = new ArrayList<>();
        adapter = new UsersAdapter(users, getContext());
        //RestClient
        twitterClient = TwitterApplication.getRestClient();

        //populate users
        screenName = getArguments().getString("screenName");
        followType = getArguments().getString("followType");

        //getUsers
        getUsers();

    }


    private void setupSwipeContainer(){
        // Lookup the swipe container view
        swipeContainer = binding.swipeContainer;
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(mRefreshListener);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorAccent);

    }

    public static FollowFragment newInstance(String screenName, String followType){
        FollowFragment followFragment = new FollowFragment();
        Bundle args = new Bundle();
        args.putString("screenName", screenName);
        args.putString("followType", followType);
        followFragment.setArguments(args);
        return followFragment;
    }

    //setup Recyclerview
    private void setupRecyclerView(){
        //get the recyclerview
        rvUsers = binding.rvUsers;
        //set the adapter
        rvUsers.setAdapter(adapter);
        //set layout manager
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext());

        rvUsers.setLayoutManager(layoutManager);

        //set dividerItem decoration
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        rvUsers.addItemDecoration(itemDecoration);

        //infinite scrolling
        rvUsers.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                getUsers();
            }
        });
    }



    protected void getUsers() {
        if(InternetCheck.isOnline()) {
            if(FOLLOWING_FRAGMENT_TYPE.equalsIgnoreCase(followType)) {
                twitterClient.getFollowingUserList(screenName, nextCursor, mGetUsersResponseHandler);
            }else{
                twitterClient.getFollowersUserList(screenName, nextCursor, mGetUsersResponseHandler);
            }
        }else{
            //display snackbar
            SnackBar.getSnackBar(InternetCheck.NO_INTERNET, this.getActivity()).show();
        }
    }



    //define refreshListener
    private final SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            //check internet availability , if not display snackbar else call twitter api
            if (!InternetCheck.isOnline()) {
                swipeContainer.setRefreshing(false);
                SnackBar.getSnackBar("No Internet connection available", getActivity()).show();
            } else {
                adapter.clear();
                nextCursor = null;
                getUsers();
            }
        }
    };


    //create getUsersResponseHandler
    public JsonHttpResponseHandler mGetUsersResponseHandler = new JsonHttpResponseHandler() {


        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d("DEBUG", "Success -- " + response.toString());

            Gson gson = new GsonBuilder().create();
            UserFollowResponse userFollowResponse = gson.fromJson(response.toString(), UserFollowResponse.class);

            ArrayList<User> users = userFollowResponse.getUsers();
            nextCursor = Long.valueOf(userFollowResponse.getNextCursorStr());
            //notify adapter
            refreshAdapter(users);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d("ERROR", errorResponse.toString());
            SnackBar.getSnackBar(getString(R.string.no_internet), getActivity()).show();
        }
    };

    //refresh adapter with newUsers
    public void refreshAdapter(List<User> newUsers){
        //notify adapter
        int curSize = adapter.getItemCount();
        users.addAll(newUsers);
        adapter.notifyItemRangeInserted(curSize, newUsers.size());
        swipeContainer.setRefreshing(false);
    }

}
