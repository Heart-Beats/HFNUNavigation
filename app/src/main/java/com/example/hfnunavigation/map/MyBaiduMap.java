package com.example.hfnunavigation.map;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.example.hfnunavigation.MessageEvent;
import com.example.hfnunavigation.MyApplication;
import com.example.hfnunavigation.MyWidgetListener;
import com.example.hfnunavigation.MyOrientationListener;
import com.example.hfnunavigation.R;
import com.example.hfnunavigation.map.route.WalkingPlaining;
import com.example.hfnunavigation.school.MySchool;
import com.example.hfnunavigation.db.SchoolLocation;
import com.example.hfnunavigation.util.LogUtil;
import com.example.hfnunavigation.util.SystemUtil;

import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;

public class MyBaiduMap {

    private LocationClient mLocationClient;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private MyOrientationListener myOrientationListener;
    private float mCurrentX;
    private final Context context;
    private MySchool hfnu;
    private List<SchoolLocation> locationsList;
    private boolean displayView = true;
    private LatLng currentLocation;
    private LatLng startPlace;
    private LatLng endPlace;
    private boolean changeStartPlace;
    private WalkingPlaining walkingPlaining;
    private MyWidgetListener myWidgetListener;
    private List<Overlay> overlays;
    private String startPlaceName;
    private String endPlaceName;

    private static class Holder {
        private static MyBaiduMap myBaiduMap = new MyBaiduMap();
    }

    private MyBaiduMap() {
        context = MyApplication.getContext();
        mLocationClient = new LocationClient(context);
        //声明LocationClient类
        myOrientationListener = new MyOrientationListener(context);
        overlays = new ArrayList<>();
    }

    public static MyBaiduMap getMyBaiduMap() {
        return Holder.myBaiduMap;
    }

    public void setmBaiduMap(BaiduMap mBaiduMap) {
        this.mBaiduMap = mBaiduMap;
    }

    public BaiduMap getmBaiduMap() {
        return mBaiduMap;
    }

    public float getmCurrentX() {
        return mCurrentX;
    }

    public void setmMapView(MapView mMapView) {
        this.mMapView = mMapView;
    }

    public LocationClient getmLocationClient() {
        return mLocationClient;
    }

    public MySchool getHfnu() {
        return hfnu;
    }

    public void setCurrentLocation(LatLng currentLocation) {
        this.currentLocation = currentLocation;
    }

    public MyWidgetListener getMyWidgetListener() {
        return myWidgetListener;
    }

    public void setStartPlaceName(String startPlaceName) {
        this.startPlaceName = startPlaceName;
    }

    public String getStartPlaceName() {
        return startPlaceName;
    }

    public void setEndPlaceName(String endPlaceName) {
        this.endPlaceName = endPlaceName;
    }

    public String getEndPlaceName() {
        return endPlaceName;
    }

    public void setDisplayView(boolean displayView) {
        this.displayView = displayView;
    }

    private void baiduMapSettings() {
        hfnu = new MySchool();
        walkingPlaining = new WalkingPlaining(mBaiduMap);
        locationsList = hfnu.getLocationsList();
        mLocationClient.registerLocationListener(new MyLocationListener());
        //注册定位回调监听接口
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //普通地图 ,mBaiduMap是地图控制器对象
        mBaiduMap.setMyLocationConfiguration(locationConfiguration());
        mMapView.showZoomControls(false);
        //控制缩放按钮是否显示,默认显示
        useOrientationListener();
        mBaiduMap.showMapPoi(false);
        // 将底图标注设置为隐藏
        addOverlay(mBaiduMap.getMapStatus());
        setBaiduMapListener();
        useMyWidgetListener();
    }

