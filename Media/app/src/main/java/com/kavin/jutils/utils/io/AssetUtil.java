package com.kavin.jutils.utils.io;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.kavin.jutils.utils.JUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * assets 工具类
 */
public final class AssetUtil {

    /**
     * 获取 assets 目录下的文件
     *
     * @param path 文件在 assets 文件夹中的路径
     * @return 文件内容
     */
    public static String getFileFromAssets(@NonNull String path) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = JUtils.getApp().getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(path)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 获取 assets 目录下的图片
     *
     * @param path 图片在 assets 文件夹中的路径
     * @return
     */
    public static Bitmap getImageFromAssets(@NonNull String path) {
        Bitmap image = null;
        AssetManager am = JUtils.getApp().getResources().getAssets();
        try {
            InputStream is = am.open(path);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
