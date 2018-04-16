package com.eug.r0b1c.r0b1block;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Upgrader {
    private static final String LOG_TAG = "Upgrader";
    private static Upgrader instance;
    private StorageReference mStorageRef;
    private Context mContext;
    private String mVersion = null;
    private String mFwVersion = null;

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
    
    }

    public void SetFwVersion(String version) {
        if (version != null) {
            mFwVersion = version;
        }
        GetCloudVersion();
    }
}
