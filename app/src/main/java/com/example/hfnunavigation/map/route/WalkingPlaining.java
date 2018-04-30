package com.example.hfnunavigation.map.route;


import android.widget.Toast;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.hfnunavigation.MyApplication;
import com.example.hfnunavigation.util.LogUtil;
import java.util.List;

public class WalkingPlaining {

    private RoutePlanSearch mSearch;
    //WalkingOverlay是baidu map api提供的用于在地图上显示步行路线的Overlay
    private WalkingRouteOverlay routeOverlay;

    public WalkingPlaining(BaiduMap mBaiduMap) {
        mSearch = RoutePlanSearch.newInstance();
        routeOverlay = new MyWalkingRouteOverlay(mBaiduMap);
        walkingRoutePlan();
    }

    public void startToFinshRoute(LatLng startingPoint, LatLng endPoint) {
        PlanNode stNode = PlanNode.withLocation(startingPoint);
        PlanNode enNode = PlanNode.withLocation(endPoint);
        mSearch.walkingSearch(new WalkingRoutePlanOption()
                .from(stNode)
                .to(enNode));
        LogUtil.d("发起步行路径规划：","成功");
    }

    private void walkingRoutePlan() {
        mSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult result) {
                //获取步行线路规划结果
                List<WalkingRouteLine> walkingRouteLines = result.getRouteLines();
                if (result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(MyApplication.getContext(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                }
                if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    // result.getSuggestAddrInfo()
                    return;
                }
                if(result.error == SearchResult.ERRORNO.NO_ERROR){
                    LogUtil.d("路径规划：","结果正确，开始路径规划");
                    WalkingRouteLine route = walkingRouteLines.get(0);
                    routeOverlay.setData(route);
                    routeOverlay.addToMap();
                    routeOverlay.zoomToSpan();
                }
            }


            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        });
    }

    public void removeRoute() {
        routeOverlay.removeFromMap();
    }

    public void destroy() {
        mSearch.destroy();
    }


}
