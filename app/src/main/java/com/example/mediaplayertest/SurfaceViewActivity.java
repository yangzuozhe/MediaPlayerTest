package com.example.mediaplayertest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

public class SurfaceViewActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback {
    SurfaceView mSfvShow;
    SurfaceHolder mSurfaceHolder;
    Button mBtnStart;
    Button mBtnPause;
    Button mBtnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_view);
        initView();

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

    private void initSurfaceHolder() {
        mSurfaceHolder = mSfvShow.getHolder();
        mSurfaceHolder.addCallback(this);
    }


    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnStart) {

        } else if (i == R.id.btnStop) {

        } else if (i == R.id.btnPause) {

        }
    }
}