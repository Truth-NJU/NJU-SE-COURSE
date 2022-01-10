package com.example.mytiktok;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class VideoListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private MyImageView imageView;
    private TextView nickName;
    private MyImageView avator;
    private TextView likeCount;
    private TextView description;

    private OnItemClickListener mListener;


    public VideoListHolder(@NonNull View itemView, OnItemClickListener listener) {
        super(itemView);
        itemView.setOnClickListener(this);
        imageView = itemView.findViewById(R.id.image);
        nickName = itemView.findViewById(R.id.text_nickname);
        avator=itemView.findViewById(R.id.avator);
        likeCount = itemView.findViewById(R.id.text_likecount);
        description=itemView.findViewById(R.id.description);
        mListener=listener;
    }

    public void bind(final Video video) {
        imageView.setImageURL(video.thumbnails);
        nickName.setText("@" + video.nickName);
        avator.setImageURL(video.avatar);
        likeCount.setText(String.valueOf(video.likeCount));
        description.setText(video.description);
    }

    @Override
    public void onClick(View v) {
        mListener.onItemClick(v,getAdapterPosition());
    }
}
