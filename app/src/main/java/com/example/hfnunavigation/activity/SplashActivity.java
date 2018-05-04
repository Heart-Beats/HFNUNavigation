package com.example.hfnunavigation.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.hfnunavigation.R;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGHT = 2330; // 延迟

    private static String[] permissionsList = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        requestPermission();
/*      1.  立即执行Runnable对象
        public final boolean post(Runnable r);
        2.  在指定的时间（uptimeMillis）执行Runnable对象
        public final boolean postAtTime(Runnable r, long uptimeMillis);
        3.  在指定的时间间隔（delayMillis）执行Runnable对象
        public final boolean postDelayed(Runnable r, long delayMillis);*/
        boolean firstInApplication = preferences.getBoolean("firstInApplication", false);
        if (!firstInApplication) {
            //当不是第一次打开应用延时后进入主活动
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    enterMainActivity();
                }
            }, SPLASH_DISPLAY_LENGHT);
        }
    }

    private void requestPermission() {
        List<String> requestPermissionsList = new ArrayList<>();
        for (String permission : permissionsList) {
            if (ContextCompat.checkSelfPermission(this,
                    permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionsList.add(permission);
            }
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        if (!requestPermissionsList.isEmpty()) {
            //当firstInApplication为true时，说明第一次打开应用且没有获取权限
            editor.putBoolean("firstInApplication", true);
            String[] requestPermissions = requestPermissionsList.toArray(new String[requestPermissionsList.size()]);
            ActivityCompat.requestPermissions(this, requestPermissions, 1);
        } else {
            //当firstInApplication为false时，说明不是第一次打开应用且已获取所有权限
            editor.putBoolean("firstInApplication", false);
        }
        editor.apply();
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
                    //当第一次打开应用且已获取权限后立马跳转主活动
                    enterMainActivity();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    private void enterMainActivity() {
        Intent mainIntent = new Intent(SplashActivity.this,
                MapActivity.class);
        SplashActivity.this.startActivity(mainIntent);
        SplashActivity.this.finish();
    }
}
