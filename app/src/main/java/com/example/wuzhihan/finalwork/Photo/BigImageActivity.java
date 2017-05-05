package com.example.wuzhihan.finalwork.Photo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.example.wuzhihan.finalwork.R;
import com.example.wuzhihan.finalwork.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by Wuzhihan on 2017/4/30.
 */

public class BigImageActivity extends Activity{
    private static final String TAG = "ImageActivity";

    private PhotoView mPhotoView;

    private String mIconUri;

    private DisplayImageOptions mOptions;

    private int mWidth;

    private int mHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null || intent.getExtras() == null) {
//            HcAppState.getInstance().removeActivity(this);
            finish();
            return;
        }
        mIconUri = intent.getStringExtra("uri");
        if (TextUtils.isEmpty(mIconUri)) {
//            HcAppState.getInstance().removeActivity(this);
            finish();
            return;
        }
        mWidth = (int) (360 * Util.getScreenDensity());
        mHeight = (int) (640 * Util.getScreenDensity());

        mWidth = intent.getIntExtra("width", mWidth);
        mHeight = intent.getIntExtra("height", mHeight);

        setContentView(R.layout.activity_big_image);
        mPhotoView = (PhotoView) findViewById(R.id.image_photo);
        mOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(false).cacheOnDisk(false)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888).build();
        ImageLoader.getInstance().loadImage(mIconUri, new ImageSize(mWidth, mHeight), mOptions, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                // TODO Auto-generated method stub
                Log.d(TAG, "#onLoadingStarted imageUri = "+imageUri);

            }

            @Override
            public void onLoadingFailed(String imageUri, View view,
                                        FailReason failReason) {
                // TODO Auto-generated method stub
                Log.d(TAG, "#onLoadingFailed imageUri = " + imageUri + " failReason = " + failReason);
//                HcAppState.getInstance().removeActivity(BigImageActivity.this);
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                // TODO Auto-generated method stub
                Log.d(TAG, "#onLoadingComplete imageUri = "+imageUri + " loadedImage = "+loadedImage + " width = "+loadedImage.getWidth() + " height = "+loadedImage.getHeight());
                if (mPhotoView != null)
                    mPhotoView.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                // TODO Auto-generated method stub
                Log.d(TAG, "#onLoadingCancelled imageUri = "+imageUri);
//                HcAppState.getInstance().removeActivity(BigImageActivity.this);
                finish();
                overridePendingTransition(0, 0);
            }
        });

        mPhotoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View view, float x, float y) {
                // TODO Auto-generated method stub
//                HcAppState.getInstance().removeActivity(BigImageActivity.this);
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
//                    HcAppState.getInstance().removeActivity(this);
                    finish();
                    overridePendingTransition(0, 0);
                }
                break;

            default:
                break;
        }
        return true;
    }
}
