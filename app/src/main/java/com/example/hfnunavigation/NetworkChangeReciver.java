package com.example.hfnunavigation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.baidu.location.LocationClient;
import com.example.hfnunavigation.map.MyBaiduMap;
import com.example.hfnunavigation.util.SystemUtil;

/**
 * 监听网络，只有当网络和定位服务都打开时开始定位
 */
public class NetworkChangeReciver extends BroadcastReceiver {

    private LocationClient locationClient = MyBaiduMap.getMyBaiduMap().getmLocationClient();

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean NetworkIsOpen = SystemUtil.checkNetworkIsOpen(context);
        boolean GPSIsOpen = SystemUtil.checkGPSIsOPen(context);
        if (!NetworkIsOpen) {
            Toast.makeText(context, "本应用使用在线定位，请确认网络已连接",
                    Toast.LENGTH_LONG).show();
        } else {
            if (!GPSIsOpen) {
                SystemUtil.openGPSSettings(context);
            } else {
                locationClient.start();
                 /*     调用LocationClient的start()方法，便可发起定位请求
        start()：启动定位SDK；stop()：关闭定位SDK。调用start()之后只需要等待定位结果自动回调即可。
        如果是单次定位，在收到定位结果之后直接调用stop()函数即可。如果stop()之后仍然想进行定位，可以再次start()等待定位结果回调即可。
        自v7.2版本起，新增LocationClient.reStart()方法，用于在某些特定的异常环境下重启定位。
        如果想按照自己逻辑请求定位，可以在start()之后按照自己的逻辑请求LocationClient.requestLocation()函数，会主动触发定位SDK内部定位逻辑，等待定位回调即可*/
            }
        }
    }
}
