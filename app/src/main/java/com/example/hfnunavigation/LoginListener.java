package com.example.hfnunavigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import com.example.hfnunavigation.activity.MapActivity;
import com.example.hfnunavigation.util.LogUtil;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginListener extends BaseUiListener {

    private static final String TAG = "LoginListener";

    private Tencent mTencent;
    private String nickName;
    private String profileURL;

    private LoginInterface mLoginInterface;

    public LoginListener(Context context, Tencent tencent) {
        super(context);
        mTencent = tencent;
    }

    public String getNickName() {
        return nickName;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setmLoginInterface(LoginInterface mLoginInterface) {
        if (context instanceof MapActivity) {
            //在设定回调接口之前先获取QQ用户的各信息
            getUserInfo();
        }
        this.mLoginInterface = mLoginInterface;
    }

    /*      登录成功返回的数据格式
       {
           "ret": 0,
           "openid": "19E8D43EB75ED256CAC70C02953F188A",  //用于唯一标识用户身份（每一个openid与QQ号码对应）
           "access_token": "65A5A933F116085E051F39CAD65084EF",//用户进行应用邀请、分享、支付等基本业务请求的凭据。
           "pay_token": "BA387A1679483A4C8585BE268C7C4128",
           "expires_in": 7776000,            // access_token的有效时间，在有效期内可以发起业务请求，过期失效。
           "pf": "desktop_m_qq-10000144-android-2002-",
           "pfkey": "2c7171fb052154f89e9d439e17e18c11",
           "msg": "",
           "login_cost": 442,
           "query_authority_cost": 322,
           "authority_cost": 0
       }*/

    //子类对登录成功后的数据进行处理
    @Override
    protected void doComplete(JSONObject values) {
        System.out.println("登录成功后处理数据");
        initOpenidAndToken(values);
        getUserInfo();
    }

    //必须设置openid和token，否则获取不到用户的信息
    private void initOpenidAndToken(JSONObject jsonObject) {
        try {
               /* 应用在每次登录之后，都会获取到openid、access_token和expires_in，在调用SDK提供的接口时，
                 后台会根据这三个参数来验证请求的合法性。
                 access_token的有效期为3个月，在有效期内，使用此token进行应用分享、邀请等操作都是可以正常完成的。
                 超过这个时间，服务器会认为token已失效，需要重新登录。*/
            //用户QQ号码转化得到的ID（当pf=qplus时返回）。
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            String accessToken = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expiresIn = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            mTencent.setOpenId(openId);
            mTencent.setAccessToken(accessToken, expiresIn);
            //失效时间
            Long failureTime = System.currentTimeMillis() + Long.parseLong(expiresIn) * 1000;
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("openid", openId);
            editor.putString("access_token", accessToken);
            editor.putLong("expires_in", failureTime);
            editor.putBoolean("isLogin", true);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   public  interface LoginInterface {

        void afterGetUserInfo();  //该方法用于确认可以获取本类中数据
    }


    private void getUserInfo() {

        //sdk给我们提供了一个类UserInfo，这个类中封装了QQ用户的一些信息，可以通过这个类拿到这些信息
        QQToken mQQToken = mTencent.getQQToken();
        UserInfo userInfo = new UserInfo(context, mQQToken);
        userInfo.getUserInfo(
                new IUiListener() {
                    //获取用户信息时返回数据格式
/*      {
            "ret": 0,      //返回码
            "msg": "",     //如果错误，返回错误信息。
            "is_lost": 0,  //判断是否有数据丢失。如果应用不使用cache，不需要关心此参数。
            "nickname": "",  //昵称。
            "gender": "",   //性别。
            "province": "",
            "city": "",
            "figureurl": "",  //头像URL
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
                    public void onComplete(Object userInfo) {
                        JSONObject userInfoJson = (JSONObject) userInfo;
                        try {
                            if (userInfoJson.has("nickname") && userInfoJson.has("figureurl")) {
                                nickName = userInfoJson.getString("nickname");//直接传递一个昵称的内容过去
                                profileURL = ((JSONObject) userInfo).getString("figureurl_qq_2");
                                LogUtil.d(TAG,"获取用户信息成功：昵称："+nickName+",头像地址："+profileURL);
                            } else {
                                LogUtil.d(TAG, "没有获取到qq用户信息");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mLoginInterface.afterGetUserInfo();
                    }

                    @Override
                    public void onError(UiError uiError) {
                        Log.e(TAG, "获取qq用户信息错误");
                        Toast.makeText(context, "获取qq用户信息错误", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Log.e(TAG, "获取qq用户信息取消");
                        Toast.makeText(context, "获取qq用户信息取消", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

}

