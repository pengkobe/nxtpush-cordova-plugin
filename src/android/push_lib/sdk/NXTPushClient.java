package com.nxt.push.sdk;

import android.content.Context;
import com.nxt.push.receiver.NXTReceiver;

/**
 * NXT推送SDK处理接口
 */
public interface NXTPushClient {

    /**
     * 注册push，初始化推送sdk
     **/
    void registerPush(Context ctx);

    /**
     * 反注册push，反注册客户端不在接收推送,经测试华为的并无效果
     **/
    void unRegisterPush(Context ctx);

    /**
     * 设置别名
     *
     * @param deviceId 手机唯一ID，客户端统一的算法生成
     * @param alias 别名
     **/
    void setAlias(Context context, String deviceId, String alias);

    /**
     * 删除别名
     *
     * @param deviceId 手机唯一ID，客户端统一的算法生成
     * @param alias 客户端别名
     **/
    void deleteAlias(Context context, String deviceId, String alias);

    /**
     * 获取PushSDK注册时生成的Token,对应华为的Token,个推的ClientId,小米的regID
     * @param context 上下文
     * @return 存在token则返回保存的token, 否则返回null
     */
    String getToken(Context context);

    /**
     * 获取当前初始化的推送客户端的类型,详见{@link NXTReceiver.PushClientType}
     * @return int类型
     */
    int getClientType();
}
