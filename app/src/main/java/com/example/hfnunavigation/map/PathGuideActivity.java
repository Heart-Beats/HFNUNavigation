package com.example.hfnunavigation.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import com.baidu.navisdk.adapter.BNRouteGuideManager;
import com.baidu.navisdk.adapter.BNRoutePlanNode;

import java.util.ArrayList;

public class PathGuideActivity extends Activity {
    private BNRoutePlanNode mBNRoutePlanNode = null;
    private Handler handler = null;
    private static final int MSG_SHOW = 1;
    private static final int MSG_HIDE = 2;
    private static final int MSG_RESET_NODE = 3;
    private ArrayList<BNRoutePlanNode> list = null;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getHandler();
        BNRouteGuideManager.getInstance().showCustomizedLayer(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        }
        View view = BNRouteGuideManager.getInstance().onCreate(this, new BNRouteGuideManager.OnNavigationListener() {
            @Override
            public void onNaviGuideEnd() {
                finish();
            }

            @Override
            public void notifyOtherAction(int arg0, int arg1, int arg2, Object arg3) {
            }
        });
        if (view != null) {
            setContentView(view);
        }
        Intent intent = getIntent();
        if (intent != null) {
            list = (ArrayList<BNRoutePlanNode>) intent.getSerializableExtra(MyNavigationActivity.ROUTE_PLAN_NODE);//接收到路线规划得到的节点集合
            mBNRoutePlanNode = list.get(0);//先取得起点节点
        }
    }

    /**
     * 管理导航功能操作生命周期将它与Activity的生命周期绑定在一起即可
     */
    @Override
    protected void onResume() {
        BNRouteGuideManager.getInstance().onResume();
        super.onResume();
        if (handler != null) {
            handler.sendEmptyMessageAtTime(MSG_SHOW, 2000);
        }
    }

    protected void onPause() {
        super.onPause();
        BNRouteGuideManager.getInstance().onPause();
    }

    @Override
    protected void onDestroy() {
        BNRouteGuideManager.getInstance().onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        BNRouteGuideManager.getInstance().onStop();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        BNRouteGuideManager.getInstance().onBackPressed(false);
    }

    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        BNRouteGuideManager.getInstance().onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    private void getHandler() {
        if (handler == null) {
            handler = new Handler(getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == MSG_SHOW) {
                        BNRouteGuideManager.getInstance().showCustomizedLayer(true);
                    } else if (msg.what == MSG_HIDE) {
                        BNRouteGuideManager.getInstance().showCustomizedLayer(false);
                    } else if (msg.what == MSG_RESET_NODE) {
                        BNRouteGuideManager.getInstance().resetEndNodeInNavi(list.get(1));
                    }
                }
            };
        }
    }
}
