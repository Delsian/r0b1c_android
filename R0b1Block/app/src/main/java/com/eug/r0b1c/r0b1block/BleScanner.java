package com.eug.r0b1c.r0b1block;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BleScanner {
    private static final String LOG_TAG = "BleScanner";
    private static final long SCAN_PERIOD = 3000;
    //private static final String targetServiceUuid = "60ae973a-d019-4dd3-884f-96e834805f11";
    private static final String targetServiceUuid = "8ec9973a-f315-4f60-9fb8-838830daea50";
    //private static final String targetServiceUuid = "0000973a-0000-4f60-9fb8-838830daea50";
    private static final String targetServiceUuidMask = "0000FFFF-0000-0000-0000-000000000000";
    private BluetoothHandler bHandler;
    private ArrayList<BleDevice> mDevList;
    private boolean mScanning = false;
    private BleScanner.AddHandler mAddHandler;
    private final BluetoothLeScanner bluetoothLeScanner;
    private final ScanCallback mLeScanCallback;

    public BleScanner(BluetoothHandler bh) {
        bHandler = bh;
        mDevList = new ArrayList<BleDevice>();
        mAddHandler = new AddHandler();
        bluetoothLeScanner = bHandler.getBluetoothAdapter().getBluetoothLeScanner();

        mLeScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {

                super.onScanResult(callbackType, result);

                final BluetoothDevice bluetoothDevice = result.getDevice();
                final int rssi = result.getRssi();
                int newIndex = inList(bluetoothDevice.getAddress());
                if ( newIndex == -1) {
                    mDevList.add(new BleDevice(bluetoothDevice, rssi));
                    Log.i(LOG_TAG, "Add "+bluetoothDevice.getAddress());
                } else {
                    mDevList.set(newIndex, new BleDevice(bluetoothDevice, rssi));
                    Log.i(LOG_TAG, "Update "+bluetoothDevice.getAddress());
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                Log.i(LOG_TAG, "Batch "+results.size());
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };
    }

    public ArrayList<BleDevice> getDeviceList(){
        return mDevList;
    }

    private int inList(String addr) {
        int pos = -1;
        for(BleDevice d:mDevList){
            if(d.getDeviceAddr().equals(addr)){
                pos = mDevList.indexOf(d);
                break;
            }
        }
        return pos;
    }

    public void scanLeDevice(final boolean enable) {
        Log.d(LOG_TAG, "scanLeDevice: "+enable);
        if (enable) {
            if (!mScanning) {
                mDevList.clear();
                // Stops scanning after a pre-defined scan period.
                mAddHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        OnScanEnd();
                    }
                }, SCAN_PERIOD);
                ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
                bluetoothLeScanner.startScan(scanFilters(), settings, mLeScanCallback);
                //bluetoothLeScanner.startScan(mLeScanCallback);
                mScanning = true;
            }
        } else {
            bluetoothLeScanner.stopScan(mLeScanCallback);
            bluetoothLeScanner.flushPendingScanResults(mLeScanCallback);
            mScanning = false;
        }
    }

    private BleDevice getDevMaxRssi() {
        int pos = 0;
        int rssi = mDevList.get(0).getDeviceRssi();
        for(BleDevice d:mDevList){
            if(rssi > d.getDeviceRssi()){
                pos = mDevList.indexOf(d);
                rssi = d.getDeviceRssi();
            }
        }
        return mDevList.get(pos);
    }

    private void OnScanEnd() {
        scanLeDevice(false);
        BleMenuState mState = bHandler.GetMenuStateH();
        if (mDevList.size() > 0) {
            bHandler.connect(getDevMaxRssi());
        } else {
            mState.SetDisconnected();
        }
    }

    private List<ScanFilter> scanFilters() {
        ScanFilter filter = new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(targetServiceUuid), ParcelUuid.fromString(targetServiceUuidMask)).build();
        List<ScanFilter> list = new ArrayList<>();
        list.add(filter);
        return list;
    }

    private class AddHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}
