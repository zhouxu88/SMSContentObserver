package com.example.smscp;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 短信验证码自动填充
 *
 * @author 周旭
 */
public class SMSContentObserver extends ContentObserver {

    private Context mContext; // 上下文
    private Handler mHandler; // 更新UI线程
    private String code; // 验证码

    public SMSContentObserver(Context context, Handler handler) {
        super(handler);
        mContext = context;
        mHandler = handler;
    }

    /**
     * 回调函数, 当所监听的Uri发生改变时，就会回调此方法
     * <p>
     * 注意当收到短信的时候会回调两次
     * 收到短信一般来说都是执行了两次onchange方法.第一次一般都是raw的这个.
     * 这个时候虽然收到了短信.但是短信还没有写入到收件箱里面
     * 然后才是另外一个,后面的数字是该短信在收件箱中的位置
     *
     * @param selfChange 此值意义不大 一般情况下该回调值false
     */
    @Override
    public void onChange(boolean selfChange, Uri uri) {

        Log.e("tag", uri.toString());

        // 第一次回调 不是我们想要的 直接返回
        if (uri.toString().equals("content://sms/raw")) {
            return;
        }

        // 第二次回调 查询收件箱里的内容
        Uri inboxUri = Uri.parse("content://sms/inbox");

        // 按时间顺序排序短信数据库
        Cursor c = mContext.getContentResolver().query(inboxUri, null, null,
                null, "date desc");
        if (c != null) {
            if (c.moveToFirst()) {

                // 获取短信提供商的手机号
                String address = c.getString(c.getColumnIndex("address"));
                // 获取短信内容
                String body = c.getString(c.getColumnIndex("body"));
                Log.i("tag", "body------->" + body);
                // 判断手机号是否为目标号码(短信提供商的号码)
                // 在这里我们的短信提供商的号码如果是固定的话.我们可以再加一个判断,这样就不会受到别的短信应用的验证码的影响了
                // 不然的话就在我们的正则表达式中,加一些自己的判断,例如短信中含有自己应用名字啊什么的...
                /*if (!address.equals("13342290623"))
                {
                    Log.i("tag","------->没有读取到内容");
                    return;
                }*/
                // 正则表达式截取短信中的6位验证码
                Pattern pattern = Pattern.compile("(\\d{6})");
                Matcher matcher = pattern.matcher(body);

                // 利用handler将得到的验证码发送给主线程
                if (matcher.find()) {
                    code = matcher.group(0);
                    //mHandler.obtainMessage(1, code).sendToTarget();
                    Message msg = Message.obtain();
                    msg.what = MainActivity.MSG_RECEIVE_CODE;
                    msg.obj = code;
                    mHandler.sendMessage(msg);
                }
            }
            c.close();
        }
    }
}
