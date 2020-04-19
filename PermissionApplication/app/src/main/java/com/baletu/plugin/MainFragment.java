package com.baletu.plugin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.baletu.permissions.ApplyPermissionManager;
import com.baletu.permissions.ApplyPermissionsActivity;
import com.baletu.permissions.PermissionDenied;
import com.baletu.permissions.PermissionGranted;
import com.hous.library.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created By Hous on 2020/4/17
 */
public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    private View view;
    private Activity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        Button btn_use_activity = view.findViewById(R.id.btn_use_activity);
        btn_use_activity.setOnClickListener(v -> ApplyPermissionsActivity.startApplyPermission(activity, new String[]{Manifest.permission.CALL_PHONE}));

        Button btn_use_fragment = view.findViewById(R.id.btn_use_fragment);
        btn_use_fragment.setOnClickListener(v -> ApplyPermissionManager.startApplyPermission(MainFragment.this, new String[]{Manifest.permission.CALL_PHONE}));
    }

    @PermissionGranted
    private void call() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:18616372906"));
        startActivity(intent);
    }

//    @PermissionDenied
//    private void permissionDenied() {
//        Toast.makeText(activity.getApplicationContext(), "fragmen来处理拒绝", Toast.LENGTH_LONG).show();
//    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }
}
