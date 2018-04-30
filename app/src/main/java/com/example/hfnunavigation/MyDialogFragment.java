package com.example.hfnunavigation;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import java.io.Serializable;
import java.util.Objects;

public class MyDialogFragment extends android.support.v4.app.DialogFragment implements View.OnClickListener{

    private MyDialogCallBack myDialogCallBack;

    public interface MyDialogCallBack extends Serializable {

        String setMyDialogTitle(); //回调设置对话框标题

        void OnDialogOkClick();   ////回调处理确定按钮的点击事件
    }

    public static MyDialogFragment getInstance(MyDialogCallBack myDialogCallBack) {
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("CallBack",myDialogCallBack);
        myDialogFragment.setArguments(bundle);
        return myDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDialogCallBack =(MyDialogCallBack) getArguments().get("CallBack");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //设置弹出的对话框背景透明
            Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        //设置不可触摸取消
        getDialog().setCanceledOnTouchOutside(false);
        return inflater.inflate(R.layout.fragment_my_dialog,container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.dialog_cancle).setOnClickListener(this);
        view.findViewById(R.id.dialog_ok).setOnClickListener(this);
         TextView title = view.findViewById(R.id.title);
         title.setText(myDialogCallBack.setMyDialogTitle());
    }

    @Override
    public void onStart() {
        super.onStart();
/*        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams;
        if (window != null) {
            windowParams = window.getAttributes();
            windowParams.dimAmount = 0.0f;
            window.setAttributes(windowParams);
        }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_cancle:
                dismiss();
                break;
            case R.id.dialog_ok:
                myDialogCallBack.OnDialogOkClick();
                dismiss();
                break;
        }
    }
}
