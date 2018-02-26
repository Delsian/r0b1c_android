package com.krashtan.eug.r0b1c.ble;

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
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.krashtan.eug.r0b1c.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothHandler {
    private static final String LOG_TAG = "BluetoothHandler";
    private static final long SCAN_PERIOD = 2000;
    private static final UUID targetServiceUuid =
            UUID.fromString("00001523-1212-efde-1523-785feabcd123");

    private Context context;
    private boolean mEnabled = false;
    private boolean mScanning = false;
    private AddHandler mAddHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private String mCurrentConnectedBLEAddr;
    private BleService mBleService;
    private ArrayList<BleDevice> mDevList;

    public BluetoothHandler(Context context) {
        this.context = context;
        mBluetoothAdapter = null;
        mCurrentConnectedBLEAddr = null;
        mAddHandler = new AddHandler();
        mDevList = new ArrayList<BleDevice>();

        if(!isSupportBle()){
            Toast.makeText(context, "your device not support BLE!", Toast.LENGTH_SHORT).show();
            ((MainActivity)context).finish();
            return ;
        }
        // open bluetooth
        if (!getBluetoothAdapter().isEnabled()) {
            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((MainActivity)context).startActivityForResult(mIntent, 1);
        }else{
            setEnabled(true);
        }
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

    public ArrayList<BleDevice> getDeviceList(){
        return mDevList;
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             final byte[] scanRecord) {

        }
    };

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

    public void scanLeDevice(final boolean enable, final ArrayAdapter adapter) {
        final ScanCallback mLeScanCallback = new ScanCallback() {

            @Override
            public void onScanResult(int callbackType, ScanResult result) {

                super.onScanResult(callbackType, result);

                final BluetoothDevice bluetoothDevice = result.getDevice();
                final int rssi = result.getRssi();
                int newIndex = inList(bluetoothDevice.getAddress());
                if ( newIndex == -1) {
                    mDevList.add(new BleDevice(bluetoothDevice, rssi));
                    Log.i(LOG_TAG, "Add "+bluetoothDevice.getAddress());
                    adapter.notifyDataSetChanged();
                } else {
                    mDevList.set(newIndex, new BleDevice(bluetoothDevice, rssi));
                    Log.i(LOG_TAG, "Update "+bluetoothDevice.getAddress());
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };

        final BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        Log.d(LOG_TAG, "scanLeDevice: "+enable);
        if (enable) {
            if (!mScanning) {
                mDevList.clear();
                adapter.notifyDataSetChanged();
                // Stops scanning after a pre-defined scan period.
                mAddHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bluetoothLeScanner.stopScan(mLeScanCallback);
                        mScanning = false;
                    }
                }, SCAN_PERIOD);
                bluetoothLeScanner.startScan(mLeScanCallback);
                mScanning = true;
            }
        } else {
            bluetoothLeScanner.stopScan(mLeScanCallback);
            mScanning = false;
        }
    }

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

    private class AddHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}
