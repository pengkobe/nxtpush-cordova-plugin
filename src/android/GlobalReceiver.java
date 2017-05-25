package com.eegrid.phonegap;

import android.content.Context;
import android.util.Log;

import com.nxt.push.receiver.NXTReceiver;

public class GlobalReceiver extends NXTReceiver {

  /**
   * 接收透传消息
   * @param context
   * @param message 收到的具体的消息内容
     */
  @Override
  public void onReceivePassThroughMessage(Context context, String message) {
    Log.i("holo_push", "========================");
    Log.i("holo_push", "透传消息:  " + message);
    // 套用极光接口发送至 UI
    String format = "window.plugins.NXTPlugin.receiveMessageInAndroidCallback(%s);";
    final String js = String.format(format, message.toString());
    NXTPushPlugin.runJSOnUiThread(js);
    Log.i("holo_push", "========================");
  }

  /**
   * 小米通知栏消息被点击
   * @param context
   * @param message 收到的具体的消息内容
     */
  @Override
  public void onNotificationMessageClicked(Context context, String message) {
    Log.i("holo_push", "========================");
    Log.i("holo_push", "小米消息:  " + message);
    String format = "window.plugins.NXTPlugin.openNotificationInAndroidCallback(%s);";
    final String js = String.format(format, message.toString());
    NXTPushPlugin.runJSOnUiThread(js);
    Log.i("holo_push", "========================");
  }

  /**
   * 接收命令设置返回信息
   * @param context
   * @param command 执行的命令的类型
   * @param success 执行命令的结果，true or false
     */
  @Override
  public void onCommandResult(Context context, String command, boolean success) {
    Log.i("holo_push", "command :" + command);
    Log.i("holo_push", "command Result :" + success);
  }
}
