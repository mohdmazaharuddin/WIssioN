package com.example.hp.wission_test.DataObjectClass;

public class LikesDO {
    private String videoId;
    private int likesCount;
    private String emailId;

    public LikesDO(String videoId, int likesCount, String emailId) {
        this.videoId = videoId;
        this.likesCount = likesCount;
        this.emailId = emailId;
    }

    public LikesDO() {
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
