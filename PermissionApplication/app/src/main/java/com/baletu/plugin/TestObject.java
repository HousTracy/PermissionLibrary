package com.baletu.plugin;

import android.util.Log;

import com.baletu.permissions.PermissionDenied;
import com.baletu.permissions.PermissionGranted;

/**
 * Created By Hous on 2020/4/21
 */
public class TestObject {

    @PermissionGranted
    private void call() {
        Log.e("TestObject", "Object处理了成功回调");
    }

    @PermissionDenied
    private void permissionDenied() {
        Log.e("TestObject", "Object处理了失败回调");
    }

}
