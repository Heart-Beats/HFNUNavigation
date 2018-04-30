package com.example.hfnunavigation.map.route;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.example.hfnunavigation.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于显示步行路线的overlay，自3.4.0版本起可实例化多个添加在地图中显示
 */
public class WalkingRouteOverlay extends OverlayManager {

    private WalkingRouteLine mRouteLine = null;

    WalkingRouteOverlay(BaiduMap baiduMap) {
        super(baiduMap);
    }

    /**
     * 设置路线数据。
     *
     * @param line 路线数据
     */
    public void setData(WalkingRouteLine line) {
        LogUtil.d("路径规划：","路线数据设置");
        mRouteLine = line;
    }

    @Override
    public final List<OverlayOptions> getOverlayOptions() {
        if (mRouteLine == null) {
            LogUtil.d("mRoute:","null");
            return null;
        }
        LogUtil.d("mRoute:","非null");
        List<OverlayOptions> overlayList = new ArrayList<>();
        List<WalkingRouteLine.WalkingStep> allWalkingStep = mRouteLine.getAllStep();  //获取步行路线中的所有步行路段
        if (allWalkingStep != null && allWalkingStep.size() > 0) {
            for (WalkingRouteLine.WalkingStep step : allWalkingStep) {
                Bundle b = new Bundle();
                b.putInt("index", allWalkingStep.indexOf(step));  //获取每个路段在列表中的索引位置并绑定信息

                // 获取每个路段起点信息并设置marker
                if (step.getEntrance() != null) {
                    OverlayOptions overlayOptions =new MarkerOptions()
                            .position(step.getEntrance().getLocation()) // 获取路段起点信息位置的坐标并设置marker 覆盖物位置
                            .rotate((360 - step.getDirection()))  //获取路段起点方向值并设置 marker 覆盖物旋转角度，逆时针
                            .zIndex(10)
                            .anchor(0.5f, 0.5f)  //设置 marker 覆盖物的锚点比例，默认（0.5f, 1.0f）水平居中，垂直下对齐
                            .extraInfo(b)  //设置 marker 覆盖物的额外信息
                            .icon(BitmapDescriptorFactory.fromAssetWithDpi("Icon_line_node.png"));
                    overlayList.add(overlayOptions);
                    LogUtil.d("设置每个路段起点标志：","成功");
                }

                // 获取最后路段的终点信息并设置marker绘制出口点
                if (allWalkingStep.indexOf(step) == (allWalkingStep.size() - 1) && step.getExit() != null) {
                    OverlayOptions overlayOptions =new MarkerOptions()
                            .position(step.getExit().getLocation())
                            .anchor(0.5f, 0.5f)
                            .zIndex(10)
                            .icon(BitmapDescriptorFactory.fromAssetWithDpi("Icon_line_node.png"));
                    overlayList.add(overlayOptions);
                    LogUtil.d("设置最后路段的终点标志：","成功");
                }
            }
        }

        // starting获取路线起点信息并设置marker
        if (mRouteLine.getStarting() != null) {
            OverlayOptions overlayOptions =new MarkerOptions()
                    .position(mRouteLine.getStarting().getLocation())
                    .icon(getStartMarker() != null ? getStartMarker() :
                            BitmapDescriptorFactory.fromAssetWithDpi("Icon_start.png"))
                    .zIndex(10);
            overlayList.add(overlayOptions);
            LogUtil.d("设置路线起点标志：","成功");
        }

        // terminal获取路线终点信息并设置marker
        if (mRouteLine.getTerminal() != null) {
            OverlayOptions overlayOptions =new MarkerOptions()
                    .position(mRouteLine.getTerminal().getLocation())
                    .icon(getTerminalMarker() != null ? getTerminalMarker() :
                            BitmapDescriptorFactory.fromAssetWithDpi("Icon_end.png"))
                    .zIndex(10);
            overlayList.add(overlayOptions);
            LogUtil.d("设置路线终点标志：","成功");
        }

        // polyline list  设置折线段折线覆盖物，即路线
        if (allWalkingStep != null && allWalkingStep.size() > 0) {
            LatLng lastStepLastPoint = null;
            for (WalkingRouteLine.WalkingStep step : allWalkingStep) {
                List<LatLng> wayPoints = step.getWayPoints();  //获取路段所经过的地理坐标集合
                if (wayPoints != null) {
                    List<LatLng> points = new ArrayList<>();
                    if (lastStepLastPoint != null) {
                        points.add(lastStepLastPoint);
                    }
                    points.addAll(wayPoints);
                    OverlayOptions overlayOptions = new PolylineOptions()
                            .points(points)   //设置折线坐标点列表
                            .width(10)    //设置折线线宽， 默认为 5， 单位：像素
                            .color(getLineColor() != 0 ? getLineColor() :
                                    Color.argb(178, 0, 78, 255))
                            .zIndex(0);
                    overlayList.add(overlayOptions);
                    //保存路段最后的点坐标
                    lastStepLastPoint = wayPoints.get(wayPoints.size() - 1);
                }
            }
            LogUtil.d("路线添加：","成功");
        }
        return overlayList;
    }

    /**
     * 覆写此方法以改变默认起点图标
     *
     * @return 起点图标
     */
    public BitmapDescriptor getStartMarker() {
        return null;
    }

    public int getLineColor() {
        return 0;
    }

    /**
     * 覆写此方法以改变默认终点图标
     *
     * @return 终点图标
     */
    public BitmapDescriptor getTerminalMarker() {
        return null;
    }

    /**
     * 处理点击事件
     *
     * @param i 被点击的step在
     *          {@link com.baidu.mapapi.search.route.WalkingRouteLine#getAllStep()}
     *          中的索引
     * @return 是否处理了该点击事件
     */
    public boolean onRouteNodeClick(int i) {
        if (mRouteLine.getAllStep() != null
                && mRouteLine.getAllStep().get(i) != null) {
            Log.d("baidumapsdk", "WalkingRouteOverlay onRouteNodeClick");
        }
        return false;
    }

/*    @Override
    public final boolean onMarkerClick(Marker marker) {
        for (Overlay mMarker : mOverlayList) {
            if (mMarker instanceof Marker && mMarker.equals(marker)) {
                if (marker.getExtraInfo() != null) {
                    onRouteNodeClick(marker.getExtraInfo().getInt("index"));
                }
            }
        }
        return true;
    }*/

    @Override
    public boolean onPolylineClick(Polyline polyline) {
        return false;
    }
}