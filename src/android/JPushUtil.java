package com.eegrid.phonegap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.jpush.android.data.JPushLocalNotification;

public class JPushUtil  {
    private final static List<String> methodList =
            Arrays.asList(
                    "addLocalNotification",
                    "areNotificationEnabled",
                    "clearAllNotification",
                    "clearLocalNotifications",
                    "clearNotificationById",
                    "getNotification",
                    "getRegistrationID",
                    "init",
                    "isPushStopped",
                    "onPause",
                    "onResume",
                    "requestPermission",
                    "removeLocalNotification",
                    "reportNotificationOpened",
                    "resumePush",
                    "setAlias",
                    "setBasicPushNotificationBuilder",
                    "setCustomPushNotificationBuilder",
                    "setDebugMode",
                    "setLatestNotificationNum",
                    "setPushTime",
                    "setTags",
                    "setTagsWithAlias",
                    "setSilenceTime",
                    "setStatisticsOpen",
                    "stopPush"
            );

    private ExecutorService threadPool = Executors.newFixedThreadPool(1);
    private static JPushUtil instance;
    private static String TAG = "JPushUtil";

    private static boolean shouldCacheMsg = false;
    private static boolean isStatisticsOpened = false;    // 是否开启统计分析功能

    public static String notificationTitle;
    public static String notificationAlert;
    public static Map<String, Object> notificationExtras = new HashMap<String, Object>();

    public static String openNotificationTitle;
    public static String openNotificationAlert;
    public static Map<String, Object> openNotificationExtras = new HashMap<String, Object>();

    // cordova 上下文
    public static Context cordovaCxt;

    public JPushUtil() {
        instance = this;
    }

    public void InitPlugin(Context ctx) {
        Log.i(TAG, "JPush initialize.");
        cordovaCxt = ctx;
        JPushInterface.init(ctx);
        //如果同时缓存了打开事件 openNotificationAlert 和 消息事件 notificationAlert，只向 UI 发打开事件。
        //这样做是为了和 iOS 统一。
        if (openNotificationAlert != null) {
            notificationAlert = null;
            transmitNotificationOpen(openNotificationTitle, openNotificationAlert,
                    openNotificationExtras);
        }
        if (notificationAlert != null) {
            transmitNotificationReceive(notificationTitle, notificationAlert,
                    notificationExtras);
        }
    }

    public void onPause(boolean multitasking) {
        Log.i(TAG, "----------------  onPause");
        shouldCacheMsg = true;
        if (isStatisticsOpened && multitasking) {
            JPushInterface.onPause(NXTPlugin.getActivity());
        }
    }

    public void onResume(boolean multitasking) {
        shouldCacheMsg = false;
        Log.i(TAG, "---------------- onResume" + "-" + openNotificationAlert
                + "-" + notificationAlert);
        if (isStatisticsOpened && multitasking) {
            JPushInterface.onResume(NXTPlugin.getActivity());
        }
        if (openNotificationAlert != null) {
            notificationAlert = null;
            transmitNotificationOpen(openNotificationTitle, openNotificationAlert,
                    openNotificationExtras);
        }
        if (notificationAlert != null) {
            transmitNotificationReceive(notificationTitle, notificationAlert,
                    notificationExtras);
        }
    }

