package com.codepath.twitter.models;


import android.os.Parcelable;

import com.codepath.twitter.MyDatabase;
import com.codepath.twitter.utils.DateUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Condition;
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
 * model for Tweet
 */
@Parcel(analyze={Tweet.class})
@Table(database = MyDatabase.class)
public class Tweet extends BaseModel implements Parcelable {

    @SerializedName("id")
    @Expose
    @PrimaryKey
    @Column
    long id;

    @SerializedName("text")
    @Expose
    @Column
    String text;

    @SerializedName("created_at")
    @Expose
    @Column
    Date createdAt;

    @SerializedName("user")
    @Expose
    @Column
    @ForeignKey(saveForeignKeyModel = false)
    User user;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "type")
    private String type;



    public Entities getEntities() {
        return entities;
    }

    public void setEntities(Entities entities) {
        this.entities = entities;
    }

    @SerializedName("entities")
    @Expose
    Entities entities;

    public ExtendEntities getExtendedEntities() {
        return extendedEntities;
    }

    public void setExtendedEntities(ExtendEntities extendedEntities) {
        this.extendedEntities = extendedEntities;
    }


    @SerializedName("extended_entities")
    @Expose
    ExtendEntities extendedEntities;

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public Long getReTweetCount() {
        return reTweetCount;
    }

    public void setReTweetCount(Long reTweetCount) {
        this.reTweetCount = reTweetCount;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public Long getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Long favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    @SerializedName("retweeted")
    @Expose
    @Column(name = "isReTweeted")
    private boolean retweeted=false;

    @SerializedName("retweet_count")
    @Expose
    @Column(name = "reTweetCount")
    private Long reTweetCount;

    @SerializedName("favorited")
    @Expose
    @Column(name = "favorited")
    private boolean favorited =false;

    @SerializedName("favorite_count")
    @Expose
    @Column(name = "favoriteCount")
    private Long favoriteCount;



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
    public static ArrayList<Tweet> getTweets(int page, String type){
        int limit = 25;
        Condition condition = Condition.column(Tweet_Table.type.getNameAlias()).eq(type);
        List<Tweet> tweets = new Select()
                .from(Tweet.class)
                .where(condition)
                .orderBy(OrderBy.fromProperty(Tweet_Table.createdAt).descending())
                .offset(limit * page)
                .limit(limit)
                .queryList();
        return (ArrayList) tweets;
    }

    public Tweet() {
    }


    public static String HOME_TIMELINE = "home";
    public static String MENTION_TIMELINE = "mentions";

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
        dest.writeString(this.type);
        dest.writeByte(this.retweeted ? (byte) 1 : (byte) 0);
        dest.writeValue(this.reTweetCount);
        dest.writeByte(this.favorited ? (byte) 1 : (byte) 0);
        dest.writeValue(this.favoriteCount);
        dest.writeString(this.relativeTime);
    }

    protected Tweet(android.os.Parcel in) {
        this.id = in.readLong();
        this.text = in.readString();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        this.user = in.readParcelable(User.class.getClassLoader());
        this.type = in.readString();
        this.retweeted = in.readByte() != 0;
        this.reTweetCount = (Long) in.readValue(Long.class.getClassLoader());
        this.favorited = in.readByte() != 0;
        this.favoriteCount = (Long) in.readValue(Long.class.getClassLoader());
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
