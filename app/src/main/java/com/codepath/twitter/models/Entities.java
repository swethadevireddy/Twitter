package com.codepath.twitter.models;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sdevired on 11/1/16.
 */
@Parcel
public class Entities  implements Parcelable {




    @SerializedName("media")
    @Expose
    public List<Media> media = new ArrayList<Media>();



    public List<Media> getMedia() {
        return media;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeTypedList(this.media);
    }

    public Entities() {
    }

    protected Entities(android.os.Parcel in) {
        this.media = in.createTypedArrayList(Media.CREATOR);
    }

    public static final Creator<Entities> CREATOR = new Creator<Entities>() {
        @Override
        public Entities createFromParcel(android.os.Parcel source) {
            return new Entities(source);
        }

        @Override
        public Entities[] newArray(int size) {
            return new Entities[size];
        }
    };
}
