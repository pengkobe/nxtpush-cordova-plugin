package com.eegrid.phonegap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NXTPlugin extends CordovaPlugin {
    private final static List<String> methodList =
            Arrays.asList(
                    "areNotificationEnabled", 
                    "init",
                    "requestPermission",
                    "setAlias",
                    "setTags",
            );

    private ExecutorService threadPool = Executors.newFixedThreadPool(1);
    private static NXTPlugin instance;
    private static Activity cordovaActivity;
    private static String TAG = "NXTPlugin";

    public NXTPlugin() {
        instance = this;
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        Log.i(TAG, "NXTPush initialize.");
        super.initialize(cordova, webView);
        cordovaActivity = cordova.getActivity();
      
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cordovaActivity = null;
        instance = null;
    }

    @Override
    public boolean execute(final String action, final JSONArray data,
                           final CallbackContext callbackContext) throws JSONException {
        if (!methodList.contains(action)) {
            return false;
        }
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Method method = NXTPlugin.class.getDeclaredMethod(action,
                            JSONArray.class, CallbackContext.class);
                    method.invoke(NXTPlugin.this, data, callbackContext);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        });
        return true;
    }

    void init(JSONArray data, CallbackContext callbackContext) {
    }

    void setTags(JSONArray data, CallbackContext callbackContext) {
    }

    void setAlias(JSONArray data, CallbackContext callbackContext) {
    }

    /**
     * 专门用来执行来自 JPush 的执行请求
     */
    void static runJSOnUiThread(String js){
        cordovaActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                instance.webView.loadUrl("javascript:" + js);
            }
        });
    }

    public Activity getActivity(){
        return cordovaActivity;
    }



    /**
     * 用于 Android 6.0 以上系统申请权限，具体可参考：
     * http://docs.Push.io/client/android_api/#android-60
     */
    void requestPermission(JSONArray data, CallbackContext callbackContext) {
    }

    /**
     * 判断是否开启了通知权限
     */
    void areNotificationEnabled(JSONArray data, final CallbackContext callback) {
        int isEnabled;
        if (hasPermission("OP_POST_NOTIFICATION")) {
            isEnabled = 1;
        } else {
            isEnabled = 0;
        }
        callback.success(isEnabled);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean hasPermission(String appOpsServiceId) {
        Context context = cordova.getActivity().getApplicationContext();
        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();

        String pkg = context.getPackageName();
        int uid = appInfo.uid;
        Class appOpsClazz = null;

        try {
            appOpsClazz = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClazz.getMethod("checkOpNoThrow",
                    Integer.TYPE, Integer.TYPE, String.class);
            Field opValue = appOpsClazz.getDeclaredField(appOpsServiceId);
            int value = opValue.getInt(Integer.class);
            Object result = checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg);
            return Integer.parseInt(result.toString()) == AppOpsManager.MODE_ALLOWED;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }
}
