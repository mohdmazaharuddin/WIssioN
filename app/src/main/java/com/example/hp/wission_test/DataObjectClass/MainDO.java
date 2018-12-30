package com.example.hp.wission_test.DataObjectClass;

public class MainDO {
    private String comment, category, caption, likes, picUrl, videoUrl, videoId;
    private int likesCount, youtubeLikes, youtubeViews;

    public MainDO(String picUrl, String category, String videoUrl, String caption, int likesCount, String videoId, int youtubeLikes, int youtubeViews) {
        this.category = category;
        this.picUrl = picUrl;
        this.videoUrl = videoUrl;
        this.caption = caption;
        this.likesCount = likesCount;
        this.videoId = videoId;
        this.youtubeLikes = youtubeLikes;
        this.youtubeViews = youtubeViews;
    }

    public String getCategory() {
        return category;
    }

    public String getCaption() {
        return caption;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public String getVideoId() {
        return videoId;
    }

}
