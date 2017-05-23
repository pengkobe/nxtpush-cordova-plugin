package com.nxt.push.util;

import android.text.TextUtils;
import java.lang.reflect.Method;

/**
 *
 */
public class RomTypeUtil {
    private static final String KEY_EMUI_VERSION_CODE = "ro.build.version.emui";
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    //要反射的系统属性类name
    private static final String SYS_PROPERTIES_CLASS_TYPE = "android.os.SystemProperties";
    //反射函数名称
    private static final String SYS_PROPERTIES_METHOD_GET = "get";

    /**
     * 判断系统ROM是否为EMUI
     *
     * @return true:是EMUI false:不是EMUI
     */
    public static boolean isEMUI() {
        try {
            Class<?> classType = Class.forName(SYS_PROPERTIES_CLASS_TYPE);
            Method getMethod = classType.getDeclaredMethod(SYS_PROPERTIES_METHOD_GET, String.class);
            String emuiVersion = (String) getMethod.invoke(classType, KEY_EMUI_VERSION_CODE);
            return !TextUtils.isEmpty(emuiVersion);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断系统ROM是否为小米的MIUI
     *
     * @return true:是MIUI false:不是MIUI
     */
    public static boolean isMIUI() {
        try {
            Class<?> classType = Class.forName(SYS_PROPERTIES_CLASS_TYPE);
            Method getMethod = classType.getDeclaredMethod(SYS_PROPERTIES_METHOD_GET, String.class);
            String miuiName = (String) getMethod.invoke(classType, KEY_MIUI_VERSION_NAME);
            String miuiCode = (String) getMethod.invoke(classType, KEY_MIUI_VERSION_CODE);
            return !TextUtils.isEmpty(miuiName) || !TextUtils.isEmpty(miuiCode);
        } catch (Exception e) {
            return false;
        }
    }
}
