package com.nxt.push.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import com.jingoal.push.receiver.JingoalReceiver;
import com.xiaomi.mipush.sdk.MiPushClient;

/**
 * 华为推送初始化以及别名管理
 */
public class JPushClient implements NXTPushClient {
    

    @Override public void registerPush(Context ctx) {
        try {
            JPushInterface.init(ctx);
        } catch (Exception e) {
            Log.e(JingoalReceiver.LOG_TAG, e.getMessage());
        }
    }

    @Override public void unRegisterPush(Context context) {
    }

    @Override public void setAlias(Context context, String deviceId, String alias) {
        // try {
        //     String alias = data.getString(0);
        //     JPushInterface.setAlias(context,
        //             alias, mTagWithAliasCallback);
        //     callbackContext.success();
        // } catch (JSONException e) {
        //     e.printStackTrace();
        //     callbackContext.error("Error reading alias JSON");
        // }
    }

    @Override public void deleteAlias(Context context, String deviceId, String alias) {
    }

    @Override public String getToken(Context context) {
    }

    @Override public int getClientType() {
        return JingoalReceiver.PushClientType.JI_GUANG;
    }

    
}