    private static JSONObject getMessageObject(String message,
                                               Map<String, Object> extras) {
        JSONObject data = new JSONObject();
        try {
            data.put("message", message);
            JSONObject jExtras = new JSONObject();
            for (Entry<String, Object> entry : extras.entrySet()) {
                if (entry.getKey().equals("cn.jpush.android.EXTRA")) {
                    JSONObject jo = null;
                    if (TextUtils.isEmpty((String) entry.getValue())) {
                        jo = new JSONObject();
                    } else {
                        jo = new JSONObject((String) entry.getValue());
                        String key;
                        Iterator keys = jo.keys();
                        while (keys.hasNext()) {
                            key = keys.next().toString();
                            jExtras.put(key, jo.getString(key));
                        }
                    }
                    jExtras.put("cn.jpush.android.EXTRA", jo);
                } else {
                    jExtras.put(entry.getKey(), entry.getValue());
                }
            }
            if (jExtras.length() > 0) {
                data.put("extras", jExtras);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    private static JSONObject getNotificationObject(String title,
                                                    String alert, Map<String, Object> extras) {
        JSONObject data = new JSONObject();
        try {
            data.put("title", title);
            data.put("alert", alert);
            JSONObject jExtras = new JSONObject();
            for (Entry<String, Object> entry : extras.entrySet()) {
                if (entry.getKey().equals("cn.jpush.android.EXTRA")) {
                    JSONObject jo;
                    if (TextUtils.isEmpty((String) entry.getValue())) {
                        jo = new JSONObject();
                    } else {
                        jo = new JSONObject((String) entry.getValue());
                        String key;
                        Iterator keys = jo.keys();
                        while (keys.hasNext()) {
                            key = keys.next().toString();
                            jExtras.put(key, jo.getString(key));
                        }
                    }
                    jExtras.put("cn.jpush.android.EXTRA", jo);
                } else {
                    jExtras.put(entry.getKey(), entry.getValue());
                }
            }
            if (jExtras.length() > 0) {
                data.put("extras", jExtras);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    static void transmitMessageReceive(String message, Map<String, Object> extras) {
        if (instance == null) {
            return;
        }
        JSONObject data = getMessageObject(message, extras);
        String format = "window.plugins.NXTPlugin.receiveMessageInAndroidCallback(%s);";
        final String js = String.format(format, data.toString());
        NXTPushPlugin.runJSOnUiThread(js);
    }

    static void transmitNotificationOpen(String title, String alert,
                                         Map<String, Object> extras) {
        if (instance == null) {
            return;
        }
        JSONObject data = getNotificationObject(title, alert, extras);
        String format = "window.plugins.NXTPlugin.openNotificationInAndroidCallback(%s);";
        final String js = String.format(format, data.toString());
        NXTPushPlugin.runJSOnUiThread(js);
        JPushUtil.openNotificationTitle = null;
        JPushUtil.openNotificationAlert = null;
    }

    static void transmitNotificationReceive(String title, String alert,
                                            Map<String, Object> extras) {
        if (instance == null) {
            return;
        }
        JSONObject data = getNotificationObject(title, alert, extras);
        String format = "window.plugins.NXTPlugin.receiveNotificationInAndroidCallback(%s);";
        final String js = String.format(format, data.toString());
        NXTPushPlugin.runJSOnUiThread(js);
        JPushUtil.notificationTitle = null;
        JPushUtil.notificationAlert = null;
    }

    static void transmitReceiveRegistrationId(String rId) {
        if (instance == null) {
            return;
        }
        JSONObject data = new JSONObject();
        try {
            data.put("registrationId", rId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String format = "window.plugins.NXTPlugin.receiveRegistrationIdInAndroidCallback(%s);";
        final String js = String.format(format, data.toString());
        NXTPushPlugin.runJSOnUiThread(js);
    }


    void init(JSONArray data, CallbackContext callbackContext) {
        JPushInterface.init(cordovaCxt);
    }

    void setDebugMode(JSONArray data, CallbackContext callbackContext) {
        boolean mode;
        try {
            mode = data.getBoolean(0);
            JPushInterface.setDebugMode(mode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void stopPush(JSONArray data, CallbackContext callbackContext) {
        JPushInterface.stopPush(cordovaCxt);
    }

    void resumePush(JSONArray data, CallbackContext callbackContext) {
        JPushInterface.resumePush(cordovaCxt);
    }

/**
 * 极光是否停止了推送
 */
    boolean isPushStopped(JSONArray data, CallbackContext callbackContext) {
        boolean isStopped = JPushInterface.isPushStopped(
                cordovaCxt);
       return isStopped;  
    }


/**
 * 设置最新通知数目
 */
    void setLatestNotificationNum(JSONArray data, CallbackContext callbackContext) {
        int num = -1;
        try {
            num = data.getInt(0);
        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.error("error reading num json");
        }
        if (num != -1) {
            JPushInterface.setLatestNotificationNumber(
                    cordovaCxt, num);
        } else {
            callbackContext.error("error num");
        }
    }

/**
 * 设置推送时间
 */
    void setPushTime(JSONArray data, CallbackContext callbackContext) {
        Set<Integer> days = new HashSet<Integer>();
        JSONArray dayArray;
        int startHour = -1;
        int endHour = -1;
        try {
            dayArray = data.getJSONArray(0);
            for (int i = 0; i < dayArray.length(); i++) {
                days.add(dayArray.getInt(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.error("error reading days json");
        }
        try {
            startHour = data.getInt(1);
            endHour = data.getInt(2);
        } catch (JSONException e) {
            callbackContext.error("error reading hour json");
        }
        Context context = cordovaCxt;
        JPushInterface.setPushTime(context, days, startHour, endHour);
       
    }

/**
 * 获取设备注册 ID
 */
    void getRegistrationID(JSONArray data, CallbackContext callbackContext) {
        Context context = cordovaCxt;
        String regID = JPushInterface.getRegistrationID(context);
        callbackContext.success(regID);
    }

    void onResume(JSONArray data, CallbackContext callbackContext) {
        JPushInterface.onResume(NXTPlugin.getActivity());
    }

    void onPause(JSONArray data, CallbackContext callbackContext) {
        JPushInterface.onPause(NXTPlugin.getActivity());
    }

    void reportNotificationOpened(JSONArray data, CallbackContext callbackContext) {
        try {
            String msgID;
            msgID = data.getString(0);
            JPushInterface.reportNotificationOpened(NXTPlugin.getActivity(), msgID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void setTags(JSONArray data, CallbackContext callbackContext) {
        try {
            HashSet<String> tags = new HashSet<String>();
            for (int i = 0; i < data.length(); i++) {
                tags.add(data.getString(i));
            }
            JPushInterface.setTags(cordovaCxt,
                    tags, mTagWithAliasCallback);
           
        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.error("Error reading tags JSON");
        }
    }

    void setAlias(JSONArray data, CallbackContext callbackContext) {
        
    }

    void setTagsWithAlias(JSONArray data, CallbackContext callbackContext) {
        HashSet<String> tags = new HashSet<String>();
        String alias;
        try {
            alias = data.getString(0);
            JSONArray tagsArray = data.getJSONArray(1);
            for (int i = 0; i < tagsArray.length(); i++) {
                tags.add(tagsArray.getString(i));
            }
            JPushInterface.setAliasAndTags(cordovaCxt,
                    alias, tags, mTagWithAliasCallback);
           
        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.error("Error reading tagAlias JSON");
        }
    }

    /**
     *   自定义通知行为，声音、震动、呼吸灯等。
     */
    void setBasicPushNotificationBuilder(JSONArray data,
                                         CallbackContext callbackContext) {
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(
                NXTPlugin.getActivity());
        builder.developerArg0 = "Basic builder 1";
        JPushInterface.setPushNotificationBuilder(1, builder);
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义推送通知栏样式，需要自己实现具体代码。
     * http://docs.jiguang.cn/client/android_tutorials/#_11
     */
    void setCustomPushNotificationBuilder(JSONArray data,
                                          CallbackContext callbackContext) {
        // CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(
        //         NXTPlugin.getActivity(), R.layout.test_notification_layout,
        //         R.id.icon, R.id.title, R.id.text);
        // JPushInterface.setPushNotificationBuilder(2, builder);
        // JPushInterface.setDefaultPushNotificationBuilder(builder);
    }

    void clearAllNotification(JSONArray data, CallbackContext callbackContext) {
        JPushInterface.clearAllNotifications(NXTPlugin.getActivity());
    }

    void clearNotificationById(JSONArray data, CallbackContext callbackContext) {
        int notificationId = -1;
        try {
            notificationId = data.getInt(0);
        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.error("error reading id json");
            return;
        }
        if (notificationId != -1) {
            JPushInterface.clearNotificationById(NXTPlugin.getActivity(), notificationId);
        } else {
            callbackContext.error("error id");
        }
    }

    void addLocalNotification(JSONArray data, CallbackContext callbackContext)
            throws JSONException {
        int builderId = data.getInt(0);
        String content = data.getString(1);
        String title = data.getString(2);
        int notificationID = data.getInt(3);
        int broadcastTime = data.getInt(4);
        String extrasStr = data.isNull(5) ? "" : data.getString(5);
        JSONObject extras = new JSONObject();
        if (!extrasStr.isEmpty()) {
            extras = new JSONObject(extrasStr);
        }

        JPushLocalNotification ln = new JPushLocalNotification();
        ln.setBuilderId(builderId);
        ln.setContent(content);
        ln.setTitle(title);
        ln.setNotificationId(notificationID);
        ln.setBroadcastTime(System.currentTimeMillis() + broadcastTime);
        ln.setExtras(extras.toString());

        JPushInterface.addLocalNotification(NXTPlugin.getActivity(), ln);
    }

    void removeLocalNotification(JSONArray data, CallbackContext callbackContext)
            throws JSONException {
        int notificationID = data.getInt(0);
        JPushInterface.removeLocalNotification(NXTPlugin.getActivity(), notificationID);
    }

    void clearLocalNotifications(JSONArray data, CallbackContext callbackContext) {
        JPushInterface.clearLocalNotifications(NXTPlugin.getActivity());
    }

    /**
     * 决定是否启用统计分析功能。
     */
    void setStatisticsOpen(JSONArray data, CallbackContext callbackContext) {
        try {
            isStatisticsOpened = data.getBoolean(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置通知静默时间
     * http://docs.jpush.io/client/android_api/#api_5
     */
    void setSilenceTime(JSONArray data, CallbackContext callbackContext) {
        try {
            int startHour = data.getInt(0);
            int startMinute = data.getInt(1);
            int endHour = data.getInt(2);
            int endMinute = data.getInt(3);
            if (!isValidHour(startHour) || !isValidMinute(startMinute)) {
                callbackContext.error("开始时间数值错误");
                return;
            }
            if (!isValidHour(endHour) || !isValidMinute(endMinute)) {
                callbackContext.error("结束时间数值错误");
                return;
            }
            JPushInterface.setSilenceTime(NXTPlugin.getActivity(), startHour, startMinute,
                    endHour, endMinute);
        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.error("error: reading json data.");
        }
    }

    private boolean isValidHour(int hour) {
        return !(hour < 0 || hour > 23);
    }

    private boolean isValidMinute(int minute) {
        return !(minute < 0 || minute > 59);
    }

    /**
     * 用于 Android 6.0 以上系统申请权限，具体可参考：
     * http://docs.Push.io/client/android_api/#android-60
     */
    void requestPermission(JSONArray data, CallbackContext callbackContext) {
        JPushInterface.requestPermission(NXTPlugin.getActivity());
    }

    private final TagAliasCallback mTagWithAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            if (instance == null) {
                return;
            }
            JSONObject data = new JSONObject();
            try {
                data.put("resultCode", code);
                data.put("tags", tags);
                data.put("alias", alias);
                final String jsEvent = String.format(
                        "cordova.fireDocumentEvent('jpush.setTagsWithAlias',%s)",
                        data.toString());
                        NXTPushPlugin.runJSOnUiThread(jsEvent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

}
