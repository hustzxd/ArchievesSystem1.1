package com.example.hustzxd.archievessystem11.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

/**
 * 用户欢迎界面
 * 用户登录成功后，进入这个界面
 * Created by buxiaoyao on 2016/7/8.
 */
public class HelloFragment extends Fragment implements View.OnClickListener {

    private TextView mNameTv;
    private Button mLogoutBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hello, container, false);

        mNameTv = (TextView) rootView.findViewById(R.id.tv_username);
        mLogoutBtn = (Button) rootView.findViewById(R.id.btn_logout);

        mNameTv.setText(Constant.username);

        mLogoutBtn.setOnClickListener(this);
        return rootView;
    }

    private void volleyPost() {

        String url = Constant.URL_LOGOUT;
        Log.d("sss-url", url);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {//s为请求返回的字符串数据
                        Log.i("sss-response", s);
                        Utils.toast(getActivity(), "登出成功,返回到登录界面");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.i("sss-errorResponse", volleyError.toString());
                    }
                }) {
            //改写 cookie
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (MyApplication.cookies != null && MyApplication.cookies.length() > 0) {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("cookie", MyApplication.cookies);
                    Log.d("sss-cookie", headers.toString());
                    return headers;
                }
                return super.getHeaders();
            }
        };
        //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
        request.setTag("helloPost");
        //将请求加入全局队列中
        MyApplication.getmQueues().add(request);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logout:
                new AlertDialog.Builder(getActivity())
                        .setTitle("确认退出当前用户吗？")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                volleyPost();

                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            default:
                break;
        }
    }
}
