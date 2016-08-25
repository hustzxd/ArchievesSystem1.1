package com.example.hustzxd.archievessystem11.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.hardware.uhf.magic.reader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.hustzxd.archievessystem11.R;
import com.example.hustzxd.archievessystem11.Utils.Utils;
import com.example.hustzxd.archievessystem11.VolleyUtils.MyApplication;
import com.example.hustzxd.archievessystem11.constant.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 权证管理借出
 * 合一证
 * Created by ℃m on 2016/8/16.
 */
public class JiechuFragment extends Fragment implements View.OnClickListener {
    private Button mTjbtn2, mSmbtn2;
    private ImageButton mDpbtn2;
    private TextView mBqbh2, mQzm2, mQzbh2, mQzzt2, mKhjl2, mSyr2, mSfz2, mJcsj2;

    private DatePickerDialog mdialog;

    private int myear, mmon, mday;

    private Handler mHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_jiechu, container, false);

        mHandler = new MainHandler();
        reader.m_handler = mHandler;

        mTjbtn2 = (Button) rootView.findViewById(R.id.btn_tj2);
        mSmbtn2 = (Button) rootView.findViewById(R.id.btn_sm2);
        mDpbtn2 = (ImageButton) rootView.findViewById(R.id.btn_dp2);

        mBqbh2 = (TextView) rootView.findViewById(R.id.tv_bqbh2);
        mQzm2 = (TextView) rootView.findViewById(R.id.tv_qzm2);
        mQzbh2 = (TextView) rootView.findViewById(R.id.tv_qzbh2);
        mQzzt2 = (TextView) rootView.findViewById(R.id.tv_qzzt2);
        mKhjl2 = (TextView) rootView.findViewById(R.id.tv_khjl2);
        mSyr2 = (TextView) rootView.findViewById(R.id.tv_syr2);
        mSfz2 = (TextView) rootView.findViewById(R.id.tv_sfz2);
        mJcsj2 = (TextView) rootView.findViewById(R.id.tv_jcsj2);

        mTjbtn2.setOnClickListener(this);
        mSmbtn2.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        myear = calendar.get(Calendar.YEAR);
        mmon = calendar.get(Calendar.MONTH);
        mday = calendar.get(Calendar.DAY_OF_MONTH);
        mJcsj2.setText(new StringBuilder().append(myear).append("-")
                .append((mmon + 1) < 10 ? "0" + (mmon + 1) : (mmon + 1))
                .append("-")
                .append((mday < 10) ? "0" + mday : mday));
        mdialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mJcsj2.setText(new StringBuilder().append(year).append("-")
                        .append((month + 1) < 10 ? "0" + (month + 1) : (month + 1))
                        .append("-")
                        .append((day < 10) ? "0" + day : day));
            }
        }, myear, mmon, mday);

        mDpbtn2.setOnClickListener(new View.OnClickListener() {
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
            case R.id.btn_sm2:
                reader.InventoryLables();
                break;
            case R.id.btn_tj2:
                //提交
                final String EPC = mBqbh2.getText().toString();
                if (TextUtils.isEmpty(EPC)) {
                    mBqbh2.setError("请先扫描标签获取信息");
                    return;
                }
                String url = Constant.URL_LOAN;
                StringRequest request = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                Utils.log("借出信息返回", s);
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    int errorCode = jsonObject.getInt("errorCode");
                                    switch (errorCode) {
                                        case 0:
                                            Utils.toast(getActivity(), "借出成功");
                                            break;
                                        default:
                                            Utils.toast(getActivity(), "借出失败");
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Utils.log("借出申请错误", volleyError.getMessage());
                                Utils.toast(getActivity(), volleyError.getMessage());
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        //将请求参数名与参数值放入map中
                        map.put("EPC", EPC);
                        return map;
                    }
                };

                MyApplication.getmQueues().add(request);
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
                    mBqbh2.setText(data);
                    final String EPC = data;
                    Utils.log("EPC", data);
                    String url = Constant.URL_GETINFO;
                    //需要服务器返回
                    //权证名，权证编号，权证状态，客户经理，所有人，所有人身份证（6个信息）
                    StringRequest request = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String s) {
                                    Utils.log("借出信息返回", s);
                                    try {
                                        JSONObject jsonObject = new JSONObject(s);
                                        int errorCode = jsonObject.getInt("errorCode");
                                        switch (errorCode) {
                                            case 0:
                                                Utils.toast(getActivity(), "获取信息成功");
                                                String name = jsonObject.getString("name");
                                                String ID = jsonObject.getString("ID");
                                                String status = jsonObject.getString("status");
                                                String managerName = jsonObject.getString("managerName");
                                                String possessorName = jsonObject.getString("possessorName");
                                                String possessorIDCard = jsonObject.getString("possessorIDCard");

                                                mQzm2.setText(name);
                                                mQzbh2.setText(ID);
                                                mQzzt2.setText(status);
                                                mKhjl2.setText(managerName);
                                                mSyr2.setText(possessorName);
                                                mSfz2.setText(possessorIDCard);

                                                break;
                                            default:
                                                Utils.toast(getActivity(), "获取信息失败");
                                                break;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    Utils.log("借出信息获取错误", volleyError.getMessage());
                                    Utils.toast(getActivity(), volleyError.getMessage());
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map = new HashMap<>();
                            //将请求参数名与参数值放入map中
                            map.put("EPC", EPC);
                            return map;
                        }
                    };

                    MyApplication.getmQueues().add(request);
                    break;
            }
            super.handleMessage(msg);
        }
    }
}