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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hfnunavigation.HistoryAdapter;
import com.example.hfnunavigation.util.MyComparator;
import com.example.hfnunavigation.MyDialogFragment;
import com.example.hfnunavigation.MyItemTouchHelperCallBack;
import com.example.hfnunavigation.MyItemTouchListener;
import com.example.hfnunavigation.R;
import com.example.hfnunavigation.db.HistoricalTrack;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HistoryActivity extends AppCompatActivity {

    private HistoryAdapter adapter;
    private MyItemTouchHelperCallBack itemTouchHelperCallBack;
    //该list保存数据库中排序后的数据，用来显示在recycleView中，
    private List<HistoricalTrack> historicalTrackList = new ArrayList<>();
    private Menu mMenu;
    //该map保存点击item后对应的checkBox位置和值
    private Map<Integer, Boolean> checkMap = new ConcurrentHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.history);
        toolbar.setTitleMarginStart(90 * 3);  //默认px为单位，测试机dp=3px
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initHistoricalTrack();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HistoryAdapter(historicalTrackList, checkMap, this);
        recyclerView.setAdapter(adapter);
        itemTouchHelperCallBack = new MyItemTouchHelperCallBack(recyclerView, historicalTrackList);
        itemTouchHelperCallBack.setItemDrag(false);
        ItemTouchHelper helper = new ItemTouchHelper(itemTouchHelperCallBack);
        helper.attachToRecyclerView(recyclerView);   //绑定目标RecyclerView对象
        recyclerView.addOnItemTouchListener(new MyItemTouchListener(recyclerView) {

            @Override
            public void onItemClick(View view, int position) {
                view.setBackgroundResource(R.drawable.item_click_bg);

            }

            @Override
            public void onItemLongPress(View view, int position) {
                if (!adapter.isCheckBoxVisibility()) {
                    showEditMenu();
                }
            }
        });
    }

    private void initHistoricalTrack() {
        //删除数据库中所有数据，用来调试程序
        //DataSupport.deleteAll(HistoricalTrack.class);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userID = preferences.getString("openid", null);
        //获取数据库中指定用户的数据，recycleView的数据源
        historicalTrackList = DataSupport.where("userID = ?", userID).find(HistoricalTrack.class);
        //该list保存数据库中的原始数据，用来调试查看数据以及确定删除数据是否正确
        List<HistoricalTrack> historicalTrackOriginList = new ArrayList<>(historicalTrackList);
        System.out.println("用户id：" + userID);
        for (HistoricalTrack historicalTrack : historicalTrackOriginList) {
            System.out.println("HistoricalTrack{" +
                    "id=" + historicalTrack.getId() +
                    ", userID='" + historicalTrack.getUserID() + '\'' +
                    ", startPlaceName='" + historicalTrack.getStartPlaceName() + '\'' +
                    ", endPlaceName='" + historicalTrack.getEndPlaceName() + '\'' +
                    ", historyTime=" + historicalTrack.getHistoryTime() + '}');
        }
        if (historicalTrackList.size() == 0) {
            TextView noHistory = findViewById(R.id.no_history);
            noHistory.setVisibility(View.VISIBLE);
        } else {
            //对数据源进行排序，这里recycleView需要排序后的数据
            Collections.sort(this.historicalTrackList, new MyComparator<HistoricalTrack>());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
        mMenu = menu;
        return true;
    }

    //隐藏菜单栏按钮
    private void hiddenEditMenu() {
        adapter.setCheckBoxVisibility(false);
        itemTouchHelperCallBack.setItemSwipe(true);
        if (null != mMenu) {
            for (int i = 0; i < mMenu.size(); i++) {
                mMenu.getItem(i).setVisible(false);

            }
        }
    }

    //显示菜单栏按钮
    private void showEditMenu() {
        adapter.setCheckBoxVisibility(true);
        itemTouchHelperCallBack.setItemSwipe(false);
        if (null != mMenu) {
            for (int i = 0; i < mMenu.size(); i++) {
                mMenu.getItem(i).setVisible(true);
                //mMenu.getItem(i).setEnabled(true);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.delete:
                final MyDialogFragment myDialogFragment = MyDialogFragment.getInstance(new MyDialogFragment.MyDialogCallBack() {
                    @Override
                    public String setMyDialogTitle() {
                        return "是否确认删除？";
                    }

                    @Override
                    public void OnDialogOkClick() {
                        int removeCount = 0;
                        //forEach遍历出checkMap中的键值对
                        for (Map.Entry<Integer, Boolean> entry : checkMap.entrySet()) {
                            if (entry.getValue()) {
                                int index = entry.getKey();   //获得值为true的key值
                                int dataBaseIndex;          //记录删除的数据在数据库中的index
                                System.out.println("要删除的index：" + index);
                                checkMap.remove(index);
                                if (removeCount == 0) {   //第一次删除时直接删除key值对应项，将同时删除源数据
                                    //计算删除的数据在数据库中的位置，通过删除的对象在数据中设定的id得到
                                    dataBaseIndex = historicalTrackList.get(index).getId();
                                    historicalTrackList.remove(index);
                                    adapter.notifyItemRemoved(index);
                                    System.out.println("列表中删除的位置：" + index);
                                    removeCount++;    //删除计数器加1
                                } else {    //删除index对应的数据，需要根据删除计数器计算出它在列表中的位置
                                    //通过删除的对象得到它在数据库中的id
                                    dataBaseIndex = historicalTrackList.get(index - removeCount).getId();
                                    historicalTrackList.remove(index - removeCount);
                                    adapter.notifyItemRemoved(index - removeCount);
                                    System.out.println("列表中删除的位置：" + (index - removeCount));
                                    removeCount++;
                                }
                                System.out.println("在数据库中的位置：" + dataBaseIndex);
                                DataSupport.delete(HistoricalTrack.class, dataBaseIndex);
                            }
                        }
                        if (removeCount != 0) {
                            Toast.makeText(HistoryActivity.this, "历史记录删除成功",
                                    Toast.LENGTH_SHORT).show();
                            hiddenEditMenu(); //删除成功隐藏菜单按钮
                        } else {
                            Toast.makeText(HistoryActivity.this, "请选择后再删除",
                                    Toast.LENGTH_SHORT).show();
                            //删除不成功菜单按钮不改变给用户选择是否继续删除
                        }
                    }
                });
                myDialogFragment.show(getSupportFragmentManager(), "delete");
                break;
            case R.id.canle_delete:
                checkMap.clear();
                hiddenEditMenu();
                break;
            case R.id.select_all:
                checkMap.clear();
                for (int position = 0; position < historicalTrackList.size(); position++) {
                    checkMap.put(position, true);
                    adapter.notifyItemChanged(position);
                }
                break;
        }
        return true;


    }

}
