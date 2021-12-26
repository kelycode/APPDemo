package com.kavin.media;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class Activity_cloth extends AppCompatActivity {


    private Bitmap bmCopy;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloth);
        Bitmap bmSrc = BitmapFactory.decodeResource(getResources(), R.drawable.awaiyi);
        bmCopy = Bitmap.createBitmap(bmSrc.getWidth(), bmSrc.getHeight(), bmSrc.getConfig());
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bmCopy);
        canvas.drawBitmap(bmSrc, new Matrix(), paint);

        iv = (ImageView) findViewById(R.id.iv);

        iv.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        for(int i = -40; i <= 40; i++){
                            for(int j = -40; j <= 40; j++){
                                //把用户划过的坐标置为透明色
                                //改变指定的像素颜色
                                if(Math.sqrt(i*i + j*j) <= 40){
                                    if(x + i < bmCopy.getWidth() && y + j < bmCopy.getHeight() && x + i >= 0 && y + j >= 0){
                                        bmCopy.setPixel(x + i, y + j, Color.TRANSPARENT);
                                        iv.setImageBitmap(bmCopy);
                                    }
                                }
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }
}