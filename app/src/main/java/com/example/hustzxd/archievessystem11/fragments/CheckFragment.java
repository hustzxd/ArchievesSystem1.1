package com.example.hustzxd.archievessystem11.fragments;

import android.app.Fragment;
import android.hardware.uhf.magic.reader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.hustzxd.archievessystem11.R;
import com.example.hustzxd.archievessystem11.Utils.Utils;

/**
 * 盘点的fragment
 * 可以进行扫描标签
 * Created by buxiaoyao on 2016/7/11.
 */
public class CheckFragment extends Fragment implements View.OnClickListener {

    private Button mCheckBtn;
    private Handler mHandler;
    private TextView mCheckTv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_check, container, false);


        mHandler = new MainHandler();
        reader.m_handler = mHandler;

        mCheckBtn = (Button) rootView.findViewById(R.id.btn_check);
        mCheckTv = (TextView) rootView.findViewById(R.id.tv_check);


        mCheckBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_check:
                //进行盘点
                Utils.log("ddd", "check");
                reader.InventoryLables();
                break;
            default:
                break;

        }
    }

    /**
     * 接收标签信息事件
     */
    private class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == reader.msgreadepc) {
                String data = (String) msg.obj;
                mCheckTv.setText(data);
            }
            super.handleMessage(msg);
        }
    }
}