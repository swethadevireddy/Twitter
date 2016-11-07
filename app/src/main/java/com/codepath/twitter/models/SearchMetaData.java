package com.codepath.twitter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sdevired on 11/6/16.
 */
public class SearchMetaData {


    public String getMaxId() {
        return maxId;
    }

    public void setMaxId(String maxId) {
        this.maxId = maxId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @SerializedName("max_id")
    @Expose
    private String maxId;


    @SerializedName("query")
    @Expose
    private String query;


}
