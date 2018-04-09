package com.eug.r0b1c.r0b1block;

import android.bluetooth.BluetoothDevice;

/**
 * Created by ekrashtan on 21.02.2018.
 */

public class BleDevice {
    private BluetoothDevice device;
    private int rssi;

    public BleDevice(BluetoothDevice dev, int r) {
        this.rssi = r;
        this.device = dev;
    }

    public String getDeviceName() {
        return (device!=null)?device.getName():"none";
    }

    public int getDeviceRssi() {
        return rssi;
    }

    public String getDeviceAddr() {
        return (device!=null)?device.getAddress():"00:00:00:00:00:00";
    }

    public void setDevice(BluetoothDevice dev) {
        this.device = dev;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
