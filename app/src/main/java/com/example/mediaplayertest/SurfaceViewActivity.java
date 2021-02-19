package com.example.mediaplayertest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class SurfaceViewActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 媒体流数据
     */
    MediaPlayer mMediaPlayer;
    /**
     * 视频的view
     */
    SurfaceView mSfvShow;
    /**
     * 装在 视频view 里的 SurfaceHolder
     */
    SurfaceHolder mSurfaceHolder;
    Button mBtnStart;
    Button mBtnPause;
    Button mBtnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_view);
        initView();
        //初始化 SurfaceHolder
        initSurfaceHolder();
    }

    private void initView() {
        mSfvShow = findViewById(R.id.sfvShow);
        mBtnStart = findViewById(R.id.btnStart);
        mBtnPause = findViewById(R.id.btnPause);
        mBtnStop = findViewById(R.id.btnStop);
        mBtnStart.setOnClickListener(this);
        mBtnPause.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
    }

    /**
     * 初始化 SurfaceHolder
     */
    private void initSurfaceHolder() {
        //通过 SurfaceView 获得 SurfaceHolder
        mSurfaceHolder = mSfvShow.getHolder();
        //注册 SurfaceHolder 的监听事件
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            /**
             * 我们在这个方法上面获取对将视频数据传进view里
             * @param holder
             */
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                initUrlVideo();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            }
        });
        //设置分辨率，不设置就默认的分辨率
        mSurfaceHolder.setFixedSize(320, 220);
    }


    /**
     * 播放网络视频
     */
    private void initUrlVideo() {
        //设置网络资源到视频中
        setUrlVideo("http://vd4.bdstatic.com/mda-jiaz57vgzpe7ei2q/mda-jiaz57vgzpe7ei2q.mp4");
        //设置 Android studio 中的文件到视频中
//        setLocalVideo();
        //设置手机存储中的mp4文件
//        setStorageVideo();
        //设置媒体的属性
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                .build();
        ///设置媒体的属性
        mMediaPlayer.setAudioAttributes(audioAttributes);
        //设置显示的视频放到 SurfaceHolder 上
        mMediaPlayer.setDisplay(mSurfaceHolder);
        //视频准备播放的监听，我们把start方法写在这里，视频加载好后会自动播放
        //注意我这里说的加载好，意思是，视频的缓存可以达到播放的程度，可能缓存才到10%，视频就可以播放了
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Toast.makeText(SurfaceViewActivity.this, "准备好了，开始播放", Toast.LENGTH_SHORT).show();
                mMediaPlayer.start();
            }
        });
    }

    /**
     * 设置网络资源到视频中
     *
     * @param path 视频的地址
     */
    private void setUrlVideo(String path) {
        //首先实例化 MediaPlayer 对象
        mMediaPlayer = new MediaPlayer();
        try {
            //将网络的视频数据源设置进去 这样写也可以 mMediaPlayer.setDataSource(path);
            mMediaPlayer.setDataSource(this, Uri.parse(path));
            //资源进行加载，异步
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 网络流媒体的缓冲监听
        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                //我这里打日志，看现在的缓存进度到哪里了
                if (!mMediaPlayer.isPlaying() && percent > 0) {
                    Log.i("Demo", percent + "");
                }
            }
        });
    }

    /**
     * 设置本地的视频的地址
     */
    private void setLocalVideo() {
        mMediaPlayer = MediaPlayer.create(this, R.raw.movise);
    }

    /**
     * 设置手机的存储的视频
     */
    private void setStorageVideo() {
        mMediaPlayer = new MediaPlayer();
        try {
            File file = new File(getExternalFilesDir(null), "movies.mp4");
            mMediaPlayer.setDataSource(file.getAbsolutePath());
            //这里是准备资源进行播放（同步，在UI线程中）
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnStart) {
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
            }
        } else if (i == R.id.btnStop) {
            mMediaPlayer.stop();
        } else if (i == R.id.btnPause) {
            mMediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.release();
    }
}