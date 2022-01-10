package com.example.mediaplayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
int getCurrentPosition()：获取当前播放的位置。
int getDuration()：获取当前播放视频的总长度。
isPlaying()：当前VideoView是否在播放视频。
void pause()：暂停
void seekTo(int msec)：从第几毫秒开始播放。
void resume()：重新播放。
void setVideoPath(String path)：以文件路径的方式设置VideoView播放的视频源。
void setVideoURI(Uri uri)：以Uri的方式设置VideoView播放的视频源，可以是网络Uri或本地Uri。
void start()：开始播放。
void stopPlayback()：停止播放。
setMediaController(MediaController controller)：设置MediaController控制器。
setOnCompletionListener(MediaPlayer.onCompletionListener l)：监听播放完成的事件。
setOnErrorListener(MediaPlayer.OnErrorListener l)：监听播放发生错误时候的事件。
setOnPreparedListener(MediaPlayer.OnPreparedListener l)：：监听视频装载完成的事件。
 */

public class MainActivity extends AppCompatActivity {
    private VideoView videoView;
    private SeekBar seekBar;
    private Button buttonPlay;
    private Button buttonStop;
    private TextView textViewTime;
    private TextView textViewCurrentPosition;
    private TextView textViewStatus;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            if (videoView.isPlaying()) {
                int current = videoView.getCurrentPosition();
                seekBar.setProgress(current);
                textViewCurrentPosition.setText(time(videoView.getCurrentPosition()));
            }
            handler.postDelayed(runnable, 500);
        }
    };

    private String getVideoPath(int resId) {
        return "android.resource://" + this.getPackageName() + "/" + resId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_video);

        //final Uri uri = Uri.parse("http://ipfs.netkiller.cn/ipfs/QmcA1Fsrt6jGTVqAUNZBqaprMEdFaFkmkzA5s2M6mF85UC");
        videoView = this.findViewById(R.id.videoView);
        videoView.setVideoPath(getVideoPath(R.raw.bytedance));
        videoView.requestFocus();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                textViewTime.setText(time(videoView.getDuration()));
                textViewStatus.setText("视频加载完毕");
                buttonPlay.setEnabled(true);

            }
        });

        // 在播放完毕被回调
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(MainActivity.this, "播放完成", Toast.LENGTH_SHORT).show();
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                // 发生错误重新播放
                play();
                Toast.makeText(MainActivity.this, "播放出错", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        textViewStatus = findViewById(R.id.textViewStatus);
        textViewStatus.setText("玩命加载中");

        textViewTime = findViewById(R.id.textViewTime);

        seekBar = findViewById(R.id.seekBar);
        // 为进度条添加进度更改事件
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

        textViewCurrentPosition = (TextView) findViewById(R.id.textViewCurrentPosition);

        buttonPlay = (Button) findViewById(R.id.buttonPlay);
        buttonStop = findViewById(R.id.buttonStop);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

    }


    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        // 当进度条停止修改的时候触发
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 取得当前进度条的刻度
            int progress = seekBar.getProgress();
            if (videoView.isPlaying()) {
                // 设置当前播放的位置
                videoView.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

        }
    };

    protected void play() {

        // 开始线程，更新进度条的刻度
        handler.postDelayed(runnable, 0);
        videoView.start();
        seekBar.setMax(videoView.getDuration());

    }

    protected void stop() {
            videoView.pause();
    }

    protected String time(long millionSeconds) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millionSeconds);
        return simpleDateFormat.format(c.getTime());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

}
