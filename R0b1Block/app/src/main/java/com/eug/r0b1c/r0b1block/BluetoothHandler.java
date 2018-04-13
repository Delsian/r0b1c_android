package com.eug.r0b1c.r0b1block;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

    private static final UUID r0b1cServiceUuid =
            UUID.fromString("60ae973a-d019-4dd3-884f-96e834805f11");
    private static final UUID DfuServiceUuid =
            UUID.fromString("0000fe59-0000-1000-8000-00805f9b34fb"); // ToDo
    private static final UUID VersionServiceUuid =
            UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    private static final UUID VERSION_UUID =
            UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");


    private Context context;
    private boolean mEnabled = false;
    private BluetoothAdapter mBluetoothAdapter;
    private String mCurrentConnectedBLEAddr;
    private String mConnectingAddr = null;
    private List<BluetoothGattService> gattServices = null;
    private BleService mBleService;
    private BleMenuState menuState;
    private BleScanner mBleScanner;
    private R0b1cService mRService = null;
    private Upgrader mUpgrader = null;
    private BluetoothGattCharacteristic verChar;

    public BluetoothHandler(Context context) {
        this.context = context;
        mBluetoothAdapter = null;
        mCurrentConnectedBLEAddr = null;
        menuState = new BleMenuState(context);

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
        context.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
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

    public boolean SendCode(final String code) {
        if (mCurrentConnectedBLEAddr != null) {
            return true;
        }
        return false;
    }

    public void connect(BleDevice dev){
        mConnectingAddr = dev.getDeviceAddr();
        Log.i(LOG_TAG, "Connecting to "+mConnectingAddr);
        Intent gattServiceIntent = new Intent(context, BleService.class);

        if(!context.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)){
            Log.e(LOG_TAG, "bindService failed!");
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);
        return intentFilter;
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
            mBleService.connect(mConnectingAddr);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBleService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.i(LOG_TAG, "On receive "+action);
            if (BleService.ACTION_GATT_CONNECTED.equals(action)) {
                mCurrentConnectedBLEAddr = mConnectingAddr;
                menuState.SetConnected();
            } else if (BleService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mCurrentConnectedBLEAddr = null;
                menuState.SetDisconnected();
                context.unbindService(mServiceConnection);
            } else if (BleService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                if (mBleService != null)
                    getCharacteristic(mBleService.getSupportedGattServices());
            } else if (BleService.ACTION_DATA_AVAILABLE.equals(action)) {
                String dataChar = intent.getStringExtra(BleService.EXTRA_DATA);
                // Check current version
                if(dataChar.equals(VERSION_UUID.toString())){
                    String ver = verChar.getStringValue(0);
                    if (ver != null) {
                        Log.i(LOG_TAG, "Version " + ver);
                        Upgrader up = Upgrader.getInstance();
                        up.CheckVersion(ver);
                    }
                }
            }
        }
    };

    public void getCharacteristic(List<BluetoothGattService> gattServices){
        this.gattServices = gattServices;
        String uuid = null;
        // get target gattservice
        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            Log.i(LOG_TAG, "GATT service "+uuid);
            if(uuid.equals(r0b1cServiceUuid.toString())){
                mRService = new R0b1cService(gattService);
            } else if(uuid.equals(VersionServiceUuid.toString())) {
                List<BluetoothGattCharacteristic> gattCharacteristics =
                        gattService.getCharacteristics();
                // get targetGattCharacteristic
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    uuid = gattCharacteristic.getUuid().toString();
                    if(uuid.equals(VERSION_UUID.toString())){
                        verChar = gattCharacteristic;
                        mBleService.readCharacteristic(verChar);
                    }
                }
            }
        }
    }

    /* Change menu icon according to state */
    public void MenuTap(MenuItem icon) {
        if (menuState.MenuTap()) {
            if (mCurrentConnectedBLEAddr == null) {
                mBleScanner.scanLeDevice(true);
            } else {
                Log.i(LOG_TAG, "Disconnect from "+mCurrentConnectedBLEAddr);
                mBleService.disconnect();
            }
        }
    }
    public void SetMenuItem(MenuItem icon) {
        menuState.SetMenuItem(icon);
    }
    public BleMenuState GetMenuStateH() { return menuState; }
}
