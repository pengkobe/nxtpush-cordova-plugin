package com.eegrid.phonegap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nxt.push.util.RomTypeUtil;
import com.nxt.push.sdk.NXTPushManager;
import com.nxt.push.sdk.NXTPushClient;

public class NXTPushPlugin extends CordovaPlugin {
  private final static List<String> methodList =
    Arrays.asList(
      "areNotificationEnabled",
      "init",
      "requestPermission",
      "setAlias",
      "setTags",

      // 以下为极光专有
      "setCustomPushNotificationBuilder",
      "addLocalNotification",
      "clearAllNotification",
      "clearLocalNotifications",
      "clearNotificationById",
      "getRegistrationID",
      "isPushStopped",
      "onPause",
      "onResume",
      "removeLocalNotification",
      "reportNotificationOpened",
      "resumePush",
      "getNotification",
      "setBasicPushNotificationBuilder",
      "setDebugMode",
      "setLatestNotificationNum",
      "setPushTime",
      "setTagsWithAlias",
      "setSilenceTime",
      "setStatisticsOpen",
      "stopPush"
    );


  private ExecutorService threadPool = Executors.newFixedThreadPool(1);
  private static NXTPushPlugin instance;
  private static Activity cordovaActivity;
  private static String TAG = "NXTPushPlugin";

  private static boolean IS_JIGUANG_PUSH = false;

  public NXTPushPlugin() {
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
    if (methodList.contains(action)) {
      threadPool.execute(new Runnable() {
        @Override
        public void run() {
          try {
            Method method = NXTPushPlugin.class.getDeclaredMethod(action,
              JSONArray.class, CallbackContext.class);
            method.invoke(NXTPushPlugin.this, data, callbackContext);
          } catch (Exception e) {
            Log.e(TAG, e.toString());
          }
        }
      });
    }

    return true;
  }

  void init(JSONArray data, CallbackContext callbackContext) {
    if (RomTypeUtil.isEMUI()) {//  || RomTypeUtil.isMIUI()
      // 注册华为
      NXTPushManager.init(cordova.getActivity().getApplicationContext());
    } else {
      // 注册极光
      IS_JIGUANG_PUSH = true;
      JPushUtil.initPlugin(cordova.getActivity().getApplicationContext());
    }
  }

  void setTags(JSONArray data, CallbackContext callbackContext) {
    if (IS_JIGUANG_PUSH) {
      JPushUtil.setTags(data, callbackContext);
    }
  }

