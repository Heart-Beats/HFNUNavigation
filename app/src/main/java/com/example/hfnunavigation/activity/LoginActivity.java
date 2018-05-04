package com.example.hfnunavigation.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.hfnunavigation.QQ.LoginListener;
import com.example.hfnunavigation.R;
import com.example.hfnunavigation.util.LogUtil;
import com.example.hfnunavigation.util.StringConstant;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private LoginListener loginListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_login);
        toolbar.setTitleMarginStart(100 * 3);  //默认px为单位，测试机dp=3px
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Button loginQQ = findViewById(R.id.login_qq);
        Button loginWechat = findViewById(R.id.login_wechat);
        loginQQ.setOnClickListener(this);
        loginWechat.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_qq:
                loginQQ();
                loginListener.setmLoginInterface(new LoginListener.LoginInterface() {
                    @Override
                    public void afterGetUserInfo() {
                        String nickName = loginListener.getNickName();
                        String profileURL = loginListener.getProfileURL();
                        Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                        intent.putExtra("login", true);
                        intent.putExtra("nickname", nickName);
                        intent.putExtra("figureurl", profileURL);
                        startActivity(intent);
                    }
                });
                break;
        }
    }

    //点击按钮之后QQ登录
    public void loginQQ() {
        //初始化，用APP ID 获取到一个Tencent实例
        Tencent mTencent = Tencent.createInstance(StringConstant.TENCENT_APP_ID, this);
        loginListener = new LoginListener(this, mTencent);
        if (!mTencent.isSessionValid()) {
            /*  实现QQ的登录，这个方法有三个参数，第一个参数是context上下文，
                 第二个参数SCOPE 是一个String类型的字符串，表示应用需要获得哪些API的权限，由“，”分隔。
                 例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
                  第三个参数，是一个事件监听器，IUiListener接口的实例，*/
            mTencent.login(this, "all", loginListener);
        }
    }

    //QQ登录后的回调，必须要有
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.d(TAG, "请求码：" + requestCode + ",结果码:" + requestCode);
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
