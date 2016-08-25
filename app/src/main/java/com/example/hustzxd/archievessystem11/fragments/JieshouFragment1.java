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
 * 权证管理借出
 * Created by ℃m on 2016/8/16.
 */
public class JieshouFragment1 extends Fragment implements View.OnClickListener {
    private Button mTjbtn1, mSmbtn1,mHeyi1;
    private ImageButton mDpbtn1;
    private TextView mBq1,mJssj1;

    private EditText mHyz1,mSyr1, mSfz1,mKhjl1;

    private DatePickerDialog mdialog;

    private int myear, mmon, mday;

    private Handler mHandler;

    private JieshouFragment mJieshouFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_jieshou1, container, false);

        mHandler = new MainHandler();
        reader.m_handler = mHandler;

        mTjbtn1 = (Button) rootView.findViewById(R.id.btn_tj1);
        mSmbtn1 = (Button) rootView.findViewById(R.id.btn_sm1);
        mHeyi1 = (Button) rootView.findViewById(R.id.btn_heyi1);
        mDpbtn1 = (ImageButton) rootView.findViewById(R.id.btn_dp1);

        mHyz1 = (EditText) rootView.findViewById(R.id.et_hyz1);
        mBq1 = (TextView) rootView.findViewById(R.id.tv_bq1);
        mSyr1 = (EditText) rootView.findViewById(R.id.et_syr1);
        mSfz1 = (EditText) rootView.findViewById(R.id.et_sfz1);
        mKhjl1 = (EditText) rootView.findViewById(R.id.et_khjl1);
        mJssj1 = (TextView) rootView.findViewById(R.id.tv_jssj1);

        mTjbtn1.setOnClickListener(this);
        mSmbtn1.setOnClickListener(this);
        mHeyi1.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        myear = calendar.get(Calendar.YEAR);
        mmon = calendar.get(Calendar.MONTH);
        mday = calendar.get(Calendar.DAY_OF_MONTH);
        mJssj1.setText(new StringBuilder().append(myear).append("-")
                .append((mmon + 1) < 10 ? "0" + (mmon + 1) : (mmon + 1))
                .append("-")
                .append((mday < 10) ? "0" + mday : mday));
        mdialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mJssj1.setText(new StringBuilder().append(year).append("-")
                        .append((month + 1) < 10 ? "0" + (month + 1) : (month + 1))
                        .append("-")
                        .append((day < 10) ? "0" + day : day));
            }
        }, myear, mmon, mday);

        mDpbtn1.setOnClickListener(new View.OnClickListener() {
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
            case R.id.btn_sm1:
                reader.InventoryLables();
                break;
            case R.id.btn_heyi1:
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                if (mJieshouFragment == null) {
                    mJieshouFragment = new JieshouFragment();
                }
                transaction.replace(R.id.fragment_content, mJieshouFragment);
                transaction.commit();
            case R.id.btn_tj1:

                final String name = mHyz1.getText().toString();
                if(TextUtils.isEmpty(name)){
                    mHyz1.setError("请输入合一证名称");
                    return;
                }
                final String possessorName = mSyr1.getText().toString();
                if(TextUtils.isEmpty(possessorName)){
                    mSyr1.setError("请输入所有人姓名");
                    return;
                }
                final String IdCard = mSfz1.getText().toString();
                String errorInfo = IdCardCheckUtil.IDCardValidate(IdCard);
                if(!"".equals(errorInfo)){
                    mSfz1.setError(errorInfo);
                    return;
                }
                final String managerID = mKhjl1.getText().toString();
                if(TextUtils.isEmpty(managerID)){
                    mKhjl1.setError("请输入客户经理编号");
                    return;
                }
                final String EPC = mBq1.getText().toString();
                if(TextUtils.isEmpty(EPC)){
                    mHyz1.setError("请扫描合一证标签");
                    return;
                }
                final String receiveTime = mJssj1.getText().toString();
                String url = Constant.URL_RECEIVE_ONE;
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
                        map.put("Name", name);
                        map.put("ownerName",possessorName);
                        map.put("IdNumber",IdCard);
                        map.put("ManagerId",managerID);
                        map.put("EPC",EPC);
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
                        mBq1.setText(data);
                    break;
            }
            super.handleMessage(msg);
        }
    }
}