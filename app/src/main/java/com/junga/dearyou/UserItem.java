package com.junga.dearyou;

import java.util.ArrayList;

public class UserItem {

    String userId;
    String email;
    String nickname;
    String imageUrl;
    String diaryName;
    ArrayList<FriendItem> friends;
    ArrayList<DiaryItem> diaries;

    public UserItem () {

    }

    public UserItem(String userId, String email, String nickname, String imageUrl, String diaryName, ArrayList<FriendItem> friends,ArrayList<DiaryItem> diaries) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.diaryName = diaryName;
        this.friends = friends;
        this.diaries = diaries;
    }
}
