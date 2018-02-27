package com.krashtan.eug.r0b1c.rdev;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.krashtan.eug.r0b1c.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ekrashtan on 27.02.2018.
 */

public class RportsSelector extends ArrayAdapter<Rports> {
    private final Context mContext;
    private final List<Rports> items;
    private final LayoutInflater mInflater;
    private final int mResource;

    public RportsSelector(@NonNull Context context, int resource) {
        super(context, resource);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        items = new ArrayList<Rports>();
        for (Rports r : Rports.values()) {
            if (r.isInternal()) items.add(r);
        }
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
        TextView devAddrTv = (TextView) view.findViewById(R.id.textDevType);
        devAddrTv.setText(items.get(position).toString());
        return view;
    }

    public Rports getItem(int i) {
        return items.get(i);
    }
}
