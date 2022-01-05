package com.kavin.camera2api;

import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.usens.androidphonecamera.R;

public class SkeletonUI {
    private Canvas canvas;//use to draw hand 2d skeleton
    private SurfaceView mSurfaceview;//show camera
    private SurfaceHolder mSurfaceHolder;

    public MainActivity mActivity;

    public SkeletonUI(MainActivity activity) {
        mActivity = activity;
    }

    /**
     * init SurfaceView
     * SurfaceView use for drawing the skeleton
     */
    public void initSurfaceView() {
        mSurfaceview = (SurfaceView) mActivity.findViewById(R.id.surfaceview);
        mSurfaceview.setZOrderOnTop(true);//on the top level
        mSurfaceview.getHolder().setFormat(PixelFormat.TRANSPARENT);//make this view transparent
        mSurfaceHolder = mSurfaceview.getHolder();
    }
}