package com.baletu.plugin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.baletu.permissions.PermissionDenied;
import com.baletu.permissions.PermissionGranted;
import com.hous.library.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class UseFragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_fragment);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.fl_container, new MainFragment());
        transaction.commit();
    }

    @PermissionGranted
    private void call() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:18616372906"));
        startActivity(intent);
    }

    @PermissionDenied
    private void permissionDenied() {
        Toast.makeText(getApplicationContext(), "activity来处理拒绝", Toast.LENGTH_LONG).show();
    }
}
