package com.eug.r0b1c.r0b1block;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.util.List;

public class R0b1cService {
    private static final String LOG_TAG = "R0b1cService";
    private BluetoothGattService mService;

    public R0b1cService(BluetoothGattService serv) {
        mService = serv;
        String uuid = null;
        List<BluetoothGattCharacteristic> gattCharacteristics =
                mService.getCharacteristics();
        // get targetGattCharacteristic
        for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
            uuid = gattCharacteristic.getUuid().toString();
            Log.i(LOG_TAG, "R0b1c char "+uuid);
        }
    }
}
