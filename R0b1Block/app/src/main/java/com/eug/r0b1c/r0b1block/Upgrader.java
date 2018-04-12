package com.eug.r0b1c.r0b1block;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

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

    public boolean CheckVersion(BluetoothGattService serv) {
        String uuid = null;
        List<BluetoothGattCharacteristic> gattCharacteristics =
                serv.getCharacteristics();
        // get targetGattCharacteristic
        for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
            uuid = gattCharacteristic.getUuid().toString();
            Log.i(LOG_TAG, "Upgr char " + uuid);
        }
        return true;
    }
}
