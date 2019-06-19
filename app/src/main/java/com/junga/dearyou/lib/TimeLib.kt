package com.junga.dearyou.lib

import java.text.SimpleDateFormat
import java.util.Date

class TimeLib {
    val TAG = TimeLib::class.java!!.getSimpleName()
    //1. 현재 시간 long으로 반환하기
    val longTime: Long?
        get() = System.currentTimeMillis()

    //2. long 형 타임스탬프를 보여주고 싶은 날짜로 변경하기.
    fun getStringDate(timestamp: Long?): String {

        val curTime = System.currentTimeMillis()
        var diffTime = (curTime - timestamp!!) / 1000 //1000으로 나눠주는 이유는 millisecond가 아닌 second만 신경을 써줄 것이므로

        val hour = diffTime / 3600
        diffTime = diffTime % 3600
        val min = diffTime / 60
        val sec = diffTime % 60

        var ret = ""


        if (hour > 24) {
            ret = SimpleDateFormat("MM/dd").format(Date(timestamp))
            if (hour > 8760) {
                ret = SimpleDateFormat("MM/dd/yyyy").format(Date(timestamp))
            }
        } else if (hour > 0) {
            ret = "$hour hours ago"
        } else if (min > 0) {
            ret = "$min mins ago"
        } else if (sec > 0) {
            ret = "just now!"
        } else {
            ret = SimpleDateFormat("MM/dd").format(Date(timestamp))
        }

        //        new SimpleDateFormat("MM/dd/yyyy").format(new Date(timestamp));

        return ret
    }

    fun SimpleDate(timestamp: Long): String {

        val curTime = System.currentTimeMillis()
        var diffTime = (curTime - timestamp) / 1000 //1000으로 나눠주는 이유는 millisecond가 아닌 second만 신경을 써줄 것이므로

        val hour = diffTime / 3600
        diffTime = diffTime % 3600
        val min = diffTime / 60
        val sec = diffTime % 60


        //이번 달이면 요일과 날짜를 보여준다.

        var returnString = ""

        if(hour > 720){
            returnString = SimpleDateFormat("MM/dd").format(timestamp)
        }else{
            returnString = SimpleDateFormat("dd").format(timestamp) + "\n"+ SimpleDateFormat("E").format(timestamp)
        }

        return returnString

        //이번 달이 아니면 날짜만 보여준다

    }

    companion object {
        @Volatile
        private var instance: TimeLib? = null
        val SEC = 60
        val MIN = 60
        val HOUR = 24
        val DAY = 30
        val MONTH = 12

        fun getInstance(): TimeLib? {
            if (instance == null) {
                synchronized(TimeLib::class.java) {
                    if (instance == null) {
                        instance = TimeLib()
                    }
                }
            }
            return instance
        }
    }

    fun isWeekend(timestamp: Long?) : Boolean {
        var day = SimpleDateFormat("E").format(timestamp);

        when (day) {
            "Sat" -> return true
            "Sun" -> return true
        }

        return false
    }
}
