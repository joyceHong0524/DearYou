package com.junga.dearyou;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class MyApp extends Application {

    public static MyApp app;


    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

    }

    public static MyApp getApp() {
        return app;
    }

    private UserItem user;

    public UserItem getUser() {
        if (user == null) {
            user = new UserItem();
        }
        return user;
    }

    public void setUser(UserItem user1) {
        this.user = user1;
    }
}
