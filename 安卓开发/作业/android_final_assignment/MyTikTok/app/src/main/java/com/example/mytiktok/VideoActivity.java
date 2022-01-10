package com.example.mytiktok;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mytiktok.Player.VideoPlayer;
import com.example.mytiktok.Player.VideoPlayerListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoActivity extends AppCompatActivity {

    private VideoPlayer videoPlayer;
    private ImageView imageView;
    //    private SeekBar seekBar;
//    private TextView textViewTime;
    private static int state = 0;

//    private final Handler handler = new Handler();
//    private final Runnable runnable = new Runnable() {
//        public void run() {
//            if (videoPlayer.isPlaying()) {
//                int current = (int) videoPlayer.getCurrentPosition();
//                seekBar.setProgress(current);
//            }
//            handler.postDelayed(runnable, 500);
//        }
//    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        state = 1;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video);

        videoPlayer = findViewById(R.id.videoView);
        imageView = findViewById(R.id.pauseImage);
//        seekBar = findViewById(R.id.seekBar);
//        textViewTime = findViewById(R.id.textViewTime);

        imageView.setAlpha(0);
        //加载native库
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }

        videoPlayer.setListener(new VideoPlayerListener());
        videoPlayer.setVideoPath(getPath());

//        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
//        play();
//        textViewTime.setText(time(videoPlayer.getDuration()));


        videoPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == 1) {
                    videoPlayer.pause();
                    imageView.setAlpha(255);
                } else {
                    videoPlayer.start();
                    imageView.setAlpha(0);
                }
                state = 1 - state;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (videoPlayer.isPlaying()) {
            videoPlayer.stop();
        }
        IjkMediaPlayer.native_profileEnd();
    }

//    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
//        // 当进度条停止修改的时候触发
//        @Override
//        public void onStopTrackingTouch(SeekBar seekBar) {
//            // 取得当前进度条的刻度
//            int progress = seekBar.getProgress();
//            videoPlayer.seekTo(progress);
//        }
//
//        @Override
//        public void onStartTrackingTouch(SeekBar seekBar) {
//        }
//
//        @Override
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//        }
//    };
//
//    protected void play() {
//        // 开始线程，更新进度条的刻度
//        handler.postDelayed(runnable, 0);
//        videoPlayer.start();
//        seekBar.setMax((int)videoPlayer.getDuration());
//    }
//
//
//    protected String time(long millionSeconds) {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
//        Calendar cal = Calendar.getInstance();
//        cal.setTimeInMillis(millionSeconds);
//        return simpleDateFormat.format(cal.getTime());
//    }


    private String getPath() {
        Intent intent = getIntent();
        String path = intent.getStringExtra("feedurl");
        return path;
    }
}