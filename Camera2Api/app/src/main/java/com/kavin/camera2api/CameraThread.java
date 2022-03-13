package com.kavin.camera2api;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;

import com.kavin.camera2api.tools.ImageTool;
import com.usens.androidphonecamera.R;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class CameraThread{
    private final int VIDEO_WIDTH = 640;//input Resolution WIDTH
    private final int VIDEO_HEIGHT = 480;//input Resolution HEIGHT
    private CameraManager mCameraManager = null;
    private CameraDevice mCameraDevice;
    public ImageReader mImageReader;
    private CaptureRequest.Builder mPreviewBuilder;
    private Handler mHandler;
    private HandlerThread mThreadHandler;
    public MainActivity mActivity;
    private TextureView mPreviewView;
    public String currentCameraID = "0";//using FrontCamera    normal id :1

    private boolean canTakePic = false;                                                       //是否可以拍照
    private CameraCaptureSession mCameraCaptureSession = null;
    private int mCameraSensorOrientation = 0;

    public CameraThread(MainActivity activity) {
        mActivity = activity;
    }

    /**
     * init Thread Only for Camera2
     */
    public void initLooper() {
        mThreadHandler = new HandlerThread("CAMERA2");
        mThreadHandler.start();
        mHandler = new Handler(mThreadHandler.getLooper());//using a new looper
//        mHandler = new Handler(getMainLooper());//using main looper
    }
    /**
     * init PreviewView
     * PreviewView use for show video preview
     */
    public void initTextureView() {
        mPreviewView = (TextureView) mActivity.findViewById(R.id.textureview);
        mPreviewView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                initCameraInfo();
            }
            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
            }
            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
                //releasecamera();
                return false;
            }
            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {
            }
        });
    }

    public void initCameraInfo()
    {
        //init camera
        mCameraManager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
        CameraCharacteristics characteristics = null;
        try {
            characteristics = mCameraManager.getCameraCharacteristics(currentCameraID);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        mCameraSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        mImageReader = ImageReader.newInstance(VIDEO_WIDTH, VIDEO_HEIGHT, ImageFormat.YUV_420_888, 2);
        mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mHandler);
    }

    public void openCamera(){
        try {
            //set camera id and open camera

            mCameraManager.openCamera(currentCameraID, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice cameraDevice) {
                    Log.i("kavin","onOpened");
                    mCameraDevice = cameraDevice;
                    createCaptureSession(cameraDevice);
                }
                @Override
                public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                    mCameraDevice.close();
                    mCameraDevice = null;
                }
                @Override
                public void onError(@NonNull CameraDevice cameraDevice, int i) {
                    Log.i("kavin","onError");
                }
            }, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void createCaptureSession(CameraDevice cameraDevice) {

        try {
            //设置预览
            mPreviewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Surface surface = new Surface(mPreviewView.getSurfaceTexture());
        mPreviewBuilder.addTarget(surface);  // 将CaptureRequest的构建器与Surface对象绑定在一起
        mPreviewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);      // 闪光灯
        mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE); // 自动对焦

        // 为相机预览，创建一个CameraCaptureSession对象
        try {
            cameraDevice.createCaptureSession(Arrays.asList( surface,mImageReader.getSurface()),new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        mCameraCaptureSession = session;
                        session.setRepeatingRequest(mPreviewBuilder.build(), mCaptureCallBack, mHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    Log.i("kavin","开启预览会话失败！！");
                }
            }, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void takePic()
    {
        if (mCameraDevice == null || !mPreviewView.isAvailable() || !canTakePic) return;

        CaptureRequest.Builder captureRequestBuilder = null;
        try {
            // 设置相机模式
            captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        // mImageReader.getSurface()需要在opencamera createCaptureSession时穿过去，不然会有问题
        captureRequestBuilder.addTarget(mImageReader.getSurface());
        captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE); // 自动对焦
        captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);     // 闪光灯
        //根据摄像头方向对保存的照片进行旋转，使其为"自然方向"
        captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, mCameraSensorOrientation);
        try {
            mCameraCaptureSession.capture(captureRequestBuilder.build(), null, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraCaptureSession.CaptureCallback mCaptureCallBack = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            canTakePic = true;
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            Log.i("kavin","onCaptureFailed 开启预览失败");
        }
    };

    private ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Log.i("kavin","拍照成功！！");
            Image img = reader.acquireLatestImage();
            Image.Plane[] planes = img.getPlanes();

            // Y-buffer
            ByteBuffer yBuffer = planes[0].getBuffer();
            int ySize = yBuffer.remaining();
            byte[] yBytes = new byte[ySize];
            yBuffer.get(yBytes);

            // VU-buffer
            ByteBuffer vuBuffer = planes[2].getBuffer();
            int vuSize = vuBuffer.remaining();
            byte[] vuBytes = new byte[vuSize];
            vuBuffer.get(vuBytes);

            //Merger yBytes and vuBytes
            byte[] yuvBytes = ImageTool.byteMerger(yBytes,vuBytes);
            img.close();
        }
    };
}
