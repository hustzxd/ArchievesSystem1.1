package com.example.hustzxd.archievessystem11.fragments;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.hustzxd.archievessystem11.R;

import java.util.Calendar;

/**
 * 权证管理借出
 * Created by ℃m on 2016/8/16.
 */
public class XiaohuiFragment extends Fragment implements View.OnClickListener {
    private Button mTjbtn4, mSmbtn4;
    private ImageButton mDpbtn4;
    private TextView mBqbh4, mXhsj4, mQzm4, mQzbh4, mQzzt4, mCjsj4;

    private DatePickerDialog mdialog;

    private int myear, mmon, mday,mhour,mmin,msec;

    private Handler mHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_xiaohui, container, false);

        mHandler = new MainHandler();
        reader.m_handler = mHandler;

        mTjbtn4 = (Button) rootView.findViewById(R.id.btn_tj4);
        mSmbtn4 = (Button) rootView.findViewById(R.id.btn_sm4);
        mDpbtn4 = (ImageButton) rootView.findViewById(R.id.btn_dp4);

        mBqbh4 = (TextView) rootView.findViewById(R.id.tv_bqbh4);
        mXhsj4 = (TextView) rootView.findViewById(R.id.tv_xhsj4);
        mQzm4 = (TextView) rootView.findViewById(R.id.tv_qzm4);
        mQzbh4 = (TextView) rootView.findViewById(R.id.tv_qzbh4);
        mQzzt4 = (TextView) rootView.findViewById(R.id.tv_qzzt4);
        mCjsj4 = (TextView) rootView.findViewById(R.id.tv_cjsj4);

        mTjbtn4.setOnClickListener(this);
        mSmbtn4.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        myear = calendar.get(Calendar.YEAR);
        mmon = calendar.get(Calendar.MONTH);
        mday = calendar.get(Calendar.DAY_OF_MONTH);
        mhour = calendar.get(Calendar.HOUR_OF_DAY);
        mmin = calendar.get(Calendar.MINUTE);
        msec = calendar.get(Calendar.SECOND);

        mXhsj4.setText(new StringBuilder().append(myear).append("-")
                .append((mmon + 1) < 10 ? "0" + (mmon + 1) : (mmon + 1))
                .append("-")
                .append((mday < 10) ? "0" + mday : mday)
                .append(" ")
                .append(mhour < 10 ? 0 + mhour : mhour).append(":")
                .append(mmin < 10 ? 0 + mmin : mmin).append(":")
                .append(msec));
        mdialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mXhsj4.setText(new StringBuilder().append(year).append("-")
                        .append((month + 1) < 10 ? "0" + (month + 1) : (month + 1))
                        .append("-")
                        .append((day < 10) ? "0" + day : day)
                        .append(" ")
                        .append(mhour < 10 ? 0 + mhour : mhour).append(":")
                        .append(mmin < 10 ? 0 + mmin : mmin).append(":")
                        .append(msec));;
            }
        }, myear, mmon, mday);

        mDpbtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdialog.show();
            }
        });
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sm4:
                reader.InventoryLables();
                break;
            case R.id.btn_tj4:
                break;
            default:
                break;
        }
    }

    /**
     * 子类需要继承Hendler类
     * 重写handleMessage(Message msg) 方法
     * 接收线程数据
     */
    private class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String data = (String) msg.obj;
            switch (msg.what) {
                case reader.msgreadepc:
                    mBqbh4.setText(data);
                    break;
            }
            super.handleMessage(msg);
        }
    }
}