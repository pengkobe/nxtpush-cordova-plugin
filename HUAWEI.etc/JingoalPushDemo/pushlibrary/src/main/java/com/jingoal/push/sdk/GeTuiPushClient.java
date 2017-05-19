package com.jingoal.push.sdk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.igexin.sdk.PushManager;
import com.jingoal.push.receiver.JingoalReceiver;

import static com.jingoal.push.receiver.JingoalReceiver.COMMAND_TYPE;
import static com.jingoal.push.receiver.JingoalReceiver.JINGOAL_PUSH_ACTION;

/**
 * 个推推送初始化以及对别名进行管理
 */
public class GeTuiPushClient implements JingoalPushClient {

    @Override
    public void registerPush(Context ctx) {
        PushManager.getInstance().initialize(ctx);
    }

    @Override
    public void unRegisterPush(Context ctx) {
        //stopService之后客户端不会收到推送消息,再次initialize之后会收到stopService期间推送过来的消息
        PushManager.getInstance().stopService(ctx);

        SharedPreferences sharedPreference =
                ctx.getSharedPreferences(JingoalReceiver.JINGOAL_PUSH_SP,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.remove(JingoalReceiver.SP_KEY_XIAOMI_TOKEN);
        edit.commit();

    }

    @Override
    public void setAlias(final Context context, String deviceId, final String alias) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean result = PushManager.getInstance().bindAlias(context, alias);
                Intent aliasintent = new Intent();
                aliasintent.setAction(JINGOAL_PUSH_ACTION);
                aliasintent.putExtra(JingoalReceiver.MESSAGE_TYPE,
                        JingoalReceiver.MessageType.COMMAND);
                aliasintent.putExtra(COMMAND_TYPE, JingoalReceiver.COMMAND_SET_ALIAS);
                aliasintent.putExtra(JingoalReceiver.COMMAND_RESULT, result);
                context.sendBroadcast(aliasintent);
            }
        }).start();
    }

    @Override
    public void deleteAlias(final Context context, String deviceId, final String alias) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean result = PushManager.getInstance().unBindAlias(context, alias, true);
                Intent aliasintent = new Intent();
                aliasintent.setAction(JINGOAL_PUSH_ACTION);
                aliasintent.putExtra(JingoalReceiver.MESSAGE_TYPE,
                        JingoalReceiver.MessageType.COMMAND);
                aliasintent.putExtra(COMMAND_TYPE, JingoalReceiver.COMMAND_DELETE_ALIAS);
                aliasintent.putExtra(JingoalReceiver.COMMAND_RESULT, result);
                context.sendBroadcast(aliasintent);
            }
        }).start();
    }

    @Override
    public String getToken(Context context) {
        SharedPreferences sharedPreference =
                context.getSharedPreferences(JingoalReceiver.JINGOAL_PUSH_SP,Context.MODE_PRIVATE);
        return sharedPreference.getString(JingoalReceiver.SP_KEY_GETUI_TOKEN, null);
    }

    @Override
    public int getClientType() {
        return JingoalReceiver.PushClientType.GE_TUI;
    }
}
