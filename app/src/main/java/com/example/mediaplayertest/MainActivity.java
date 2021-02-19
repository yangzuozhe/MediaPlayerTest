package com.example.mediaplayertest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button mBtn1;
    Button mBtn2;
    Button mBtn3;
    Button mBtn4;
    /**
     * 短音效
     */
    SoundPool mSoundPool;
    /**
     * 本地的player
     */
    MediaPlayer mLocalMediaPlayer;
    /**
     * 网络资源的player
     */
    MediaPlayer mUrlMediaPlayer;
    /**
     * 本地音乐进度条
     */
    ProgressBar mPbMusic;
    /**
     * 网络资源音乐进度条
     */
    ProgressBar mPbUrlMusic;
    int mMusic = 0;
    boolean mIsStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //findBy
        initView();

        //初始化，短一些的音效
        initSoundPool();
        checkSP();
        initUrlMediaPlayer();
    }

    private void initView() {
        mBtn1 = findViewById(R.id.btn1);
        mBtn2 = findViewById(R.id.btn2);
        mBtn3 = findViewById(R.id.btn3);
        mPbMusic = findViewById(R.id.pbMusic);
        mPbUrlMusic = findViewById(R.id.pbUrlMusic);
        //点击播放短的音频
        mBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通过 mSoundPool.load 加载音频，转化为int类型
                //我们可以在加载完毕的回调那里进行播放
                mMusic = mSoundPool.load(MainActivity.this, R.raw.music, 1);
            }
        });
        //点击播放长的音频
        mBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocalMediaPlayer();
            }
        });
        //网络资源的歌曲
        mBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUrlMediaPlayer != null) {
                    if (mUrlMediaPlayer.isPlaying()) {
                        resetUrlMediaPlayer();
                    }
                    //准备资源
                    mUrlMediaPlayer.prepareAsync();

                }
            }
        });
    }

    /**
     * 初始化 SoundPool 短音频，短一点的音效
     */
    private void initSoundPool() {
        //如果大于5.0
        if (Build.VERSION.SDK_INT >= 21) {
            //首先准备一个 AudioAttributes 实例
            AudioAttributes attributes;
            //AudioAttributes 音频属性取代了 AudioManager 音频流
            attributes = new AudioAttributes.Builder()
                    //设置描述音频信号的预期用途的属性
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    //设置描述音频信号(如语音)的内容类型的属性
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            //实例化 SoundPool.Builder
            SoundPool.Builder spb = new SoundPool.Builder();
            //转换音频格式
            spb.setAudioAttributes(attributes);
            //传入音频最大的数量
            spb.setMaxStreams(10);
            //创建 SoundPool 对象
            mSoundPool = spb.build();
        } else {
            //如果小于Android 5.0，实例化 mSoundPool 对象的方式就简单地多
            //注意这里使用 AudioManager 的流媒体音乐
            mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }
        //判断音频加载完毕的监听
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Toast.makeText(MainActivity.this, "加载完毕完毕，准备播放", Toast.LENGTH_SHORT).show();
                if (mMusic != 0) {
                    //第一个参数id，即传入池中的顺序，第二个和第三个参数为左右声道，第四个参数为优先级，第五个是否循环播放，0不循环，-1循环
                    //最后一个参数播放比率，范围0.5到2，通常为1表示正常播放
                    //将我们的音频通过 mSoundPool.play 播放出来
                    mSoundPool.play(mMusic, 1, 1, 0, 0, 1);
                }
            }
        });
    }

    private void checkSP() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            initLocalMediaPlayer();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initLocalMediaPlayer();
            } else {
                Toast.makeText(this, "你需要同意权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 播放长音频(获取手机存储的音乐)
     */
    private void initLocalMediaPlayer() {
        //是否有挂载
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        //获取本地的音乐，位置是，res/raw/long_music.mp3
//        mMediaPlayer = MediaPlayer.create(this, R.raw.long_music);
        mLocalMediaPlayer = new MediaPlayer();
        //这里千万要注意，我们这里不可以使用   File file = new File(Environment.getExternalStorageDirectory()+"/Download","long_music.mp3");
        //在 api29 以后我们只能访问 getExternalFilesDir 或者 getExternalCacheDir
        File file = new File(getExternalFilesDir(null), "long_music.mp3");
        try {
            //这里要使用 setDataSource 方法添加音乐文件的位置
            mLocalMediaPlayer.setDataSource(file.getPath());
            //这里做启动音乐文件的准备
            mLocalMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //首先准备一个 AudioAttributes 实例
        AudioAttributes attributes;
        //AudioAttributes 音频属性取代了 AudioManager 音频流
        attributes = new AudioAttributes.Builder()
                //设置描述音频信号的预期用途的属性
                .setUsage(AudioAttributes.USAGE_MEDIA)
                //设置描述音频信号(如语音)的内容类型的属性
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        //设置音乐属性
        mLocalMediaPlayer.setAudioAttributes(attributes);

        mLocalMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                setPosition(mLocalMediaPlayer, mPbMusic);
            }
        });

    }

    /**
     * 使用网络歌曲获取当前歌曲
     */
    private void initUrlMediaPlayer() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        mUrlMediaPlayer = new MediaPlayer();
        try {
            mUrlMediaPlayer.setDataSource("http://fm111.img.xiaonei.com/tribe/20070613/10/52/A314269027058MUS.mp3");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //首先准备一个 AudioAttributes 实例
        AudioAttributes attributes;
        //AudioAttributes 音频属性取代了 AudioManager 音频流
        attributes = new AudioAttributes.Builder()
                //设置描述音频信号的预期用途的属性
                .setUsage(AudioAttributes.USAGE_MEDIA)
                //设置描述音频信号(如语音)的内容类型的属性
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        //设置音乐属性
        mUrlMediaPlayer.setAudioAttributes(attributes);
        //网络流媒体的缓冲监听（只有当percent大于0的时候才可以播放）
        mUrlMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                Log.i("Demo", percent + "");
                //当缓存进度值 percent 大于 0 时就可以播放音乐
                //这个 percent 就相当于我们进度条的灰色条
                if (percent > 0) {
                    startUrlMediaPlayer();
                }
            }
        });
        //网络流媒体播放结束监听
        mUrlMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(MainActivity.this, "歌曲播放结束", Toast.LENGTH_SHORT).show();
            }
        });
        // 设置错误信息监听，这个其实看，下面的红色报错信息就可以了
        mUrlMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.i("Error", "what：" + what + "\t extra:" + extra);
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //最后要回收Pool中的资源
        mSoundPool.release();
        stopLocalMediaPlayer();
        stopUrlMediaPlayer();
    }

    /**
     * 释放本地音频资源
     */
    private void stopLocalMediaPlayer() {
        if (mLocalMediaPlayer != null && mLocalMediaPlayer.isPlaying()) {
            mLocalMediaPlayer.stop();
            mLocalMediaPlayer.release();
            mLocalMediaPlayer = null;
        }
    }

    /**
     * 释放网络音频资源
     */
    private void stopUrlMediaPlayer() {
        if (mUrlMediaPlayer != null && mUrlMediaPlayer.isPlaying()) {
            mUrlMediaPlayer.stop();
            mUrlMediaPlayer.release();
            mUrlMediaPlayer = null;
        }
    }

    /**
     * 开始播放本地音乐
     */
    private void startLocalMediaPlayer() {
        if (mLocalMediaPlayer.isPlaying()) {
            //如果正在播放了就重新播放
            mLocalMediaPlayer.seekTo(0);
        } else {
            mLocalMediaPlayer.start();
        }
    }

    /**
     * 开始播放网络资源的音乐
     */
    private void startUrlMediaPlayer() {
        //防止重复一直调用start
        if (!mIsStart) {
            return;
        }
        mIsStart = false;
        mUrlMediaPlayer.start();
        setPosition(mUrlMediaPlayer, mPbUrlMusic);
    }

    /**
     * 重置网络歌曲的播放状态
     */
    private void resetUrlMediaPlayer() {
        mIsStart = true;
        mUrlMediaPlayer.reset();
        try {
            mUrlMediaPlayer.setDataSource("http://fm111.img.xiaonei.com/tribe/20070613/10/52/A314269027058MUS.mp3");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设定音乐进度
     */
    private void setPosition(final MediaPlayer mediaPlayer, final ProgressBar progressBar) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    //得到歌曲的时间
                    final int songTime = mediaPlayer.getDuration();
                    //得到当前的播放位置
                    final int currentPosition = mediaPlayer.getCurrentPosition();
                    //如果播完了就不再运行线程
                    if (songTime == currentPosition) {
                        return;
                    }
                    try {
                        //如果获取到了歌曲的总时间
                        if (songTime != 0) {
                            final int progress = (int) (((float) currentPosition / songTime) * 100);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(progress);
                                }
                            });
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

}