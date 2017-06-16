package com.nxt.pushdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.nxt.push.receiver.JingoalReceiver;
import com.nxt.push.sdk.JingoalPushManager;

public class MainActivity extends Activity implements View.OnClickListener {

    TextView txtTokend;

    TextView txtContent;

    TextView txtClientType;

    private static MainActivity instance;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        txtTokend = (TextView) findViewById(R.id.txtToken);
        txtContent = (TextView) findViewById(R.id.txt_content);
        txtClientType = (TextView) findViewById(R.id.txt_client_type);
        setOnclicListener(R.id.register, R.id.unregister, R.id.set_alias, R.id.unset_alias,
                R.id.get_token, R.id.btn_clientType);
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public void setRecvPushMessage(String pusgMsg) {
        txtContent.setText(pusgMsg);
    }

    private void setOnclicListener(Integer... ids) {
        for (int i = 0; i < ids.length; i++) {
            findViewById(ids[i]).setOnClickListener(this);
        }
    }

    private void register() {
        JingoalPushManager.init(getApplicationContext());
    }

    private void unRegister() {
        JingoalPushManager.unRegisterPush(getApplicationContext());
    }

    private void setAlias() {
        JingoalPushManager.setAlias(getApplicationContext(), 123456789 + "","holo_111");
    }

    private void unSetAlias() {
        JingoalPushManager.deleteAlias(getApplicationContext(), 123456789 + "","holo_111");
    }

    private void getToken() {
        String token = JingoalPushManager.getToken(getApplicationContext());
        txtTokend.setText(token);
    }

    private void getClientType() {
        int type = JingoalPushManager.getClientType();
        if(type==JingoalReceiver.PushClientType.GE_TUI) {
            txtClientType.setText("个推");
        } else if(type==JingoalReceiver.PushClientType.HUA_WEI) {
            txtClientType.setText("华为");
        } else if(type==JingoalReceiver.PushClientType.XIAO_MI) {
            txtClientType.setText("小米");
        }
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                register();
                break;
            case R.id.unregister:
                unRegister();
                break;
            case R.id.set_alias:
                setAlias();
                break;
            case R.id.unset_alias:
                unSetAlias();
                break;
            case R.id.get_token:
                getToken();
                break;
            case R.id.btn_clientType:
                getClientType();
                break;
        }
    }
}
