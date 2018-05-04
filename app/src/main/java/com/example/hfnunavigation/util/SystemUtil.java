package com.example.hfnunavigation.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import com.example.hfnunavigation.activity.MapActivity;

public class SystemUtil {

    /* 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的  true表示开启 */
    public static boolean checkGPSIsOPen(Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager != null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;
    }

    /**
     * 跳转GPS设置
     */
    public static void openGPSSettings(final Context context) {
        //GPS没有打开则弹出对话框
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("提示")
                .setMessage("当前应用需要访问位置信息才可正常使用\n\n请点击设置打开定位服务")
                // 拒绝, 退出应用
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                android.os.Process.killProcess(android.os.Process.myPid());    //获取PID
                                System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
                            }
                        })

                .setPositiveButton("设置",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //跳转GPS设置界面
                                Activity activity = (Activity) context;
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                activity.startActivityForResult(intent, MapActivity.GPS_REQUEST_CODE);
                            }
                        })

                .setCancelable(false)
                .show();
    }

    public static boolean checkNetworkIsOpen(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //ConnectivityManager系统服务类,专门用于管理网络连接
        NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return networkInfo != null && networkInfo.isAvailable();
    }
}