    private void addOverlay(MapStatus changedMapstatus) {
        List<OverlayOptions> overlayOptionsList = new ArrayList<>();
        for (SchoolLocation schoolLocation : locationsList) {
            //获得地点名称
            String locationName = schoolLocation.getLocationName();
            //定义Maker坐标点
            LatLng position = new LatLng(schoolLocation.getLatitude(), schoolLocation.getLongitude());
            //构建Marker图标
            View markerView = View.inflate(context, R.layout.school_marker_style, null);
            TextView markerText;
            //自定义标注文字的显示位置
            switch (locationName) {
                case "图书馆":
                case "逸夫楼":
                case "实训基地1":
                case "荷塘":
                case "竹园广场":
                case "6#":
                case "7#":
                case "8#":
                case "9#":
                case "10#":
                case "新疆楼":
                case "乒乓球桌":
                    markerText = markerView.findViewById(R.id.right_of_marker);
                    break;
                case "竹园":
                case "平房1":
                case "平房2":
                case "平房3":
                case "3#":
                case "4#":
                case "银杏树林":
                case "5#":
                case "小卖部":
                case "开水房":
                    markerText = markerView.findViewById(R.id.left_of_marker);
                    break;
                case "足球场":
                case "主席台":
                case "理发店":
                case "南门":
                    markerText = markerView.findViewById(R.id.above_of_marker);
                    break;
                default:
                    markerText = markerView.findViewById(R.id.below_of_marker);
                    break;
            }
            markerText.setText(locationName);
            //TextView默认使用PX作为单位，SP刚好是PX的三倍
            float PXSize = (changedMapstatus.zoom - 18) * 3 + markerText.getTextSize() / 3;
            markerText.setTextSize(PXSize);
            //System.out.println(markerText.getTextSize()+"***"+ PXSize);
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(markerView);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions overlayOptions = new MarkerOptions()
                    .position(position)
                    .icon(bitmap)
                    .flat(true)
                    .title(locationName)
                    .anchor(0.5f, 0.5f);//锚点设置为自定义标注图标的中心，刚好为icon_marker的位置
            overlayOptionsList.add(overlayOptions);
        }
        overlays = mBaiduMap.addOverlays(overlayOptionsList);
        overlays.add(mBaiduMap.addOverlay(getGroundOverlay())) ;
        //地图中添加覆盖物
    }


    //在地图中自定义图片图层（图片覆盖物）
    @NonNull
    private OverlayOptions getGroundOverlay() {
        //定义Ground显示的图片
        BitmapDescriptor bdGround = BitmapDescriptorFactory
                .fromResource(R.drawable.ground_overlay);
        //定义Ground覆盖物选项
        return new GroundOverlayOptions()
                .positionFromBounds(hfnu.getHfnuRange())
                .image(bdGround)
                .transparency(0.2f);
    }

    /**
     * 设置地图中用到的监听器
     */
    private void setBaiduMapListener() {

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {

            MapStatus beforeChangingMapStatus = mBaiduMap.getMapStatus();

            /**
             * 手势操作地图，设置地图状态等操作导致地图状态开始改变。
             * @param mapStatus 地图状态改变开始时的地图状态
             */
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
            }

            /** 因某种操作导致地图状态开始改变。
             * @param mapStatus 地图状态改变开始时的地图状态
             * @param reason 表示地图状态改变的原因，取值有：
             * 1：用户手势触发导致的地图状态改变,比如双击、拖拽、滑动底图
             * 2：SDK导致的地图状态改变, 比如点击缩放控件、指南针图标
             * 3：开发者调用,导致的地图状态改变
             */
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int reason) {
                if (REASON_GESTURE == reason) {
                    beforeChangingMapStatus = mapStatus;
                }
            }

