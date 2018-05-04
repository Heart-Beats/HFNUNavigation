package com.example.hfnunavigation.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;
import com.bumptech.glide.Glide;
import com.example.hfnunavigation.QQ.LoginListener;
import com.example.hfnunavigation.MessageEvent;
import com.example.hfnunavigation.MyApplication;
import com.example.hfnunavigation.MyDialogFragment;
import com.example.hfnunavigation.NetworkChangeReciver;
import com.example.hfnunavigation.R;
import com.example.hfnunavigation.db.HistoricalTrack;
import com.example.hfnunavigation.map.MyBaiduMap;
import com.example.hfnunavigation.util.LogUtil;
import com.example.hfnunavigation.util.StringConstant;
import com.example.hfnunavigation.util.SystemUtil;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.Date;
import de.hdodenhof.circleimageview.CircleImageView;

public class MapActivity extends AppCompatActivity implements View.OnClickListener {

    private MyBaiduMap myBaiduMap = MyBaiduMap.getMyBaiduMap();
    public static final int GPS_REQUEST_CODE = 1;

    private FloatingActionButton floatingButton;    // 悬浮按钮
    private CardView inputLocationView;             // 整个输入地址视图
    private CircleImageView profilePicture;         // 头像框
    private TextView startingPlace;                 // 起始地标签
    private EditText inputStartingPlace;           // 起始地输入框
    private TextView myLocation;                    // 我的地址标签
    private ConstraintLayout selectDestination;    // 输入目的地以及导航按钮等整个视图
    private EditText inputDestination;             // 目的地输入框
    private DrawerLayout drawerLayout;            //滑动菜单
    private NavigationView navView;                //滑动菜单页面布局
    private CircleImageView navProfilePicture;    //滑动菜单中头布局头像
    private TextView userName;                     //滑动菜单中头像下方文本

