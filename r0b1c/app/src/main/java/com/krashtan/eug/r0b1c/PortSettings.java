package com.krashtan.eug.r0b1c;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by ekrashtan on 26.02.2018.
 */

public class PortSettings extends LinearLayout {
    private Context mContext;

    private TextView mName;
    private Spinner mType;

    public PortSettings(Context context) {
        super(context);
        mContext = context;
        setOrientation(LinearLayout.VERTICAL);

        mName = new TextView(mContext);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.height = 60;
        mName.setTextSize(16);
        this.addView(mName, lp);
        mType = new Spinner(mContext);
        this.addView(mType);
    }

    public void SetPortParams(int port) {
        mName.setText("Port "+port);
    }
}
