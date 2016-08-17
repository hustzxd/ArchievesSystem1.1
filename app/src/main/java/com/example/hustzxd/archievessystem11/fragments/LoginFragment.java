package com.example.hustzxd.archievessystem11.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.hustzxd.archievessystem11.R;
import com.example.hustzxd.archievessystem11.Utils.Utils;
import com.example.hustzxd.archievessystem11.VolleyUtils.MyApplication;
import com.example.hustzxd.archievessystem11.constant.Constant;
import com.snad.loadingbutton.LoadingButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录fragment
 * Created by buxiaoyao on 2016/7/7.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {


    private HelloFragment mHelloFragment;

    private AutoCompleteTextView mUserNameEt;
    private EditText mPasswordEt;
    private LoadingButton mLoginBtn;

    private String mPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        mUserNameEt = (AutoCompleteTextView) rootView.findViewById(R.id.et_username);
        mPasswordEt = (EditText) rootView.findViewById(R.id.et_password);
        mLoginBtn = (LoadingButton) rootView.findViewById(R.id.btn_login);
        mLoginBtn.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:

                Constant.username = mUserNameEt.getText().toString();
                mPassword = mPasswordEt.getText().toString();

                if (TextUtils.isEmpty(Constant.username)) {
                    mUserNameEt.setError("请输入用户名");
                    return;
                }
                if (TextUtils.isEmpty(mPassword)) {
                    mPasswordEt.setError("请输入密码");
                    return;
                }
                volleyPost();
                mLoginBtn.showLoading();
                break;
            default:
                break;

        }
    }


    /**
     * 使用Post方式返回String类型的请求结果数据
     * <p/>
     * new StringRequest(int method,String url,Listener listener,ErrorListener errorListener)
     * method：请求方式，Get请求为Method.GET，Post请求为Method.POST
     * url：请求地址
     * listener：请求成功后的回调
     * errorListener：请求失败的回调
     */
    private void volleyPost() {


        String url = Constant.URL_LOGIN;
        Log.d("sss-url", url);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {//s为请求返回的字符串数据

                        Utils.log("登录返回正确", s);
                        try {
                            JSONObject resultJson = new JSONObject(s);
                            boolean result = resultJson.getBoolean("logstatus");
                            Utils.log("result", result + "");
                            if (result) {
                                Toast.makeText(getActivity(), "登录成功", Toast.LENGTH_SHORT).show();
                                Constant.isLogin = true;

                                mLoginBtn.setText("登录成功");
                                mLoginBtn.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mLoginBtn.showButtonText();
                                        FragmentManager fm = getFragmentManager();
                                        FragmentTransaction transaction = fm.beginTransaction();
                                        if (mHelloFragment == null) {
                                            mHelloFragment = new HelloFragment();
                                        }
                                        transaction.replace(R.id.fragment_content, mHelloFragment);
                                        transaction.commit();
                                    }
                                }, 1000);
                            } else {
                                mLoginBtn.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Utils.toast(getActivity(), "用户名或密码错误");
                                        mLoginBtn.setText("登录失败，重新登录");
                                        mLoginBtn.showButtonText();
                                    }
                                }, 1000);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                ,
                new Response.ErrorListener()

                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.i("sss-errorResponse", volleyError.toString());
                        Utils.toast(getActivity(), volleyError.getMessage());
                        mLoginBtn.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mLoginBtn.setText("登录失败，重新登录");
                                mLoginBtn.showButtonText();
                            }
                        }, 1000);
                    }
                }
        )

        {

            //删除 2016/8/16

//            @Override
//            protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                Response<String> superResponse = super.parseNetworkResponse(response);
//                Map<String, String> responseHeaders = response.headers;
//                String rawCookies = responseHeaders.get("Set-Cookie");
//                MyApplication.cookies = rawCookies.substring(0, rawCookies.indexOf(";"));
//                Log.d("sss", "sessionid---" + MyApplication.cookies);
//                return superResponse;
//            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                //将请求参数名与参数值放入map中
                map.put(Constant.USERNAME, Constant.username);
                map.put(Constant.PASSWORD, mPassword);
                map.put(Constant.MOBILE, Constant.TRUE);
                return map;
            }
        };
        //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
        request.setTag("loginPost");
        //将请求加入全局队列中
        MyApplication.getmQueues().

                add(request);
    }
}
