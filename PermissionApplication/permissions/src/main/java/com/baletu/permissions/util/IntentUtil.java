package com.baletu.permissions.util;

import android.content.Intent;

/**
 * Created By Hous on 2020/4/17
 */
public class IntentUtil {
    
    /**
     * 获取Intent中int参数值
     *
     * @param intent        Intent
     * @param key           key值
     * @param default_vaule 默认值
     */
    public static int getIntentIntExtra(Intent intent, String key, int default_vaule) {
        if (intent != null && intent.hasExtra(key)) {
            return intent.getIntExtra(key, default_vaule);
        }
        return default_vaule;
    }
    
    /**
     * 获取Intent中Serializable参数值
     *
     * @param intent        Intent
     * @param key           key值
     * @param default_vaule 默认值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getIntentSerializabaleExtra(Intent intent, String key, T default_vaule) {
        if (intent != null && intent.hasExtra(key)) {
            return (T) intent.getSerializableExtra(key);
        }
        return default_vaule;
    }
    
    /**
     * 获取Intent中StringArray参数值
     *
     * @param intent Intent
     * @param key    key值
     */
    public static String[] getIntentStringArrayExtra(Intent intent, String key) {
        if (intent != null && intent.hasExtra(key)) {
            return intent.getStringArrayExtra(key);
        }
        return new String[]{};
    }
}
