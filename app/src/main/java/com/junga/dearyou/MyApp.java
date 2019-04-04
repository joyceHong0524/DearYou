package com.junga.dearyou;

import android.app.Application;

public class MyApp extends Application {

    //If you want to use this methods,
    //E.x. MyApp myApp = (MyApp) getApplication();
    //myApp.getUser_nick;

    private String user_email;
    private String user_password;

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public void setBothEmailAndPassword(String email, String password){
        this.user_email= email;
        this.user_password=  password;
    }
}
