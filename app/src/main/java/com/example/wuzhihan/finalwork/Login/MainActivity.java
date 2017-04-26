package com.example.wuzhihan.finalwork.Login;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wuzhihan.finalwork.R;
import com.example.wuzhihan.finalwork.Signin.sign.AttendancePage;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class MainActivity extends Activity implements View.OnClickListener {

    private EditText id_editText;
    private EditText password_editText;
    private TextView register_textView;
    private Button login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bmob.initialize(this,"183cfbe73f48d56166828fe44fa557d2");

        register_textView = (TextView)findViewById(R.id.register_user_login);
        register_textView.setOnClickListener(this);
        login_button = (Button)findViewById(R.id.login_btn);
        login_button.setOnClickListener(this);

        id_editText = (EditText)findViewById(R.id.login_user);
        password_editText = (EditText)findViewById(R.id.login_pw);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.login_btn) {
            final Dialog dialog = ProgressDialog.show(MainActivity.this,null,"登录中...",true,false);
            final String userId = id_editText.getText().toString();
            String password = password_editText.getText().toString();
            if (userId.isEmpty()){Toast.makeText(getApplicationContext(), "账号不能为空", Toast.LENGTH_SHORT).show();}
            if (password.isEmpty()){Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();}
            if (!userId.isEmpty()&&!password.isEmpty()){
                BmobUser bmobUser = new BmobUser();
                bmobUser.setUsername(userId);
                bmobUser.setPassword(password);
                bmobUser.login(new SaveListener<BmobUser>() {
                    @Override
                    public void done(BmobUser bmobUser, BmobException e) {
                        if (e==null){

                            Toast.makeText(MainActivity.this,"登陆成功", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(MainActivity.this, AttendancePage.class);
                            intent.putExtra("username",userId);
                            startActivity(intent);
                            dialog.dismiss();
                            finish();
                        }else {
                            Toast.makeText(MainActivity.this,"账号或密码错误", Toast.LENGTH_SHORT).show();
                            Log.i("bomb","失败:"+e.getMessage()+","+e.getErrorCode());
                        }
                    }

                });

            }else{
                Toast.makeText(MainActivity.this,"账号或密码错误", Toast.LENGTH_SHORT).show();
            }
        }
        if (id == R.id.register_user_login){
            Intent intent = new Intent(MainActivity.this,Register.class);
            startActivity(intent);
        }

    }
}
