package com.example.mytiktok;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/*
 * 利用RecyclerView显示视频列表 (一页显示多个item)
 */

public class VideoListAdapter extends RecyclerView.Adapter<VideoListHolder> {

    private final List<Video> videoList = new ArrayList<>();

    private OnItemClickListener mCickListener;

    public void refresh(List<Video> newVideos) {
        videoList.clear();
        if (newVideos != null) {
            videoList.addAll(newVideos);
        }
        notifyDataSetChanged();
    }

    public List<Video> getVideoList() {
        return this.videoList;
    }

    @NonNull
    @Override
    public VideoListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_item, parent, false);
        return new VideoListHolder(itemView, mCickListener);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mCickListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoListHolder holder, int position) {
        holder.bind(videoList.get(position));
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public void setData(List<Video> videoLi) {
        for (int i = 0; i < videoLi.size(); i++) {
            videoList.add(videoLi.get(i));
        }
    }
}

