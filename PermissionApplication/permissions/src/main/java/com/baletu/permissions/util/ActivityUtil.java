package com.baletu.permissions.util;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.baletu.permissions.IgnoreCallback;
import com.baletu.permissions.PermissionGranted;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created By Hous on 2020/4/17
 */
public class ActivityUtil {
    
    
    /**
     * 通过反射获取当前application的activity栈
     * <p>
     * 经源码分析LoadedApk中会持有一个ActivityThread的实例对象，可以通过该对象反射获取Activity栈
     *
     * @param application application
     * @return activity栈
     */
    @SuppressWarnings("unchecked")
    private static List<AppCompatActivity> getActivityStackByApplication(@NonNull Application application) {
        List<AppCompatActivity> stack = new ArrayList<>();
        try {
            //反射获取LoadedApk
            Class<Application> mApplicationClass = Application.class;
            Field mLoadedApkField = mApplicationClass.getDeclaredField("mLoadedApk");
            mLoadedApkField.setAccessible(true);
            Object mLoadedApk = mLoadedApkField.get(application);
            
            //反射获取LoadedApk中ActivityThread实例
            Class<?> mLoadedApkClass = mLoadedApk.getClass();
            Field mActivityThreadField = mLoadedApkClass.getDeclaredField("mActivityThread");
            mActivityThreadField.setAccessible(true);
            Object mActivityThread = mActivityThreadField.get(mLoadedApk);
            
            //反射获取ActivityThread中的mActivities
            Class<?> mActivityThreadClass = mActivityThread.getClass();
            Field mActivitiesField = mActivityThreadClass.getDeclaredField("mActivities");
            mActivitiesField.setAccessible(true);
            Object mActivities = mActivitiesField.get(mActivityThread);
            
            if (mActivities instanceof Map) {
                Map<Object, Object> map = (Map<Object, Object>) mActivities;
                for (Map.Entry<Object, Object> entry : map.entrySet()) {
                    Object value = entry.getValue();
                    Class<?> mActivityClientRecordClass = value.getClass();
                    Field mActivityField = mActivityClientRecordClass.getDeclaredField("activity");
                    mActivityField.setAccessible(true);
                    Object activity = mActivityField.get(value);
                    stack.add((AppCompatActivity) activity);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return stack;
    }
    
    /**
     * 获取调用权限申请类的实例对象
     */
    public static AppCompatActivity getCallerActivityInstance(@NonNull Application application, @NonNull String callerClassName) {
        List<AppCompatActivity> stack = getActivityStackByApplication(application);
        if (stack.isEmpty()) {
            return null;
        }
        for (AppCompatActivity activity : stack) {
            if (callerClassName.equals(activity.getClass().getSimpleName())) {
                return activity;
            }
        }
        throw new IllegalStateException("There is no caller activity found in the activity stack, Please check your code");
    }
    
    /**
     * activity如果有fragment，找到activity处理权限的fragment，如果没有则检查activity有没有处理
     *
     * @return 处理权限的fragment
     */
    public static Fragment findDeclaredCallbackFragment(@NonNull AppCompatActivity activity) {
        List<Fragment> fragments = activity.getSupportFragmentManager().getFragments();
        if (fragments.size() > 0) {
            for (Fragment parent : fragments) {
                if (parent.isVisible()) {
                    List<Fragment> mChildFragments = parent.getChildFragmentManager().getFragments();
                    if (mChildFragments.size() > 0) {
                        for (Fragment child : mChildFragments) {
                            //如果子fragment有处理权限申请成功后的回调并且没有忽略则返回
                            if (child.isVisible() && hasTargetAnnotation(child.getClass(), PermissionGranted.class) && !hasTargetAnnotation(child.getClass(), IgnoreCallback.class)) {
                                return child;
                            }
                        }
                    }
                    //如果子fragment没有处理，检查父fragment是否有处理
                    if (hasTargetAnnotation(parent.getClass(), PermissionGranted.class) && !hasTargetAnnotation(parent.getClass(), IgnoreCallback.class)) {
                        return parent;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * 判断是否相应的注解
     */
    private static boolean hasTargetAnnotation(@NonNull Class source, @NonNull Class<? extends Annotation> annotationClass) {
        Field[] fields = source.getDeclaredFields();
        for (Field field : fields) {
            Annotation annotation = field.getAnnotation(annotationClass);
            if (annotation != null) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取调用者的应用程序名
     */
    public static String getCallerApplicationName(@NonNull AppCompatActivity activity) {
        PackageManager pm = activity.getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
            return pm.getApplicationLabel(info).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
