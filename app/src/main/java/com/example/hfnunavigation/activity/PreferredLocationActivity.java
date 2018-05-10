package com.example.hfnunavigation.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.hfnunavigation.MyItemTouchHelperCallBack;
import com.example.hfnunavigation.MyItemTouchListener;
import com.example.hfnunavigation.PreferredLocationAdapter;
import com.example.hfnunavigation.R;
import com.example.hfnunavigation.db.HistoricalTrack;
import com.example.hfnunavigation.util.MyComparator;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreferredLocationActivity extends AppCompatActivity {

    private List<PreferredLocation> preferredLocationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferred_location);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.preferred_location);
        toolbar.setTitleMarginStart(90 * 3);  //默认px为单位，测试机dp=3px
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initPreferredLocation();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        PreferredLocationAdapter adapter = new PreferredLocationAdapter(preferredLocationList, this);
        recyclerView.setAdapter(adapter);
        MyItemTouchHelperCallBack itemTouchHelperCallBack =
                new MyItemTouchHelperCallBack(recyclerView, preferredLocationList);
        itemTouchHelperCallBack.setItemSwipe(false);
        ItemTouchHelper helper = new ItemTouchHelper(itemTouchHelperCallBack);
        helper.attachToRecyclerView(recyclerView);   //绑定目标RecyclerView对象
        recyclerView.addOnItemTouchListener(new MyItemTouchListener(recyclerView) {

            @Override
            public void onItemClick(View view, int position) {
                view.setBackgroundResource(R.drawable.item_click_bg);
            }

            @Override
            public void onItemLongPress(View view, int position) {

            }
        });
    }

    private void initPreferredLocation() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userID = preferences.getString("openid", null);
        //获得指定列的用户数据
        List<HistoricalTrack> historicalTrackList = DataSupport.select("startPlaceName",
                "endPlaceName").where("userID = ?", userID).find(HistoricalTrack.class);
        if (historicalTrackList.size() == 0) {
            TextView noHistory = findViewById(R.id.no_history);
            noHistory.setVisibility(View.VISIBLE);
        } else {
            preferredLocationList = totalHistoricalTrack(historicalTrackList);
            //将次数按从大到小排列
            Collections.sort(preferredLocationList, new MyComparator<>());
        }
    }

    //分析总计历史轨迹
    private List<PreferredLocation> totalHistoricalTrack(List<HistoricalTrack> historicalTrackList) {
        //存放分析后的历史轨迹
        List<PreferredLocation> preferredLocations = new ArrayList<>();
        for (HistoricalTrack historicalTrack : historicalTrackList) {
            //第一条历史记录直接放入偏爱列表中
            if (preferredLocations.size() == 0) {
                PreferredLocation perferredloaction = new PreferredLocation(historicalTrack.getStartPlaceName()
                        , historicalTrack.getEndPlaceName(), 1);
                preferredLocations.add(perferredloaction);
                //偏爱列表中只有一条数据，历史轨迹不需要与其比较，跳出从第二条开始
                continue;
            }
            //用来标记是否需要添加偏爱地点
            boolean addLocation = true;
            for (int i = 0; i < preferredLocations.size(); i++) {
                PreferredLocation preferredLocation = preferredLocations.get(i);
                //将历史轨迹与偏爱地点列表中的数据比对
                if ((historicalTrack.getStartPlaceName().equals(preferredLocation.startPlaceName)
                        && historicalTrack.getEndPlaceName().equals(preferredLocation.endPlaceName))) {
                    //如果已有数据，直接覆盖并修改次数
                    preferredLocations.set(i, new PreferredLocation(historicalTrack.getStartPlaceName()
                            , historicalTrack.getEndPlaceName(), ++preferredLocation.histortyTimes));
                    //如果有重复数据则不需要添加偏爱地点
                    addLocation = false;
                }
            }
            if (addLocation) {
                preferredLocations.add(new PreferredLocation(historicalTrack.getStartPlaceName()
                        , historicalTrack.getEndPlaceName(), 1));
            }
        }
        return preferredLocations;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    /**
     * 该类作为内部类存放偏爱地点数据
     */
    public class PreferredLocation {

        private String startPlaceName;
        private String endPlaceName;
        private int histortyTimes;

        public String getStartPlaceName() {
            return startPlaceName;
        }

        public String getEndPlaceName() {
            return endPlaceName;
        }

        public int getHistortyTimes() {
            return histortyTimes;
        }

        PreferredLocation(String startPlaceName, String endPlaceName, int histortyTimes) {
            this.startPlaceName = startPlaceName;
            this.endPlaceName = endPlaceName;
            this.histortyTimes = histortyTimes;
        }
    }
}
