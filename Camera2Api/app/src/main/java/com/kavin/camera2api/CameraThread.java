package com.kavin.camera2api;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.Surface;
import android.os.HandlerThread;
import android.content.Context;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CameraThread extends HandlerThread {
    private Handler mThreadHandler;
    private Context mContext;
    private int mCameraIndex;
    private CameraManager mCameraManager;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCameraCaptureSession;
    private ImageReader mImageReader;
    private CaptureRequest.Builder mCaptureRequestBuilder;
    private AtomicBoolean mIsCameraOpened;
    private byte[] mCurrentImage;
    //Listener for imagereader
    private ImageReader.OnImageAvailableListener mReaderListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = null;
            try {
                image = reader.acquireLatestImage();
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (image != null) {
                    image.close();
                }
            }
        }
    };
    //Callbacks in camera open
    private CameraDevice.StateCallback mCameraOpenStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            startVideoCapture();
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            cameraDevice.close();
            mCameraDevice = null;
            Toast.makeText(mContext, "Failed to open camera", Toast.LENGTH_SHORT).show();
        }
    };

    //Calbacks in setup capture session
    private CameraCaptureSession.StateCallback mCreateCaptureSessionStateCallback = new CameraCaptureSession.StateCallback() {

        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
            mCameraCaptureSession = cameraCaptureSession;
            updatePreview();
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
            Toast.makeText(mContext, "Failed to start capture session.", Toast.LENGTH_SHORT).show();
        }
    };

    protected void updatePreview() {
        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            mCameraCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, mThreadHandler);
            mIsCameraOpened.set(true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startVideoCapture() {
        try {
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            mCaptureRequestBuilder.addTarget(mImageReader.getSurface());
            List<Surface> outputSurfaces = new ArrayList<Surface>(1);
            outputSurfaces.add(mImageReader.getSurface());
            mCameraDevice.createCaptureSession(outputSurfaces,
                    mCreateCaptureSessionStateCallback, mThreadHandler);
        } catch (CameraAccessException e) {

            Toast.makeText(mContext, "Failed to start capturing.", Toast.LENGTH_SHORT).show();
        }
    }

    public CameraThread(Context context) {
        super("CameraThread");
        mIsCameraOpened = new AtomicBoolean(false);
        mContext = context;
        //Thread start
        start();
        mThreadHandler = new Handler(getLooper());
        mCurrentImage = new byte[640 * 480 * 3];
        //Set image reader to read image
        mImageReader = ImageReader.newInstance(640, 480, ImageFormat.YUV_420_888, 1);
        mImageReader.setOnImageAvailableListener(mReaderListener, mThreadHandler);
        //Get CameraManager
        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
    }

    public void startCameraFromOwner(final int cameraIndex) {
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                stopCameraInternal();
            }
        });
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, "Camera permission not granted.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (cameraIndex < mCameraManager.getCameraIdList().length) {
                mCameraIndex = cameraIndex;
                mThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        startCameraInternal(cameraIndex);
                    }
                });
            }
        } catch (CameraAccessException e) {
            Toast.makeText(mContext, "Cannot access the camera.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCameraInternal(final int cameraIndex) {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, "Camera permission not granted.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String cameraId = mCameraManager.getCameraIdList()[cameraIndex];
            mCameraManager.openCamera(cameraId, mCameraOpenStateCallback, mThreadHandler);
        } catch (Exception e) {
            Toast.makeText(mContext, "Cannot access the camera.", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopCameraFromOwner() {
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                stopCameraInternal();
            }
        });
    }

    private void stopCameraInternal() {
        mIsCameraOpened.set(false);
        if (mCameraCaptureSession != null) {
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    public int getNumCamera() {
        if (mCameraManager == null) {
            return 0;
        } else {
            try {
                return mCameraManager.getCameraIdList().length;
            } catch (Exception e) {
                return 0;
            }
        }
    }

    public boolean isCameraOpened() {
        return mIsCameraOpened.get();
    }

}
