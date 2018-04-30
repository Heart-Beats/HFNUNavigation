package com.example.hfnunavigation.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.hfnunavigation.HistoryAdapter;
import com.example.hfnunavigation.util.MyComparator;
import com.example.hfnunavigation.MyDialogFragment;
import com.example.hfnunavigation.MyItemTouchHelperCallBack;
import com.example.hfnunavigation.MyItemTouchListener;
import com.example.hfnunavigation.R;
import com.example.hfnunavigation.db.HistoricalTrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HistoryActivity extends AppCompatActivity {

    private HistoryAdapter adapter;
    private List<HistoricalTrack> historicalTrackList = new ArrayList<>();
    private Menu mMenu;
    private Map<Integer, Boolean> checkMap = new ConcurrentHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initHistory();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HistoryAdapter(historicalTrackList, checkMap, this);
        recyclerView.setAdapter(adapter);
        MyItemTouchHelperCallBack itemTouchHelperCallBack =
                new MyItemTouchHelperCallBack(recyclerView, historicalTrackList);
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


    private void initHistory() {
        HistoricalTrack historicalTrack1 = new HistoricalTrack();
        historicalTrack1.setStartPlaceName("北门");
        historicalTrack1.setEndPlaceName("行知楼");
        historicalTrack1.setHistoryTime(new Date(System.currentTimeMillis() - 1000 * 60 * 60 - 1000 * 60));
        HistoricalTrack historicalTrack2 = new HistoricalTrack();
        historicalTrack2.setStartPlaceName("博约楼");
        historicalTrack2.setEndPlaceName("逸夫楼");
        historicalTrack2.setHistoryTime(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24));
        HistoricalTrack historicalTrack3 = new HistoricalTrack();
        historicalTrack3.setStartPlaceName("8#");
        historicalTrack3.setEndPlaceName("荷园");
        historicalTrack3.setHistoryTime(new Date(System.currentTimeMillis() - 1000 * 60 * 30));
        historicalTrackList.add(historicalTrack1);
        historicalTrackList.add(historicalTrack2);
        historicalTrackList.add(historicalTrack3);
        historicalTrackList.add(historicalTrack1);
        historicalTrackList.add(historicalTrack2);
        historicalTrackList.add(historicalTrack3);
        historicalTrackList.add(historicalTrack1);
        historicalTrackList.add(historicalTrack2);
        historicalTrackList.add(historicalTrack3);
        historicalTrackList.add(historicalTrack1);
        historicalTrackList.add(historicalTrack2);
        historicalTrackList.add(historicalTrack3);
        Collections.sort(this.historicalTrackList, new MyComparator<HistoricalTrack>());
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
        if (null != mMenu) {
            for (int i = 0; i < mMenu.size(); i++) {
                mMenu.getItem(i).setVisible(false);

            }
        }
    }

    //显示菜单栏按钮
    private void showEditMenu() {
        adapter.setCheckBoxVisibility(true);
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
                                System.out.println("要删除的index：" + index);
                                checkMap.remove(index);
                                if (removeCount == 0) {   //第一次删除时直接删除key值对应项，将同时删除源数据
                                    historicalTrackList.remove(index);
                                    adapter.notifyItemRemoved(index);
                                    System.out.println("列表中删除的位置：" + index);
                                    removeCount++;    //删除计数器加1
                                } else {    //删除index对应的数据，需要根据删除计数器计算出它在列表中的位置
                                    historicalTrackList.remove(index - removeCount);
                                    adapter.notifyItemRemoved(index - removeCount);
                                    System.out.println("列表中删除的位置：" + (index - removeCount));
                                    removeCount++;
                                }
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
                myDialogFragment.show(getSupportFragmentManager(), "MyDialog");
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
