package com.junga.dearyou;

import java.sql.Time;
import java.sql.Timestamp;

public class DiaryItem {

    String authorId;
    Timestamp date;
    String content;
    boolean isLocked;

    public DiaryItem(String authorId, Timestamp date, String content, boolean isLocked) {
        this.authorId = authorId;
        this.date = date;
        this.content = content;
        this.isLocked = isLocked;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
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


}
