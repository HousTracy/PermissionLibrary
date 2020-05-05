package com.baletu.permissions;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.baletu.permissions.util.ActivityUtil;
import com.baletu.permissions.util.PermissionSettingUtil;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created By Hous on 2020/4/17
 */
public class ApplyPermissionManager extends Fragment {

    /**
     * Fragment的Tag
     */
    private static final String FRAGMENT_TAG = "ApplyPermission";

    /**
     * 用来区分多种权限申请成功时的回调字段key值
     */
    private static final String KEY_REQUEST_CODE = "request_code";

    /**
     * 申请权限key值
     */
    private static final String KEY_PERMISSIONS = "permissions";


    /**
     * 申请的权限
     */
    private String[] permissions = new String[]{};

    /**
     * 当前fragment绑定的上下文
     */
    private AppCompatActivity activity;

    /**
     * 调用申请权限的回调类
     */
    private WeakReference<Object> source;

    /**
     * 用来区分多种权限申请成功时的回调
     */
    private int requesetCode = -1;

    /**
     * 缺少的权限
     */
    private String lackPermission;

    /**
     * 缺少权限时的提示
     */
    private Map<String, String> promptMap = new HashMap<>();


    /**
     * 申请权限
     *
     * @param target      调用者
     * @param permissions 申请的权限
     */
    public static void startApplyPermission(@NonNull Object target, String[] permissions) {
        AppCompatActivity source = null;
        if (target instanceof AppCompatActivity) {
            source = (AppCompatActivity) target;
        } else if (target instanceof Fragment) {
            source = (AppCompatActivity) ((Fragment) target).getActivity();
        }
        if (source == null) {
            throw new IllegalArgumentException("The source object is neither an activity or a fragment");
        }
        startApplyPermission(source, target, permissions);
    }

    /**
     * 申请权限
     *
     * @param source      调用者
     * @param target      回调拥有者
     * @param permissions 申请的权限
     */
    public static void startApplyPermission(@NonNull AppCompatActivity source, @NonNull Object target, String[] permissions) {
        startApplyPermission(source, target, permissions, -1);
    }

    /**
     * 申请权限
     *
     * @param target      回调拥有者
     * @param source      调用者
     * @param permissions 申请的权限
     * @param requestCode 申请权限成功后用来区分的code
     */
    public static void startApplyPermission(@NonNull AppCompatActivity source, @NonNull Object target, String[] permissions, int requestCode) {
        FragmentManager fm = source.getSupportFragmentManager();
        ApplyPermissionManager manager = (ApplyPermissionManager) fm.findFragmentByTag(FRAGMENT_TAG);
        if (manager == null) {
            manager = new ApplyPermissionManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(manager, FRAGMENT_TAG).commitAllowingStateLoss();
            fm.executePendingTransactions();
        }
        Bundle args = new Bundle();
        args.putStringArray(KEY_PERMISSIONS, permissions);
        args.putInt(KEY_REQUEST_CODE, requestCode);
        manager.setArguments(args);
        manager.init(target);
    }

    /**
     * 初始化
     *
     * @param source 调用申请权限的类
     */
    private void init(Object source) {
        this.source = new WeakReference<>(source);
        //接收参数
        Bundle bundle = getArguments();
        if (bundle != null) {
            permissions = bundle.getStringArray(KEY_PERMISSIONS);
            requesetCode = bundle.getInt(KEY_REQUEST_CODE, -1);
        }
        initLackPermissionPrompt();
        //申请权限
        apply();
    }

