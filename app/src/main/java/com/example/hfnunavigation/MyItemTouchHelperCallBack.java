package com.example.hfnunavigation;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.Collections;
import java.util.List;

//借助该ItemTouchHelper.Callback与其中的方法就能很好的处理实现拖动、删除。
public class MyItemTouchHelperCallBack extends ItemTouchHelper.Callback {

    private RecyclerView mRecyclerView;
    private List mListData;
    private boolean itemDrag;

    public MyItemTouchHelperCallBack(RecyclerView recyclerView, List mListData) {
        this.mRecyclerView = recyclerView;
        this.mListData = mListData;
    }


    public void setItemDrag(boolean itemDrag) {
        this.itemDrag = itemDrag;
    }

    //该方法主要作用是定义移动标识，所以针对拖动效果，我们可以首先定义拖动标识,通过ItemTouchHelper提供的参数来定义：
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = 0;
        int swipeFlags = 0;
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager
                || recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN
                    | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        } else {
            if (itemDrag) {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            }
            // if (viewHolder.getAdapterPosition() != 0)
            swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    private void itemMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        System.out.println("长按，移动！");
        //viewHolder是起始位置，而target是目标位置
        int fromPosition = viewHolder.getAdapterPosition();  //在适配器中的位置，布局中可能和此不一样
        int toPosition = target.getAdapterPosition();
        if (fromPosition < toPosition)
            //向下拖动
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mListData, i, i + 1);  //交换数据位置
            }
        else {
            //向上拖动
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mListData, i, i - 1);
            }
        }
        recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
    }

    //定义完MovementFlags以后，在我们拖动的时候回调用onMove方法
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        itemMove(recyclerView,viewHolder,target);
        return true;
    }

    //定义完MovementFlags以后，在我们滑动的时候回调用onSwiped方法
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int positon = viewHolder.getAdapterPosition();
        mRecyclerView.getAdapter().notifyItemRemoved(positon);
        mListData.remove(positon);
    }

    //定义自己的拖动背景
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder.itemView.setScaleX(1.05f);  //放大view
            viewHolder.itemView.setScaleY(1.05f);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    //拖动完成之后会调用，用来清除背景状态。
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setScaleX(1.0f);   //还原view
        viewHolder.itemView.setScaleY(1.0f);
    }
}
