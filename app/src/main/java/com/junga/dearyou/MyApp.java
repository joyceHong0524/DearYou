package com.junga.dearyou;

import android.app.Application;

import java.util.ArrayList;

public class MyApp extends Application {


    ArrayList<DiaryItem> diaries;
    ArrayList<FriendItem> friends;
    UserItem user;


    public ArrayList<DiaryItem> getDiaries() {
        return diaries;
    }

    public void setDiaries(ArrayList<DiaryItem> diaries) {
        this.diaries = diaries;
    }

    public ArrayList<FriendItem> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<FriendItem> friends) {
        this.friends = friends;
    }

    public UserItem getUser() {
        return user;
    }

    public void setUser(UserItem user) {
        this.user = user;
    }

}
