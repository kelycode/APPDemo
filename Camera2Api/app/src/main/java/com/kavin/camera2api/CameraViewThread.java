package com.kavin.camera2api;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import static android.os.Looper.getMainLooper;

public class CameraViewThread extends HandlerThread {

    private ImageView mImageView;
    private TextView mTextView;
    private Handler mThreadHandler;
    private byte[] mCurrentImage;

    public CameraViewThread(ImageView imageView, TextView textView) {
        super("CameraViewThread");
        mImageView = imageView;
        mTextView = textView;
        mCurrentImage = new byte[640 * 480 * 3];
        //Thread start
        start();
        mThreadHandler = new Handler(getLooper());

        mThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                waitForFrame();
            }
        }, 2000);
    }

    private void waitForFrame() {
        int nrOfPixels = 640 * 480; //
        int pixels[] = new int[nrOfPixels];
        for (int i = 0; i < nrOfPixels; i++) {
            int r = mCurrentImage[3 * i];
            int g = mCurrentImage[3 * i + 1];
            int b = mCurrentImage[3 * i + 2];
            pixels[i] = Color.rgb(r, g, b);
        }

        final Bitmap bitmap = Bitmap.createBitmap(pixels, 640, 480, Bitmap.Config.ARGB_8888);
        Handler mainHandler = new Handler(getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                mImageView.setImageBitmap(bitmap);
            }
        });
        mThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                waitForFrame();
            }
        }, 100);
    }
}
