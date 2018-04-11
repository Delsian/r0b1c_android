package com.eug.r0b1c.r0b1block;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Upgrader {
    private StorageReference mStorageRef;

    public Upgrader() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }
}
