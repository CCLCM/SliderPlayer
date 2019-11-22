package com.chencl.slierdemo.video;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;


import com.chencl.slierdemo.R;

import java.util.ArrayList;

public class VideoPlayer extends AppCompatActivity {
    /**
     * android.widget.VideoView：视频播放器控件
     */

    private CustomVideoView videoView;
    private int mCurrentVideoIndex = 0;
    private ArrayList<String> patchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video);
        Intent getPath = getIntent();

        patchList = getPath.getStringArrayListExtra("videoURL");

        if (patchList.size() == 0 || patchList == null) {
            return;
        }
        bindViews();
    }

    private void bindViews() {
        /**绑定视频视图*/
        videoView = findViewById(R.id.sv_video_player);
        videoView.setVideoPath(patchList.get(mCurrentVideoIndex % patchList.size()));
        /**
         * 为 VideoView 视图设置媒体控制器，设置了之后就会自动由进度条、前进、后退等操作
         */
        //videoView.setMediaController(new MediaController(this));

        /**视频准备完成时回调
         * */
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                Log.d("tag", "--------------视频准备完毕,可以进行播放.......");
            }
        });
        /**
         * 视频播放完成时回调
         */
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.setOnPreparedListener(null);
                mCurrentVideoIndex += 1;
                videoView.setVideoPath(patchList.get(mCurrentVideoIndex % patchList.size()));
                /**播放完成时，再次循环播放*/
                videoView.start();
            }
        });

        /**
         * 视频播放发送错误时回调
         */
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });

        /**开始播放视频
         * */
        videoView.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView = null;
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

}