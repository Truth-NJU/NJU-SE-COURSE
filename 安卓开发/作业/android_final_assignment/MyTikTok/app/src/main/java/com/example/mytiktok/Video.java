package com.example.mytiktok;

import com.google.gson.annotations.SerializedName;

public class Video {
    @SerializedName("_id")
    public String id;
    @SerializedName("feedurl")
    public String feedUrl;
    @SerializedName("nickname")
    public String nickName;
    @SerializedName("description")
    public String description;
    @SerializedName("likecount")
    public int likeCount;
    @SerializedName("avatar")
    public String avatar;
    @SerializedName("thumbnails")
    public String thumbnails;

    @Override
    public String toString() {
        return "Video{" +
                "id='" + id + '\'' +
                ", feedUrl='" + feedUrl + '\'' +
                ", nickName='" + nickName + '\'' +
                ", description='" + description + '\'' +
                ", likeCount=" + likeCount +
                ", avatar='" + avatar + '\'' +
                ", avatar='" + avatar + '\'' +
                ", thumbnails='" + thumbnails + '\'' +
                '}';
    }
}
