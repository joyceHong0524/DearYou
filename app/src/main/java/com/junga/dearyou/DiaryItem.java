package com.junga.dearyou;




import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.sql.Time;

import java.sql.Timestamp;
import java.util.Map;

public class DiaryItem {

    String authorId;
//    Date date;
    String content;
    boolean isLocked;
    String title;
    String diaryId;
    Long time;

    public DiaryItem(String diaryId, String authorId, String title, String content, boolean isLocked,Long time) {
        this.diaryId = diaryId;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.isLocked = isLocked;
        this.time = time;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public DiaryItem() {}

    public String getAuthorId() {
        return authorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public void setDiaryId(String diaryId){ this.diaryId = diaryId;}

    public String getDiaryId() {return diaryId;}

}
