package com.eug.r0b1c.r0b1block;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothHandler {
    private static final String LOG_TAG = "BluetoothHandler";

    private Context context;
    private boolean mEnabled = false;
    private BluetoothAdapter mBluetoothAdapter;
    private String mCurrentConnectedBLEAddr;
    private BleService mBleService;
    private BleMenuState menuState;
    private BleScanner mBleScanner;

    public BluetoothHandler(Context context) {
        this.context = context;
        mBluetoothAdapter = null;
        mCurrentConnectedBLEAddr = null;
        menuState = new BleMenuState();

        if (!isSupportBle()) {
            Toast.makeText(context, "your device not support BLE!", Toast.LENGTH_SHORT).show();
            ((MainActivity) context).finish();
            return;
        }
        // open bluetooth
        if (!getBluetoothAdapter().isEnabled()) {
            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((MainActivity) context).startActivityForResult(mIntent, 1);
        } else {
            setEnabled(true);
        }

        mBleScanner = new BleScanner(this);
    }


    public boolean isSupportBle(){
        // is support 4.0 ?
        final BluetoothManager bluetoothManager = (BluetoothManager)
                context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null)
            return false;
        else
            return true;
    }

    public BluetoothAdapter getBluetoothAdapter(){
        return mBluetoothAdapter;
    }

    public void setEnabled(boolean enabled){
        mEnabled = enabled;
    }

    public boolean isEnabled(){
        return mEnabled;
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             final byte[] scanRecord) {

        }
    };


    public void connect(String deviceAddress){
        mCurrentConnectedBLEAddr = deviceAddress;
        Intent gattServiceIntent = new Intent(context, BleService.class);

        if(!context.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)){
            System.out.println("bindService failed!");
        }

        //context.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBleService != null) {
            final boolean result = mBleService.connect(mCurrentConnectedBLEAddr);
            Log.i(LOG_TAG, "Connected to "+mCurrentConnectedBLEAddr+" "+result);
            if (!result) {
                mCurrentConnectedBLEAddr = null;
            }
        }else{
            System.out.println("mBleService = null");
        }
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBleService = ((BleService.LocalBinder) service).getService();
            if (!mBleService.initialize()) {
                Log.e("onServiceConnected", "Unable to initialize Bluetooth");
                ((MainActivity) context).finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBleService.connect(mCurrentConnectedBLEAddr);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBleService = null;
        }
    };

    /* Change menu icon according to state */
    public void MenuTap(MenuItem icon) {
        if (menuState.MenuTap()) {
            mBleScanner.scanLeDevice(true);
        }
    }
    public void SetMenuItem(MenuItem icon) {
        menuState.SetMenuItem(icon);
    }
    public BleMenuState GetMenyStateH () { return menuState; }
}
