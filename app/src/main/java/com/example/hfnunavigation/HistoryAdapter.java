package com.example.hfnunavigation;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.hfnunavigation.activity.MapActivity;
import com.example.hfnunavigation.db.HistoricalTrack;
import com.example.hfnunavigation.map.MyBaiduMap;
import com.example.hfnunavigation.util.StringConstant;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoricalTrack> historicalTrackList;
    private Map<Integer, Boolean> checkMap;
    private boolean checkBoxVisibility;
    private Context context;

    //使用viewHolder与item布局上的控件进行绑定
    class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView startPlaceName;
        TextView endPlaceName;
        TextView historyTime;

        ViewHolder(View itemView) {
            super(itemView);
            startPlaceName = itemView.findViewById(R.id.start_place_name);
            endPlaceName = itemView.findViewById(R.id.end_place_name);
            historyTime = itemView.findViewById(R.id.history_time);
            checkBox = itemView.findViewById(R.id.check_box);
        }
    }

    public HistoryAdapter(List<HistoricalTrack> historicalTrackList, Map<Integer, Boolean> checkMap
            , Context context) {
        this.historicalTrackList = historicalTrackList;
        this.checkMap = checkMap;
        this.context = context;
    }

    //创建视图
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent,
                false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxVisibility) {
                    if (holder.checkBox.isChecked()) {
                        checkMap.put(holder.getAdapterPosition(), false);
                    } else {
                        checkMap.put(holder.getAdapterPosition(), true);
                    }
                }
                if (checkMap.containsKey(holder.getAdapterPosition())) {
                    holder.checkBox.setChecked(checkMap.get(holder.getAdapterPosition()));
                } else {
                    MyBaiduMap myBaiduMap = MyBaiduMap.getMyBaiduMap();
                    myBaiduMap.setStartPlaceName(holder.startPlaceName.getText().toString());
                    myBaiduMap.setEndPlaceName(holder.endPlaceName.getText().toString());
                    if (myBaiduMap.checkStartAndEndPlace()) {
                        Intent intent = new Intent(context, MapActivity.class);
                        context.startActivity(intent);
                        myBaiduMap.startRoutePlaning();
                        EventBus.getDefault().post(new MessageEvent("发起路径规划"));
                    }
                }
            }

        });
        return holder;
    }


    /*         getPosition:为一开始创建视图时对应的位置，查阅资料后发现[已弃用]
               getLayoutPosition(); [条目在最新布局计算中的位置]
               getAdapterPosition(); [条目在是适配器中的位置]

               adapter和layout的位置会有时间差(<16ms), 如果你改变了Adapter的数据然后刷新视图,
               layout需要过一段时间才会更新视图, 在这段时间里面, 这两个方法返回的position会不一样.

               在notifyDataSetChanged之后并不能马上获取Adapter中的position, 要等布局结束之后才能获取到.

               对于Layout的position, 在notifyItemInserted之后, Layout不能马上获取到新的position,
               因为布局还没更新(需要<16ms的时间刷新视图), 所以只能获取到旧的,
               但是Adapter中的position就可以马上获取到最新的position.
               */
    //使用holder对控件进行绑定设置数据
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HistoricalTrack historicalTrack = historicalTrackList.get(position);
        if (historicalTrack.isCheckBoxVisibility()) {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }
        if (checkMap.containsKey(position)) {  //确保不会发生错位
            holder.checkBox.setChecked(checkMap.get(position));
        } else {
            holder.checkBox.setChecked(false);
        }
        holder.startPlaceName.setText(historicalTrack.getStartPlaceName());
        holder.endPlaceName.setText(historicalTrack.getEndPlaceName());
        Date historyTime = historicalTrack.getHistoryTime();
        Date currentTime = new Date();
        int[] analysisHistoryDateResult = analysisDate(historyTime);
        int[] analysisCurrentDateResult = analysisDate(currentTime);
        displayItemTime(holder, analysisHistoryDateResult, analysisCurrentDateResult);
    }

    /**
     * @param date 传入需要分析的日期
     * @return 返回一个字符数组，由年月日时分的具体数字组成
     */
    private int[] analysisDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日HH时mm分", Locale.CHINA);
        String timeString = dateFormat.format(date);
        String[] dateSegmentString = timeString.split("[^\\d]");   //匹配非数字
        int[] dateSegmentInt = new int[dateSegmentString.length];
        for (int i = 0; i < dateSegmentString.length; i++) {
            dateSegmentInt[i] = Integer.parseInt(dateSegmentString[i]);
        }
        return dateSegmentInt;
    }

    private void displayItemTime(ViewHolder holder, int[] analysisHistoryDateResult, int[] analysisCurrentDateResult) {
        for (int i = 0; i < analysisHistoryDateResult.length; i++) {
            if (analysisHistoryDateResult[i] != analysisCurrentDateResult[i]) {
                if (i == 0) {   //年份不同
                    //首先判断是否为相邻年份
                    if (analysisCurrentDateResult[i] - analysisHistoryDateResult[i] == 1) {
                        //再来判断是否为相邻月份
                        if (analysisCurrentDateResult[i + 1] == 1 &&
                                analysisHistoryDateResult[i + 1] == 12) {
                            //再来判断是否为相邻的一天
                            if (analysisCurrentDateResult[i + 2] == 1 &&
                                    analysisHistoryDateResult[i + 2] == 31) {
                                //再判断是否刚好为凌晨
                                if (analysisCurrentDateResult[i + 3] == 0 &&
                                        (analysisCurrentDateResult[i + 3] -
                                                analysisHistoryDateResult[i + 3] == -23)) {
                                    //判断是否一小时以内
                                    int duration = 60 + analysisCurrentDateResult[i + 4]
                                            - analysisHistoryDateResult[i + 4];
                                    //时长,单位：分钟
                                    if (duration <= 60) {
                                        holder.historyTime.setText(duration + StringConstant.BEFORE_MINUTES);
                                    } else {  //超过一小时显示时间戳
                                        holder.historyTime.setText(StringConstant.YESTERDAY +
                                                analysisHistoryDateResult[i + 3]
                                                + StringConstant.COLON + analysisHistoryDateResult[i + 4]);
                                    }
                                } else {
                                    holder.historyTime.setText(StringConstant.YESTERDAY +
                                            analysisHistoryDateResult[3]
                                            + StringConstant.COLON + analysisHistoryDateResult[4]);
                                }
                            }
                        }
                    } else {
                        holder.historyTime.setText(analysisHistoryDateResult[i] + StringConstant.YEAR
                                + analysisHistoryDateResult[i + 1] + StringConstant.MONTH
                                + analysisHistoryDateResult[i + 2] + StringConstant.DAY);
                    }
                    return;  //当年份不匹配时不再向下判断
                } else if (i == 1) {  //月份不同
                    //首先判断是否为相邻月份
                    if (analysisCurrentDateResult[i] - analysisHistoryDateResult[i] == 1) {
                        //再来判断是否为相邻的一天
                        if (analysisCurrentDateResult[i + 1] == 1 &&
                                (analysisHistoryDateResult[i + 1] == 31 ||
                                        analysisHistoryDateResult[i + 1] == 30 ||
                                        analysisHistoryDateResult[i + 1] == 29 ||
                                        analysisHistoryDateResult[i + 1] == 28)) {
                            //再判断是否刚好为凌晨
                            if (analysisCurrentDateResult[i + 2] == 0 &&
                                    (analysisCurrentDateResult[i + 2] -
                                            analysisHistoryDateResult[i + 2] == -23)) {
                                //判断是否一小时以内
                                int duration = 60 + analysisCurrentDateResult[i + 3] -
                                        analysisHistoryDateResult[i + 3];
                                //时长,单位：分钟
                                if (duration <= 60) {
                                    holder.historyTime.setText(duration + StringConstant.BEFORE_MINUTES);
                                } else {  //超过一小时显示时间戳
                                    holder.historyTime.setText(StringConstant.YESTERDAY +
                                            analysisHistoryDateResult[i + 2]
                                            + StringConstant.COLON + analysisHistoryDateResult[i + 3]);
                                }
                            } else {
                                holder.historyTime.setText(StringConstant.YESTERDAY + analysisHistoryDateResult[3]
                                        + StringConstant.COLON + analysisHistoryDateResult[4]);
                            }
                        }
                    } else {
                        holder.historyTime.setText(StringConstant.THIS_YEAR + analysisHistoryDateResult[i]
                                + StringConstant.MONTH + analysisHistoryDateResult[i + 1] + StringConstant.DAY);
                    }
                    return;   //当月份不匹配时不再向下判断
                } else if (i == 2) {    //天不同
                    if (analysisCurrentDateResult[i] - analysisHistoryDateResult[i] == 1) {
                        //判断是否刚好为凌晨
                        if (analysisCurrentDateResult[i + 1] == 0 &&
                                (analysisCurrentDateResult[i + 1] - analysisHistoryDateResult[i + 1] == -23)) {
                            int duration = 60 + analysisCurrentDateResult[i + 2] - analysisHistoryDateResult[i + 2];
                            //时长,单位：分钟
                            if (duration <= 60) {
                                holder.historyTime.setText(duration + StringConstant.BEFORE_MINUTES);
                            } else {  //超过一小时显示时间戳
                                holder.historyTime.setText(StringConstant.YESTERDAY + analysisHistoryDateResult[i + 1]
                                        + StringConstant.COLON + analysisHistoryDateResult[i + 2]);

                            }
                        } else {
                            holder.historyTime.setText(StringConstant.YESTERDAY + analysisHistoryDateResult[i + 1]
                                    + StringConstant.COLON + analysisHistoryDateResult[i + 2]);
                        }
                    } else {
                        holder.historyTime.setText(StringConstant.THIS_MONTH + analysisHistoryDateResult[i]
                                + StringConstant.DAY + analysisHistoryDateResult[i + 1] + StringConstant.HOUR);
                    }
                    return;   //当天数不匹配时不再向下判断
                } else if (i == 3) {   //小时不同
                    if (analysisCurrentDateResult[i] - analysisHistoryDateResult[i] == 1) {
                        //判断小时之差是否为1
                        int duration = 60 + analysisCurrentDateResult[i + 1] - analysisHistoryDateResult[i + 1];
                        //时长,单位：分钟
                        if (duration <= 60) {
                            holder.historyTime.setText(duration + StringConstant.BEFORE_MINUTES);
                        } else {  //超过一小时显示时间戳
                            holder.historyTime.setText(StringConstant.TODAY + analysisHistoryDateResult[i]
                                    + StringConstant.COLON + analysisHistoryDateResult[i + 1]);
                        }
                    } else {
                        holder.historyTime.setText(StringConstant.TODAY + analysisHistoryDateResult[i]
                                + StringConstant.COLON + analysisHistoryDateResult[i + 1]);
                    }
                    return;   //当小时不匹配时不再向下判断
                } else if (i == 4) {     //分钟不同
                    holder.historyTime.setText(analysisCurrentDateResult[i]
                            - analysisHistoryDateResult[i] + StringConstant.BEFORE_MINUTES);
                    return;  //当分钟不匹配时不再向下判断
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return historicalTrackList.size();
    }

    public boolean isCheckBoxVisibility() {
        return checkBoxVisibility;
    }

    public void setCheckBoxVisibility(boolean checkBoxVisibility) {
        this.checkBoxVisibility = checkBoxVisibility;
        if (checkBoxVisibility) {
            displayCheckBox();
        } else {
            hideCheckBox();
        }
    }

    private void displayCheckBox() {
        for (HistoricalTrack historicalTrack : historicalTrackList) {
            historicalTrack.setCheckBoxVisibility(true);
            // notifyItemChanged(historicalTrackList.indexOf(historicalTrack));
        }

        notifyDataSetChanged();
    }

    private void hideCheckBox() {
        for (HistoricalTrack historicalTrack : historicalTrackList) {
            historicalTrack.setCheckBoxVisibility(false);
            //notifyItemChanged(historicalTrackList.indexOf(historicalTrack));
        }
        notifyDataSetChanged();
    }

}
