package com.junga.dearyou;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class DiaryItem {

    String authorId;
//    Date date;
    String content;
    boolean isLocked;
    String title;

    public DiaryItem(String authorId, String title, String content, boolean isLocked) {
        this.authorId = authorId;
        this.title = title;
//        this.date = date;
        this.content = content;
        this.isLocked = isLocked;
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

//    public Timestamp getDate() {
//        return date;
//    }
//
//    public void setDate(Timestamp date) {
//        this.date = date;
//    }

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


}