    private boolean confirmExit = true;          //用来确认用户是否退出程序
    private Date lastClickBack;                   //上一次点击返回按钮的时间
    private Tencent mTencent;                      //Tencent API中必须有的实例
    private LoginListener loginListener;           //QQ登录时回调接口
    private boolean login;                         //登录状态标志位
    private String nickName;                        //用户昵称
    private String profileURL;                      //用户头像地址
    private String userID;                           //通过QQ号转换来的用户id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* 在使用SDK各组件之前初始化context信息，传入ApplicationContext
           注意该方法要在setContentView方法之前实现  */
        SDKInitializer.initialize(MyApplication.getContext());
        setContentView(R.layout.activity_main);
        MapView mapView = findViewById(R.id.bmapView);
        //获取地图控件引用
        myBaiduMap.setmMapView(mapView);
        myBaiduMap.setmBaiduMap(mapView.getMap());
        customViewInitialize();
        initStatus();
        requestLocation();
    }

    private void customViewInitialize() {
        floatingButton = findViewById(R.id.navigation_route);
        inputLocationView = findViewById(R.id.input_location_layout);
        inputStartingPlace = findViewById(R.id.input_starting_place);
        profilePicture = inputLocationView.findViewById(R.id.profile_picture);
        startingPlace = findViewById(R.id.starting_place);
        myLocation = findViewById(R.id.my_location);
        selectDestination = findViewById(R.id.select_destination);
        inputDestination = findViewById(R.id.input_destination);
        Button cancleButton = findViewById(R.id.button_cancle);  //取消按钮
        Button navigationButton = findViewById(R.id.button_navigation);  //导航按钮
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        Resources resource = getBaseContext().getResources();
        ColorStateList csl = resource.getColorStateList(R.color.navigation_menu_item_color);
        //设置导航菜单中item的状态
        navView.setItemTextColor(csl);
        //设置MenuItem默认选中项
        //navView.getMenu().getItem(0).setChecked(true);
        navProfilePicture = navView.getHeaderView(0).findViewById(R.id.nav_profile_picture);
        userName = navView.getHeaderView(0).findViewById(R.id.user_name);
        floatingButton.setOnClickListener(this);
        profilePicture.setOnClickListener(this);
        cancleButton.setOnClickListener(this);
        navigationButton.setOnClickListener(this);
        navProfilePicture.setOnClickListener(this);
    }

    private void initStatus() {
        mTencent = Tencent.createInstance(StringConstant.TENCENT_APP_ID, this);
        loginListener = new LoginListener(this, mTencent);
        //从SharedPreferences读出上次退出软件时登录状态
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        login = pref.getBoolean("isLogin", false);
        if (login) {
            String openId = pref.getString("openid", null);
            String accessToken = pref.getString("access_token", null);
            Long failureTime = pref.getLong("expires_in", 0); // 实际值需要通过上面介绍的方法来计算
            Long effectiveTime = (failureTime - System.currentTimeMillis()) / 1000;
            System.out.println("有效时间：" + effectiveTime + "s");
            if (effectiveTime <= 0) {
                Toast.makeText(this, "登录已过期，需重新登录", Toast.LENGTH_SHORT).show();
                if (!mTencent.isSessionValid()) {
                    mTencent.login(this, "all", loginListener);
                    //选取重新登录后的用户头像地址和昵称
                    loginListener.setmLoginInterface(new LoginListener.LoginInterface() {
                        @Override
                        public void afterGetUserInfo() {
                            //过期获取新的头像URL和昵称
                            nickName = loginListener.getNickName();
                            profileURL = loginListener.getProfileURL();
                            loginStatus();
                        }
                    });
                }
            } else {
                //必须设置openid和accessToken，否则获取不到QQ用户的信息
                mTencent.setOpenId(openId);
                mTencent.setAccessToken(accessToken, effectiveTime.toString());
                loginListener.setmLoginInterface(new LoginListener.LoginInterface() {
                    @Override
                    public void afterGetUserInfo() {
                        //没有过期获取头像URL和昵称
                        nickName = loginListener.getNickName();
                        profileURL = loginListener.getProfileURL();
                        System.out.println("昵称：" + nickName);
                        System.out.println("头像地址：" + profileURL);
                        loginStatus();
                    }
                });
            }
            //获取每次打开程序时最新登录的用户ID
            userID = pref.getString("openid", null);
        } else {
            logoutStatus();
        }
        System.out.println("用户Id：" + userID);
    }

    //登录状态头像及昵称设置
    private void loginStatus() {
        userName.setText(nickName);
        userName.setTextSize(20);
        Glide.with(this).load(profileURL).into(profilePicture);
        Glide.with(this).load(profileURL).into(navProfilePicture);
    }

    //登出状态头像及昵称设置
    private void logoutStatus() {
        userName.setText(R.string.click_login_text);
        userName.setTextSize(15);
        Glide.with(this).load(R.drawable.profile_picture).into(profilePicture);
        Glide.with(this).load(R.drawable.profile_picture).into(navProfilePicture);
    }

    private void requestLocation() {
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        NetworkChangeReciver networkChangeReciver = new NetworkChangeReciver();
        registerReceiver(networkChangeReciver, intentFilter);
        //动态注册广播监听网络变化
        myBaiduMap.myBaiduMapStart();
        EventBus.getDefault().register(this);
        setViewListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GPS_REQUEST_CODE:
                LogUtil.d("GPS_REQUEST_CODE:", GPS_REQUEST_CODE + "");
                myBaiduMap.getmLocationClient().start();
                break;
                //重新登录时回调需要使用
            case Constants.REQUEST_LOGIN:
            case Constants.REQUEST_APPBAR:
                Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventHandle(MessageEvent messageEvent) {
        switch (messageEvent.getMessage()) {
            case "悬浮按钮显示":
                floatingButton.show();
                myBaiduMap.setDisplayView(false);
                break;
            case "悬浮按钮隐藏":
                floatingButton.hide();
                myBaiduMap.setDisplayView(true);
                break;
            case "输入地点布局显示":
                inputLocationView.setVisibility(View.VISIBLE);
                break;
            case "输入地点布局隐藏":
                inputLocationView.setVisibility(View.GONE);
                defaultInputLocView();
                break;
            case "输入地点信息错误":
                inputStartingPlace.setText("");
                inputDestination.setText("");
                break;
            case "发起路径规划":
                System.out.println("发起路径规划");
                inputLocationView.setVisibility(View.GONE);
                drawerLayout.closeDrawers();
                break;
        }
    }

    private void changedInputLocView() {
        profilePicture.setVisibility(View.GONE);
        startingPlace.setVisibility(View.VISIBLE);
        inputStartingPlace.setHint("");
        if (TextUtils.isEmpty(inputStartingPlace.getText().toString())) {
            myLocation.setVisibility(View.VISIBLE);
        }
        selectDestination.setVisibility(View.VISIBLE);
    }

    private void defaultInputLocView() {
        profilePicture.setVisibility(View.VISIBLE);
        startingPlace.setVisibility(View.GONE);
        inputStartingPlace.setHint(R.string.search_point_location);
        inputStartingPlace.setText("");
        inputDestination.setText("");
        myLocation.setVisibility(View.GONE);
        selectDestination.setVisibility(View.GONE);
    }

    private void setViewListener() {
        inputStartingPlace.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    changedInputLocView();
                }
            }
        });

        inputStartingPlace.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            // 当起始地输入框不为空时我的位置标签不显示，否则显示
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(inputStartingPlace.getText().toString())) {
                    myLocation.setVisibility(View.GONE);
                } else {
                    myLocation.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = new Intent();
                switch (item.getItemId()) {
                    case R.id.history:
                        if (login) {
                            intent.setClass(MapActivity.this, HistoryActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MapActivity.this, "请登录后再查看历史记录", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.preferred_location:
                        if (login) {
                            intent.setClass(MapActivity.this, PreferredLocationActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MapActivity.this, "请登录后再查看偏好地点", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.about:
                        intent.setClass(MapActivity.this, AplicationAboutActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        //获取软键盘对象
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        switch (v.getId()) {
            case R.id.navigation_route:  //悬浮按钮点击事件
                myBaiduMap.getMyWidgetListener().floatingButtonClickListener();
                if (login) {
                    if (SystemUtil.checkNetworkIsOpen(this)) {
                        HistoricalTrack historicalTrack = new HistoricalTrack();
                        historicalTrack.setUserID(userID);
                        historicalTrack.setStartPlaceName("我的位置");
                        historicalTrack.setEndPlaceName(myBaiduMap.getEndPlaceName());
                        historicalTrack.setHistoryTime(new Date());
                        historicalTrack.save();
                    } else {
                        Toast.makeText(this, "获取路线失败，请打开网络后再试", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.button_cancle:     //取消按钮点击事件
                inputLocationView.setVisibility(View.GONE);
                defaultInputLocView();
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                break;
            case R.id.button_navigation:  //导航按钮点击事件
                myBaiduMap.setStartPlaceName(inputStartingPlace.getText().toString().trim());
                myBaiduMap.setEndPlaceName(inputDestination.getText().toString().trim());
                myBaiduMap.getMyWidgetListener().navigationButtonClickListener();
                if (myBaiduMap.checkStartAndEndPlace()) {
                    if (login) {
                        if (SystemUtil.checkNetworkIsOpen(this)) {
                            HistoricalTrack historicalTrack = new HistoricalTrack();
                            historicalTrack.setUserID(userID);
                            historicalTrack.setStartPlaceName(myBaiduMap.getStartPlaceName());
                            historicalTrack.setEndPlaceName(myBaiduMap.getEndPlaceName());
                            historicalTrack.setHistoryTime(new Date());
                            historicalTrack.save();
                        } else {
                            Toast.makeText(this, "获取路线失败，请打开网络后再试", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                //隐藏软键盘
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                break;
            case R.id.profile_picture:    //主界面头像点击事件
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.nav_profile_picture:   //滑动菜单头布局头像点击事件
                if (!login) {
                    Intent intent = new Intent(MapActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    MyDialogFragment myDialogFragment = MyDialogFragment.getInstance(new MyDialogFragment.MyDialogCallBack() {
                        @Override
                        public String setMyDialogTitle() {
                            return "是否确认退出登录？";
                        }

                        @Override
                        public void OnDialogOkClick() {
                            mTencent.logout(MapActivity.this);
                            login = false;
                            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MapActivity.this);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("openid", null);
                            editor.putString("access_token", null);
                            editor.putLong("expires_in", 0);
                            editor.putBoolean("isLogin", false);
                            editor.apply();
                            logoutStatus();
                            LogUtil.d("登出后状态：", "login:" + pref.getBoolean("isLogin", true));
                            Toast.makeText(MapActivity.this, "退出登录成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                    myDialogFragment.show(getSupportFragmentManager(), "logout");
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        myBaiduMap.start();
        super.onStart();
    }

    @Override
    protected void onResume() {
        myBaiduMap.resume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        // myBaiduMap.pause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        myBaiduMap.stop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        myBaiduMap.destroy();
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //确认是请求登录的活动后才执行下面操作
        if (intent.getBooleanExtra("login", false)) {
            setIntent(intent);
            nickName = intent.getStringExtra("nickname");
            profileURL = intent.getStringExtra("figureurl");
            if (nickName != null && profilePicture != null) {
                loginStatus();
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
                login = pref.getBoolean("isLogin", false);
                //获取每次登录后最新的用户ID
                userID = pref.getString("openid", null);
                LogUtil.d("登录后状态：", "login:" + login + ",昵称：" + nickName + ",头像：" + profileURL);
                System.out.println("用户Id：" + userID);
            } else {
                Toast.makeText(this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {   //连续按两次返回按钮才会退出程序
        confirmExit = !confirmExit;
        if (!confirmExit) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            lastClickBack = new Date(System.currentTimeMillis());
        }
        Date currentClickBack = new Date(System.currentTimeMillis());
        //两次点击返回按钮时间间隔
        long timeInterval = currentClickBack.getTime() - lastClickBack.getTime();
        if (timeInterval <= 2000) {  // 仅当两次点击时间之差不大于2秒时才会退出
            if (confirmExit) {
                finish();
            }
        } else {  //当时间两次点击时间之差大于2秒时再给出提示信息并改变状态
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            lastClickBack = new Date(System.currentTimeMillis());
            confirmExit = !confirmExit;    // 需改变状态下一次点击返回才会退出
        }
        //super.onBackPressed();
    }
}
