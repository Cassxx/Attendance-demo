package com.example.wuzhihan.finalwork.Signin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.example.wuzhihan.finalwork.R;
import com.example.wuzhihan.finalwork.TopBarView;
import com.example.wuzhihan.finalwork.model.PhotoUrl;
import com.example.wuzhihan.finalwork.model.SignIn;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Wuzhihan on 2017/5/2.
 */

public class SignListByDayActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "SignListByDayActivity";

//    private SignListByDayResponse mResponse;
    private MapView mapView;
    private AMap aMap;
    private LinearLayout mSignListByDay;
    private LinearLayout mNotSignListByDay;
    private UiSettings mUiSettings;
    private ImageView imgLeftBtn;
    private ImageView imgRightBtn;
    private Calendar calendar;
    private TextView dateText;
    private TextView remarks;
    private List<String> PhotoUrlList = new ArrayList<>();

    private ImageView mImage1;
    private ImageView mImage2;
    private ImageView mImage3;
    private ImageView mImage4;
    /**
     * Mark标记
     */
    private Marker marker;
    /**
     * 自定义头部
     */
    private TopBarView mTopBarView;

    private String mUsername;
    private String signDay;

    Bitmap bitmap;
    Bitmap bitmap2;
    Bitmap bitmap3;
    Bitmap bitmap4;


    android.os.Handler handler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==0x9527){
                mImage1.setImageBitmap(bitmap);
                mImage2.setImageBitmap(bitmap2);
                mImage3.setImageBitmap(bitmap3);
                mImage4.setImageBitmap(bitmap4);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_list_by_day);
        initView(savedInstanceState);
        initData();
        searchState();
    }

    private void initData(){
        Intent intent = getIntent();
        signDay = intent.getStringExtra("inTime");
        mUsername = intent.getStringExtra("username");
        mTopBarView.setTitle("单日详情");
        String[] signDays = signDay.split("-");
        dateText.setText(signDays[0] + "年" + signDays[1] + "月" + signDays[2] + "日");
//        isEmptyMap();

    }

    private void initView(Bundle savedInstanceState){
        mTopBarView = (TopBarView)findViewById(R.id.center_top_bar);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();
        mUiSettings = aMap.getUiSettings();//实例化UiSettings类
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 普通地图模式
        mapView.setVisibility(View.GONE);
//        aMap.setOnMarkerClickListener(this);
        mUiSettings.setZoomControlsEnabled(false);
        remarks = (TextView)findViewById(R.id.field_sign_description);
        dateText = (TextView)findViewById(R.id.map_tun_tv);
        calendar = Calendar.getInstance();

        mImage1 = (ImageView) findViewById(R.id.field_sign_image1);
        mImage2 = (ImageView) findViewById(R.id.field_sign_image2);
        mImage3 = (ImageView) findViewById(R.id.field_sign_image3);
        mImage4 = (ImageView) findViewById(R.id.field_sign_image4);
    }

    public void searchState(){
        BmobQuery query = new BmobQuery("SignIn");
        query.addWhereEqualTo("username",mUsername);
        query.addWhereEqualTo("inTime",signDay);
        query.findObjects(new FindListener<SignIn>() {
            @Override
            public void done(List<SignIn> list, BmobException e) {
                if (e==null){
                    if (list.get(0).getSignState()==3){
                        mapView.setVisibility(View.VISIBLE);
                        double getLat = Double.valueOf(list.get(0).getLatitude());
                        double getLng = Double.valueOf(list.get(0).getLongitude());
                        LatLng markPlace = new LatLng(getLat,getLng);
                        changeCamera(
                                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                        markPlace,16,0,30
                                )),null
                        );
                        //以下这个是标记上面这个经纬度在地图的位置是
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(getLat,getLng));
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.poi_marker_pressed)));
                        markerOptions.draggable(false);
                        marker = aMap.addMarker(markerOptions);

                        remarks.setText(list.get(0).getRemarks());

                        BmobQuery query = new BmobQuery("Photo");
                        query.addWhereEqualTo("Time",signDay);
                        query.findObjects(new FindListener<PhotoUrl>() {
                            @Override
                            public void done(List<PhotoUrl> list, BmobException e) {
                                if (e==null){
                                    for (PhotoUrl url : list){
                                        String photourl = url.getURL();
                                        PhotoUrlList.add(photourl);
                                    }
                                    new Thread(){
                                        @Override
                                        public void run() {
                                            try {
                                                if (PhotoUrlList.size()==1) {
                                                    URL url = new URL(PhotoUrlList.get(0));
                                                    InputStream is =url.openStream();
                                                    bitmap = BitmapFactory.decodeStream(is);
                                                    handler.sendEmptyMessage(0x9527);
                                                    is.close();
                                                }else if (PhotoUrlList.size()==2){
                                                    URL url = new URL(PhotoUrlList.get(0));
                                                    URL url2 = new URL(PhotoUrlList.get(1));
                                                    InputStream is =url.openStream();
                                                    InputStream is2 =url2.openStream();
                                                    bitmap = BitmapFactory.decodeStream(is);
                                                    bitmap2 = BitmapFactory.decodeStream(is2);
                                                    handler.sendEmptyMessage(0x9527);
                                                    is.close();
                                                    is2.close();
                                                }else if (PhotoUrlList.size()==3){
                                                    URL url = new URL(PhotoUrlList.get(0));
                                                    URL url2 = new URL(PhotoUrlList.get(1));
                                                    URL url3 = new URL(PhotoUrlList.get(2));
                                                    InputStream is =url.openStream();
                                                    InputStream is2 =url2.openStream();
                                                    InputStream is3 =url3.openStream();
                                                    bitmap = BitmapFactory.decodeStream(is);
                                                    bitmap2 = BitmapFactory.decodeStream(is2);
                                                    bitmap3 = BitmapFactory.decodeStream(is3);
                                                    handler.sendEmptyMessage(0x9527);
                                                    is.close();
                                                    is2.close();
                                                    is3.close();
                                                }else if (PhotoUrlList.size()==4){
                                                    URL url = new URL(PhotoUrlList.get(0));
                                                    URL url2 = new URL(PhotoUrlList.get(1));
                                                    URL url3 = new URL(PhotoUrlList.get(2));
                                                    URL url4 = new URL(PhotoUrlList.get(3));
                                                    InputStream is =url.openStream();
                                                    InputStream is2 =url2.openStream();
                                                    InputStream is3 =url3.openStream();
                                                    InputStream is4 =url4.openStream();
                                                    bitmap = BitmapFactory.decodeStream(is);
                                                    bitmap2 = BitmapFactory.decodeStream(is2);
                                                    bitmap3 = BitmapFactory.decodeStream(is3);
                                                    bitmap4 = BitmapFactory.decodeStream(is4);
                                                    handler.sendEmptyMessage(0x9527);
                                                    is.close();
                                                    is2.close();
                                                    is3.close();
                                                    is4.close();
                                                }


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }.start();
                                }
                            }
                        });

                    }else if (list.get(0).getSignState()==1){
                        remarks.setText("正常签到");
                    }else if (list.get(0).getSignState()==2){
                        remarks.setText("迟到、早退");
                    }else{
                    }
                }
            }
        });
    }

    /**
     * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
     */
    private void changeCamera(CameraUpdate update, AMap.CancelableCallback callback) {
        //带动画的移动
        aMap.animateCamera(update, 1000, callback);
    }


    @Override
    public void onClick(View v) {
    }

}
