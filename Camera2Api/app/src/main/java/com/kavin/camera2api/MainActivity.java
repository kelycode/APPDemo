package com.kavin.camera2api;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Handler mThreadHandler;
    private ImageView mImageView;
    private TextView mTextView;
    private CameraThread mCameraThread;
    private CameraViewThread mCameraViewThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCameraThread = new CameraThread(getApplicationContext());

        mTextView = (TextView) findViewById(R.id.textView);
        mImageView = (ImageView) findViewById(R.id.imageView);


        //Request Camera permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 0);
        }

        mThreadHandler = new Handler(getMainLooper());
        mThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //               mCameraThread.startCameraFromOwner(0);
            }
        }, 1000);
        mThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCameraViewThread = new CameraViewThread(mImageView, mTextView);
            }
        }, 3000);
    }
}
