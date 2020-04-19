package com.baletu.permissions;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.WindowManager;

import com.baletu.permissions.util.ActivityUtil;
import com.baletu.permissions.util.IntentUtil;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

/**
 * Created By Hous on 2020/3/30
 * <p>
 * 使用Activity申请权限
 */
public class ApplyPermissionsActivity extends AppCompatActivity {


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
     * 调用申请权限的activity
     */
    private WeakReference<AppCompatActivity> activity;

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
     * 调用者的包名
     */
    private String callerPackageName;


    /**
     * 跳转至权限申请Activity
     *
     * @param activity    源activity
     * @param permissions 申请的权限
     */
    public static void startApplyPermission(@NonNull Activity activity, String[] permissions) {
        Intent intent = new Intent(activity, ApplyPermissionsActivity.class);
        intent.putExtra(KEY_PERMISSIONS, permissions);
        activity.startActivityForResult(intent, PermissionSettingUtil.REQUEST_FOR_APPLY_PERMISSION);
    }

    /**
     * 跳转至权限申请Activity
     *
     * @param activity    源activity
     * @param permissions 申请的权限
     * @param requestCode 申请权限成功后用来区分的code
     */
    public static void startApplyPermission(@NonNull Activity activity, String[] permissions, int requestCode) {
        Intent intent = new Intent(activity, ApplyPermissionsActivity.class);
        intent.putExtra(KEY_PERMISSIONS, permissions);
        intent.putExtra(KEY_REQUEST_CODE, requestCode);
        activity.startActivityForResult(intent, PermissionSettingUtil.REQUEST_FOR_APPLY_PERMISSION);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        init();
        apply();
    }

    private void init() {
        permissions = IntentUtil.getIntentStringArrayExtra(getIntent(), KEY_PERMISSIONS);
        requesetCode = IntentUtil.getIntentIntExtra(getIntent(), KEY_REQUEST_CODE, -1);
        activity = new WeakReference<>(getParentActivity());

        initLackPermissionPrompt();
    }

    /**
     * 缺少权限时的对应提示
     */
    private void initLackPermissionPrompt() {
        String applicationName = getCallerApplicationName();
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

    /**
     * 获取调用者的应用程序名
     */
    private String getCallerApplicationName() {
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(callerPackageName, PackageManager.GET_META_DATA);
            return pm.getApplicationLabel(info).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 申请权限
     */
    private void apply() {
        if (checkPermissions()) {
            findPermissionCallback(PermissionGranted.class);

        } else {
            if (!hasRequestPermission()) {
                ActivityCompat.requestPermissions(this, permissions, PermissionSettingUtil.REQUEST_FOR_APPLY_PERMISSION);
                updateRequestPermissionStatus();
            } else {
                if (hasPermissionRationale()) {
                    ActivityCompat.requestPermissions(this, permissions, PermissionSettingUtil.REQUEST_FOR_APPLY_PERMISSION);
                } else {
                    if (!findPermissionCallback(PermissionDenied.class)) {
                        showPermissionSetting();
                    }
                }
            }
        }
    }

    /**
     * 获取申请权限的类
     *
     * @return 申请权限的类
     */
    private AppCompatActivity getParentActivity() {
        if (getCallingActivity() != null) {
            int lastDotIndex = getCallingActivity().getClassName().lastIndexOf(".");
            String parentClassName = getCallingActivity().getShortClassName().substring(lastDotIndex + 1);
            callerPackageName = getCallingActivity().getPackageName();
            return ActivityUtil.getCallerActivityInstance(getApplication(), parentClassName);
        }
        throw new IllegalStateException("ApplyPermissionsActivity has no parent activity is not allowed!");
    }

    /**
     * 检查所有需要的权限
     *
     * @return true为全部授权
     */
    private boolean checkPermissions() {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(ApplyPermissionsActivity.this, lackPermission)) {
                updatePermissionRationaleStatus();
                finish();
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
    private void updatePermissionRationaleStatus() {
        if (activity.get().getIntent() == null) {
            Intent intent = new Intent();
            intent.putExtra("request_permission_rationale", true);
            activity.get().setIntent(intent);
        } else {
            activity.get().getIntent().putExtra("request_permission_rationale", true);
        }
    }

    /**
     * 获取调用者是否勾选了不再询问
     *
     * @return true 勾选了不再询问
     */
    private boolean hasPermissionRationale() {
        if (activity.get().getIntent() == null) {
            return false;
        }
        return activity.get().getIntent().getBooleanExtra("request_permission_rationale", false);
    }

    /**
     * 调用者已申请过权限
     */
    private void updateRequestPermissionStatus() {
        if (activity.get().getIntent() == null) {
            Intent intent = new Intent();
            intent.putExtra("has_request_permission", true);
            activity.get().setIntent(intent);
        } else {
            activity.get().getIntent().putExtra("has_request_permission", true);
        }
    }

    /**
     * 获取调用者是否申请过权限
     *
     * @return true 申请过
     */
    private boolean hasRequestPermission() {
        if (activity.get().getIntent() == null) {
            return false;
        }
        return activity.get().getIntent().getBooleanExtra("has_request_permission", false);
    }

    /**
     * 弹出权限开启对话框
     */
    private void showPermissionSetting() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("").setMessage(promptMap.get(lackPermission));
        dialog.setCancelable(false);
        dialog.setPositiveButton("去设置", (dial, which) -> {
            PermissionSettingUtil.toPermissionSetting(ApplyPermissionsActivity.this);
            finish();
        });
        dialog.setNegativeButton("暂不", (dial, which) -> {
            dial.dismiss();
            finish();
        });
        if (!isFinishing()) {
            dialog.create().show();
        }
    }

    /**
     * 反射执行申请权限后的逻辑
     *
     * @return true为找到了注解方法
     */
    private boolean afterApplyPermission(Class source, Object instance, @NonNull Class<? extends Annotation> annotationClass) {
        try {
            List<Method> methods = new ArrayList<>();
            for (Method method : source.getDeclaredMethods()) {
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
                method.invoke(instance);
                finish();
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
                        method.invoke(instance);
                        finish();
                        return true;
                    }

                } else if (annotation instanceof PermissionDenied) {
                    if (((PermissionDenied) annotation).code() == 0 && requesetCode == -1) {
                        throw new IllegalArgumentException(String.format("Annotation Code not found in method %s()", method.getName()));
                    }
                    if (((PermissionDenied) annotation).code() == requesetCode) {
                        method.setAccessible(true);
                        method.invoke(instance);
                        finish();
                        return true;
                    }
                }
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 寻找权限申请后的回调
     */
    private boolean findPermissionCallback(@NonNull Class<? extends Annotation> annotationClass) {
        Fragment fragment = ActivityUtil.findDeclaredCallbackFragment(activity.get());
        if (fragment == null) {
            return afterApplyPermission(activity.get().getClass(), activity.get(), annotationClass);

        } else {
            boolean result = afterApplyPermission(fragment.getClass(), fragment, annotationClass);
            //Fragment中未找到
            if (!result) {
                return afterApplyPermission(activity.get().getClass(), activity.get(), annotationClass);
            }
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
    }
}
