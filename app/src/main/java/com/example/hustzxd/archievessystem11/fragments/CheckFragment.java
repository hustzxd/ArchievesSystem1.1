package com.example.hustzxd.archievessystem11.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.hustzxd.archievessystem11.R;

/**
 * 盘点的fragment
 * 可以进行扫描标签
 * Created by buxiaoyao on 2016/7/11.
 */
public class CheckFragment extends Fragment implements View.OnClickListener {

    private Button mCheckBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_check, container, false);

        mCheckBtn = (Button) rootView.findViewById(R.id.btn_check);
        mCheckBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_check:
                //进行盘点
                
                break;
            default:
                break;

        }
    }
}
