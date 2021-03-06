package com.kavin.camera2api;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.core.app.ActivityCompat;

import com.usens.androidphonecamera.R;

public class MainActivity extends Activity{

    public static final int MULTIPLE_REQUEST_CODE = 8736;
    public static MainActivity mActivity;

    public CameraThread mCameraThread;
    Button take_picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        take_picture = findViewById(R.id.take_picture);


        mCameraThread = new CameraThread(this);
        mCameraThread.initTextureView();

        //check and ask for CAMERA,READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE permission
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, MULTIPLE_REQUEST_CODE);
        }

        take_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("kavin","Onlick");
                mCameraThread.takePic();
            }
        });
    }

    /**
     * Permissions call back
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean permissionAllGranted = true;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(mActivity)
                        .setMessage("Insufficient permissions, please restart and agree to the permissions")
                        .setPositiveButton("Quit",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        mActivity.finish();
                                    }
                                })
                        .show();
                permissionAllGranted = false;
                break;
            }
        }
        //if permissions are all granted, init uSensSkeleton
        if (permissionAllGranted) {
                //After init uSensSkeleton, open the camera
                // for Camera2 running only when SKD >=23
                mCameraThread.initLooper();
                mCameraThread.openCamera();
        }
    }

    @Override
    protected void onDestroy() {
        if (mCameraThread.mImageReader!= null) {
            mCameraThread.mImageReader.close();
            mCameraThread.mImageReader = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }
}
