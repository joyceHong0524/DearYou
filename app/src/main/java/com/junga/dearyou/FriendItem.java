package com.junga.dearyou;

public class FriendItem {
    String userId;
    String friendName;
    boolean isFriend;

    public FriendItem(){

    }

    public FriendItem(String userId, String friendName,boolean isFriend) {
        this.userId = userId;
        this.friendName = friendName;
        this.isFriend = isFriend;
    }
}
