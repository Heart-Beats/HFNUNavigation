package com.example.hfnunavigation.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.example.hfnunavigation.R;
import com.example.hfnunavigation.util.HttpUtil;
import com.example.hfnunavigation.util.LogUtil;
import com.example.hfnunavigation.util.StringConstant;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import okhttp3.internal.Util;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button loginQQ;
    private Button loginWechat;
    String nickName;
    String imageURL;
    Bitmap bitmap;
    boolean isServerSideLogin = false;
    private Tencent mTencent;

    private static final String TAG = "LoginActivity";

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
        loginQQ = findViewById(R.id.login_qq);
        loginWechat = findViewById(R.id.login_wechat);
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
/*                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra("login", true);
                intent.putExtra("nickname", nickName);
                intent.putExtra("profilePicture", bitmap);
                startActivity(intent);*/
                break;
        }
    }

    //点击按钮之后QQ登录
    public void loginQQ() {
        //初始化，用APP ID 获取到一个Tencent实例
        mTencent = Tencent.createInstance(StringConstant.TENCENT_APP_ID, this);
        if (!mTencent.isSessionValid()) {
            /*  实现QQ的登录，这个方法有三个参数，第一个参数是context上下文，
                 第二个参数SCOPE 是一个String类型的字符串，表示应用需要获得哪些API的权限，由“，”分隔。
                 例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
                  第三个参数，是一个事件监听器，IUiListener接口的实例，*/
            mTencent.login(this, "all", loginListener);
            isServerSideLogin = false;
        } else {
            if (isServerSideLogin) { // Server-Side 模式的登陆, 先退出，再进行SSO登陆
                mTencent.logout(this);
                mTencent.login(this, "all", loginListener);
                isServerSideLogin = false;
                return;
            }
            mTencent.logout(this);
        }
    }

    IUiListener loginListener = new BaseUiListener() {

        /*       登录成功返回的数据格式
        {
            "ret": 0,
                "openid": "19E8D43EB75ED256CAC70C02953F188A",
                "access_token": "65A5A933F116085E051F39CAD65084EF",
                "pay_token": "BA387A1679483A4C8585BE268C7C4128",
                "expires_in": 7776000,
                "pf": "desktop_m_qq-10000144-android-2002-",
                "pfkey": "2c7171fb052154f89e9d439e17e18c11",
                "msg": "",
                "login_cost": 442,
                "query_authority_cost": 322,
                "authority_cost": 0
        }*/

        //子类对数据进行处理
        @Override
        protected void doComplete(JSONObject values) {
            initOpenidAndToken(values);
            //获取QQ用户的各信息
            getUserInfo();
        }

        ////设置openid和token，否则获取不到用户的信息
        private void initOpenidAndToken(JSONObject jsonObject) {
            try {
                String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
                String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
                //用户QQ号码转化得到的ID（当pf=qplus时返回）。
                String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
                if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                        && !TextUtils.isEmpty(openId)) {
                    mTencent.setAccessToken(token, expires);
                    mTencent.setOpenId(openId);
                }
            } catch (Exception e) {
            }
        }

        private void getUserInfo() {

            //sdk给我们提供了一个类UserInfo，这个类中封装了QQ用户的一些信息，可以通过这个类拿到这些信息
            QQToken mQQToken = mTencent.getQQToken();
            UserInfo userInfo = new UserInfo(LoginActivity.this, mQQToken);
            userInfo.getUserInfo(new IUiListener() {
                //获取用户信息时返回数据格式
/*                {
                    "ret": 0,
                        "msg": "",
                        "is_lost": 0,
                        "nickname": "",
                        "gender": "",
                        "province": "",
                        "city": "",
                        "figureurl": "",
                        "figureurl_1": "",
                        "figureurl_2": "",
                        "figureurl_qq_1": "",
                        "figureurl_qq_2": "",
                        "is_yellow_vip": "0",
                        "vip": "0",
                        "yellow_vip_level": "0",
                        "level": "0",
                        "is_yellow_year_vip": "0"
                }*/
                        @Override
                        public void onComplete(final Object o) {
                            JSONObject userInfoJson = (JSONObject) o;
                            try {
                                nickName = userInfoJson.getString("nickname");//直接传递一个昵称的内容过去
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            //子线程 获取并传递头像图片，由Handler更新
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (((JSONObject) o).has("figureurl")) {
                                        try {
                                            String headUrl = ((JSONObject) o).getString("figureurl_qq_2");
                                            imageURL = headUrl;
                                            Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                                            intent.putExtra("login", true);
                                            intent.putExtra("nickname", nickName);
                                            intent.putExtra("figureurl", imageURL);
                                            startActivity(intent);
                                            HttpUtil.sendHttpRequest(headUrl, new Callback() {
                                                @Override
                                                public void onFailure(Call call, IOException e) {

                                                }

                                                @Override
                                                public void onResponse(Call call, Response response) {
                                                    InputStream inputStream = response.body().byteStream();
                                                    bitmap = BitmapFactory.decodeStream(inputStream);
                                                }
                                            });
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).start();
                            if (nickName != null && bitmap != null) {
                                LogUtil.d(TAG,"获取数据成功，昵称："+nickName+",头像:"+bitmap);
                            }
                        }

                        @Override
                        public void onError(UiError uiError) {
                            Log.e(TAG, "获取qq用户信息错误");
                            Toast.makeText(LoginActivity.this, "获取qq用户信息错误", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancel() {
                            Log.e(TAG, "获取qq用户信息取消");
                            Toast.makeText(LoginActivity.this, "获取qq用户信息取消", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    };


    //QQ登录后的回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.d(TAG, "请求码：" + requestCode + ",结果码:" + requestCode);
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //实现回调
    private abstract class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            if (null == response) {
                LogUtil.d(TAG, "返回为空, 登录失败");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                LogUtil.d(TAG, "返回为空,登录失败");
                return;
            }
            LogUtil.d(TAG, response.toString() + ":登录成功");
            doComplete((JSONObject) response);
        }

        //数据处理在子类中实现
        abstract void doComplete(JSONObject values);

        @Override
        public void onError(UiError e) {
            LogUtil.d(TAG, "登陆出错 " + e.errorDetail);
        }

        @Override
        public void onCancel() {
            LogUtil.d(TAG, "取消登录 ");
            if (isServerSideLogin) {
                isServerSideLogin = false;
            }
        }
    }

}
