package com.example.hustzxd.archievessystem11.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hustzxd.archievessystem11.Bean.TagInfo;
import com.example.hustzxd.archievessystem11.R;

import java.util.List;

/**
 * 显示标签信息listView的适配器
 * Created by buxiaoyao on 2016/7/13.
 */
public class MyAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private List<TagInfo> mList;


    public MyAdapter(Context context, List<TagInfo> list) {
        mLayoutInflater = LayoutInflater.from(context);
        mList = list;
    }

    public List<TagInfo> getList() {
        return mList;
    }

    public void setList(List<TagInfo> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_listview, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mEPC = (TextView) convertView.findViewById(R.id.tv_epc);
            viewHolder.mNum = (TextView) convertView.findViewById(R.id.tv_num);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TagInfo tagInfo = mList.get(position);
        viewHolder.mEPC.setText(tagInfo.getEPC());
        viewHolder.mNum.setText(tagInfo.getNum().toString());
        return convertView;
    }

    private class ViewHolder {
        TextView mEPC;
        TextView mNum;
    }
}
