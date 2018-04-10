package com.eug.r0b1c.r0b1block;


import android.view.MenuItem;

public class BleMenuState {
    private MenuStateE state = MenuStateE.DISCONNECTED;
    private MenuItem icon;

    public BleMenuState() {

    }

    public void SetMenuItem(MenuItem item) { this.icon = item; }

    public void SetConnected() {
        icon.setIcon(R.drawable.ic_ble_green_24dp);
        state = MenuStateE.CONNECTED;
    }

    public void SetDisonnected() {
        icon.setIcon(R.drawable.ic_ble_white_24dp);
        state = MenuStateE.DISCONNECTED;
    }

    // return true if scan required
    public boolean MenuTap() {
        switch (state) {
            case DISCONNECTED:
                icon.setIcon(R.drawable.ic_ble_scan_24dp);
                state = MenuStateE.SCANNING; // SCAN
                return true;
            case CONNECTED:
                icon.setIcon(R.drawable.ic_ble_grlock_24dp);
                state = MenuStateE.CONN_LOCK;
                break;
            case CONN_LOCK:
                icon.setIcon(R.drawable.ic_ble_white_24dp);
                state = MenuStateE.DISCONNECTED;
                break;
        }
        return false;
    }

    private enum MenuStateE {
        DISCONNECTED, // Disconnected, periodically scan for any device
        CONNECTED, // connected, but MAC not stored
        SCANNING,  // Scanning for device
        CONN_LOCK, // connected and locked to MAC
        DISC_LOCK; // disconnected with preferred MAC
    }
}
