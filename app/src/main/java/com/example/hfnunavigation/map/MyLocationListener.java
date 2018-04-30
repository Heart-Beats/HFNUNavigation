package com.example.hfnunavigation.map;

import android.content.Context;
import android.widget.Toast;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.example.hfnunavigation.MyApplication;
import com.example.hfnunavigation.util.LogUtil;
import com.example.hfnunavigation.util.SystemUtil;


public class MyLocationListener extends BDAbstractLocationListener {


    private final Context context = MyApplication.getContext();
    private boolean isFirstLocate = true;
    private MyBaiduMap myBaiduMap = MyBaiduMap.getMyBaiduMap();

    @Override
    public void onReceiveLocation(BDLocation location) {
        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
        double latitude = location.getLatitude();    //获取纬度信息
        double longitude = location.getLongitude();    //获取经度信息
        float radius = location.getRadius();    //获取定位精度，默认值为0.0f
        String coorType = location.getCoorType();
        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
        int errorCode = location.getLocType();
        //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明

        firstIn(location);
        rangeJudgment(latitude, longitude);
        //location.setRadius(10.0f);
        // 构造定位数据
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(myBaiduMap.getmCurrentX())
                .latitude(latitude)
                .longitude(longitude)
                .build();

        // 设置定位数据
        myBaiduMap.getmBaiduMap().setMyLocationData(locData);
        //保存当前位置
        myBaiduMap.setCurrentLocation(new LatLng(latitude,longitude));
    }

    private void firstIn(BDLocation location) {
        if (isFirstLocate) {
            double hfnuCenterLatitude = 31.756809;  //合师中心纬度
            double hfnuCenterLongitude = 117.235116;  //合师中心经度
            LatLng latLng = new LatLng(hfnuCenterLatitude, hfnuCenterLongitude);
            //存放经纬度
            MapStatus newMapStatus = new MapStatus.Builder().target(latLng).zoom(18.2f).build();
            //设置地图的中心，缩放级别
            MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(newMapStatus);
            myBaiduMap.getmBaiduMap().animateMapStatus(update);
            isFirstLocate = false;
            LogUtil.d("位置", location.getCity() + location.getDistrict() + location.getStreet() + location.getBuildingName());
        }
    }

    private void rangeJudgment(double latitude, double longitude) {
        LatLngBounds latLngBounds = myBaiduMap.getHfnu().getHfnuRange();
        LatLng southWestLatlng = latLngBounds.southwest;
        LatLng northEastLatlng = latLngBounds.northeast;
        if (latitude < southWestLatlng.latitude
                || longitude < southWestLatlng.longitude
                || latitude > northEastLatlng.latitude
                || longitude > northEastLatlng.longitude) {
            Toast.makeText(context, "当前位置不在合师范围内！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param locType           定位类型
     * @param diagnosticType    诊断类型
     * @param diagnosticMessage 诊断信息
     */
    @Override
    public void onLocDiagnosticMessage(int locType, int diagnosticType, String diagnosticMessage) {
        super.onLocDiagnosticMessage(locType, diagnosticType, diagnosticMessage);
        LogUtil.d("定位错误诊断信息：", "locType:" + locType + ",diagnosticType:" + diagnosticType + ",diagnosticMessage:" + diagnosticMessage);
        switch (locType) {
            case 161:
                if (1 == diagnosticType) {
                    Toast.makeText(this.context, "网络定位成功，打开gps会更好！", Toast.LENGTH_SHORT).show();
                } else if (2 == diagnosticType) {
                    Toast.makeText(this.context, "移动网络定位成功，打开wifi会更好！", Toast.LENGTH_SHORT).show();
                }
                break;
            case 67:
                if (3 == diagnosticType) {
                    Toast.makeText(this.context, "离线定位失败，请检查网络并重试！", Toast.LENGTH_SHORT).show();
                }
                break;
            case 62:
                if (4 == diagnosticType) {
                    Toast.makeText(this.context, "定位失败，请允许获取位置权限并重试！", Toast.LENGTH_SHORT).show();
                } else if (5 == diagnosticType) {
                    Toast.makeText(this.context, "定位失败，请打开GPS并重试！", Toast.LENGTH_SHORT).show();
                    if (!SystemUtil.checkGPSIsOPen(this.context)) {
                        LogUtil.d("gps是否打开:", "" + SystemUtil.checkGPSIsOPen(context));
                    }
                } else if (6 == diagnosticType) {
                    Toast.makeText(this.context, "定位失败，请插入sim卡或打开wifi并重试！", Toast.LENGTH_SHORT).show();
                } else if (7 == diagnosticType) {
                    Toast.makeText(this.context, "定位失败，请关闭飞行模式并重试！", Toast.LENGTH_SHORT).show();
                } else if (9 == diagnosticType) {
                    Toast.makeText(this.context, "定位失败，无法获取任何位置信息！", Toast.LENGTH_SHORT).show();
                }
                break;
            case 167:
                if (8 == diagnosticType) {
                    Toast.makeText(this.context, "网络定位失败，百度位置服务无法计算位置！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Toast.makeText(this.context, "未知原因，定位失败！", Toast.LENGTH_SHORT).show();
        }
    }

}
