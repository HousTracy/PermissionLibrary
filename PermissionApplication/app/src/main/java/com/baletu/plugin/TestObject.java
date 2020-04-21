package com.baletu.plugin;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.baletu.permissions.PermissionDenied;
import com.baletu.permissions.PermissionGranted;

/**
 * Created By Hous on 2020/4/21
 */
public class TestObject {

    private Activity activity;

    public TestObject(Activity activity) {
        this.activity = activity;
    }

    @PermissionGranted
    private void call() {
        Toast.makeText(activity.getApplicationContext(), "Object处理了成功回调", Toast.LENGTH_LONG).show();
    }

    @PermissionDenied
    private void permissionDenied() {
        Toast.makeText(activity.getApplicationContext(), "Object处理了拒绝回调", Toast.LENGTH_LONG).show();
    }

}
