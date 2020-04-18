package com.baletu.permissions.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;

/**
 * Created By Hous on 2019/11/18
 * 跳转设置页面
 */
public class PermissionSettingUtil {
    /**
     * Build.MANUFACTURER
     */
    //华为
    private static final String MANUFACTURER_HUAWEI = "Huawei";
    //魅族
    private static final String MANUFACTURER_MEIZU = "Meizu";
    //小米
    private static final String MANUFACTURER_XIAOMI = "Xiaomi";
    //索尼
    private static final String MANUFACTURER_SONY = "Sony";
    //OPPO
    private static final String MANUFACTURER_OPPO = "OPPO";
    //LG
    private static final String MANUFACTURER_LG = "LG";
    //VIVO
    private static final String MANUFACTURER_VIVO = "vivo";
    //乐视
    private static final String MANUFACTURER_LETV = "Letv";
    
    /**
     * request code
     */
    public static final int REQUEST_FOR_APPLY_PERMISSION = 0x100;
    
    
    /**
     * 此函数可以自己定义
     *
     * @param activity activity
     */
    public static void toPermissionSetting(@NonNull Activity activity) {
        switch (Build.MANUFACTURER) {
            case MANUFACTURER_HUAWEI:
                huawei(activity);
                break;
            case MANUFACTURER_MEIZU:
                meizu(activity);
                break;
            case MANUFACTURER_XIAOMI:
                xiaomi(activity);
                break;
            case MANUFACTURER_SONY:
                sony(activity);
                break;
            case MANUFACTURER_OPPO:
                OPPO(activity);
                break;
            case MANUFACTURER_VIVO:
                VIVO(activity);
                break;
            case MANUFACTURER_LG:
                LG(activity);
                break;
            case MANUFACTURER_LETV:
                letv(activity);
                break;
            default:
                applicationInfo(activity);
                break;
        }
    }
    
    /**
     * 华为权限设置页面
     *
     * @param activity activity
     */
    private static void huawei(Activity activity) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", activity.getPackageName());
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
            intent.setComponent(comp);
            activity.startActivityForResult(intent, REQUEST_FOR_APPLY_PERMISSION);
        } catch (Exception e) {
            applicationInfo(activity);
        }
    }
    
    /**
     * 魅族权限设置页面
     *
     * @param activity activity
     */
    private static void meizu(Activity activity) {
        try {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", activity.getPackageName());
            activity.startActivityForResult(intent, REQUEST_FOR_APPLY_PERMISSION);
        } catch (Exception e) {
            applicationInfo(activity);
        }
    }
    
    /**
     * 小米权限设置页面
     *
     * @param activity activity
     */
    private static void xiaomi(Activity activity) {
        try {
            Intent intent = new Intent();
//                intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            intent.setAction("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.putExtra("extra_pkgname", activity.getPackageName());
            activity.startActivityForResult(intent, REQUEST_FOR_APPLY_PERMISSION);
        } catch (Exception e) {
            applicationInfo(activity);
        }
    }
    
    /**
     * 索尼权限设置页面
     *
     * @param activity activity
     */
    private static void sony(Activity activity) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", activity.getPackageName());
            ComponentName comp = new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity");
            intent.setComponent(comp);
            activity.startActivityForResult(intent, REQUEST_FOR_APPLY_PERMISSION);
        } catch (Exception e) {
            applicationInfo(activity);
        }
    }
    
    /**
     * OPPO权限设置页面
     *
     * @param activity activity
     */
    private static void OPPO(Activity activity) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", activity.getPackageName());
            ComponentName comp = new ComponentName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
            intent.setComponent(comp);
            activity.startActivityForResult(intent, REQUEST_FOR_APPLY_PERMISSION);
        } catch (Exception e) {
            applicationInfo(activity);
        }
    }
    
    /**
     * VIVO 权限设置页面
     *
     * @param activity activity
     */
    private static void VIVO(Activity activity) {
        try {
            Intent open = activity.getPackageManager().getLaunchIntentForPackage("com.vivo.securedaemonservice");
            activity.startActivity(open);
        } catch (Exception e) {
            applicationInfo(activity);
        }
    }
    
    /**
     * LG权限设置页面
     *
     * @param activity activity
     */
    private static void LG(Activity activity) {
        try {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", activity.getPackageName());
            ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
            intent.setComponent(comp);
            activity.startActivityForResult(intent, REQUEST_FOR_APPLY_PERMISSION);
        } catch (Exception e) {
            applicationInfo(activity);
        }
    }
    
    /**
     * 乐视权限设置页面
     *
     * @param activity activity
     */
    private static void letv(Activity activity) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", activity.getPackageName());
            ComponentName comp = new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps");
            intent.setComponent(comp);
            activity.startActivityForResult(intent, REQUEST_FOR_APPLY_PERMISSION);
        } catch (Exception e) {
            applicationInfo(activity);
        }
    }
    
    /**
     * 360只能打开到自带安全软件
     *
     * @param activity activity
     */
    private static void _360(Activity activity) {
        try {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", activity.getPackageName());
            ComponentName comp = new ComponentName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
            intent.setComponent(comp);
            activity.startActivityForResult(intent, REQUEST_FOR_APPLY_PERMISSION);
        } catch (Exception e) {
            applicationInfo(activity);
        }
    }
    
    /**
     * 应用信息界面
     *
     * @param activity activity
     */
    private static void applicationInfo(Activity activity) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivityForResult(intent, REQUEST_FOR_APPLY_PERMISSION);
    }
}

