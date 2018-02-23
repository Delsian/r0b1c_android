package com.krashtan.eug.r0b1c;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.krashtan.eug.r0b1c.ble.BleDeviceSelector;
import com.krashtan.eug.r0b1c.ble.BluetoothHandler;

/**
 * Created by ekrashtan on 22.02.2018.
 */

public class SetupFragment extends Fragment {
    private static final String LOG_TAG = "SetupFragment";
    private Spinner mBleDevices;
    private BleDeviceSelector mSelector;
    private Context context;

    public void SetContext(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup, container, false);
        return view;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mBleDevices = view.findViewById(R.id.ble_devices);
        final BluetoothHandler bluetoothHandler = ((MainActivity)context).getBluetoothHandler();
        mSelector = new BleDeviceSelector(context,
                R.layout.ble_item_list, bluetoothHandler.getDeviceList());
        mBleDevices.setAdapter(mSelector);
        mBleDevices.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    bluetoothHandler.scanLeDevice(true);
                }
                return false;
            }
        });

        mBleDevices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String dev = ((TextView)view.findViewById(R.id.textViewDevName)).getText().toString();
                //mTextMessage.setText(dev);
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
