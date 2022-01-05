package com.kavin.camera2api.tools;

public class ImageTool {

    /*
     * for 420p  planes[2].getPixelStride() == 1
     */
    public static void yuv420p_to_yuv420sp(byte[] uv420PBytes,byte[] uvNV21Bytes, int width, int height){
        int i, j;
        int y_size = width * height;
        for (j = 0, i = 0; j < y_size/2; j+=2, i++)
        {
            uvNV21Bytes[j] = uv420PBytes[i + 1/4 * y_size];//v
            uvNV21Bytes[j+1] = uv420PBytes[i];//u
        }

    }
    /**
     * Degree 90
     * */
    public static byte[] rotateYUV420Degree90(byte[] data, int imageWidth,int imageHeight) {

        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth)
                        + (x - 1)];
                i--;
            }
        }
        return yuv;
    }

    /**
     * Negative 90
     * */
    public static byte[] rotateYUV420Negative90( byte[] src,int imageWidth,int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        int nWidth = 0, nHeight = 0;
        int wh = 0;
        int uvHeight = 0;

        if (imageWidth != nWidth || imageHeight != nHeight) {
            nWidth = imageWidth;
            nHeight = imageHeight;
            wh = imageWidth * imageHeight;
            uvHeight = imageHeight / 2;
        }

        // rotate Y
        int k = 0;
        for (int i = 0; i < imageWidth; i++) {
            int nPos = imageWidth - 1;
            for (int j = 0; j < imageHeight; j++) {
                yuv[k] = src[nPos - i];
                k++;
                nPos += imageWidth;
            }
        }

        for (int i = 0; i < imageWidth; i += 2) {
            int nPos = wh + imageWidth - 1;
            for (int j = 0; j < uvHeight; j++) {
                yuv[k] = src[nPos - i - 1];
                yuv[k + 1] = src[nPos - i];
                k += 2;
                nPos += imageWidth;
            }
        }
        return yuv;
    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }
}
