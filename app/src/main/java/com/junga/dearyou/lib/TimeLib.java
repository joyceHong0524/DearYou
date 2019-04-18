package com.junga.dearyou.lib;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeLib {
    public final String TAG = TimeLib.class.getSimpleName();
    private volatile static TimeLib instance;
    public static final int SEC = 60;
    public static final int MIN = 60;
    public static final int HOUR = 24;
    public static final int DAY = 30;
    public static final int MONTH = 12;

    public static TimeLib getInstance() {
        if (instance == null){
            synchronized (TimeLib.class){
                if(instance == null){
                    instance = new TimeLib();
                }
            }
        }
        return instance;
    }
    //1. 현재 시간 long으로 반환하기
    public Long getLongTime(){
        Long time = System.currentTimeMillis();
        return time;
    }
    //2. long 형 타임스탬프를 보여주고 싶은 날짜로 변경하기.
    public String getStringDate(Long timestamp){

        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - timestamp) / 1000; //1000으로 나눠주는 이유는 millisecond가 아닌 second만 신경을 써줄 것이므로

        long hour =diffTime/3600;
        diffTime =diffTime%3600;
        long min = diffTime/60;
        long sec = diffTime%60;

        String ret = "";


        if(hour > 24){
            ret = new SimpleDateFormat("MM/dd").format(new Date(timestamp));
            if (hour>8760){
                ret = new SimpleDateFormat("MM/dd/yyyy").format(new Date(timestamp));
            }
        }
        else if(hour > 0){
            ret = hour+" hours ago";
        }
        else if(min > 0){
            ret = min+" mins ago";
        }
        else if(sec > 0){
            ret = "just now!";
        }
        else{
            ret = new SimpleDateFormat("MM/dd").format(new Date(timestamp));
        }

//        new SimpleDateFormat("MM/dd/yyyy").format(new Date(timestamp));

        return ret;
    }

}
