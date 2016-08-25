package com.example.hustzxd.archievessystem11.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.hustzxd.archievessystem11.R;
import com.example.hustzxd.archievessystem11.Utils.IdCardCheckUtil;
import com.example.hustzxd.archievessystem11.Utils.Utils;
import com.example.hustzxd.archievessystem11.VolleyUtils.MyApplication;
import com.example.hustzxd.archievessystem11.constant.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 权证管理借出(土地证和房产证)
 * Created by ℃m on 2016/8/16.
 */
public class JieshouFragment extends Fragment implements View.OnClickListener {
    private Button mTjbtn0, mSmbtn00,mSmbtn01,mHeyi;
    private ImageButton mDpbtn0;
    private TextView mBq00,mBq01,mJssj0;

    private EditText mTdz0,mFcz0,mSyr0,mSfz0,mKhjl0;

    private DatePickerDialog mdialog;

    private int myear, mmon, mday;
    private int flag;

    private Handler mHandler;

    private JieshouFragment1 mJieshouFragment1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_jieshou, container, false);

        mHandler = new MainHandler();
        reader.m_handler = mHandler;

        mTjbtn0 = (Button) rootView.findViewById(R.id.btn_tj0);
        mSmbtn00 = (Button) rootView.findViewById(R.id.btn_sm00);
        mSmbtn01 = (Button) rootView.findViewById(R.id.btn_sm01);
        mHeyi = (Button) rootView.findViewById(R.id.btn_heyi);
        mDpbtn0 = (ImageButton) rootView.findViewById(R.id.btn_dp0);

        mTdz0 = (EditText) rootView.findViewById(R.id.et_tdz0);
        mFcz0 = (EditText) rootView.findViewById(R.id.et_fcz0);
        mBq00 = (TextView) rootView.findViewById(R.id.tv_bq00);
        mBq01 = (TextView) rootView.findViewById(R.id.tv_bq01);
        mSyr0 = (EditText) rootView.findViewById(R.id.et_syr0);
        mSfz0 = (EditText) rootView.findViewById(R.id.et_sfz0);
        mKhjl0 = (EditText) rootView.findViewById(R.id.et_khjl0);
        mJssj0 = (TextView) rootView.findViewById(R.id.tv_jssj0);

        mTjbtn0.setOnClickListener(this);
        mSmbtn00.setOnClickListener(this);
        mSmbtn01.setOnClickListener(this);
        mHeyi.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        myear = calendar.get(Calendar.YEAR);
        mmon = calendar.get(Calendar.MONTH);
        mday = calendar.get(Calendar.DAY_OF_MONTH);
        mJssj0.setText(new StringBuilder().append(myear).append("-")
                .append((mmon + 1) < 10 ? "0" + (mmon + 1) : (mmon + 1))
                .append("-")
                .append((mday < 10) ? "0" + mday : mday));
        mdialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mJssj0.setText(new StringBuilder().append(year).append("-")
                        .append((month + 1) < 10 ? "0" + (month + 1) : (month + 1))
                        .append("-")
                        .append((day < 10) ? "0" + day : day));
            }
        }, myear, mmon, mday);

        mDpbtn0.setOnClickListener(new View.OnClickListener() {
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
            case R.id.btn_sm00:
                flag = 0;
                reader.InventoryLables();
                break;
            case R.id.btn_sm01:
                flag = 1;
                reader.InventoryLables();
                break;
            case R.id.btn_heyi:
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                if (mJieshouFragment1 == null) {
                    mJieshouFragment1 = new JieshouFragment1();
                }
                transaction.replace(R.id.fragment_content, mJieshouFragment1);
                transaction.commit();
            case R.id.btn_tj0:
                //提交土地证和房产证接收信息
                //检查各个输入是否为空
                final String landName = mTdz0.getText().toString();
                if(TextUtils.isEmpty(landName)){
                    mTdz0.setError("请输入土地证名称");
                    return;
                }
                final String houseName = mFcz0.getText().toString();
                if(TextUtils.isEmpty(houseName)){
                    mFcz0.setError("请输入房产证名称");
                    return;
                }
                final String possessorName = mSyr0.getText().toString();
                if(TextUtils.isEmpty(possessorName)){
                    mSyr0.setError("请输入所有人姓名");
                    return;
                }
                final String IdCard = mSfz0.getText().toString();
                String errorInfo = IdCardCheckUtil.IDCardValidate(IdCard);
                if(!"".equals(errorInfo)){
                    mSfz0.setError(errorInfo);
                    return;
                }
                final String managerID = mKhjl0.getText().toString();
                if(TextUtils.isEmpty(managerID)){
                    mKhjl0.setError("请输入客户经理编号");
                    return;
                }
                final String landEPC = mBq00.getText().toString();
                if(TextUtils.isEmpty(landEPC)){
                    mBq00.setError("点击左侧按钮扫描土地证标签");
                    return;
                }
                final String houseEPC = mBq01.getText().toString();
                if(TextUtils.isEmpty(houseEPC)){
                    mBq01.setError("点击右侧按钮扫描房产证标签");
                    return;
                }
                final String receiveTime = mJssj0.getText().toString();
                String url = Constant.URL_RECEIVE_TWO;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                Utils.log("接收成功",s);
                                try {
                                    JSONObject json = new JSONObject(s);
                                    int errorCode = json.getInt("errorCode");
                                    switch (errorCode){
                                        case 0:
                                            Utils.toast(getActivity(),"提交成功");
                                            break;
                                        case -1:
                                            Utils.log("接收错误原因：",json.getString("errorInfo"));
                                            Utils.toast(getActivity(),"客户经理ID输入错误，或者未注册");
                                            break;
                                        case -2:
                                            Utils.log("接收错误原因：",json.getString("errorInfo"));
                                            Utils.toast(getActivity(),"EPC不能重复，扫描时请避免其他标签的干扰");
                                            break;
                                        default:
                                            Utils.toast(getActivity(),"未知错误");
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Utils.log("失败",volleyError.getMessage());
                        Utils.toast(getActivity(),volleyError.getMessage());
                    }

                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        //将请求参数名与参数值放入map中
                        map.put("landName", landName);
                        map.put("houseName",houseName);
                        map.put("ownerName",possessorName);
                        map.put("IdNumber",IdCard);
                        map.put("ManagerId",managerID);
                        map.put("landEPC",landEPC);
                        map.put("houseEPC",houseEPC);
                        map.put("receiveTime",receiveTime);
                        return map;
                    }
                };
                MyApplication.getmQueues().add(stringRequest);
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
                    if(flag == 0)
                    mBq00.setText(data);
                    else
                    mBq01.setText(data);
                    break;
            }
            super.handleMessage(msg);
        }
    }
}