package com.baletu.permissions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created By Hous on 2020/3/30
 * 权限拒绝
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PermissionDenied {
    
    /**
     * used for  multi group permission request in the same activity
     * and it should be equal to the code sent into the ApplyPermissionsActivity
     *
     * @see ApplyPermissionsActivity
     */
    int code() default 0;
}
