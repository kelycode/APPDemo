package com.kavin.media;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kavin.jutils.utils.permission.PermissionRequest;

public class Activity_Scale extends AppCompatActivity {

    Button bt_click;
    ImageView iv_dog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale);
        requestPremission();


    }

    public void click_specially(View view) {
        Bitmap bpsrc = BitmapFactory.decodeFile("/sdcard/dog.jpg");
        Bitmap bpCopy = Bitmap.createBitmap(bpsrc.getWidth(), bpsrc.getHeight(), bpsrc.getConfig());
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bpCopy);
        Matrix mt = new Matrix();

        //平移
        //mt.setTranslate(20, 40);
        //缩放
        //sx：水平方向的缩放比例
        //sy：竖直方向的缩放比例
        //mt.setScale(0.5f, 0.5f);
        //mt.setScale(0.5f, 0.5f, bmCopy.getWidth() / 2, bmCopy.getHeight() / 2);
        //旋转
        mt.setRotate(45, bpCopy.getWidth() / 2, bpCopy.getHeight() / 2);

        //镜面
        //mt.setScale(-1, 1);
        //mt.postTranslate(bmCopy.getWidth(), 0);

        //倒影
        //mt.setScale(1, -1);
        //mt.postTranslate(0, bpCopy.getHeight());

        canvas.drawBitmap(bpsrc, mt, paint);

        ImageView iv_src = findViewById(R.id.iv_src);
        ImageView iv_copy = findViewById(R.id.iv_copy);
        iv_src.setImageBitmap(bpsrc);
        iv_copy.setImageBitmap(bpCopy);
    }

    // crate backup picture.
    public void click_bak(View view) {
        //这个对象是只读的
        Bitmap bmSrc = BitmapFactory.decodeFile("/sdcard/dog.jpg");

        //创建图片副本
        //1.在内存中创建一个与原图一模一样大小的bitmap对象，创建与原图大小一致的白纸
        Bitmap bmCopy = Bitmap.createBitmap(bmSrc.getWidth(), bmSrc.getHeight(), bmSrc.getConfig());

        //2.创建画笔对象
        Paint paint = new Paint();

        //3.创建画板对象，把白纸铺在画板上
        Canvas canvas = new Canvas(bmCopy);

        //4.开始作画，把原图的内容绘制在白纸上
        canvas.drawBitmap(bmSrc, new Matrix(), paint);

        ImageView iv_src = (ImageView) findViewById(R.id.iv_src);
        ImageView iv_copy = (ImageView) findViewById(R.id.iv_copy);
        iv_src.setImageBitmap(bmSrc);
        iv_copy.setImageBitmap(bmCopy);
    }

    // load big picture.
    public void click(View view) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;

        BitmapFactory.decodeFile("/storage/emulated/0/dog.jpg", opt);

        int imageWidth = opt.outWidth;
        int imageHeight = opt.outHeight;
        Display dp = getWindowManager().getDefaultDisplay();
        int screenWidth = dp.getWidth();
        int screenHeight = dp.getHeight();

        // 计算缩放比例
        int scale = 2;
        int scaleWidth = imageWidth / screenWidth;
        int scaleHeight = imageHeight / screenHeight;
        if (scaleWidth >= scaleHeight && scaleWidth >= 1) {
            scale = scaleWidth;
        } else if (scaleWidth < scaleHeight && scaleHeight >= 1) {
            scale = scaleHeight;
        }

        // 设置缩放比例
        opt.inSampleSize = scale;
        opt.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile("/storage/emulated/0/dog.jpg", opt);

        ImageView iv = (ImageView) findViewById(R.id.iv_src);
        iv.setImageBitmap(bm);

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