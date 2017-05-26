package com.nxt.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 今目标推送消息广播接收器,用于接收不同厂家的第三方push sdk推送过来的消息,统一处理,
 * UI必须要继承该类,并注册在 AndroidManifest.xml 文件中
 */
public abstract class NXTReceiver extends BroadcastReceiver {

  public static final String LOG_TAG = "nxt_push_tag";
  public static final String JINGOAL_PUSH_ACTION = "com.nxt.push.receiver.nxtreceiver";
  public static final String MSG_CONTENT = "msg_content";
  public static final String PUS_CLIENT_TYPE = "push_client_type";

  /**
   * 广播接收到的Intent中包含的消息类型,see{@link MessageType}
   */
  public static final String MESSAGE_TYPE = "message_type";
  /**
   * 发送command类型请求的结果
   */
  public static final String COMMAND_RESULT = "command_result";

  /**
   * 发送的command的类型
   */
  public static final String COMMAND_TYPE = "command_type";
  //command的类型:注册
  public static final String COMMAND_REGISTER = "command_register";
  //command的类型:设置别名
  public static final String COMMAND_SET_ALIAS = "command_set_alias";
  //command的类型:删除别名
  public static final String COMMAND_DELETE_ALIAS = "command_delete_alias";

  /**
   * 保存PushSDK初始化时的一些数据
   */
  public static final String JINGOAL_PUSH_SP = "JingoalPushSP";
  //保存华为推送初始化生成的token
  public static final String SP_KEY_HUAWEI_TOKEN = "SPKeyHuaweiToken";
  //小米Push初始化返回的regId
  public static final String SP_KEY_XIAOMI_TOKEN = "SPKeyXiaomiRegId";

  /**
   * Push的客户端类型
   * 小米:3 华为:4
   */
  public static class PushClientType {
    //小米
    public static final int XIAO_MI = 3;
    //华为
    public static final int HUA_WEI = 4;
  }

  /**
   * 广播接收到的Intent中包含的消息类型
   */
  public static class MessageType {
    //commond类型消息
    public static final String COMMAND = "command";
    //服务推送的消息
    public static final String MESSAGE = "message";
  }

  @Override
  public final void onReceive(Context context, Intent intent) {
    Log.i("NXTReceiver", "我收到消息了！");
    if (MessageType.MESSAGE.equals(intent.getStringExtra(MESSAGE_TYPE))) {
      if (intent.getIntExtra(PUS_CLIENT_TYPE, -1) == PushClientType.XIAO_MI) {
        Log.i("onNotificationMClicked", "就要出发JS方法了");
        onNotificationMessageClicked(context, intent.getStringExtra(MSG_CONTENT));
      } else {
        onReceivePassThroughMessage(context, intent.getStringExtra(MSG_CONTENT));
      }
    } else if (MessageType.COMMAND.equals(intent.getStringExtra(MESSAGE_TYPE))) {
      onCommandResult(context, intent.getStringExtra(COMMAND_TYPE),
        intent.getBooleanExtra(COMMAND_RESULT, false));
    }
  }

  /**
   * 接收透传消息的接口
   *
   * @param message 收到的具体的消息内容
   **/
  public abstract void onReceivePassThroughMessage(Context context, String message);

  /**
   * 接收小米推送消息，此消息会在小米通知栏消息点击时触发
   *
   * @param message 收到的具体的消息内容
   **/
  public abstract void onNotificationMessageClicked(Context context, String message);

  /**
   * 注册SDK、设置别名、删除别名以及其他command的执行结果
   *
   * @param command 执行的命令的类型
   * @param success 执行命令的结果，true or false
   **/
  public abstract void onCommandResult(Context context, String command, boolean success);
}
