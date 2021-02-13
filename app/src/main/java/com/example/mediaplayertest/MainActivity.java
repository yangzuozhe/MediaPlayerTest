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
        mBtn1 = findViewById(R.id.btn1);
        initSoundPool();
        mBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 mMusic = mSoundPool.load(MainActivity.this,R.raw.music,1);
            }
        });
    }
    private void initSoundPool(){
        //如果大于5.0
        if (Build.VERSION.SDK_INT >= 21){
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
        }else {
            //如果小于Android 5.0，实例化 mSoundPool 对象的方式就简单地多
            //注意这里使用 AudioManager 的流媒体音乐
            mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        }

        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Toast.makeText(MainActivity.this,"播放完毕",Toast.LENGTH_SHORT).show();
                if (mMusic != 0){
                    //第一个参数id，即传入池中的顺序，第二个和第三个参数为左右声道，第四个参数为优先级，第五个是否循环播放，0不循环，-1循环
                    //最后一个参数播放比率，范围0.5到2，通常为1表示正常播放
                    mSoundPool.play(mMusic,1,1,0,0,1);
                }
            }
        });

    }
}