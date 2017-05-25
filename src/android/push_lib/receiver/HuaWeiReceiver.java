package com.nxt.push.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import com.huawei.android.pushagent.api.PushEventReceiver;

import static com.nxt.push.receiver.NXTReceiver.COMMAND_TYPE;
import static com.nxt.push.receiver.NXTReceiver.JINGOAL_PUSH_ACTION;
import static com.nxt.push.receiver.NXTReceiver.LOG_TAG;
import static com.nxt.push.receiver.NXTReceiver.MSG_CONTENT;
import static com.nxt.push.receiver.NXTReceiver.PUS_CLIENT_TYPE;

/**
 * 接收华为Push推送过来的消息
 */
public class HuaWeiReceiver extends PushEventReceiver {

    /**
     * pushToken申请成功后,会自动回调该方法,应用可以通过该接口中获取token。
     * yipeng:获取到相关的 token 后存储到 sharedPreference 中，可以在 PushClient 中获取
     */
    @Override public void onToken(Context context, String token, Bundle extras) {
        Intent intent = new Intent();
        intent.setAction(JINGOAL_PUSH_ACTION);
        intent.putExtra(NXTReceiver.MESSAGE_TYPE, NXTReceiver.MessageType.COMMAND);
        intent.putExtra(COMMAND_TYPE, NXTReceiver.COMMAND_REGISTER);
        intent.putExtra(NXTReceiver.COMMAND_RESULT, true);
        context.sendBroadcast(intent);
        Log.i(LOG_TAG,"HW_TOKEN:"+token);


        /**
         * 将华为 token 存储在本地，便于 unRegister 使用
         */
      SharedPreferences sharedPreference =
                context.getSharedPreferences(NXTReceiver.JINGOAL_PUSH_SP,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.putString(NXTReceiver.SP_KEY_HUAWEI_TOKEN, token);
        edit.commit();

    }

    /**
     * 推送消息下来时会自动回调onPushMsg方法实,现应用透传消息处理
     *
     * @param extras 扩展信息,暂时不启用
     * @return 统一返回false, 暂时没有作用（官方文档未予说明）
     */
    @Override public boolean onPushMsg(Context context, byte[] msg, Bundle extras) {
        Intent intent = new Intent(JINGOAL_PUSH_ACTION);
        intent.putExtra(PUS_CLIENT_TYPE, NXTReceiver.PushClientType.HUA_WEI);
        intent.putExtra(MSG_CONTENT, new String(msg));
        intent.putExtra(NXTReceiver.MESSAGE_TYPE, NXTReceiver.MessageType.MESSAGE);
        context.sendBroadcast(intent);
        return false;
    }

    /**
     * 供子类继承实现,实现业务事件。该方法会在设置标签、LBS信息之后、点击打开通知栏消息、
     * 点击通知栏上的按钮之后被调用。由业务决定是否调用该函数。
     *
     * @param event 事件类型,event为枚举类型,事件定义如下:
     * <pre>
     * {@code
     *  public static enum Event
     *  {
     *     NOTIFICATION_OPENED,//通知栏中的通知被点击打开
     *     NOTIFICATION_CLICK_BTN,//通知栏中的通知被点击打开
     *     PLUGINRSP, //标签上报回应
     *  }
     * }<pre/>
     */
    @Override public void onEvent(Context context, Event event, Bundle extras) {
        if (Event.PLUGINRSP.equals(event)) {
            final int TYPE_TAG = 2;
            int reportType = extras.getInt(BOUND_KEY.PLUGINREPORTTYPE, -1);
            boolean isSuccess = extras.getBoolean(BOUND_KEY.PLUGINREPORTRESULT, false);
            if(TYPE_TAG == reportType) {
                Intent intent = new Intent();
                intent.setAction(JINGOAL_PUSH_ACTION);
                intent.putExtra(NXTReceiver.MESSAGE_TYPE, NXTReceiver.MessageType.COMMAND);
                intent.putExtra(COMMAND_TYPE, NXTReceiver.COMMAND_SET_ALIAS);
                intent.putExtra(NXTReceiver.COMMAND_RESULT, isSuccess);
                context.sendBroadcast(intent);
            }
        }
    }
}
