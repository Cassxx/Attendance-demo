package com.example.wuzhihan.finalwork.Signin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wuzhihan.finalwork.R;
import com.example.wuzhihan.finalwork.model.SignListByMonth;
import com.example.wuzhihan.finalwork.model.SignIn;
import com.example.wuzhihan.finalwork.model.SignOut;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.TimeZone;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Wuzhihan on 17/4/6.
 */

public class AttendancePageActivity extends Activity implements View.OnClickListener{

    private Button mButton_signIn;
    private Button mButton_signOut;
    private TextView mTv_signInfo;
    public static String IP;
    public static String MAC;

    List<SignListByMonth> isAlreadySignInList = new ArrayList<>();
    List<SignListByMonth> isAlreadySignOutList = new ArrayList<>();

    private TextView localTimeText;
    private TextView localDateText;
    private TextView localWeekText;

    //小时钟
    private static final String DATE_FORMAT = "%02d:%02d";
    private static final int REFRESH_DELAY = 500;

    private String mUsername;

    private String ytime;
    private String ynm;



    private final Handler handler = new Handler();

    private final Runnable mTimeRefresher = new Runnable() {

        @Override
        public void run() {
            final Date d = new Date();
            localTimeText.setText(String.format(DATE_FORMAT, d.getHours(),
                    d.getMinutes(), d.getSeconds()));
            Calendar c = Calendar.getInstance();
            c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            String mYear = String.valueOf(c.get(Calendar.YEAR));
            String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
            String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
            String mWeek = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
            if ("1".equals(mWeek)) {
                mWeek = "日";
            } else if ("2".equals(mWeek)) {
                mWeek = "一";
            } else if ("3".equals(mWeek)) {
                mWeek = "二";
            } else if ("4".equals(mWeek)) {
                mWeek = "三";
            } else if ("5".equals(mWeek)) {
                mWeek = "四";
            } else if ("6".equals(mWeek)) {
                mWeek = "五";
            } else if ("7".equals(mWeek)) {
                mWeek = "六";
            }
            localDateText.setText(mYear + "年" + mMonth + "月" + mDay + "日");
            localWeekText.setText("周" + mWeek);
            handler.postDelayed(this, REFRESH_DELAY);
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        handler.post(mTimeRefresher);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(mTimeRefresher);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        initData();
        initView();
    }

    public void initData(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mUsername = bundle.getString("username");

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        ytime = simpleDateFormat.format(date);
        ynm = simpleDateFormat2.format(date);
//        SearchToday();
        isAlreadySignInList.clear();
        isAlreadySignOutList.clear();
    }

    public void initView(){
        mButton_signIn = (Button)findViewById(R.id.go_work_img_btn);
        mButton_signOut = (Button)findViewById(R.id.off_work_img_btn);
        mTv_signInfo = (TextView)findViewById(R.id.sign_details);
        mButton_signIn.setOnClickListener(this);
        mButton_signOut.setOnClickListener(this);
        mTv_signInfo.setOnClickListener(this);

        localTimeText = (TextView)findViewById(R.id.time);
        localDateText = (TextView)findViewById(R.id.date);
        localWeekText = (TextView)findViewById(R.id.week);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.go_work_img_btn){
            AlertDialog.Builder builder = new AlertDialog.Builder(AttendancePageActivity.this);
            builder.setTitle("考勤方式").setMessage("选择一个考勤方式:");
            builder.setNegativeButton("正常",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (checkWifi(AttendancePageActivity.this)==1){
                                Toast.makeText(AttendancePageActivity.this,"您连接的是移动网络,签到失败!",Toast.LENGTH_SHORT).show();
                            }else if (checkWifi(AttendancePageActivity.this)==3){
                                Toast.makeText(AttendancePageActivity.this,"您没有连接网络,签到失败!",Toast.LENGTH_SHORT).show();
                            }else if (checkWifi(AttendancePageActivity.this)==2) {
                                IP = getLocalIpAddress();
                                MAC = getLocalMacAddress();
                                SearchTodayIn();
                            }
                        }
                    });
            builder.setNeutralButton("外勤",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (checkWifi(AttendancePageActivity.this)==3) {
                                Toast.makeText(AttendancePageActivity.this, "您没有连接网络,签出失败!", Toast.LENGTH_SHORT).show();
                            }else {
                                BmobQuery query = new BmobQuery("SignIn");
                                Date now = new Date(System.currentTimeMillis());
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                String nowTime = simpleDateFormat.format(now);
                                query.addWhereEqualTo("username", mUsername);
                                query.addWhereEqualTo("inTime", nowTime);
                                query.findObjects(new FindListener<SignIn>() {

                                    @Override
                                    public void done(List<SignIn> list, BmobException e) {
                                        if (e == null) {
                                            for (SignIn signIn : list) {
                                                SignListByMonth signListByMonth = new SignListByMonth();
                                                String checkDate = signIn.getInTime();
                                                signListByMonth.setCheckDate(checkDate);
                                                String signTime = signIn.getCreatedAt();
                                                signListByMonth.setSignTime(signTime);
                                                isAlreadySignInList.add(signListByMonth);
                                            }
                                            if (isAlreadySignInList.size() == 0) {
                                                Intent intent = new Intent(AttendancePageActivity.this, RemoteSignActivity.class);
                                                intent.putExtra("username", mUsername);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(AttendancePageActivity.this, "签到失败!今天已经签到过了", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });

                            }

                        }
                    });
            builder.setPositiveButton("请假",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (checkWifi(AttendancePageActivity.this)==3) {
                                Toast.makeText(AttendancePageActivity.this, "您没有连接网络,签出失败!", Toast.LENGTH_SHORT).show();
                            }else{
                                BmobQuery query = new BmobQuery("SignIn");
                                Date now = new Date(System.currentTimeMillis());
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                String nowTime = simpleDateFormat.format(now);
                                query.addWhereEqualTo("username",mUsername);
                                query.addWhereEqualTo("inTime",nowTime);
                                query.findObjects(new FindListener<SignIn>() {

                                    @Override
                                    public void done(List<SignIn> list, BmobException e) {
                                        if (e == null) {
                                            for (SignIn signIn : list) {
                                                SignListByMonth signListByMonth = new SignListByMonth();
                                                String checkDate = signIn.getInTime();
                                                signListByMonth.setCheckDate(checkDate);
                                                String signTime = signIn.getCreatedAt();
                                                signListByMonth.setSignTime(signTime);
                                                isAlreadySignInList.add(signListByMonth);
                                            }
                                            if (isAlreadySignInList.size()==0) {
                                                SignIn signIn = new SignIn();
                                                signIn.setUsername(mUsername);
                                                signIn.setInTime(ytime);
                                                signIn.setYearAndMonth(ynm);
                                                signIn.setSignState(5);
                                                signIn.save(new SaveListener<String>() {
                                                    @Override
                                                    public void done(String s, BmobException e) {
                                                        if (e==null){
                                                            Toast.makeText(AttendancePageActivity.this, "请假成功!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }else{
                                                Toast.makeText(AttendancePageActivity.this, "请假失败!今天已经签到过了", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });

                            }

                        }
                    });
            builder.show();
        }
        if (id == R.id.off_work_img_btn){
            if (checkWifi(AttendancePageActivity.this)==1){
                Toast.makeText(AttendancePageActivity.this,"您连接的是移动网络,签出失败!",Toast.LENGTH_SHORT).show();
            }else if (checkWifi(AttendancePageActivity.this)==3){
                Toast.makeText(AttendancePageActivity.this,"您没有连接网络,签出失败!",Toast.LENGTH_SHORT).show();
            }else if (checkWifi(AttendancePageActivity.this)==2) {
                IP = getLocalIpAddress();
                MAC = getLocalMacAddress();

                SearchTodayOut();

            }
        }
        if (id == R.id.sign_details){
//            Intent i = getIntent();
//            Bundle bundle = i.getExtras();
//            String username = bundle.getString("username");
            Intent intent = new Intent(AttendancePageActivity.this,MonthCalendarActivity.class);
            intent.putExtra("username",mUsername);
            startActivity(intent);
        }

    }

    //查找今天的签到
    public void SearchTodayIn(){
        BmobQuery query = new BmobQuery("SignIn");
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String nowTime = simpleDateFormat.format(now);
        query.addWhereEqualTo("username",mUsername);
        query.addWhereEqualTo("inTime",nowTime);
        query.findObjects(new FindListener<SignIn>() {

            @Override
            public void done(List<SignIn> list, BmobException e) {
                if (e == null) {
                    for (SignIn signIn : list) {
                        SignListByMonth signListByMonth = new SignListByMonth();
                        String checkDate = signIn.getInTime();
                        signListByMonth.setCheckDate(checkDate);
//                        String signTime = signIn.getCreatedAt();
//                        signListByMonth.setSignTime(signTime);
                        isAlreadySignInList.add(signListByMonth);
                    }
                    isLate(isAlreadySignInList);

                }
            }
        });
    }

    //是否迟到
    public void isLate(List<SignListByMonth> signListByMonths) {
        long InTIme = 90000L;
            if (signListByMonths.size() != 0) {
                Toast.makeText(AttendancePageActivity.this, "签到失败!今天已经签到过了", Toast.LENGTH_SHORT).show();
            }else{
//                BmobQuery query = new BmobQuery("SignIn");
                Date now = new Date(System.currentTimeMillis());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String nowTime = simpleDateFormat.format(now);

                String ss = nowTime.substring(nowTime.length() - 2,nowTime.length());
                String mm = nowTime.substring(nowTime.length() - 5,nowTime.length() - 3);
                String HH = nowTime.substring(nowTime.length() - 8,nowTime.length() - 6);
                long t = Long.valueOf(HH + mm + ss);
                if (t > InTIme) {
                    Late();
                } else {
                    noLate();
                }
            }
    }

    //迟到了
    public void Late(){
        SignIn signIn = new SignIn();
        signIn.setUsername(mUsername);
        signIn.setInTime(ytime);
        signIn.setYearAndMonth(ynm);
        signIn.setSignState(2);
        signIn.setIP(IP);
        signIn.setMAC(MAC);
        signIn.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    if (isAlreadySignInList.size()>0){
                        Toast.makeText(AttendancePageActivity.this, "签到失败!今天已经签到过了", Toast.LENGTH_SHORT).show();
                    }else {
                        new AlertDialog.Builder(AttendancePageActivity.this).setTitle("提示").setMessage("你已经迟到了!")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                            finish();
                                    }
                                }).show();
                        Toast.makeText(AttendancePageActivity.this, "签到成功!\n IP:" + IP + "\nMAC 地址:" + MAC + "\n时间:" + ytime, Toast.LENGTH_LONG).show();
//                            mButton_signIn.setClickable(false);
//                            mButton_signOut.setClickable(true);
                    }
                } else {
                    Toast.makeText(AttendancePageActivity.this, "签到失败!", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    //没迟到
    public void noLate(){
        SignIn signIn = new SignIn();
        signIn.setUsername(mUsername);
        signIn.setInTime(ytime);
        signIn.setYearAndMonth(ynm);
        signIn.setSignState(1);
        signIn.setIP(IP);
        signIn.setMAC(MAC);
        signIn.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    if (isAlreadySignInList.size()>0){
                        Toast.makeText(AttendancePageActivity.this, "签到失败!今天已经签到过了", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(AttendancePageActivity.this, "签到成功!\n IP:" + IP + "\nMAC 地址:" + MAC + "\n时间:" + ytime, Toast.LENGTH_LONG).show();
//                            mButton_signIn.setClickable(false);
//                            mButton_signOut.setClickable(true);
                    }
                } else {
                    Toast.makeText(AttendancePageActivity.this, "签到失败!", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    //查找今天的签出
    public void SearchTodayOut(){
        BmobQuery query = new BmobQuery("SignOut");
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String nowTime = simpleDateFormat.format(now);
        query.addWhereEqualTo("username",mUsername);
        query.addWhereEqualTo("outTime",nowTime);
        query.findObjects(new FindListener<SignOut>() {

            @Override
            public void done(List<SignOut> list, BmobException e) {
                if (e == null) {
                    for (SignOut signOut : list) {
                        SignListByMonth signListByMonth = new SignListByMonth();
                        String checkDate = signOut.getOutTime();
                        signListByMonth.setCheckDate(checkDate);
//                        String signTime = signin.getCreatedAt();
//                        signListByMonth.setSignTime(signTime);
                        isAlreadySignOutList.add(signListByMonth);
                    }
                    isEarly(isAlreadySignOutList);
                }
            }
        });
    }

    //是否早退
    public void isEarly(List<SignListByMonth> signListByMonths) {
        long OutTime = 180000L;
        if (signListByMonths.size() != 0) {
            Toast.makeText(AttendancePageActivity.this, "签出失败!今天已经签出过了", Toast.LENGTH_SHORT).show();
        }else{
            Date now = new Date(System.currentTimeMillis());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String nowTime = simpleDateFormat.format(now);

            String ss = nowTime.substring(nowTime.length() - 2,nowTime.length());
            String mm = nowTime.substring(nowTime.length() - 5,nowTime.length() - 3);
            String HH = nowTime.substring(nowTime.length() - 8,nowTime.length() - 6);
            long t = Long.valueOf(HH + mm + ss);
            if (t < OutTime) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示");
                builder.setMessage("还没到下班时间,确定要早退吗?");
                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Early();
                            }
                        });
                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();

            } else {
                noEarly();
            }
        }
    }

    //早退了
    public void Early(){
        SignOut signOut = new SignOut();
        signOut.setUsername(mUsername);
        signOut.setOutTime(ytime);
        signOut.setYearAndMonth(ynm);
        signOut.setSignState(2);
        signOut.setIP(IP);
        signOut.setMAC(MAC);
        signOut.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    if (isAlreadySignOutList.size()>0){
                        Toast.makeText(AttendancePageActivity.this, "签出失败!今天已经签出过了", Toast.LENGTH_SHORT).show();
                    }else {

                        Toast.makeText(AttendancePageActivity.this, "签出成功!\n IP:" + IP + "\nMAC 地址:" + MAC + "\n时间:" + ytime, Toast.LENGTH_LONG).show();
//                            mButton_signIn.setClickable(false);
//                            mButton_signOut.setClickable(true);
                    }
                } else {
                    Toast.makeText(AttendancePageActivity.this, "签出失败!", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    //没早退
    public void noEarly(){
        SignOut signOut = new SignOut();
        signOut.setUsername(mUsername);
        signOut.setOutTime(ytime);
        signOut.setYearAndMonth(ynm);
        signOut.setSignState(1);
        signOut.setIP(IP);
        signOut.setMAC(MAC);
        signOut.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    if (isAlreadySignOutList.size()>0){
                        Toast.makeText(AttendancePageActivity.this, "签出失败!今天已经签出过了", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(AttendancePageActivity.this, "签出成功!\n IP:" + IP + "\nMAC 地址:" + MAC + "\n时间:" + ytime, Toast.LENGTH_LONG).show();
//                            mButton_signIn.setClickable(false);
//                            mButton_signOut.setClickable(true);
                    }
                } else {
                    Toast.makeText(AttendancePageActivity.this, "签出失败!", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    //检查连接的是什么网络
    public Integer checkWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
            if (mNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return 1;//返回1,连接的是移动网络
            } else if (mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return 2;//返回2,连接的是WIFI
            }
        } else {
                return 3;//返回3,没有连接网络
            }
        return 3;
    }

    //获取IP
    public String getLocalIpAddress(){
        try{
            for (Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
                 enumeration.hasMoreElements();){
                NetworkInterface networkInterface = enumeration.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses();
                     enumIpAddr.hasMoreElements();){
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()){
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("IP地址为:",e.toString());
        }
        return null;
    }


    //获取MAC地址
    public String getLocalMacAddress(){
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        return info.getMacAddress();
    }


}
