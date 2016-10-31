package com.codepath.twitter.models;


import android.os.Parcelable;

import com.codepath.twitter.MyDatabase;
import com.codepath.twitter.utils.DateUtil;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by sdevired on 10/26/16.
 */
@Parcel(analyze={Tweet.class})
@Table(database = MyDatabase.class)
public class Tweet extends BaseModel implements Parcelable {
    @PrimaryKey
    @Column
    long id;

    @Column
    String text;

    @Column
    Date createdAt;

    @Column
    @ForeignKey(saveForeignKeyModel = false)
    User user;


    public String getRelativeTime() {
        return relativeTime;
    }

    public void setRelativeTime(String relativeTime) {
        this.relativeTime = relativeTime;
    }

    String relativeTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date created_at) {
        this.createdAt = created_at;
    }

    public User getUser() {
        return user;
    }



    public void setUser(User user) {
        this.user = user;
    }


     public static Tweet fromJSONObject(JSONObject o){
        Tweet t = new Tweet();
        try {
            t.id = o.getLong("id");
            t.text = o.getString("text");
            t.createdAt = DateUtil.formatTwitterDate(o.getString("created_at"));
            t.user = User.fromJSONObject(o.getJSONObject("user"));
            t.relativeTime = DateUtil.getRelativeTime(t.createdAt);
            return t;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static ArrayList<Tweet> fromJsonArray(JSONArray a){
        ArrayList<Tweet> tweets = new ArrayList<>();
        for(int i = 0 ; a.length() >  i ; i++){
            Tweet t = null;
            try {
                t = fromJSONObject(a.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tweets.add(t);
        }
        return tweets;
    }


    /**
     * Retrieve tweets from db
     * @param page
     * @return
     */
    public static ArrayList<Tweet> getTweets(int page){
        int limit = 25;
        List<Tweet> tweets = new Select()
                .from(Tweet.class)
                .orderBy(OrderBy.fromProperty(Tweet_Table.createdAt).descending())
                .offset(limit * page)
                .limit(limit)
                .queryList();
        return (ArrayList) tweets;
    }

    public Tweet() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.text);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeParcelable(this.user, flags);
        dest.writeString(this.relativeTime);
    }

    protected Tweet(android.os.Parcel in) {
        this.id = in.readLong();
        this.text = in.readString();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        this.user = in.readParcelable(User.class.getClassLoader());
        this.relativeTime = in.readString();
    }

    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(android.os.Parcel source) {
            return new Tweet(source);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}
