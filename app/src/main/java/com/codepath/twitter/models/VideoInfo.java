package com.codepath.twitter.models;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sdevired on 11/3/16.
 */
@Parcel
public class VideoInfo  implements Parcelable {
    public List<Long> getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(List<Long> aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public Long getDurationMillis() {
        return durationMillis;
    }

    public void setDurationMillis(Long durationMillis) {
        this.durationMillis = durationMillis;
    }

    public List<Variant> getVariants() {
        return variants;
    }

    public void setVariants(List<Variant> variants) {
        this.variants = variants;
    }

    @SerializedName("aspect_ratio")
    @Expose
    private List<Long> aspectRatio = new ArrayList<Long>();
    @SerializedName("duration_millis")
    @Expose
    private Long durationMillis;
    @SerializedName("variants")
    @Expose
    private List<Variant> variants = new ArrayList<Variant>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeList(this.aspectRatio);
        dest.writeValue(this.durationMillis);
        dest.writeTypedList(this.variants);
    }

    public VideoInfo() {
    }

    protected VideoInfo(android.os.Parcel in) {
        this.aspectRatio = new ArrayList<Long>();
        in.readList(this.aspectRatio, Long.class.getClassLoader());
        this.durationMillis = (Long) in.readValue(Long.class.getClassLoader());
        this.variants = in.createTypedArrayList(Variant.CREATOR);
    }

    public static final Creator<VideoInfo> CREATOR = new Creator<VideoInfo>() {
        @Override
        public VideoInfo createFromParcel(android.os.Parcel source) {
            return new VideoInfo(source);
        }

        @Override
        public VideoInfo[] newArray(int size) {
            return new VideoInfo[size];
        }
    };
}
