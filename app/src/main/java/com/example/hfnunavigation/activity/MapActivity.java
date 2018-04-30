package com.example.hfnunavigation.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.example.hfnunavigation.MessageEvent;
import com.example.hfnunavigation.MyApplication;
import com.example.hfnunavigation.NetworkChangeReciver;
import com.example.hfnunavigation.R;
import com.example.hfnunavigation.map.MyBaiduMap;
import com.example.hfnunavigation.util.HttpUtil;
import com.example.hfnunavigation.util.LogUtil;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MapActivity extends AppCompatActivity implements View.OnClickListener{

    private MyBaiduMap myBaiduMap = MyBaiduMap.getMyBaiduMap();
    public static final int GPSREQUESTCODE = 1;

    private static String[] permissionsList = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


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
    private CircleImageView navProfilePicture;
    private TextView userName;

    private boolean confirmExit = true;
    private Date lastClickBack;

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
        requestPermission();
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
        Resources resource=getBaseContext().getResources();
        ColorStateList csl=resource.getColorStateList(R.color.navigation_menu_item_color);
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

    private void requestPermission() {
        List<String> requestPermissionsList = new ArrayList<>();
        for (String permission : permissionsList) {
            if (ContextCompat.checkSelfPermission(MapActivity.this,
                    permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionsList.add(permission);
            }
        }
        if (!requestPermissionsList.isEmpty()) {
            String[] requestPermissions = requestPermissionsList.toArray(new String[requestPermissionsList.size()]);
            ActivityCompat.requestPermissions(MapActivity.this, requestPermissions, 1);
        } else {
            requestLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
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
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GPSREQUESTCODE:
                LogUtil.d("GPSREQUESTCODE:", GPSREQUESTCODE + "");
                myBaiduMap.getmLocationClient().start();
                break;
            default:
                break;
        }
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
                        intent.setClass(MapActivity.this, HistoryActivity.class);
                        startActivity(intent);
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
                //隐藏软键盘
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                break;
            case R.id.profile_picture:    //主界面头像点击事件
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.nav_profile_picture:   //滑动菜单头布局头像点击事件
                Intent intent = new Intent(MapActivity.this, LoginActivity.class);
                startActivity(intent);
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
        myBaiduMap.pause();
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
        if (intent.getBooleanExtra("login",false)) {
            setIntent(intent);
            String nickName = intent.getStringExtra("nickname");
            String imageurl = intent.getStringExtra("figureurl");
            if (nickName != null && profilePicture != null) {
                userName.setText(nickName);
                Glide.with(this).load(imageurl).into(profilePicture);
                Glide.with(this).load(imageurl).into(navProfilePicture);

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
