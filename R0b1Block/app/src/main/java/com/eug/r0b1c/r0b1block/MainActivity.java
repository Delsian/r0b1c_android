package com.eug.r0b1c.r0b1block;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.Manifest;

import com.eug.r0b1c.r0b1block.upgrade.Upgrader;
import com.google.blockly.android.AbstractBlocklyActivity;
import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.google.blockly.model.DefaultBlocks;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AbstractBlocklyActivity {
    private static final String TAG = "MainActivity";
    private static final String TOOLBOX = "toolbox.xml";

    private BluetoothHandler bluetoothHandler;

    // Add custom blocks to this list.
    private static final List<String> BLOCK_DEFINITIONS = Arrays.asList(
            DefaultBlocks.LOGIC_BLOCKS_PATH,
            DefaultBlocks.LOOP_BLOCKS_PATH,
            DefaultBlocks.MATH_BLOCKS_PATH,
            DefaultBlocks.VARIABLE_BLOCKS_PATH,
            "Motor.json"
    );
    private static final List<String> JAVASCRIPT_GENERATORS = Arrays.asList(
            // Custom block generators go here. Default blocks are already included.
            "Motor.js"
    );
    private Handler mHandler;
    //private TextView mGeneratedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();

        // request permissions, BLE requires location
        int permissionCheck = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != 0) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        }
        bluetoothHandler = new BluetoothHandler(this);

        // Set upgrader context
        Upgrader up = Upgrader.getInstance();
        up.SetContext(this);
    }

    public BluetoothHandler getBluetoothHandler() {
        return bluetoothHandler;
    }

    @NonNull
    @Override
    protected String getToolboxContentsXmlPath() {
        return TOOLBOX;
    }

    @NonNull
    @Override
    protected List<String> getBlockDefinitionsJsonPaths() {
        return BLOCK_DEFINITIONS;
    }

    @NonNull
    @Override
    protected List<String> getGeneratorsJsPaths() {
        return JAVASCRIPT_GENERATORS;
    }

    CodeGenerationRequest.CodeGeneratorCallback mCodeGeneratorCallback =
            new CodeGenerationRequest.CodeGeneratorCallback() {
                @Override
                public void onFinishCodeGeneration(final String generatedCode) {
                    bluetoothHandler.SendCode(generatedCode);
                }
            };

    @Override
    protected View onCreateContentView(int parentId) {
        View root = getLayoutInflater().inflate(R.layout.activity_main, null);
        return root;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);
        bluetoothHandler.SetMenuItem(menu.findItem(R.id.action_ble));
        return true;
    }

    @Override
    protected CodeGenerationRequest.CodeGeneratorCallback getCodeGenerationCallback() {
        return mCodeGeneratorCallback;
    }

    @Override
    protected int getActionBarMenuResId() {
        return R.menu.actionbar;
    }

    @Override
    protected void onInitBlankWorkspace() {
        // Initialize available variable names.
        getController().addVariable("item");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == R.id.action_ble) {
            bluetoothHandler.MenuTap(item);
        }
        return true;
    }

    public void IsUpgradeRequired(String verOld, String verNew) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("New r0b1c version available: "+ verNew + "\nCurrent version "+verOld)
                .setTitle(R.string.upg_req);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Upgrader up = Upgrader.getInstance();
                up.DoUpgrade();
            }
        });
        builder.setNegativeButton(R.string.later, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.show();
    }
}
