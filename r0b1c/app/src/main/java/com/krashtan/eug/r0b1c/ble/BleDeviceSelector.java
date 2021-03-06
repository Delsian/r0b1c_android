package com.krashtan.eug.r0b1c.ble;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.krashtan.eug.r0b1c.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ekrashtan on 21.02.2018.
 */

public class BleDeviceSelector extends ArrayAdapter<String> {

    private final Context mContext;
    private final List<BleDevice> items;
    private final LayoutInflater mInflater;
    private final int mResource;

    public BleDeviceSelector(@NonNull Context context, @LayoutRes int resource,
                             @NonNull List objects) {
        super(context, resource, 0, objects);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        items = objects;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent){
        final View view = mInflater.inflate(mResource, parent, false);

        TextView devNameTv = (TextView) view.findViewById(R.id.textViewDevName);
        TextView devAddrTv = (TextView) view.findViewById(R.id.textViewDevAddr);
        ImageView imageView=(ImageView) view.findViewById(R.id.img_rssi);

        BleDevice deviceData = items.get(position);

        devNameTv.setText(deviceData.getDeviceName());
        devAddrTv.setText("[" + deviceData.getDeviceAddr()+"]");

        int rssi = deviceData.getDeviceRssi();
        int rescode = 0;
        if (rssi>-56) rescode = 4;
        else if (rssi > -75) rescode = 3;
        else if (rssi > -84) rescode = 2;
        int id = mContext.getResources().getIdentifier("signal" + rescode, "drawable", mContext.getPackageName());
        imageView.setImageResource(id);

        return view;
    }
}
