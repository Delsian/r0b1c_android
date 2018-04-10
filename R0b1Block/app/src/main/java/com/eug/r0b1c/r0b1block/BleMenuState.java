package com.eug.r0b1c.r0b1block;


import android.view.MenuItem;

public class BleMenuState {
    private MenuStateE state = MenuStateE.DISCONNECT;
    private MenuItem icon;

    public BleMenuState() {

    }

    public void SetMenuItem(MenuItem item) { this.icon = item; }

    public void SetConnect(boolean conn) {

    }

    public void MenuTap() {
        switch (state) {
            case DISCONNECT:
                icon.setIcon(R.drawable.ic_ble_green_24dp);
                state = MenuStateE.CONNECT; // SCAN
                break;
            case CONNECT:
                icon.setIcon(R.drawable.ic_ble_white_24dp);
                state = MenuStateE.DISCONNECT;
                break;
        }
    }

    private enum MenuStateE {
        DISCONNECT, CONNECT, SCAN;
    }
}
