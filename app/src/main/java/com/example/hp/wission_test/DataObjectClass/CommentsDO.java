package com.example.hp.wission_test.DataObjectClass;

public class CommentsDO {
    private String name;
    private String comment;
    private String videoId;

    public CommentsDO(String name, String comment, String videoId) {
        this.name = name;
        this.comment = comment;
        this.videoId = videoId;
    }

    public CommentsDO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
