package com.example.simplechatapp;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String userId;
    private String name;
    private String avatar;
    private String email;

    public User() {
    }

    public User(String userId, String name, String avatar, String email) {
        this.userId = userId;
        this.name = name;
        this.avatar = avatar;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.name);
        dest.writeString(this.avatar);
        dest.writeString(this.email);
    }

    protected User(Parcel in) {
        this.userId = in.readString();
        this.name = in.readString();
        this.avatar = in.readString();
        this.email = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
