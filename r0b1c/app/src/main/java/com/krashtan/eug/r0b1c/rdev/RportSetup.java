package com.krashtan.eug.r0b1c.rdev;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.krashtan.eug.r0b1c.R;

/**
 * Created by ekrashtan on 27.02.2018.
 */

public class RportSetup extends View {
    private static final String LOG_TAG ="RportSetup";
    private Context mContext;
    private Rports mConfig;
    private View mView;

    public RportSetup(Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        mView = inflater.inflate(R.layout.item_port_settings, null);
        Log.i(LOG_TAG, "X "+mView.getHeight()+" Y "+mView.getWidth());
    }

    public View getView() {
        return mView;
    }
}
