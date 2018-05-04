package com.example.hfnunavigation;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hfnunavigation.activity.MapActivity;
import com.example.hfnunavigation.activity.PreferredLocationActivity;
import com.example.hfnunavigation.map.MyBaiduMap;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class PerferredLocationAdapter extends RecyclerView.Adapter<PerferredLocationAdapter.ViewHolder> {

    private List<PreferredLocationActivity.PreferredLocation> preferredLocationList;
    private Context context;

    //使用viewHolder与item布局上的控件进行绑定
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView startPlaceName;
        TextView endPlaceName;
        TextView historyTimes;

        ViewHolder(View itemView) {
            super(itemView);
            startPlaceName = itemView.findViewById(R.id.start_place_name);
            endPlaceName = itemView.findViewById(R.id.end_place_name);
            historyTimes = itemView.findViewById(R.id.history_times);
        }
    }

    public PerferredLocationAdapter(List<PreferredLocationActivity.PreferredLocation>
                                            preferredLocationList, Context context) {
        this.preferredLocationList = preferredLocationList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.preferred_location_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PreferredLocationActivity.PreferredLocation preferredLocation = preferredLocationList.get(position);
        holder.startPlaceName.setText(preferredLocation.getStartPlaceName());
        holder.endPlaceName.setText(preferredLocation.getEndPlaceName());
        holder.historyTimes.setText("共" + preferredLocation.getHistortyTimes() + "次");
    }

    @Override
    public int getItemCount() {
        return preferredLocationList.size();
    }
}