    /**
     * 缺少权限时的对应提示
     */
    private void initLackPermissionPrompt() {
        if (promptMap.size() == 0) {
            String applicationName = ActivityUtil.getCallerApplicationName(activity);
            //CAMERA权限组
            promptMap.put(Manifest.permission.CAMERA, String.format("您未允许%s获取手机相机权限，可前往系统设置中开启", applicationName));
            //CALENDAR权限组
            promptMap.put(Manifest.permission.READ_CALENDAR, String.format("您未允许%s获取手机日历权限，可前往系统设置中开启", applicationName));
            promptMap.put(Manifest.permission.WRITE_CALENDAR, String.format("您未允许%s获取手机日历权限，可前往系统设置中开启", applicationName));
            //CONTACTS权限组
            promptMap.put(Manifest.permission.READ_CONTACTS, String.format("您未允许%s获取读取通讯录权限，可前往系统设置中开启", applicationName));
            promptMap.put(Manifest.permission.WRITE_CONTACTS, String.format("您未允许%s获取读取通讯录权限，可前往系统设置中开启", applicationName));
            promptMap.put(Manifest.permission.GET_ACCOUNTS, String.format("您未允许%s获取读取通讯录权限，可前往系统设置中开启", applicationName));
            //LOCATION权限组
            promptMap.put(Manifest.permission.ACCESS_COARSE_LOCATION, String.format("您未允许%s获取定位权限，可前往系统设置中开启", applicationName));
            promptMap.put(Manifest.permission.ACCESS_FINE_LOCATION, String.format("您未允许%s获取定位权限，可前往系统设置中开启", applicationName));
            //STORAGE权限组
            promptMap.put(Manifest.permission.READ_EXTERNAL_STORAGE, String.format("您未允许%s获取SD卡权限，可前往系统设置中开启", applicationName));
            promptMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, String.format("您未允许%s获取SD卡权限，可前往系统设置中开启", applicationName));
            //PHONE权限组
            promptMap.put(Manifest.permission.CALL_PHONE, String.format("您未允许%s获取拨打电话权限，可前往系统设置中开启", applicationName));
            promptMap.put(Manifest.permission.READ_PHONE_STATE, String.format("您未允许%s获取读取手机设置权限，可前往系统设置中开启", applicationName));
            //SMS权限组
            promptMap.put(Manifest.permission.SEND_SMS, String.format("您未允许%s获取读取手机发送短信权限，可前往系统设置中开启", applicationName));
            promptMap.put(Manifest.permission.RECEIVE_SMS, String.format("您未允许%s获取读取收取短信权限，可前往系统设置中开启", applicationName));
            promptMap.put(Manifest.permission.READ_SMS, String.format("您未允许%s获取读取手机短信权限，可前往系统设置中开启", applicationName));
            //MICROPHONE权限组
            promptMap.put(Manifest.permission.RECORD_AUDIO, String.format("您未允许%s获取麦克风权限，可前往系统设置中开启", applicationName));
        }
    }


    /**
     * 申请权限
     */
    private void apply() {
        if (checkPermissions()) {
            findPermissionCallback(PermissionGranted.class);

        } else {
            //判断是否申请过权限
            //1.如果未申请，判断shouldShowRequestPermissionRationale的值，如果未勾选不再询问，则shouldShowRequestPermissionRationale为true，否则为false；
            //2.如果已申请，判断shouldShowRequestPermissionRationale的值，如果未勾选不再询问，则shouldShowRequestPermissionRationale为true，否则为false；
            //综上两者是异或关系
            if (!hasRequestPermission() ^ !hasPermissionRationale()) {
                if (!findPermissionCallback(PermissionDenied.class)) {
                    showPermissionSetting();
                }

            } else {
                requestPermissions(permissions, PermissionSettingUtil.REQUEST_FOR_APPLY_PERMISSION);
                updateRequestPermissionStatus(true);
            }
        }
    }


    /**
     * 检查所有需要的权限
     *
     * @return true为全部授权
     */
    private boolean checkPermissions() {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                lackPermission = permission;
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            findPermissionCallback(PermissionGranted.class);

        } else {
            //为false表示用户点了禁止但没有勾选不再询问
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, lackPermission)) {
                updatePermissionRationaleStatus(true);
            } else {
                if (!findPermissionCallback(PermissionDenied.class)) {
                    showPermissionSetting();
                }
            }
        }
    }

    /**
     * 调用者勾选了不再询问
     */
    private void updatePermissionRationaleStatus(boolean status) {
        if (activity.getIntent() == null) {
            Intent intent = new Intent();
            intent.putExtra("request_permission_rationale", status);
            activity.setIntent(intent);
        } else {
            activity.getIntent().putExtra("request_permission_rationale", status);
        }
    }

    /**
     * 获取调用者是否勾选了不再询问
     *
     * @return true 勾选了不再询问
     */
    private boolean hasPermissionRationale() {
        if (activity.getIntent() == null) {
            return false;
        }
        return activity.getIntent().getBooleanExtra("request_permission_rationale", false);
    }

    /**
     * 调用者已申请过权限
     */
    private void updateRequestPermissionStatus(boolean status) {
        if (activity.getIntent() == null) {
            Intent intent = new Intent();
            intent.putExtra("has_request_permission", status);
            activity.setIntent(intent);
        } else {
            activity.getIntent().putExtra("has_request_permission", status);
        }
    }

    /**
     * 获取调用者是否申请过权限
     *
     * @return true 申请过
     */
    private boolean hasRequestPermission() {
        if (activity.getIntent() == null) {
            return false;
        }
        return activity.getIntent().getBooleanExtra("has_request_permission", false);
    }

    /**
     * 弹出权限开启对话框
     */
    private void showPermissionSetting() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle("").setMessage(promptMap.get(lackPermission));
        dialog.setCancelable(false);
        dialog.setPositiveButton("去设置", (dial, which) -> {
            PermissionSettingUtil.toPermissionSetting(activity);
            dial.dismiss();
        });
        dialog.setNegativeButton("暂不", (dial, which) -> dial.dismiss());
        if (!activity.isFinishing()) {
            dialog.create().show();
        }
    }

    /**
     * 反射执行申请权限后的逻辑
     *
     * @return true为找到了注解方法
     */
    private boolean findPermissionCallback(@NonNull Class<? extends Annotation> annotationClass) {
        try {
            List<Method> methods = new ArrayList<>();
            for (Method method : source.get().getClass().getDeclaredMethods()) {
                Annotation annotation = method.getAnnotation(annotationClass);
                if (annotation != null) {
                    methods.add(method);
                }
            }
            if (methods.size() == 0) {
                return false;
            }
            if (methods.size() == 1) {
                Method method = methods.get(0);
                method.setAccessible(true);
                method.invoke(source.get());
                return true;
            }
            for (Method method : methods) {
                Annotation annotation = method.getAnnotation(annotationClass);
                if (annotation instanceof PermissionGranted) {
                    if (((PermissionGranted) annotation).code() == 0 && requesetCode == -1) {
                        throw new IllegalArgumentException(String.format("Annotation Code not found in method %s()", method.getName()));
                    }
                    if (((PermissionGranted) annotation).code() == requesetCode) {
                        method.setAccessible(true);
                        method.invoke(source.get());
                        return true;
                    }

                } else if (annotation instanceof PermissionDenied) {
                    if (((PermissionDenied) annotation).code() == 0 && requesetCode == -1) {
                        throw new IllegalArgumentException(String.format("Annotation Code not found in method %s()", method.getName()));
                    }
                    if (((PermissionDenied) annotation).code() == requesetCode) {
                        method.setAccessible(true);
                        method.invoke(source.get());
                        return true;
                    }
                }
            }

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();

        } finally {
            updateRequestPermissionStatus(false);
            updatePermissionRationaleStatus(false);
        }
        return false;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (AppCompatActivity) context;
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.activity = (AppCompatActivity) activity;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        source = null;
    }
}
