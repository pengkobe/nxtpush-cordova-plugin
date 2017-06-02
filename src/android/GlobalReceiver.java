package com.eegrid.phonegap;

import android.content.Context;
import android.util.Log;

import com.nxt.push.receiver.NXTReceiver;

import org.json.JSONException;
import org.json.JSONObject;

public class GlobalReceiver extends NXTReceiver {

  /**
   * 接收透传消息
   *
   * @param context
   * @param message 收到的具体的消息内容
   */
  @Override
  public void onReceivePassThroughMessage(Context context, String message) {
    Log.i("holo_push", "========================");
    Log.i("holo_push", "透传消息:  " + message);
    // 套用极光接口发送至 UI
    String format = "window.plugins.NXTPlugin.receiveMessageInAndroidCallback(%s);";
    try {
      JSONObject data = new JSONObject();
      data.put("message", message);
      final String js = String.format(format, data.toString());
      NXTPushPlugin.runJSOnUiThread(js);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    Log.i("holo_push", "========================");
  }

  /**
   * 小米/华为 通知栏消息被点击（事实上，不仅仅包括被点击的消息 ）
   *
   * @param context
   * @param message 收到的具体的消息内容
   */
  @Override
  public void onNotificationMessageClicked(Context context, String message) {
    Log.i("holo_push begin", "========================");
    Log.i("holo_push", "小米消息/华为消息:  " + message);
    String format = "window.plugins.NXTPlugin.openNotificationInAndroidCallback(%s);";
    try {
      JSONObject data = new JSONObject();
      data.put("extras", message);
      final String js = String.format(format, data.toString());
      NXTPushPlugin.runJSOnUiThread(js);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    Log.i("holo_push end", "========================");
  }


  /**
   * 接收命令设置返回信息
   *
   * @param context
   * @param command 执行的命令的类型
   * @param success 执行命令的结果，true or false
   */
  @Override
  public void onCommandResult(Context context, String command, boolean success) {
    Log.i("holo_push", "command :" + command);
    Log.i("holo_push", "command Result :" + success);
  }

  @Override
  public void onHuaWeiRigisterResult(Context context, String token, boolean success) {
    Log.i("华为返回token:", token);
    String format = "window.plugins.NXTPlugin.onReceiveHuaWeiToken(%s);";
    try {
      JSONObject data = new JSONObject();
      data.put("token", token);
      final String js = String.format(format, data.toString());
      NXTPushPlugin.runJSOnUiThread(js);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    Log.i("华为返回token end", "========================");
  }


}
