package com.nxt.push.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import io.ionic.starter.R;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;
import java.util.List;

import static com.nxt.push.receiver.NXTReceiver.COMMAND_TYPE;
import static com.nxt.push.receiver.NXTReceiver.JINGOAL_PUSH_ACTION;
import static com.nxt.push.receiver.NXTReceiver.LOG_TAG;
import static com.nxt.push.receiver.NXTReceiver.MSG_CONTENT;
import static com.nxt.push.receiver.NXTReceiver.PUS_CLIENT_TYPE;
import static com.xiaomi.mipush.sdk.MiPushClient.COMMAND_REGISTER;

/**
 * 用于接收小米Push推送过来的消息,所有方法运行在非 UI 线程中。
 */
public class XiaomiReceiver extends PushMessageReceiver {

    /**
     * 方法用来接收服务器向客户端发送的自定义动作通知消息，
     * 这个回调方法会在用户手动点击通知后触发
     */
    @Override public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        Intent intent = new Intent(JINGOAL_PUSH_ACTION);
        intent.putExtra(PUS_CLIENT_TYPE, NXTReceiver.PushClientType.XIAO_MI);
        intent.putExtra(MSG_CONTENT, message.getContent());
        intent.putExtra(NXTReceiver.MESSAGE_TYPE, NXTReceiver.MessageType.MESSAGE);
        context.sendBroadcast(intent);
    }

    /**
     * 方法用来接收客户端向服务器发送命令后的响应结果。
     */
    @Override public void onCommandResult(Context context, MiPushCommandMessage message) {
        Log.v(LOG_TAG, "onCommandResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments!=null && arguments.size() > 0) ? arguments.get(0) : null);
        String log;
        Intent intent = new Intent();
        intent.setAction(JINGOAL_PUSH_ACTION);
        intent.putExtra(NXTReceiver.MESSAGE_TYPE, NXTReceiver.MessageType.COMMAND);

        if(COMMAND_REGISTER.equals(command)) {
            Log.i(LOG_TAG,"XM_TOKEN:"+cmdArg1);

            SharedPreferences sharedPreference =
                    context.getSharedPreferences(NXTReceiver.JINGOAL_PUSH_SP,Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreference.edit();
            edit.putString(NXTReceiver.SP_KEY_XIAOMI_TOKEN, cmdArg1);
            edit.commit();

            intent.putExtra(COMMAND_TYPE, COMMAND_REGISTER);
            if(message.getResultCode()==ErrorCode.SUCCESS) {
                intent.putExtra(NXTReceiver.COMMAND_RESULT, true);
            } else {
                intent.putExtra(NXTReceiver.COMMAND_RESULT, false);
            }
            context.sendBroadcast(intent);
        } else if(MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            intent.putExtra(COMMAND_TYPE, NXTReceiver.COMMAND_SET_ALIAS);
            if(message.getResultCode()==ErrorCode.SUCCESS) {
                intent.putExtra(NXTReceiver.COMMAND_RESULT, true);
            } else {
                log = context.getString(R.string.mipush_set_alias_fail, message.getReason());
                intent.putExtra(NXTReceiver.COMMAND_RESULT, false);
                Log.e(LOG_TAG, log);
            }
            context.sendBroadcast(intent);
        } else if(MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            intent.putExtra(COMMAND_TYPE, NXTReceiver.COMMAND_DELETE_ALIAS);
            if(message.getResultCode()==ErrorCode.SUCCESS) {
                intent.putExtra(NXTReceiver.COMMAND_RESULT, true);
            } else {
                log = context.getString(R.string.mipush_unset_alias_fail, message.getReason());
                intent.putExtra(NXTReceiver.COMMAND_RESULT, false);
                Log.e(LOG_TAG, log);
            }
            context.sendBroadcast(intent);
        }
    }

    @Override public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        super.onReceiveRegisterResult(context, message);
    }
}
