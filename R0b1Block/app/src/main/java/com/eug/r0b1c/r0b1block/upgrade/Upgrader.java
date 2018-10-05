package com.eug.r0b1c.r0b1block.upgrade;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.eug.r0b1c.r0b1block.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class Upgrader {
    private static final String LOG_TAG = "Upgrader";
    private static final int MAX_DFU_ZIP_SIZE = 512*1024;
    private static Upgrader instance;
    private StorageReference mStorageRef;
    private Context mContext;
    private String mVersion = null;
    private String mFwVersion = null;
    private File localDfuZip;

    private Upgrader() {
    }

    public void SetContext(Context c) {
        mContext = c;
    }

    public static synchronized Upgrader getInstance() {
        if (instance == null) {
            instance = new Upgrader();
        }
        return instance;
    }

    private void GetCloudVersion() {
        if (mVersion == null) { // Get data from cloud only once
            try {
                mStorageRef = FirebaseStorage.getInstance().getReference();
                StorageReference gsVersion = mStorageRef.child("version.txt");
                gsVersion.getBytes(16).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        mVersion = new String(bytes);
                        Log.d(LOG_TAG, "Remote version: " + mVersion);
                        CheckUpgrade();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        mVersion = "0.0.0";
                    }
                });
            } catch (Exception e) {
                mVersion = "0.0.0"; // No internet connection
            }
        } else {
            CheckUpgrade(); // We already have new version
        }
    }

    private void CheckUpgrade() {
        if (mVersion != null && !mVersion.equalsIgnoreCase("0.0.0") && mFwVersion != null) {
            if (mVersion.compareTo(mFwVersion) > 0) {
                if (mContext != null) {
                    ((MainActivity)mContext).IsUpgradeRequired(mFwVersion,mVersion);
                }
            }
        }
    }

    public void DoUpgrade() {
        try {
            StorageReference gsFw = mStorageRef.child("dfu.zip");
            localDfuZip = new File( mContext.getCacheDir(),"dfu"+mVersion+".zip" );
            gsFw.getFile(localDfuZip).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d(LOG_TAG, "Got file "+localDfuZip.getAbsolutePath());

                    // Process DFU

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            });
        } catch (Exception e) {
            Log.d(LOG_TAG, "Err " + e.getLocalizedMessage());
        }
    }

    public void SetFwVersion(String version) {
        if (version != null) {
            mFwVersion = version;
        }
        GetCloudVersion();
    }
}
