package com.example.smscp;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;


/**
 * 短信验证码自动填写功能的实现
 * 用第三方服务：Bmob云来做的发送短信
 */
public class MainActivity extends AppCompatActivity {

    public static final int MSG_RECEIVE_CODE = 1; //收到短信的验证码
    private EditText codeEdt; //短信验证码的输入框
    private SMSContentObserver smsContentObserver;

    //回调接口
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_RECEIVE_CODE) {
                codeEdt.setText(msg.obj.toString()); //设置读取到的内容
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        codeEdt = (EditText) findViewById(R.id.smsCode);

        findViewById(R.id.send_sms_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                senSMSCode();
            }
        });

        smsContentObserver = new SMSContentObserver(
                MainActivity.this, handler);

        //ContentObserver注册
        getContentResolver().registerContentObserver(
                Uri.parse("content://sms/"), true, smsContentObserver);

    }

    /**
     * 取消注册
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(smsContentObserver);
    }


    //使用Bmob云的短信验证功能来发送短信
    private void senSMSCode() {

        BmobSMS.requestSMSCode("13342290623", "知么网络", new QueryListener<Integer>() {

            @Override
            public void done(Integer smsId, BmobException ex) {
                if (ex == null) {//验证码发送成功
                    Log.i("smile", "短信id：" + smsId);//用于后续的查询本次短信发送状态
                    Toast.makeText(MainActivity.this, "发送验证码成功", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
