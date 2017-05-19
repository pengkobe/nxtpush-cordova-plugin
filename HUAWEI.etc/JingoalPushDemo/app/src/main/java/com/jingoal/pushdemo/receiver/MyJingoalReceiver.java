package com.jingoal.pushdemo.receiver;

import android.content.Context;
import android.util.Log;
import com.jingoal.push.receiver.JingoalReceiver;
import com.jingoal.push.sdk.JingoalPushManager;
import com.jingoal.pushdemo.MainActivity;

public class MyJingoalReceiver extends JingoalReceiver {

    @Override public void onReceivePassThroughMessage(Context context, String message) {
        Log.i("holo_push", "========================");
        Log.i("holo_push", "透传消息:  " + message);
        if(MainActivity.getInstance()!=null) {
            MainActivity.getInstance().setRecvPushMessage(message);
        }
        Log.i("holo_push", "========================");
    }

    @Override public void onNotificationMessageClicked(Context context, String message) {
        Log.i("holo_push", "========================");
        Log.i("holo_push", "小米消息:  " + message);
        if(MainActivity.getInstance()!=null) {
            MainActivity.getInstance().setRecvPushMessage(message);
        }
        Log.i("holo_push", "========================");
    }

    @Override public void onCommandResult(Context context, String command, boolean success) {
        Log.i("holo_push", "command :" + command);
        Log.i("holo_push", "command Result :" + success);
        if(JingoalReceiver.COMMAND_REGISTER.equals(command)) {
            Log.i("holo_push",
                    "clientType: " + JingoalPushManager.getClientType() + "");
        }
    }
}
