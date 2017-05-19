package com.jingoal.push.sdk;

import android.content.Context;
import com.jingoal.push.receiver.JingoalReceiver;
import com.jingoal.push.util.RomTypeUtil;
import com.xiaomi.mipush.sdk.MiPushClient;

/**
 * Jingoal Push SDK 初始化类,会根据手机类型选择初始化不同厂商的Push SDK
 */
public class JingoalPushManager {

    private static JingoalPushClient jingoalPushClient;

    //手机系统订制商-华为
    public static final String BRAND_HUAWEI = "HUAWEI";//HUAWEI
    public static final String BRAND_HONOR = "HONOR";//HONOR
    //手机系统订制商-小米
    public static final String BRAND_XIAOMI = "XIAOMI";//Xiaomi

    /**
     * 注册push，初始化推送sdk
     **/
    public synchronized static void init(Context ctx) {
        if(RomTypeUtil.isEMUI()) {
            jingoalPushClient = new HuaWeiPushClient();
        } else if(RomTypeUtil.isMIUI()) {
            jingoalPushClient = new XiaomiPushClient();
        } else {
            jingoalPushClient = new GeTuiPushClient();
        }
        jingoalPushClient.registerPush(ctx);
    }

    /**
     * 反注册push，反注册客户端不在接收推送,经测试华为的并无效果
     **/
    public static void unRegisterPush(Context ctx) {
        if(jingoalPushClient!=null) {
            jingoalPushClient.unRegisterPush(ctx);
        }
    }

    /**
     * 设置别名
     *
     * @param deviceId 手机唯一ID，客户端统一的算法生成
     * @param alias 客户端别名
     **/
    public static void setAlias(Context context, String deviceId, String alias) {
        if(jingoalPushClient!=null) {
            jingoalPushClient.setAlias(context, deviceId, alias);
        }
    }

    /**
     * 删除别名
     *
     * @param deviceId 手机唯一ID，客户端统一的算法生成
     * @param alias 客户端别名
     **/
    public static void deleteAlias(Context context, String deviceId, String alias) {
        if(jingoalPushClient!=null) {
            jingoalPushClient.deleteAlias(context, deviceId, alias);
        }
    }

    /**
     * 获取PushSDK注册时生成的Token,对应华为的Token,个推的ClientId,小米的regID
     *
     * @param context 上下文
     * @return 存在token则返回保存的token, 否则返回null
     */
    public static String getToken(Context context) {
        if(jingoalPushClient!=null) {
            return jingoalPushClient.getToken(context);
        }
        return null;
    }

    /**
     * 获取当前初始化的推送客户端的类型,详见{@link JingoalReceiver.PushClientType}
     *
     * @return int类型, 如果客户端没有初始化则返回-1;
     */
    public static int getClientType() {
        if(jingoalPushClient!=null) {
            return jingoalPushClient.getClientType();
        }
        return -1;
    }
}
