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
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import com.kavin.camera2api.tools.ImageTool;
import com.usens.androidphonecamera.R;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class CameraThread implements TextureView.SurfaceTextureListener{
    private final int VIDEO_WIDTH = 640;//input Resolution WIDTH
    private final int VIDEO_HEIGHT = 480;//input Resolution HEIGHT
    private CameraDevice mCameraDevice;
    public ImageReader mImageReader;
    private CaptureRequest.Builder mPreviewBuilder;
    private Handler mHandler;
    private HandlerThread mThreadHandler;
    private Size mPreviewSize;
    public MainActivity mActivity;
    private TextureView mPreviewView;
    public String currentCameraID = "0";//using FrontCamera    normal id :1


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
        mPreviewView.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (android.os.Build.VERSION.SDK_INT < 23) {
            openCamera();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        configureTransform(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
    /**
     * Rotate View base on moble phone orientation
     */
    public void configureTransform(int viewWidth, int viewHeight) {
        if (null == mPreviewView || null == mPreviewSize || null == mActivity) {
            return;
        }
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        //Log.d("Surface.ROTATION_90","Surface.ROTATION_90   "+rotation);
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        } else if (Surface.ROTATION_0 == rotation) {
            matrix.postScale(3/4f, 4/3f, centerX, centerY);
            matrix.postScale(4/3f, 4/3f, centerX, centerY);
        }
        mPreviewView.setTransform(matrix);
    }

    /**
     * start Preview and register imageReader
     * @param camera using camera device
     * Call startPreview(Camera2) @ onOpened()
     */
    SurfaceTexture texture;
    private void startPreview(CameraDevice camera) throws CameraAccessException {
        while (!mPreviewView.isAvailable());
        texture = mPreviewView.getSurfaceTexture();
        texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface surface = new Surface(texture);
        try {
            // create preview request
            mPreviewBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        //open AE and AF mode
        mPreviewBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
        //get image data by YUV_420_NV21 type
        mImageReader = ImageReader.newInstance(VIDEO_WIDTH, VIDEO_HEIGHT, ImageFormat.YUV_420_888, 2);
        mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mHandler);
        //add two target
        // using surface to show camera video
        // using mImageReader to get image raw data
        mPreviewBuilder.addTarget(surface);
        mPreviewBuilder.addTarget(mImageReader.getSurface());
        camera.createCaptureSession(Arrays.asList( surface,mImageReader.getSurface()),mSessionStateCallback, mHandler);
    }

    /**
     *  Camera2 camera ->onOpened()->startPreview(Camera2)
     *  Camera2 open() @ onRequestPermissionsResult() when SDK ver>=23 but @ onSurfaceTextureAvailable() when SDK version < 23
     */
    public void openCamera(){
        try {
            //init cameraManager
            CameraManager cameraManager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(currentCameraID);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[0];
            ///* the above code is also @setupPreviewSize(cameraID); */
            configureTransform(mPreviewView.getWidth(),mPreviewView.getHeight());
            //set camera id and open camera
            cameraManager.openCamera(currentCameraID, mCameraDeviceStateCallback, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private CameraCaptureSession.StateCallback mSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            try {
                updatePreview(session);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onConfigureFailed(CameraCaptureSession session) {

        }
    };

    private void updatePreview(CameraCaptureSession session) throws CameraAccessException {
        session.setRepeatingRequest(mPreviewBuilder.build(), null, mHandler);
    }

    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            try {
                mCameraDevice = camera;
                startPreview(camera);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onDisconnected(CameraDevice camera) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        @Override
        public void onError(CameraDevice camera, int error) {
        }
    };

    /**
     * ImageReader call back
     */
    private ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image img = reader.acquireLatestImage();
            if (img == null) {
                return;
            }

            imageProcess(img);
            img.close();
        }

        private void imageProcess(Image image) {
            Image.Plane[] planes = image.getPlanes();

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

        }
    };
}
