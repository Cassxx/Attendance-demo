package com.example.wuzhihan.finalwork.Signin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.example.wuzhihan.finalwork.Photo.BigImageActivity;
import com.example.wuzhihan.finalwork.R;
import com.example.wuzhihan.finalwork.TopBarView;
import com.example.wuzhihan.finalwork.Util;
import com.example.wuzhihan.finalwork.WzhApplication;
import com.example.wuzhihan.finalwork.model.PhotoUrl;
import com.example.wuzhihan.finalwork.model.SignIn;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Wuzhihan on 2017/4/28.
 */

public class RemoteSignActivity extends Activity implements View.OnClickListener,View.OnLongClickListener, LocationSource, AMapLocationListener {

    private static final String TAG = "RemoteActivity";

    private TextView mAddr;

    private TextView mSwitch;

    private EditText mDescription;

    private LinearLayout mParent;

    private FrameLayout mImageParent1;
    private ImageView mImage1;
    private ImageView mDelete1;

    private FrameLayout mImageParent2;
    private ImageView mImage2;
    private ImageView mDelete2;

    private FrameLayout mImageParent3;
    private ImageView mImage3;
    private ImageView mDelete3;

    private FrameLayout mImageParent4;
    private ImageView mImage4;
    private ImageView mDelete4;

    private ImageView mAddBtn;

    private TextView mSubmit;

    private DisplayImageOptions mOptions;

    private int mWidth;

    private OnLocationChangedListener mListener;
    public AMapLocationClient locationClient;
    public AMapLocationClientOption locationOption;

    /**
     * 未被添加的图片的layout
     */
    private List<FrameLayout> mLayouts = new ArrayList<>();
    /**
     * 图片uri列表
     */
    private Map<FrameLayout, String> mUris = new HashMap<FrameLayout, String>();
    /**
     * 选定位置的经纬坐标
     */
    private String mLatitude;
    private String mLongitude;

    /**
     * 根据选定位置的经纬坐标定位
     */
    private MapView mapView;
    private AMap aMap;
    /**
     * Mark标记
     */
    private Marker marker;
    private LatLng markPlace;
    /**
     * 自定义头部
     */
    private TopBarView mTopBarView;


    private String mUsername;
    private String ytime;
    private String ynm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote_sign_layout);

        initView(savedInstanceState);
        initData();
