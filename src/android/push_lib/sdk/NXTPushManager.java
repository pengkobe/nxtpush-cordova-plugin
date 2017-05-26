package com.nxt.push.sdk;

import android.content.Context;
import android.util.Log;

import com.nxt.push.receiver.NXTReceiver;
import com.nxt.push.util.RomTypeUtil;
import com.xiaomi.mipush.sdk.MiPushClient;

/**
 * Jingoal Push SDK 初始化类,会根据手机类型选择初始化不同厂商的Push SDK
 */
public class NXTPushManager {

  private static NXTPushClient nxtPushClient;

  // 手机系统订制商-华为
  public static final String BRAND_HUAWEI = "HUAWEI";//HUAWEI
  public static final String BRAND_HONOR = "HONOR";//HONOR
  // 手机系统订制商-小米
  public static final String BRAND_XIAOMI = "XIAOMI";//Xiaomi

  /**
   * 注册push，初始化推送sdk
   **/
  public synchronized static void init(Context ctx) {
    if (RomTypeUtil.isEMUI()) {
      Log.i("华为初始化推送sdk", "========================");
      nxtPushClient = new HuaWeiPushClient();
      nxtPushClient.registerPush(ctx);
    } else if (RomTypeUtil.isMIUI()) {
      Log.i("小米初始化推送sdk", "========================");
      nxtPushClient = new XiaomiPushClient();
      nxtPushClient.registerPush(ctx);
    } else {
      Log.i("怪物出现了", "========================");
    }

  }

  /**
   * 反注册push，反注册客户端不在接收推送,经测试华为的并无效果
   **/
  public static void unRegisterPush(Context ctx) {
    if (nxtPushClient != null) {
      nxtPushClient.unRegisterPush(ctx);
    }
  }

  /**
   * 设置别名
   *
   * @param deviceId 手机唯一ID，客户端统一的算法生成
   * @param alias    客户端别名
   **/
  public static void setAlias(Context context, String deviceId, String alias) {
    Log.i("小米设置别名", "========================");
    if (nxtPushClient != null) {
      nxtPushClient.setAlias(context, deviceId, alias);
    }else {
      Log.i("小米设置别名nxtPushClient不存在", "========================");
    }
  }

  /**
   * 删除别名
   *
   * @param deviceId 手机唯一ID，客户端统一的算法生成
   * @param alias    客户端别名
   **/
  public static void deleteAlias(Context context, String deviceId, String alias) {
    if (nxtPushClient != null) {
      nxtPushClient.deleteAlias(context, deviceId, alias);
    }
  }

  /**
   * 获取PushSDK注册时生成的Token,对应华为的Token,个推的ClientId,小米的regID
   *
   * @param context 上下文
   * @return 存在token则返回保存的token, 否则返回null
   */
  public static String getToken(Context context) {
    if (nxtPushClient != null) {
      return nxtPushClient.getToken(context);
    }
    return null;
  }

  /**
   * 获取当前初始化的推送客户端的类型,详见{@link NXTReceiver.PushClientType}
   *
   * @return int类型, 如果客户端没有初始化则返回-1;
   */
  public static int getClientType() {
    if (nxtPushClient != null) {
      return nxtPushClient.getClientType();
    }
    return -1;
  }
}
