package com.junga.dearyou.lib;

import android.text.TextUtils;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckLib {
    private volatile static CheckLib instance;

    public static CheckLib getInstance() {
        if (instance == null){
            synchronized (CheckLib.class){
                if(instance == null){
                    instance = new CheckLib();
                }
            }
        }
        return instance;
    }

    public boolean isValidPassword(String target) {
        Pattern p = Pattern.compile("(^[a-zA-Z0-9~_&*%.!]*$)");
        Matcher m = p.matcher(target);
        if (m.find() && !target.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")){
            return true;
        }else{
            return false;
        }
}

    public boolean isValidNickname(String target){
        Pattern p = Pattern.compile("(^[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣]*$)");
        Matcher m = p.matcher(target);

        return m.find();
    }

    public boolean isValidEmail(String target) {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
