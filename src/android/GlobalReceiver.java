package com.eegrid.phonegap;

import android.content.Context;
import android.util.Log;
import com.nxt.push.receiver.NXTReceiver;
import com.nxt.push.sdk.NXTPushManager;

public class GlobalReceiver extends NXTReceiver {

    @Override public void onReceivePassThroughMessage(Context context, String message) {
        Log.i("holo_push", "========================");
        Log.i("holo_push", "透传消息:  " + message);
        // if(MainActivity.getInstance()!=null) {
        //     MainActivity.getInstance().setRecvPushMessage(message);
        // }
        Log.i("holo_push", "========================");
    }

    @Override public void onNotificationMessageClicked(Context context, String message) {
        Log.i("holo_push", "========================");
        Log.i("holo_push", "小米消息:  " + message);
        // if(MainActivity.getInstance()!=null) {
        //     MainActivity.getInstance().setRecvPushMessage(message);
        // }
        Log.i("holo_push", "========================");
    }

    @Override public void onCommandResult(Context context, String command, boolean success) {
        Log.i("holo_push", "command :" + command);
        Log.i("holo_push", "command Result :" + success);
        // if(JingoalReceiver.COMMAND_REGISTER.equals(command)) {
        //     Log.i("holo_push",
        //             "clientType: " + JingoalPushManager.getClientType() + "");
        // }
    }
}
