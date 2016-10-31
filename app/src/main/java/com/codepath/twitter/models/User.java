package com.codepath.twitter.models;


import android.os.Parcelable;

import com.codepath.twitter.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by sdevired on 10/26/16.
 * model for User
 */
@Table(database = MyDatabase.class)
@Parcel(analyze={User.class})
public class User extends BaseModel implements Parcelable{
    @Column
    private String name;

    @PrimaryKey
    @Column
    long id;

    @Column
    String profileImageUrl;

    @Column
    String screenName;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }


    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }
    public static User fromJSONObject(JSONObject o){
        User u = new User();
        try {
            u.id = o.getLong("id");
            u.name = o.getString("name");
            u.profileImageUrl = o.getString("profile_image_url").replace("_normal.jpg","_bigger.jpg");
            u.screenName = "@"+o.getString("screen_name");
            return u;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;


    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.id);
        dest.writeString(this.profileImageUrl);
        dest.writeString(this.screenName);
    }

    public User() {
    }

    protected User(android.os.Parcel in) {
        this.name = in.readString();
        this.id = in.readLong();
        this.profileImageUrl = in.readString();
        this.screenName = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(android.os.Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