  void setAlias(JSONArray data, CallbackContext callbackContext) {
    try {
      if (IS_JIGUANG_PUSH) {
        JPushUtil.setAlias(data, callbackContext);
      } else {

        if (data.getString(0) == "") { // 与极光 api 耦合
          NXTPushManager.unRegisterPush(cordova.getActivity().getApplicationContext());
        } else { // 注册华为与小米
          NXTPushManager.setAlias(cordova.getActivity().getApplicationContext(), "USELESS", data.getString(0));
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
      callbackContext.error("Error reading NXTPushPlugin setAlias JSON");
    }

  }

  /**
   * 极光专有方法开始
   */
  void setDebugMode(JSONArray data, CallbackContext callbackContext) {
    if (IS_JIGUANG_PUSH) {
      JPushUtil.setDebugMode(data, callbackContext);
    }
  }

  void stopPush(JSONArray data, CallbackContext callbackContext) {
    if (IS_JIGUANG_PUSH) {
      JPushUtil.stopPush(data, callbackContext);
    } else {
      NXTPushManager.stopPush(cordova.getActivity().getApplicationContext());
    }
  }

  void resumePush(JSONArray data, CallbackContext callbackContext) {
    if (IS_JIGUANG_PUSH) {
      JPushUtil.resumePush(data, callbackContext);
    } else {
      NXTPushManager.resumePush(cordova.getActivity().getApplicationContext());
    }
  }

  boolean isPushStopped(JSONArray data, CallbackContext callbackContext) {
    if (IS_JIGUANG_PUSH) {
      boolean ret = JPushUtil.isPushStopped(data, callbackContext);
      return ret;
    }
    return false;
  }

  void setLatestNotificationNum(JSONArray data, CallbackContext callbackContext) {
    if (IS_JIGUANG_PUSH) {
      JPushUtil.setLatestNotificationNum(data, callbackContext);
    }
  }

  void setPushTime(JSONArray data, CallbackContext callbackContext) {
    if (IS_JIGUANG_PUSH) {
      JPushUtil.setPushTime(data, callbackContext);
    }
  }

  void getRegistrationID(JSONArray data, CallbackContext callbackContext) {
    if (IS_JIGUANG_PUSH) {
      JPushUtil.getRegistrationID(data, callbackContext);
    }
  }

  void onResume(JSONArray data, CallbackContext callbackContext) {
    if (IS_JIGUANG_PUSH) {
      JPushUtil.onResume(data, callbackContext);
    }
  }

  void onPause(JSONArray data, CallbackContext callbackContext) {
    if (IS_JIGUANG_PUSH) {
      JPushUtil.onPause(data, callbackContext);
    }
  }

  void reportNotificationOpened(JSONArray data, CallbackContext callbackContext) {
    if (IS_JIGUANG_PUSH) {
      JPushUtil.reportNotificationOpened(data, callbackContext);
    }
  }

  void setTagsWithAlias(JSONArray data, CallbackContext callbackContext) {
    if (IS_JIGUANG_PUSH) {
      JPushUtil.setTagsWithAlias(data, callbackContext);
    }
  }

  void setBasicPushNotificationBuilder(JSONArray data,
                                       CallbackContext callbackContext) {
    if (IS_JIGUANG_PUSH) {
      JPushUtil.setBasicPushNotificationBuilder(data, callbackContext);
    }
  }

  void setCustomPushNotificationBuilder(JSONArray data, CallbackContext callbackContext) {
    if (IS_JIGUANG_PUSH) {
      JPushUtil.setCustomPushNotificationBuilder(data, callbackContext);
    }
  }

  void clearAllNotification(JSONArray data, CallbackContext callbackContext) {
    if (IS_JIGUANG_PUSH) {
      JPushUtil.clearAllNotification(data, callbackContext);
    }
  }

  void clearNotificationById(JSONArray data, CallbackContext callbackContext) {
    if (IS_JIGUANG_PUSH) {
      JPushUtil.clearNotificationById(data, callbackContext);
    }
  }

  void addLocalNotification(JSONArray data, CallbackContext callbackContext)
    throws JSONException {
    if (IS_JIGUANG_PUSH) {
      JPushUtil.addLocalNotification(data, callbackContext);
    }
  }

  void removeLocalNotification(JSONArray data, CallbackContext callbackContext)
    throws JSONException {
    if (IS_JIGUANG_PUSH) {
      JPushUtil.removeLocalNotification(data, callbackContext);
    }
  }

  void clearLocalNotifications(JSONArray data, CallbackContext callbackContext) {
    if (IS_JIGUANG_PUSH) {
      JPushUtil.clearLocalNotifications(data, callbackContext);
    }
  }

  void setStatisticsOpen(JSONArray data, CallbackContext callbackContext) {
    if (IS_JIGUANG_PUSH) {
      JPushUtil.setStatisticsOpen(data, callbackContext);
    }

  }

  void setSilenceTime(JSONArray data, CallbackContext callbackContext) {
    if (IS_JIGUANG_PUSH) {
      JPushUtil.setSilenceTime(data, callbackContext);
    }
  }

  /**
   * 用于 Android 6.0 以上系统申请权限，具体可参考：
   * http://docs.Push.io/client/android_api/#android-60
   */
  void requestPermission(JSONArray data, CallbackContext callbackContext) {
    if (IS_JIGUANG_PUSH) {
      JPushUtil.requestPermission(data, callbackContext);
    }
  }


  /** 极光专有方法结束 */

  /**
   * 专门用来执行来自 JPush 的执行请求
   */
  public static void runJSOnUiThread(final String js) {
    if (instance == null) {
      return;
    }
    cordovaActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        instance.webView.loadUrl("javascript:" + js);
      }
    });
  }

  public static Activity getActivity() {
    return cordovaActivity;
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
      Log.i(TAG, "JPush InvocationTargetException.");
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
