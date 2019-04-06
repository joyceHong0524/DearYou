package com.junga.dearyou;

import java.io.Serializable;
import java.util.ArrayList;

public class UserItem {

    public String userId;
    public String email;
    public String nickname;
    public String imageUrl;
    public String diaryName;
    //   public ArrayList<FriendItem> friends;
    public ArrayList<DiaryItem> diaries;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDiaryName() {
        return diaryName;
    }

    public void setDiaryName(String diaryName) {
        this.diaryName = diaryName;
    }

    public ArrayList<DiaryItem> getDiaries() {
        return diaries;
    }

    public void setDiaries(ArrayList<DiaryItem> diaries) {
        this.diaries = diaries;
    }



    public UserItem(String userId, String email, String nickname, String imageUrl, String diaryName,ArrayList<DiaryItem> diaries) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.diaryName = diaryName;
//        this.friends = friends;
        this.diaries = diaries;
    }
}
