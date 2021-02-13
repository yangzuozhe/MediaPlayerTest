package com.example.mediaplayertest;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button mBtn1;
    Button mBtn2;
    Button mBtn3;
    Button mBtn4;
    SoundPool mSoundPool;
    int mMusic = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initSoundPool();

    }
    private void initView(){
        mBtn1 = findViewById(R.id.btn1);
        mBtn1 = findViewById(R.id.btn2);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //最后要回收Pool中的资源
        mSoundPool.release();
    }
}