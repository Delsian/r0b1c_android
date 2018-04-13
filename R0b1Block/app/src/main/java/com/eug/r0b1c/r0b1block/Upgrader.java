package com.eug.r0b1c.r0b1block;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.UUID;

public class Upgrader {
    private static final String LOG_TAG = "Upgrader";
    private static Upgrader instance;
    private StorageReference mStorageRef;

    private Upgrader() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public static synchronized Upgrader getInstance() {
        if (instance == null) {
            instance = new Upgrader();
        }
        return instance;
    }

    public boolean CheckVersion(String version) {

        return true;
    }
}
