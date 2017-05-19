package com.jingoal.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import com.igexin.sdk.PushConsts;
import com.jingoal.push.R;

import static com.jingoal.push.receiver.JingoalReceiver.COMMAND_TYPE;
import static com.jingoal.push.receiver.JingoalReceiver.JINGOAL_PUSH_ACTION;
import static com.jingoal.push.receiver.JingoalReceiver.LOG_TAG;
import static com.jingoal.push.receiver.JingoalReceiver.MSG_CONTENT;
import static com.jingoal.push.receiver.JingoalReceiver.PUS_CLIENT_TYPE;

/**
 * 接收个推Push推送的消息
 */
public class GeTuiReceiver extends BroadcastReceiver {

    //个推初始化分配给客户端的ID
    private static final String CLIENT_ID = "clientid";
    //个推消息key
    private static final String PAYLOAD = "payload";

    @Override public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_CLIENTID:
                String clientID = bundle.getString(CLIENT_ID);
                Log.i(LOG_TAG,"GT_TOKEN:"+clientID);

                //初始化个推sdk，生成clientID后，保存到sp文件
                SharedPreferences sharedPreference =
                    context.getSharedPreferences(JingoalReceiver.JINGOAL_PUSH_SP,Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPreference.edit();
                edit.putString(JingoalReceiver.SP_KEY_GETUI_TOKEN, clientID);
                edit.commit();

                Intent regintent = new Intent();
                regintent.setAction(JINGOAL_PUSH_ACTION);
                regintent.putExtra(JingoalReceiver.MESSAGE_TYPE,
                    JingoalReceiver.MessageType.COMMAND);
                regintent.putExtra(COMMAND_TYPE, JingoalReceiver.COMMAND_REGISTER);
                regintent.putExtra(JingoalReceiver.COMMAND_RESULT, true);
                context.sendBroadcast(regintent);
                break;
            case PushConsts.GET_MSG_DATA:
                byte[] payload = bundle.getByteArray(PAYLOAD);
                if(payload!=null) {
                    Intent msgintent = new Intent(JINGOAL_PUSH_ACTION);
                    msgintent.putExtra(PUS_CLIENT_TYPE, JingoalReceiver.PushClientType.HUA_WEI);
                    msgintent.putExtra(MSG_CONTENT, new String(payload));
                    msgintent.putExtra(JingoalReceiver.MESSAGE_TYPE,
                        JingoalReceiver.MessageType.MESSAGE);
                    context.sendBroadcast(msgintent);
                }
                break;
            case PushConsts.SET_TAG_RESULT:
                String sn = bundle.getString("sn");
                String code = bundle.getString("code");
                Intent aliasintent = new Intent();
                aliasintent.setAction(JINGOAL_PUSH_ACTION);
                aliasintent.putExtra(JingoalReceiver.MESSAGE_TYPE,
                    JingoalReceiver.MessageType.COMMAND);
                aliasintent.putExtra(COMMAND_TYPE, JingoalReceiver.COMMAND_SET_ALIAS);
                switch (Integer.valueOf(code)) {
                    case PushConsts.SETTAG_SUCCESS: {
                        Log.i(LOG_TAG, context.getString(R.string.getui_settag_success));
                        aliasintent.putExtra(JingoalReceiver.COMMAND_RESULT, true);
                        context.sendBroadcast(aliasintent);
                        break;
                    }
                    case PushConsts.SETTAG_ERROR_COUNT:
                        Log.i(LOG_TAG, context.getString(R.string.getui_settag_error_count));
                        aliasintent.putExtra(JingoalReceiver.COMMAND_RESULT, false);
                        context.sendBroadcast(aliasintent);
                        break;

                    case PushConsts.SETTAG_ERROR_FREQUENCY:
                        Log.i(LOG_TAG, context.getString(R.string.getui_settag_error_frequency));
                        aliasintent.putExtra(JingoalReceiver.COMMAND_RESULT, false);
                        context.sendBroadcast(aliasintent);
                        break;

                    case PushConsts.SETTAG_ERROR_REPEAT:
                        Log.i(LOG_TAG, context.getString(R.string.getui_settag_error_repeat));
                        aliasintent.putExtra(JingoalReceiver.COMMAND_RESULT, false);
                        context.sendBroadcast(aliasintent);
                        break;

                    case PushConsts.SETTAG_ERROR_UNBIND:
                        Log.i(LOG_TAG, context.getString(R.string.getui_settag_error_unbind));
                        aliasintent.putExtra(JingoalReceiver.COMMAND_RESULT, false);
                        context.sendBroadcast(aliasintent);
                        break;

                    case PushConsts.SETTAG_ERROR_EXCEPTION:
                        Log.i(LOG_TAG, context.getString(R.string.getui_settag_error_exception));
                        aliasintent.putExtra(JingoalReceiver.COMMAND_RESULT, false);
                        context.sendBroadcast(aliasintent);
                        break;

                    case PushConsts.SETTAG_ERROR_NULL:
                        Log.i(LOG_TAG, context.getString(R.string.getui_settag_error_null));
                        aliasintent.putExtra(JingoalReceiver.COMMAND_RESULT, false);
                        context.sendBroadcast(aliasintent);
                        break;
                }
                break;
        }
    }
}
