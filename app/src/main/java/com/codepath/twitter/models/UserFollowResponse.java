package com.codepath.twitter.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sdevired on 10/26/16.
 * model for UserFollowResponse
 */
public class UserFollowResponse{

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    @SerializedName("users")
    @Expose
    ArrayList<User> users = new ArrayList<User>();

    public String getNextCursorStr() {
        return nextCursorStr;
    }

    public void setNextCursorStr(String nextCursorStr) {
        this.nextCursorStr = nextCursorStr;
    }

    @SerializedName("next_cursor_str")
    @Expose
    private String nextCursorStr;



}
