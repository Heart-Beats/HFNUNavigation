package com.example.hfnunavigation.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.example.hfnunavigation.MyApplication;

import java.util.ArrayList;

public class MyNavigationActivity extends Activity {

    private final Context context;
    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    private String authinfo = null;

    public MyNavigationActivity() {
        context = MyApplication.getContext();
        initNaviPath();
    }

    private void initNaviPath() {//初始化导航路线的导航引擎
        BNOuterTTSPlayerCallback ttsCallback = null;
        BaiduNaviManager.getInstance().init(this, null, null, new BaiduNaviManager.NaviInitListener() {

            @Override
            public void onAuthResult(int status, String msg) {
                if (status==0) {
                    authinfo = "key校验成功!";
                }else{
                    authinfo = "key校验失败!"+msg;
                }
                MyNavigationActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, authinfo, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void initSuccess() {
                Toast.makeText(context, "百度导航引擎初始化成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void initStart() {
                Toast.makeText(context, "百度导航引擎初始化开始", Toast.LENGTH_LONG).show();
            }

            @Override
            public void initFailed() {
                Toast.makeText(context, "百度导航引擎初始化失败", Toast.LENGTH_LONG).show();
            }
        }, ttsCallback);
    }

    public void routeplanToNavi(LatLng startPlace, LatLng endPlace) {

        BNRoutePlanNode sNode = new BNRoutePlanNode(startPlace.longitude,startPlace.latitude,null,null, BNRoutePlanNode.CoordinateType.BD09LL);
        BNRoutePlanNode eNode = new BNRoutePlanNode(endPlace.longitude,endPlace.latitude,null,null, BNRoutePlanNode.CoordinateType.BD09LL);
        ArrayList<BNRoutePlanNode> list = new ArrayList<>();
        list.add(sNode);
        list.add(eNode);
        BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new MyRoutePlanListener(list));
        System.out.println("路线规划:"+String.valueOf(list.size()));
    }

    class MyRoutePlanListener implements BaiduNaviManager.RoutePlanListener {//路线规划监听器接口类
        private ArrayList<BNRoutePlanNode> mList;

        private MyRoutePlanListener(ArrayList<BNRoutePlanNode> list) {
            mList = list;
        }

        @Override
        public void onJumpToNavigator() {
            Intent intent = new Intent(MyNavigationActivity.this, PathGuideActivity.class);
            intent.putExtra(ROUTE_PLAN_NODE, mList);//将得到所有的节点集合传入到导航的Activity中去
            startActivity(intent);
            System.out.println("导航跳转");
        }

        @Override
        public void onRoutePlanFailed() {

        }

    }

}
