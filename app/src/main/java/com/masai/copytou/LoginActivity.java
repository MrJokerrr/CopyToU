package com.masai.copytou;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.masai.copytou.utils.VersionUtils;

/**
 * Created by masai on 2018/11/1.
 */

public class LoginActivity extends AppCompatActivity {

    private EditText etPwd;
    private Button btnLogin;
    private TextView tvVersion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initView();

    }

    private void initView() {

        tvVersion = (TextView) findViewById(R.id.tv_login_version);
        tvVersion.setText("当前版本:" + VersionUtils.getLocalVersionName(this));
        etPwd = (EditText) findViewById(R.id.et_login_pwd);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginIn();
            }
        });

        etPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                loginIn();
                return false;
            }
        });

    }

    private void loginIn() {
        String pwd = etPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            showToastMsg("请输入密码");
            return;
        }

        if ("99999999".equals(pwd)) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            showToastMsg("密码不正确,请重新输入!");
            etPwd.setText("");
            return;
        }
    }

    private void showToastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
