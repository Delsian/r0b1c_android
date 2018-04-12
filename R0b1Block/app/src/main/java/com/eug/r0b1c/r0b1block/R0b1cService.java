package com.eug.r0b1c.r0b1block;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.util.List;
import java.util.UUID;

public class R0b1cService {
    private static final String LOG_TAG = "R0b1cService";
    private BluetoothGattService mService;
    private UUID ProgCharUuid =
            UUID.fromString("60ae973b-d019-4dd3-884f-96e834805f11");
    private UUID PortCharUuid =
            UUID.fromString("60ae973c-d019-4dd3-884f-96e834805f11");
    private UUID ButtonCharUuid =
            UUID.fromString("60ae973e-d019-4dd3-884f-96e834805f11");
    private BluetoothGattCharacteristic mProgChar;
    private BluetoothGattCharacteristic mPortChar;
    private BluetoothGattCharacteristic mButtonChar;

    public R0b1cService(BluetoothGattService serv) {
        mService = serv;
        String uuid = null;
        List<BluetoothGattCharacteristic> gattCharacteristics =
                mService.getCharacteristics();
        // get targetGattCharacteristic
        for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
            uuid = gattCharacteristic.getUuid().toString();
            Log.i(LOG_TAG, "R0b1c char "+uuid);
            if(uuid.equals(ButtonCharUuid.toString())){
                mButtonChar = gattCharacteristic;
            } else if(uuid.equals(PortCharUuid.toString())) {
                mPortChar = gattCharacteristic;
            } else if(uuid.equals(ProgCharUuid.toString())) {
                mProgChar = gattCharacteristic;
            }
        }
    }

    public boolean R0b1cSendProgram(final String code) {
        if (mProgChar == null) {
            return false;
        } else {

            return true;
        }
    }
}
