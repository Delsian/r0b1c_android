package com.krashtan.eug.r0b1c;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.krashtan.eug.r0b1c.ble.BleDeviceSelector;
import com.krashtan.eug.r0b1c.ble.BluetoothHandler;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MainActivity";
    private TextView mTextMessage;
    private BluetoothHandler bluetoothHandler;
    private Spinner mBleDevices;
    BleDeviceSelector mSelector;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    //mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    //mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = findViewById(R.id.message);
        mBleDevices = findViewById(R.id.ble_devices);
        bluetoothHandler = new BluetoothHandler(this);
        mSelector = new BleDeviceSelector(this,
                R.layout.ble_item_list, bluetoothHandler.getDeviceList());
        mBleDevices.setAdapter(mSelector);
        bluetoothHandler.scanLeDevice(true);
        mBleDevices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String dev = ((TextView)view.findViewById(R.id.textViewDevName)).getText().toString();
                //mTextMessage.setText(dev);
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mBleDevices.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    bluetoothHandler.scanLeDevice(true);
                }
                return false;
            }
        });

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void showMessage(String str){
        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    public void updateDevList() {
        mSelector.notifyDataSetChanged();
        Log.i(LOG_TAG, "update");
    }
}
