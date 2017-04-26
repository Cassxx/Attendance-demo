package com.example.wuzhihan.finalwork.Login;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wuzhihan.finalwork.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        id_editText = (EditText)findViewById(R.id.mobile_register_one_et);
        pwd_editText = (EditText)findViewById(R.id.mobile_register_two_et);
        next_button = (Button)findViewById(R.id.register_next_btn);
        next_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        String userId = id_editText.getText().toString();
        String pwd = pwd_editText.getText().toString();
        Person person = new Person();
        person.setUsername(userId);
        person.setPassword(pwd);

        person.signUp(new SaveListener<Person>() {
            @Override
            public void done(Person person, BmobException e) {
                if (e == null) {
                    Toast.makeText(Register.this, "创建成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Register.this, "创建失败:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.i("bomb", "失败:" + e.getMessage() + "," + e.getErrorCode());
                }
            }

        });

    }
}
