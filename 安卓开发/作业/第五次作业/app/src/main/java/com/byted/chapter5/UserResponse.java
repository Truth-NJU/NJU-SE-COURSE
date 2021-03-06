package com.byted.chapter5;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class UserResponse {
    @SerializedName("errorCode")
    public int errorCode;
    @SerializedName("errorMsg")
    public String errorMsg;
    @SerializedName("data")
    User user;

    @Override
    public String toString() {
        return "UserResponse{" +
                "errorCode=" + errorCode +
                ", errorMsg='" + errorMsg + '\'' +
                ", user=" + user +
                '}';
    }
}
