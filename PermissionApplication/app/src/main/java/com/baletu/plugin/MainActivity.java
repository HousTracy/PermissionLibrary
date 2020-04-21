package com.baletu.plugin;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.baletu.permissions.ApplyPermissionManager;
import com.baletu.permissions.ApplyPermissionsActivity;
import com.baletu.permissions.PermissionDenied;
import com.baletu.permissions.PermissionGranted;
import com.baletu.permissions.util.ActivityUtil;
import com.hous.library.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_use_activity = findViewById(R.id.btn_use_activity);
        btn_use_activity.setOnClickListener(v -> ApplyPermissionsActivity.startApplyPermission(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}));


        Button btn_use_fragment = findViewById(R.id.btn_use_fragment);
        btn_use_fragment.setOnClickListener(v -> ApplyPermissionManager.startApplyPermission(MainActivity.this,new TestObject(MainActivity.this), new String[]{Manifest.permission.CALL_PHONE}));

        Button to_fragment = findViewById(R.id.to_fragment);
        to_fragment.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, UseFragmentActivity.class)));
    }

    @PermissionGranted
    private void call() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:XXXXX"));
        startActivity(intent);
    }

    @PermissionDenied
    private void permissionDenied() {
        Toast.makeText(getApplicationContext(), "activity来处理拒绝", Toast.LENGTH_LONG).show();
    }
}
