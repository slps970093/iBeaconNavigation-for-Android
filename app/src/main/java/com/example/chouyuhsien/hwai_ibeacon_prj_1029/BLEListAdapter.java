package com.example.chouyuhsien.hwai_ibeacon_prj_1029;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Chou Yu Hsien on 2017/10/29.
 */

public class BLEListAdapter extends BaseAdapter {
    private LinkedList<HashMap<String,BLEList>> mData;
    private Context mContext;
    private ArrayList deviceName;
    public  BLEListAdapter(LinkedList<HashMap<String,BLEList>> mData,Context mContext,ArrayList list){
        this.mData = mData;
        this.mContext = mContext;
        this.deviceName = list;
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_blelist,parent,false);
        ImageView img_icon = (ImageView) convertView.findViewById(R.id.ble_icon);
        TextView ble_title = (TextView) convertView.findViewById(R.id.ble_title);
        TextView ble_content = (TextView) convertView.findViewById(R.id.ble_content);
        HashMap<String,BLEList> data = mData.get(position);// 取出HashMap
        img_icon.setBackgroundResource(data.get(deviceName.get(position)).getaIcon());
        ble_title.setText(data.get(deviceName.get(position)).getaDeviceName());
        ble_content.setText(data.get(deviceName.get(position)).getaContent());
        return convertView;
    }
}
