package com.junga.dearyou;

public class DiaryItem {

    String authorId;
    String date;
    String content;
    boolean isLocked;

    public DiaryItem() {

    }

    public DiaryItem(String authorId, String date, String content, boolean isLocked) {
        this.authorId = authorId;
        this.date = date;
        this.content = content;
        this.isLocked = isLocked;
    }
}
