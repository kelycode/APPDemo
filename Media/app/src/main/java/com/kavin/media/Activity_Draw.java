package com.kavin.media;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import com.kavin.jutils.utils.permission.PermissionRequest;

public class Activity_Draw extends AppCompatActivity {

    private ImageView iv;
    int startX;
    int startY;
    private Canvas canvas;
    private Paint paint;
    private Bitmap bpCopy;

    private final static String TAG = "Activity_Draw";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        requestPremission();

        // 加载画画板的背景图
        Bitmap bpSrc = BitmapFactory.decodeFile("/sdcard/dog.jpg");

        Log.i(TAG,bpSrc.getWidth()+" -- "+bpSrc.getHeight()+" -- "+bpSrc.getConfig());
        bpCopy = Bitmap.createBitmap(bpSrc.getWidth(),bpSrc.getHeight(),bpSrc.getConfig());

        paint = new Paint();
        canvas = new Canvas(bpCopy);
        canvas.drawBitmap(bpSrc,new Matrix(),paint);
        iv = findViewById(R.id.iv);
        iv.setImageBitmap(bpCopy);

        // 设置触摸侦听
        iv.setOnTouchListener(new View.OnTouchListener() {

            // 触摸屏幕时，触摸事件产生时，此方法调用
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    // 用户手指摸到屏幕
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        break;
                    // 用户手指正在滑动
                    case MotionEvent.ACTION_MOVE:
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        canvas.drawLine(startX, startY, x, y, paint);
                        // 每次绘制完毕之后，本次绘制的结束坐标变成下一次绘制的初始坐标
                        startX = x;
                        startY = y;
                        iv.setImageBitmap(bpCopy);
                        break;
                    // 用户手指离开屏幕
                    case MotionEvent.ACTION_UP:
                        break;

                }
                // true：告诉系统，这个触摸事件由我来处理
                // false：告诉系统，这个触摸事件我不处理，这时系统会把触摸事件传递给imageview的父节点
                return true;
            }
        });
    }

    // set paint color
    public void red(View v)
    {
        paint.setColor(Color.RED);
    }
    public void green(View v)
    {
        paint.setColor(Color.GREEN);
    }

    public void brush(View v)
    {
        //set paint width
        paint.setStrokeWidth(7);
    }

    public void save(View v)
    {
        File file = new File("/sdcard/dog.jpg");
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        bpCopy.compress(Bitmap.CompressFormat.JPEG,100,fos);

        // 发送sd卡就绪广播
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
        intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
        sendBroadcast(intent);
    }

    private void requestPremission() {
        PermissionRequest permissRequest;
        String premission[] = {
                Manifest.permission.CALL_PHONE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };
        permissRequest = new PermissionRequest(this);
        permissRequest.autoShowTip(true);
        permissRequest.permissions(premission);
        permissRequest.execute();
    }
}