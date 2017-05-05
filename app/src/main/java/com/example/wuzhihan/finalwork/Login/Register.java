package com.example.wuzhihan.finalwork.Login;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wuzhihan.finalwork.R;
import com.example.wuzhihan.finalwork.TopBarView;
import com.example.wuzhihan.finalwork.model.Person;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Wuzhihan on 17/3/25.
 */

public class Register extends Activity implements View.OnClickListener {

    private EditText id_editText;
    private EditText pwd_editText;
    private Button next_button;

    private String imei;
    private String imsi;
    /**
     * 自定义头部
     */
    private TopBarView mTopBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        Bmob.initialize(this,"183cfbe73f48d56166828fe44fa557d2");

        mTopBarView = (TopBarView)findViewById(R.id.register_top_bar);
        mTopBarView.setTitle("用户注册");
        mTopBarView.setSettingsVisiable(View.GONE);

        id_editText = (EditText)findViewById(R.id.mobile_register_one_et);
        pwd_editText = (EditText)findViewById(R.id.mobile_register_two_et);
        next_button = (Button)findViewById(R.id.register_next_btn);
        next_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        String userId = id_editText.getText().toString();
        String pwd = pwd_editText.getText().toString();
        imei = getIMEI(this);
        imsi = getIMSI(this);
        Person person = new Person();
        person.setUsername(userId);
        person.setPassword(pwd);
        person.setImei(imei);
        person.setImsi(imsi);

        person.signUp(new SaveListener<Person>() {
            @Override
            public void done(Person person, BmobException e) {
                if (e == null) {
                    Toast.makeText(Register.this, "创建成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(Register.this, "创建失败:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.i("bomb", "失败:" + e.getMessage() + "," + e.getErrorCode());
                }
            }

        });

    }

    /**
     * 获取手机IMEI号
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();

        return imei;
    }

    /**
     * 获取手机IMSI号
     */
    public static String getIMSI(Context context){
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = mTelephonyMgr.getSubscriberId();

        return imsi ;
    }
}
