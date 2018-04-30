package com.example.hfnunavigation.map.route;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.example.hfnunavigation.R;

class MyWalkingRouteOverlay extends WalkingRouteOverlay {

    private boolean useDefaultIcon = false;

     MyWalkingRouteOverlay(BaiduMap baiduMap) {
        super(baiduMap);
    }

    @Override
    public BitmapDescriptor getStartMarker() {
        if (!useDefaultIcon) {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_start_node);
        }
        return null;
    }

    @Override
    public BitmapDescriptor getTerminalMarker() {
        if (!useDefaultIcon) {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_end_node);
        }
        return null;
    }
}
