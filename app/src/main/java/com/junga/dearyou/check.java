package com.junga.dearyou;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class check extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        UserItem user = new UserItem("hi","so@gmail.com","dflkj","df","Dfadfa",new ArrayList<DiaryItem>());

        MyApp.getApp().setUser(user);

        UserItem getUser = MyApp.getApp().getUser();

        Log.d("check",getUser.getEmail());

        toString(getUser);

    }

    private void toString(UserItem user){
        Log.d("df",user.toString());
        Log.d("id",user.getUserId());
    }
}