            /**
             * 地图状态变化中
             * @param mapStatus 当前地图状态
             */
            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
            }

            /**
             * 地图状态改变结束
             * @param mapStatus 地图状态改变结束后的地图状态
             */
            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                LatLngBounds latLngBounds = hfnu.getHfnuRange();
                LatLng southWestLatlng = latLngBounds.southwest;
                LatLng northEastLatlng = latLngBounds.northeast;
                LatLng mapCenterLatLng = mapStatus.target;
                //当前地图状态地图的操作中心点为地理坐标,会改变
                //Point point = mapStatus.targetScreen;
                //当前地图状态地图的操作中心点在屏幕中的坐标始终不变
                if (mapCenterLatLng.latitude < southWestLatlng.latitude
                        || mapCenterLatLng.longitude < southWestLatlng.longitude
                        || mapCenterLatLng.latitude > northEastLatlng.latitude
                        || mapCenterLatLng.longitude > northEastLatlng.longitude) {
                   // mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(beforeChangingMapStatus));
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(beforeChangingMapStatus));
                }
                if (mapStatus.zoom < 17.5) {
                   // mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(hfnu.getHfnuRange()));
                    // 如果缩放太小设置显示在屏幕中的地图地理范围为合师
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(hfnu.getHfnuRange()));
                }
                //如果缩放时重新添加Overlay
                if (mapStatus.zoom != beforeChangingMapStatus.zoom) {
                    mBaiduMap.hideInfoWindow();
                    EventBus.getDefault().post(new MessageEvent("悬浮按钮隐藏"));
                    //当地图缩放时，infowindow隐藏，悬浮按钮也需要隐藏
                    for (Overlay overlay : overlays) {
                        overlay.remove();
                    }
                    overlays.clear();
                    addOverlay(mapStatus);
                }
                if (displayView) {//仅当infoWindow关闭时，输入地点布局才显示
                    if (beforeChangingMapStatus.target.latitude < mapCenterLatLng.latitude) {
                        EventBus.getDefault().post(new MessageEvent("输入地点布局显示"));
                        //地图下滑时显示输入地点布局
                    } else {
                        EventBus.getDefault().post(new MessageEvent("输入地点布局隐藏"));
                        //地图上滑时隐藏输入地点布局
                    }
                }
            }
        });

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                View infoView = View.inflate(context, R.layout.info_window_style, null);
                for (SchoolLocation location : locationsList) {
                    String markerTitle = marker.getTitle();
                    if (null == markerTitle) {
                        return false;
                    }
                    if (markerTitle.equals(location.getLocationName())) {
                        LatLng locationPosition = new LatLng(location.getLatitude(), location.getLongitude());
                        //点击marker时，地图以marker为中心点
                        MapStatus updateStatus = new MapStatus.Builder().target(locationPosition).build();
                        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(updateStatus));
                        //mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(updateStatus));
                        //创建InfoWindow展示的view
                        TextView locationInfo = infoView.findViewById(R.id.location_info);
                        locationInfo.setText(location.getMoreInfo());
                        //创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
                        InfoWindow mInfoWindow = new InfoWindow(infoView, locationPosition, -40);
                        //显示InfoWindow
                        mBaiduMap.showInfoWindow(mInfoWindow);
                        endPlace = locationPosition;
                        endPlaceName = location.getLocationName();
                        walkingPlaining.removeRoute();
                        //当infoWindow显示时清除路线
                    }
                }
                EventBus.getDefault().post(new MessageEvent("悬浮按钮显示"));
                EventBus.getDefault().post(new MessageEvent("输入地点布局隐藏"));
                //当InfoWindow显示时，悬浮按钮也显示并且隐藏输入地点布局
                ImageView closeInfoWindow = infoView.findViewById(R.id.close_info_window);
                closeInfoWindow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBaiduMap.hideInfoWindow();
                        EventBus.getDefault().post(new MessageEvent("悬浮按钮隐藏"));
                        //点击InfoWindow上的关闭按钮时，InfoWindow和悬浮按钮都不显示
                    }
                });
                return true;
            }
        });

    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        //设置是否需要获取当前地址的详细信息

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        /*可选，设置定位模式，默认高精度   LocationMode.Hight_Accuracy：高精度；
                                         LocationMode. Battery_Saving：低功耗；
                                         LocationMode. Device_Sensors：仅使用设备；*/
        option.setCoorType("bd09ll");
        /*可选，设置返回经纬度坐标类型，默认gcj02
        gcj02：国测局坐标；
        bd09ll：百度经纬度坐标；
        bd09：百度墨卡托坐标；
        海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标*/

        option.setScanSpan(1000);
        /*可选，设置发起定位请求的间隔，int类型，单位ms
        如果设置为0，则代表单次定位，即仅定位一次，默认为0
        如果设置非0，需设置1000ms以上才有效*/

        option.setOpenGps(true);
        /*可选，设置是否使用gps，默认false
        使用高精度和仅用设备两种定位模式的，参数必须设置为true*/

        option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(true);
        /*可选，定位SDK内部是一个service，并放到了独立进程。
        设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)*/

        option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5 * 60 * 1000);
        /*可选，7.2版本新增能力
        如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位*/

        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        mLocationClient.setLocOption(option);
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
    }

    //自定义定位模式、定位图标、精度圈颜色
    private MyLocationConfiguration locationConfiguration() {
        MyLocationConfiguration.LocationMode mCurrentMode;
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;//定位普通态
        //mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;   //默认为 LocationMode.NORMAL 普通态
        //mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;  //定位罗盘态
        //BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
        int accuracyCircleFillColor = 0x8F71DEFC;//自定义精度圈填充颜色, 前两位表示透明度
        int accuracyCircleStrokeColor = 0xFF30F0F6;//自定义精度圈边框颜色
        return new MyLocationConfiguration(mCurrentMode, true, null,
                accuracyCircleFillColor, accuracyCircleStrokeColor);
    }

    /*定位结合方向传感器，从而可以实时监测到X轴坐标的变化，从而就可以检测到
       定位图标方向变化，只需要将这个动态变化的X轴的坐标更新myCurrentX值，
       最后在MyLocationData data.driection(mCurrentX);  */
    private void useOrientationListener() {
        myOrientationListener.setMyOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {//监听方向的改变，方向改变时，需要得到地图上方向图标的位置
                mCurrentX = x;
                LogUtil.d("方向:x---->", String.valueOf(x));
            }
        });
    }

    public void startRoutePlaning() {
        if (!changeStartPlace) { // 默认起点为当前位置
            startPlace = currentLocation;
            startPlaceName = "我的位置";
        }
        if (SystemUtil.checkNetworkIsOpen(context)) {
            walkingPlaining.startToFinshRoute(startPlace, endPlace);
        } else {
            Toast.makeText(context, "获取路线失败，请打开网络后再试", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkStartAndEndPlace() {
        boolean inputStartContextTrue = false;  // 判断输入的起点地点信息是否正确
        boolean inputEndContextTrue = false;    // 判断输入的终点地点信息是否正确
        if (TextUtils.isEmpty(startPlaceName)||"我的位置".equals(startPlaceName)) {
            changeStartPlace = false;
            inputStartContextTrue = true;
        }
        for (SchoolLocation schoolLocation : locationsList) {
            String schoolLocationName = schoolLocation.getLocationName();
            double locationLatitude = schoolLocation.getLatitude();
            double locationLongitude = schoolLocation.getLongitude();
            if (schoolLocationName.equals(startPlaceName)) {
                changeStartPlace = true;
                startPlace = new LatLng(locationLatitude, locationLongitude);
                inputStartContextTrue = true;
            }
            if (schoolLocationName.equals(endPlaceName)) {
                endPlace = new LatLng(locationLatitude, locationLongitude);
                inputEndContextTrue = true;
            }
        }
        return inputStartContextTrue && inputEndContextTrue;
    }


    private void useMyWidgetListener() {
        myWidgetListener = new MyWidgetListener() {
            @Override
            public void floatingButtonClickListener() {
                changeStartPlace = false; // 点击悬浮按钮以当前位置进行路线规划
                startRoutePlaning();
                mBaiduMap.hideInfoWindow();
                EventBus.getDefault().post(new MessageEvent("悬浮按钮隐藏"));
            }

            @Override
            public void navigationButtonClickListener() {
                if (checkStartAndEndPlace()) {
                    startRoutePlaning();
                    //walkingPlaining.removeRoute();
                    EventBus.getDefault().post(new MessageEvent("输入地点布局隐藏"));
                } else {
                    Toast.makeText(context, "输入地点信息有误，请重新输入", Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(new MessageEvent("输入地点信息错误"));
                }
            }
        };
    }

    public void start() {
        mBaiduMap.setMyLocationEnabled(true);//开启允许定位
        //开启方向传感器
        myOrientationListener.start();
    }

    public void stop() {
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();//关闭定位
        myOrientationListener.stop();//关闭方向传感器
    }

    public void destroy() {
        mMapView.onDestroy();
        // 当不需要定位图层时关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        walkingPlaining.destroy();
    }

    public void resume() {
        mBaiduMap.setMyLocationEnabled(true);
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    public void pause() {
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    public void myBaiduMapStart() {
        initLocation();
        baiduMapSettings();
    }
}
