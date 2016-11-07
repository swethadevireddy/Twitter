package com.codepath.twitter.models;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Date;

/**
 * Created by Shyam Rokde on 2/27/16.
 */
@Parcel
public class Message implements Parcelable {


  @SerializedName("recipient")
  @Expose
  public User user;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @SerializedName("id")
  @Expose
  public long id;

  @SerializedName("text")
  @Expose
  public String message;

  @SerializedName("created_at")
  @Expose
  public Date createdAt;

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Message() {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(android.os.Parcel dest, int flags) {
    dest.writeParcelable(this.user, flags);
    dest.writeLong(this.id);
    dest.writeString(this.message);
    dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
  }

  protected Message(android.os.Parcel in) {
    this.user = in.readParcelable(User.class.getClassLoader());
    this.id = in.readLong();
    this.message = in.readString();
    long tmpCreatedAt = in.readLong();
    this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
  }

  public static final Creator<Message> CREATOR = new Creator<Message>() {
    @Override
    public Message createFromParcel(android.os.Parcel source) {
      return new Message(source);
    }

    @Override
    public Message[] newArray(int size) {
      return new Message[size];
    }
  };
}