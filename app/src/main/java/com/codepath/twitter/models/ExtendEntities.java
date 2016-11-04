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
public class ExtendEntities  implements Parcelable {


    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @SerializedName("video_info")
    @Expose
    public VideoInfo videoInfo;


    @SerializedName("type")
    @Expose
    public String type;

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    @SerializedName("media_url")
    @Expose
    public String mediaUrl;

    @SerializedName("media")
    @Expose
    private List<Media> media = new ArrayList<Media>();

    /**
     *
     * @return
     *     The media
     */
    public List<Media> getMedia() {
        return media;
    }

    /**
     *
     * @param media
     *     The media
     */
    public void setMedia(List<Media> media) {
        this.media = media;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeParcelable(this.videoInfo, flags);
        dest.writeString(this.type);
        dest.writeString(this.mediaUrl);
        dest.writeList(this.media);
    }

    public ExtendEntities() {
    }

    protected ExtendEntities(android.os.Parcel in) {
        this.videoInfo = in.readParcelable(VideoInfo.class.getClassLoader());
        this.type = in.readString();
        this.mediaUrl = in.readString();
        this.media = new ArrayList<Media>();
        in.readList(this.media, Media.class.getClassLoader());
    }

    public static final Creator<ExtendEntities> CREATOR = new Creator<ExtendEntities>() {
        @Override
        public ExtendEntities createFromParcel(android.os.Parcel source) {
            return new ExtendEntities(source);
        }

        @Override
        public ExtendEntities[] newArray(int size) {
            return new ExtendEntities[size];
        }
    };
}
