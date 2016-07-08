package com.example.hustzxd.archievessystem11.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hustzxd.archievessystem11.R;

/**
 * 用户欢迎界面
 * 用户登录成功后，进入这个界面
 * Created by buxiaoyao on 2016/7/8.
 */
public class HelloFragment extends Fragment {
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        return rootView;
    }


}
