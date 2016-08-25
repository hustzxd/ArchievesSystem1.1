package com.example.hustzxd.archievessystem11.fragments;

import android.app.Fragment;
import android.hardware.uhf.magic.reader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.hustzxd.archievessystem11.Bean.TagInfo;
import com.example.hustzxd.archievessystem11.R;
import com.example.hustzxd.archievessystem11.Utils.Utils;

/**
 * 扫描单个标签
 * 读写单个标签内部块
 * Created by ℃m on 2016/7/16.
 */
public class ScanFragment extends Fragment implements View.OnClickListener{

    private Button mScanBtn,mScanBtnr,mScanBtnw,mModify;
    private Handler mHandler;
    private TextView mScanTv,mScanid,mScanr,mScanw,mModifyo,mModifyn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scan, container, false);

        mHandler = new MainHandler();
        reader.m_handler = mHandler;

        mScanBtn = (Button) rootView.findViewById(R.id.btn_scan);
        mScanBtnr = (Button) rootView.findViewById(R.id.btn_scanr);
        mScanBtnw = (Button) rootView.findViewById(R.id.btn_scanw);
        mModify = (Button) rootView.findViewById(R.id.btn_modify);

        mScanTv = (TextView) rootView.findViewById(R.id.tv_scan);
        mScanid = (TextView) rootView.findViewById(R.id.tv_scanid);
        mScanr = (TextView) rootView.findViewById(R.id.tv_scanr);
        mScanw = (TextView) rootView.findViewById(R.id.tv_scanw);
        mModifyo = (TextView) rootView.findViewById(R.id.tv_oldkey);
        mModifyn = (TextView) rootView.findViewById(R.id.tv_newkey);

        mScanBtn.setOnClickListener(this);
        mScanBtnr.setOnClickListener(this);
        mScanBtnw.setOnClickListener(this);
        mModify.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        //String mimaStr = "00000000";//默认密码00 00 00 00
        String oldkey = mModifyo.getText().toString().trim();
        String newkey = mModifyn.getText().toString().trim();

        int positiondata=3;//默认数据区为USER
        int positionkey=0;//密码在RFU区
        int nadd = Integer.valueOf("0").intValue();//默认USER区起始地址0
        int naddkey = Integer.valueOf("2").intValue();//RFU区，访问口令起始地址04H-07H
        int ndatalen=Integer.valueOf("12").intValue();//写入字符串长度默认最大12*2字节

        int keylen = Integer.valueOf("4").intValue();//密码长度为8位，4字节

        byte[] passw = reader.stringToBytes(oldkey);
        byte[] epc = reader.stringToBytes(reader.m_strPCEPC);
        byte btMemBank=(byte)positiondata;
        byte MemKey=(byte)positionkey;

        switch (v.getId()) {
            case R.id.btn_scan:
                //进行单个标签扫描
                Utils.log("ddd", "scanid");

                if (oldkey == null || oldkey.equals("")) {
                    mScanTv.setText( "Please enter your 8 - digit password!!");
                }else {
                    reader.InventoryLables();
                }

                break;
            case R.id.btn_scanr:
                //进行单个标签数据区读
                Utils.log("ddd", "scanr");


                if(reader.m_strPCEPC != null && reader.m_strPCEPC.length() > 0){
                    reader.ReadLables(passw, epc.length, epc, btMemBank, nadd, ndatalen);
                }else{
                mScanTv.setText("Please select Lables first");
            }
                break;
            case R.id.btn_scanw:
                //进行单个标签数据区写
                Utils.log("ddd", "scanw");

                if(reader.m_strPCEPC != null && reader.m_strPCEPC.length() > 0){
                    byte[] pwrite= new byte[ndatalen*2];
                    String dataE= mScanw.getText().toString().trim();
                    String dataU=strtouni(dataE);
                    byte[] myByte = reader.stringToBytes(dataU);
                    System.arraycopy(myByte, 0, pwrite, 0, myByte.length > ndatalen * 2?ndatalen * 2:myByte.length);
                    int aa = epc.length;
                    reader.Writelables(passw, epc.length, epc, btMemBank, nadd, ndatalen*2, pwrite );
                }else{
                    mScanTv.setText("Please select Lables first");
                }
                break;
            case R.id.btn_modify:
                //进行秘钥修改
                Utils.log("ddd", "modify");

                if(reader.m_strPCEPC != null && reader.m_strPCEPC.length() > 0){
                    byte[] newpassw = reader.stringToBytes(newkey);

                    byte[] keywirte = new byte[keylen];
                    System.arraycopy(newpassw, 0, keywirte, 0, newpassw.length > keylen?keylen:newpassw.length);

                    int bb = epc.length;
                    reader.Writelables(passw, epc.length, epc, MemKey, naddkey, keylen, keywirte );
                }else{
                    mScanTv.setText("Please select Lables first");
                }

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
            switch(msg.what) {
                case reader.msgreadepc:
                    mScanTv.setText(data);
                    mScanid.setText(data);
                    reader.m_strPCEPC = data;
                    break;
                case reader.msgreadwrireepc:
                    mScanTv.setText(data);
                    if(data.charAt(0)=='E'){
                        mScanr.setText(data);
                    }else {
                        int ed = data.indexOf("0000");
                        String tmp;
                        if (ed != -1) {
                            tmp = data.substring(0, ed);
                        } else {
                            tmp = data;
                        }
                        String dataE = unitostr(tmp);
                        mScanr.setText(dataE);
                    }
                    break;
                case reader.msgreadwrite:
                    mScanTv.setText(data);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    private String strtouni(String str)
    {
        str = (str == null ? "" : str);
        String tmp;
        StringBuffer sb = new StringBuffer(1000);
        char c;
        int i, j;
        sb.setLength(0);
        for (i = 0; i < str.length(); i++)
        {
            c = str.charAt(i);
            j = (c >>>8); //取出高8位
            j = j&0xFF;
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);
            j = (c & 0xFF); //取出低8位
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);

        }
        return (new String(sb));
    }

    public String unitostr (String str)
    {
        str = (str == null ? "" : str);
        //if (str.indexOf("\\u") == -1)//如果不是unicode码则原样返回
        //return str;

        StringBuffer sb = new StringBuffer(1000);

        for (int i = 0; i < str.length();)
        {
            String strTemp = str.substring(i, i + 4);
            String value = strTemp.substring(0);
            int c = 0;
            for (int j = 0; j < value.length(); j++)
            {
                char tempChar = value.charAt(j);
                int t = 0;
                switch (tempChar)
                {
                    case 'A':
                        t = 10;
                        break;
                    case 'B':
                        t = 11;
                        break;
                    case 'C':
                        t = 12;
                        break;
                    case 'D':
                        t = 13;
                        break;
                    case 'E':
                        t = 14;
                        break;
                    case 'F':
                        t = 15;
                        break;
                    default:
                        t = tempChar - 48;
                        break;
                }

                c += t * ((int) Math.pow(16, (value.length() - j - 1)));
            }
            sb.append((char) c);
            i = i + 4;
        }
        return sb.toString();
    }
}


