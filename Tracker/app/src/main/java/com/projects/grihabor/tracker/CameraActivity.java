package com.projects.grihabor.tracker;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.nio.ByteBuffer;

public class CameraActivity extends Activity {

    private static final String TAG = "CameraActivity";
    private Camera mCamera;
    private CameraPreview mPreview;
    private NetworkTask nt;

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            Log.d(TAG, "Failed to open the camera");
        }
        return c; // returns null if camera is unavailable
    }

    private Uri fileUri;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private Camera.PictureCallback mPicture;

    static class SendHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Create an instance of Camera
        mCamera = getCameraInstance();
        if(mCamera == null){
            finish();
        }
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);


        mPicture = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.d("DEBUG", "onPictureTaken");
                nt = new NetworkTask(new SendHandler());
                nt.execute(ByteBuffer.wrap(data));

            }
        };

        Button b = (Button) findViewById(R.id.button_capture);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG", "onClick - takePicture");
                mCamera.takePicture(null, null, mPicture);
            }
        });
    }



}
