package com.example.chouyuhsien.hwai_ibeacon_prj_1029;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * Created by Chou Yu Hsien on 2017/10/29.
 */

public class BLEListAdapter extends BaseAdapter {
    private LinkedList<BLEList> mData;
    private Context mContext;
    public  BLEListAdapter(LinkedList<BLEList> mData,Context mContext){
        this.mData = mData;
        this.mContext = mContext;
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
        img_icon.setBackgroundResource(mData.get(position).getaIcon());
        ble_title.setText(mData.get(position).getaDeviceName());
        ble_content.setText(mData.get(position).getaContent());
        return convertView;
    }
}
