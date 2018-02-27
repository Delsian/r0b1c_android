package com.krashtan.eug.r0b1c;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.krashtan.eug.r0b1c.rdev.Rports;
import com.krashtan.eug.r0b1c.rdev.RportsSelector;

/**
 * Created by ekrashtan on 26.02.2018.
 */

public class PortSettings extends LinearLayout {
    private static final String LOG_TAG = "PortSettings";
    private Context mContext;
    private TextView mName;
    private Spinner mType;
    private Rports mPortType;

    public PortSettings(Context context) {
        super(context);
        mContext = context;
        mPortType = Rports.DUMMY;
        setOrientation(LinearLayout.VERTICAL);

        mName = new TextView(mContext);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.height = 60;
        mName.setTextSize(16);
        this.addView(mName, lp);
        final RportsSelector rs = new RportsSelector(context, R.layout.rport_spinner_item);
        mType = new Spinner(mContext);
        mType.setAdapter(rs);
        mType.setSelection(0);
        mType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPortType = rs.getItem(i);
                Log.i(LOG_TAG, "PortDev "+mPortType.toString()+" val "+mPortType.val());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        this.addView(mType);
    }

    public void SetPortParams(int port) {
        mName.setText("Port "+port);
    }
}
