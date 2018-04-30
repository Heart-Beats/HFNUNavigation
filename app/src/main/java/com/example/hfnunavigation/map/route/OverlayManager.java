package com.example.hfnunavigation.map.route;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLngBounds;
import com.example.hfnunavigation.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 该类提供一个能够显示和管理多个Overlay的基类
 * <p>
 * 复写{@link #getOverlayOptions()} 设置欲显示和管理的Overlay列表
 * </p>
 * <p>
 * 通过
 * {@link com.baidu.mapapi.map.BaiduMap#setOnMarkerClickListener(com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener)}
 * 将覆盖物点击事件传递给OverlayManager后，OverlayManager才能响应点击事件。
 * <p>
 * 复写{@link #(com.baidu.mapapi.map.Marker)} 处理Marker点击事件
 * </p>
 */
public abstract class OverlayManager implements BaiduMap.OnPolylineClickListener {

    private BaiduMap mBaiduMap;
    private List<Overlay> mOverlayList;

    /**
     * 通过一个BaiduMap 对象构造
     *
     * @param baiduMap BaiduMap 对象
     */
    OverlayManager(BaiduMap baiduMap) {
        mBaiduMap = baiduMap;
        // mBaiduMap.setOnMarkerClickListener(this);
        if (mOverlayList == null) {
            mOverlayList = new ArrayList<>();
        }
    }

    /**
     * 覆写此方法设置要管理的Overlay列表
     *
     * @return 管理的Overlay列表
     */
    public abstract List<OverlayOptions> getOverlayOptions();

    /**
     * 将所有Overlay 添加到地图上
     */
    public final void addToMap() {
        if (mBaiduMap == null) {
            return;
        }
        removeFromMap();
        List<OverlayOptions> overlayOptions = getOverlayOptions();
        LogUtil.d("overlayOptions的大小：",""+overlayOptions.size());
        if (overlayOptions != null) {
            for (OverlayOptions option :overlayOptions) {
                mOverlayList.add(mBaiduMap.addOverlay(option));
            }
        }
    }

    /**
     * 将所有路线规划中添加的Overlay 从 地图上消除
     */
    public final void removeFromMap() {
        for (Overlay marker : mOverlayList) {
            marker.remove();
        }
        mOverlayList.clear();
    }

    /**
     * 缩放地图，使所有Overlay都在合适的视野内
     * <p>
     * 注： 该方法只对Marker类型的overlay有效
     * </p>
     */
    public void zoomToSpan() {
        if (mBaiduMap == null) {
            return;
        }
        if (mOverlayList.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Overlay overlay : mOverlayList) {
                // polyline 中的点可能太多，只需要按添加的marker 缩放
                if (overlay instanceof Marker) {
                    builder.include(((Marker) overlay).getPosition());
                }
            }
            mBaiduMap.setMapStatus(MapStatusUpdateFactory
                    .newLatLngBounds(builder.build()));
        }
    }

}