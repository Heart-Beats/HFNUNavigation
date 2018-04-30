package com.example.hfnunavigation;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class MyItemTouchListener implements RecyclerView.OnItemTouchListener {

   private GestureDetectorCompat mGestureDetectorCompat;
    private RecyclerView mRecyclerView;

/*    private float lastX ;
    private float lastY ;
    private long lastDownTime ;
    private boolean mIsLongPressed ;
    private boolean mIsSingleClick;*/

    protected MyItemTouchListener(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
       mGestureDetectorCompat = new GestureDetectorCompat(mRecyclerView.getContext(), new MyGestureListener());
    }

    ///**
    // *  判断是否有长按动作发生
    // * @param lastX         按下时X坐标
    // * @param lastY         按下时Y坐标
    // * @param thisX         移动时X坐标
    // * @param thisY         移动时Y坐标
    // * @param lastDownTime  按下时间
    // * @param thisEventTime 移动时间
    // * @param longPressTime 判断长按时间的阀值
    // */
/*    private boolean isLongPressed(float lastX, float lastY, float thisX,float thisY, long lastDownTime, long thisEventTime, long longPressTime) {
        float offsetX = Math.abs(thisX - lastX);
        float offsetY = Math.abs(thisY - lastY);
        //间隔时间
        long intervalTime = thisEventTime - lastDownTime;
        System.out.println("间隔时间"+intervalTime+"ms");
        if (offsetX <= 10 && offsetY <=10 && intervalTime >= longPressTime) {
            return true;
        }
        return false;
    }

    private boolean isSingleClick(long lastDownTime, long thisEventTime, long clickTime) {
        //间隔时间
        long intervalTime = thisEventTime - lastDownTime;
        if ( intervalTime <=clickTime) {
            return true;
        }
        return false;
    }*/

    //处理触摸事件
    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        //向GestureDetectorCompat传递MotionEvent为了获取触摸的坐标
       mGestureDetectorCompat.onTouchEvent(e);
    }

    //拦截触摸事件
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
/*        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                System.out.println("按下");
                lastX = e.getX();
                lastY = e.getY();
                lastDownTime = e.getDownTime();    // getDownTime() ：按下开始时间
                break;                             // getEventTime() ： 事件结束时间
            case MotionEvent.ACTION_MOVE:
                System.out.println("移动");
                if (!mIsLongPressed) {
                    mIsLongPressed = isLongPressed(lastX, lastY, e.getX(), e.getY(), lastDownTime, e.getEventTime(), 500);
                    System.out.println("mIsLongPressed："+mIsLongPressed);
                }

                break;
            case MotionEvent.ACTION_UP:
                System.out.println("松开");
                if (!mIsSingleClick) {
                    mIsSingleClick = isSingleClick( lastDownTime,e.getEventTime(), 150);
                    System.out.println("mIsSingleClick："+mIsSingleClick);
                }
                break;
        }

        if(mIsSingleClick ){
            //根据触摸坐标来获取childView
            View childView = rv.findChildViewUnder(e.getX(),e.getY());
            mIsSingleClick = false;
            if(childView != null){
                //回调onItemClick方法
                onItemClick(rv.getChildViewHolder(childView),rv.getChildLayoutPosition(childView));
                return true;
            }
            return false;
        }
        if( mIsLongPressed ){
            View childView = rv.findChildViewUnder(e.getX(),e.getY());
            mIsLongPressed = false;
            if(childView != null){
                //回调onItemClick方法
                onItemLongPress(childView,rv.getChildLayoutPosition(childView));
                return true;
            }
            return false;
        }*/

        //向GestureDetectorCompat传递MotionEvent为了获取触摸的坐标
        mGestureDetectorCompat.onTouchEvent(e);
        return false;
    }

    //处理触摸冲突
    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    //item点击回调
    public abstract void onItemClick(View view , int position);

    //item长按
    public abstract void onItemLongPress(View view , int position);

    //SimpleOnGestureListener是手势监听OnGestureListener简单的封装类
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        //点击屏幕
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View childView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            if (childView != null) {
              onItemClick(childView,mRecyclerView.getChildLayoutPosition(childView));
            }
            return true;
        }

        //长按屏幕
        @Override
        public void onLongPress(MotionEvent e) {
/*             getX()/getY()：获得事件发生时,触摸的中间区域的X/Y坐标，由这两个函数获得的X/Y值是相对坐标，
           相对于消费这个事件的视图的左上角的坐标。 */
            View childView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            if (childView != null) {
                onItemLongPress(childView,mRecyclerView.getChildLayoutPosition(childView));
            }
        }
    }
}