//        initLocation();
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(RemoteSignActivity.this));
    }

    private void initView(Bundle savedInstanceState){
        mTopBarView = (TopBarView)findViewById(R.id.center_top_bar);
        mTopBarView.setTitle("外勤签到");

        mAddr = (TextView) findViewById(R.id.field_sign_addr);
//        mSwitch = (TextView) findViewById(R.id.field_sign_switch_addr);

        mDescription = (EditText) findViewById(R.id.field_sign_description);

        mParent = (LinearLayout) findViewById(R.id.field_sign_images_parent);

        mImageParent1 = (FrameLayout) findViewById(R.id.field_sign_image_parent1);
        mImage1 = (ImageView) findViewById(R.id.field_sign_image1);
        mDelete1 = (ImageView) findViewById(R.id.field_sign_delete_image1);

        mImageParent2 = (FrameLayout) findViewById(R.id.field_sign_image_parent2);
        mImage2 = (ImageView) findViewById(R.id.field_sign_image2);
        mDelete2 = (ImageView) findViewById(R.id.field_sign_delete_image2);

        mImageParent3 = (FrameLayout) findViewById(R.id.field_sign_image_parent3);
        mImage3 = (ImageView) findViewById(R.id.field_sign_image3);
        mDelete3 = (ImageView) findViewById(R.id.field_sign_delete_image3);

        mImageParent4 = (FrameLayout) findViewById(R.id.field_sign_image_parent4);
        mImage4 = (ImageView) findViewById(R.id.field_sign_image4);
        mDelete4 = (ImageView) findViewById(R.id.field_sign_delete_image4);

        mAddBtn = (ImageView) findViewById(R.id.field_sign_add_btn);

        mSubmit = (TextView) findViewById(R.id.field_sign_submit_btn);

//        mSwitch.setOnClickListener(this);
        mAddBtn.setOnClickListener(this);
        mSubmit.setOnClickListener(this);

        mImage1.setOnClickListener(this);
        mImage2.setOnClickListener(this);
        mImage3.setOnClickListener(this);
        mImage4.setOnClickListener(this);

        mDelete1.setOnClickListener(this);
        mDelete2.setOnClickListener(this);
        mDelete3.setOnClickListener(this);
        mDelete4.setOnClickListener(this);

        mImage1.setOnLongClickListener(this);
        mImage2.setOnLongClickListener(this);
        mImage3.setOnLongClickListener(this);
        mImage4.setOnLongClickListener(this);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();
        aMap.setLocationSource(this);//设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);
//        aMap.setMyLocationStyle(AMap.MAP_TYPE_NORMAL);
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);//普通地图模式
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));


    }

    private void initData(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mUsername = bundle.getString("username");
        mTopBarView.setTitle(mUsername);

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        ytime = simpleDateFormat.format(date);
        ynm = simpleDateFormat2.format(date);

        mLayouts.add(mImageParent1);
        mLayouts.add(mImageParent2);
        mLayouts.add(mImageParent3);
        mLayouts.add(mImageParent4);
        mParent.removeViews(0, 4);



    }


    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationClient.stopLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        locationClient.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.field_sign_add_btn) {
            startCamera();
        } else if (id == R.id.field_sign_submit_btn) {
            if (mUris.size()!=0) {
                for (String value : mUris.values()){
                    String picPath=value.substring(7,value.length());
                    System.out.println(picPath);
                    final ProgressDialog pd = new ProgressDialog(RemoteSignActivity.this);
                    final BmobFile bmobFile = new BmobFile(new File(picPath));
                    bmobFile.uploadblock(new UploadFileListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e==null){
                                //bmobFile.getFileUrl()--返回的上传文件的完整地址
//                                Toast.makeText(RemoteSignActivity.this,"上传文件成功" ,Toast.LENGTH_SHORT).show();
                                System.out.println(bmobFile.getFilename());
                                System.out.println(bmobFile.getFileUrl());
                                Date now = new Date(System.currentTimeMillis());
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                String nowTime = simpleDateFormat.format(now);
                                PhotoUrl photoUrl = new PhotoUrl();
                                photoUrl.setURL(bmobFile.getFileUrl());
                                photoUrl.setTime(nowTime);
                                photoUrl.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if (e==null){

                                        }
                                    }
                                });
                                pd.dismiss();
                            }else{
                                Toast.makeText(RemoteSignActivity.this,"上传文件失败：" + e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                SignIn signIn = new SignIn();
                signIn.setUsername(mUsername);
                signIn.setInTime(ytime);
                signIn.setYearAndMonth(ynm);
                signIn.setSignState(3);
                signIn.setRemarks(mDescription.getText().toString());
                signIn.setLatitude(mLatitude);
                signIn.setLongitude(mLongitude);
                signIn.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e==null){
                            Toast.makeText(RemoteSignActivity.this, "外勤签到成功!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "请拍摄照片",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.field_sign_delete_image1) {
            Log.d(TAG, " #onClick child size = " + mParent.getChildCount());
            mUris.remove(mImageParent1);
            mLayouts.add(mImageParent1);
            mDelete1.setVisibility(View.GONE);
            mParent.removeView(mImageParent1);
            Bitmap src = ((BitmapDrawable) mImage1.getDrawable()).getBitmap();
            mImage1.setImageBitmap(null);
            if (src != null) {
                src.recycle();
                src = null;
            }

            mAddBtn.setVisibility(View.VISIBLE);
        } else if (id == R.id.field_sign_delete_image2) {
            mUris.remove(mImageParent2);
            Log.d(TAG, " #onClick child size = " + mParent.getChildCount());
            mLayouts.add(mImageParent2);
            mDelete2.setVisibility(View.GONE);
            mParent.removeView(mImageParent2);
            Bitmap src = ((BitmapDrawable) mImage2.getDrawable()).getBitmap();
            mImage2.setImageBitmap(null);
            if (src != null) {
                src.recycle();
                src = null;
            }
            mAddBtn.setVisibility(View.VISIBLE);
        } else if (id == R.id.field_sign_delete_image3) {
            mUris.remove(mImageParent3);
            Log.d(TAG, " #onClick child size = " + mParent.getChildCount());
            mLayouts.add(mImageParent3);
            mDelete3.setVisibility(View.GONE);
            mParent.removeView(mImageParent3);
            Bitmap src = ((BitmapDrawable) mImage3.getDrawable()).getBitmap();
            mImage3.setImageBitmap(null);
            if (src != null) {
                src.recycle();
                src = null;
            }
            mAddBtn.setVisibility(View.VISIBLE);
        } else if (id == R.id.field_sign_delete_image4) {
            mUris.remove(mImageParent4);
            mLayouts.add(mImageParent4);
            mParent.removeView(mImageParent4);
            mDelete4.setVisibility(View.GONE);
            Bitmap src = ((BitmapDrawable) mImage4.getDrawable()).getBitmap();
            mImage4.setImageBitmap(null);
            if (src != null) {
                src.recycle();
                src = null;
            }
            mAddBtn.setVisibility(View.VISIBLE);
        } else if (id == R.id.field_sign_image1) {
            if (mDelete1.getVisibility() == View.VISIBLE) {
                mDelete1.setVisibility(View.GONE);
            } else {
                startBigImageActivity(mUris.get(mImageParent1));
            }
        } else if (id == R.id.field_sign_image2) {
            if (mDelete2.getVisibility() == View.VISIBLE) {
                mDelete2.setVisibility(View.GONE);
            } else {
                startBigImageActivity(mUris.get(mImageParent2));
            }
        } else if (id == R.id.field_sign_image3) {
            if (mDelete3.getVisibility() == View.VISIBLE) {
                mDelete3.setVisibility(View.GONE);
            } else {
                startBigImageActivity(mUris.get(mImageParent3));
            }
        } else if (id == R.id.field_sign_image4) {
            if (mDelete4.getVisibility() == View.VISIBLE) {
                mDelete4.setVisibility(View.GONE);
            } else {
                startBigImageActivity(mUris.get(mImageParent4));
            }
        }
    }

    private void startBigImageActivity(String uri) {
        Intent intent = new Intent(this, BigImageActivity.class);
        intent.putExtra("uri", uri);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onLongClick(View v) {
        int id = v.getId();
        if (id == R.id.field_sign_image1) {
            if (mDelete1.getVisibility() != View.VISIBLE) {
                mDelete1.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.field_sign_image2) {
            if (mDelete2.getVisibility() != View.VISIBLE) {
                mDelete2.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.field_sign_image3) {
            if (mDelete3.getVisibility() != View.VISIBLE) {
                mDelete3.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.field_sign_image4) {
            if (mDelete4.getVisibility() != View.VISIBLE) {
                mDelete4.setVisibility(View.VISIBLE);
            }
        }
        return true;
    }

    private String mImagePath = null;

    private void startCamera() {

        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        String directory = WzhApplication.getImagePhotoPath();
        String filename = System.currentTimeMillis() + ".jpg";
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();
        File file = new File(getExternalCacheDir(), filename);
//        imageuri = Uri.fromFile(file);
        mImagePath = file.getAbsolutePath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, Util.REQUEST_CODE_FROM_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        Log.d(TAG , " onActivityResult requestCode=" + requestCode + " resultCode=" + resultCode + " data = " + data);
//		if (data == null) return;
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case Util.REQUEST_CODE_FROM_CAMERA:
                        if (data == null) {
                            final Uri uri = Uri.fromFile(new File(mImagePath));
                            Log.d(TAG , " #onActivityResult uri = " + uri);
                            ImageLoader.getInstance().loadImage(uri.toString(), new ImageSize(mWidth, mWidth), mOptions, new ImageLoadingListener() {

                                @Override
                                public void onLoadingStarted(String imageUri, View view) {
                                    // TODO Auto-generated method stub
                                    Log.d(TAG , "#onLoadingStarted imageUri = " + imageUri);
                                }

                                @Override
                                public void onLoadingFailed(String imageUri, View view,
                                                            FailReason failReason) {
                                    // TODO Auto-generated method stub
                                    Log.d(TAG , "#onLoadingFailed imageUri = " + imageUri + " failReason = " + failReason);
                                }

                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    // TODO Auto-generated method stub
                                    Log.d(TAG , "#onLoadingComplete imageUri = " + imageUri + " loadedImage = " + loadedImage + " width = " + loadedImage.getWidth() + " height = " + loadedImage.getHeight());
                                    FrameLayout layout = mLayouts.remove(0);
                                    mParent.addView(layout, mParent.getChildCount() - 1);
                                    layout.setVisibility(View.VISIBLE);
                                    if (layout == mImageParent1) {
                                        mImage1.setImageBitmap(loadedImage);
                                    } else if (layout == mImageParent2) {
                                        mImage2.setImageBitmap(loadedImage);
                                    } else if (layout == mImageParent3) {
                                        mImage3.setImageBitmap(loadedImage);
                                    } else if (layout == mImageParent4) {
                                        mImage4.setImageBitmap(loadedImage);
                                    }
                                    mUris.put(layout, uri.toString());
                                    if (mUris.size() == 4) {
                                        mAddBtn.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onLoadingCancelled(String imageUri, View view) {
                                    // TODO Auto-generated method stub
                                    Log.d(TAG , "#onLoadingCancelled imageUri = " + imageUri);
                                }
                            });
//                            startCrop(Uri.fromFile(new File(mImagePath)), null);
                        } else if (data.getData() != null) {
//                            startCrop(data.getData(), null);
                        } else if (data.getExtras() != null) {
//                            startCrop(null, (Bitmap) data.getExtras().get("data"));
                        }
                        if (data != null) {
                            Log.d(TAG , " onActivityResult uri = " + data.getData() + " extras = " + data.getExtras());
                        }

                        break;
                    default:
                        break;
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (locationClient == null){
            //初始化定位
            locationClient = new AMapLocationClient(getApplicationContext());
            //初始化AMapLocationClientOption对象
            locationOption = new AMapLocationClientOption();
            //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            locationOption.setNeedAddress(true);
            locationOption.setOnceLocation(true);
            locationOption.setWifiActiveScan(true);
            locationOption.setMockEnable(false);
            locationOption.setInterval(2000);
            locationClient.setLocationOption(locationOption);
            locationClient.setLocationListener(this);
            locationClient.startLocation();

        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (locationClient!=null){
            locationClient.stopLocation();
            locationClient.onDestroy();
        }
        locationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation!=null){
            if (aMapLocation.getErrorCode()==0){
                mListener.onLocationChanged(aMapLocation);
                StringBuilder sb = new StringBuilder();
                //定位成功回调信息,设置相关消息
                int type = aMapLocation.getLocationType();
                String address = aMapLocation.getAddress();
                sb.append(type+address);
                marker = aMap.addMarker(new MarkerOptions()
                        .position(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.poi_marker_pressed)))
                        .draggable(false));
                mLatitude=String.valueOf(aMapLocation.getLatitude());
                mLongitude=String.valueOf(aMapLocation.getLongitude());
                mAddr.setText(sb);
//                Toast.makeText(RemoteSignActivity.this,sb.toString(),Toast.LENGTH_SHORT).show();
            }else {
                Log.i("error info:",aMapLocation.getErrorCode()+"----"+aMapLocation.getErrorInfo());
            }
        }
    }
}
