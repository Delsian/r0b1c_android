package com.krashtan.eug.r0b1c;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.krashtan.eug.r0b1c.ble.BluetoothHandler;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MainActivity";
    private BluetoothHandler bluetoothHandler;
    private SetupFragment mSetupFragment;
    private ControlFragment mControlFragment;
    private ProgramFragment mProgramFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_setup:
                    selectedFragment = mSetupFragment;
                    break;
                case R.id.navigation_control:
                    selectedFragment = mControlFragment;
                    break;
                case R.id.navigation_program:
                    selectedFragment = mProgramFragment;
                    break;
                default:
                    return false;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, selectedFragment);
            transaction.commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSetupFragment = new SetupFragment();
        mSetupFragment.SetContext(this);
        mControlFragment = new ControlFragment();
        mProgramFragment = new ProgramFragment();
        bluetoothHandler = new BluetoothHandler(this);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, mSetupFragment);
        transaction.commit();
    }

    public BluetoothHandler getBluetoothHandler() {
        return bluetoothHandler;
    }
}
