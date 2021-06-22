package com.e.app_baseshow;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.e.app_message.MessageActivity;
import com.e.mylibrary.JsonMsgNumber;
import com.e.mylibrary.UserIn;
import com.e.util.HttpUtil;
import com.e.util.NetWork;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MessageService extends Service {
    private static final String TAG = MessageService.class.getSimpleName();
    private static final int NOTIFICATION_ID =1 ;
    static boolean serviceIsLive=false;
    static String message="暂时没有消息";
    int card=-1;
    boolean tip=true;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
        // 获取服务通知
        Notification notification = createForegroundNotification(message);
        //将服务置于启动状态 ,NOTIFICATION_ID指的是创建的通知的ID
        startForeground(NOTIFICATION_ID, notification);
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(1000*60*2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    checkupreaded();
                }
            }
        });
        thread.start();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");
        return null;
    }
    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        // 标记服务关闭
        MessageService.serviceIsLive = false;
        // 移除通知
        stopForeground(true);
        super.onDestroy();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        // 标记服务启动
        MessageService.serviceIsLive = true;
        return super.onStartCommand(intent, flags, startId);
    }
    /**
     * 创建服务通知
     */
    private Notification createForegroundNotification(String aaa) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 唯一的通知通道的id.
        String notificationChannelId = "notification_channel_id_01";

        // Android8.0以上的系统，新建消息通道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //用户可见的通道名称
            String channelName = "Foreground Service Notification";
            //通道的重要程度
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId, channelName, importance);
            notificationChannel.setDescription("Channel description");
            //LED灯
            notificationChannel.enableLights(false);
            //notificationChannel.setLightColor(Color.RED);
            //震动
            //notificationChannel.setVibrationPattern(new long[]{0, 0,0, 0});
            notificationChannel.enableVibration(false);
            notificationChannel.setSound(null,null);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, notificationChannelId);
        //通知小图标
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //通知标题
        builder.setContentTitle("消息");
        //通知内容
        builder.setContentText(aaa);
        //设定通知显示的时间
        builder.setWhen(System.currentTimeMillis());
        //设定启动的内容
        Intent activityIntent = new Intent(this, MessageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        //创建通知并返回
        return builder.build();
    }
    private void messageNo(String message){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 通知渠道的id
            String id = "my_channel_01";
            // 用户可以看到的通知渠道的名字.
            CharSequence name = getString(R.string.channel_name);
            // 用户可以看到的通知渠道的描述
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            //注意Name和description不能为null或者""
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(false);
            mChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(false);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400});
            //最后在notificationmanager中创建该通知渠道
            notificationManager.createNotificationChannel(mChannel);
            builder.setChannelId(id);
        }
        builder.setContentInfo("补充内容");
        builder.setContentText(message);
        builder.setContentTitle("消息");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker("新消息");
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        Intent intent = new Intent(this, MessageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent,PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notificationManager.notify(2, notification);
    }
    private void checkupreaded(){
        Log.d(TAG, "checkupreaded: "+card);
        List<UserIn> newsList = LitePal.where("i =?", "1").find(UserIn.class);
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                if (newsList.get(i).getCard()==0){
                    card=0;
                }else {
                    card=newsList.get(i).getCard();
                }
            }
        }
        if (card>0) {
            if (NetWork.isNetworkConnected(getApplicationContext())) {
                HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/bookBorrow/checkupreaded?card=" + card, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        final String responseData = Objects.requireNonNull(response.body()).string();
                        JsonMsgNumber jsonRootBean = new GsonBuilder()
                                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                .create()
                                .fromJson(responseData, JsonMsgNumber.class);
                        if (jsonRootBean.getStatus() == 200) {
                            if (jsonRootBean.getData() > 0) {
                                messageNo("有" + jsonRootBean.getData() + "条消息");
                                // 获取服务通知
                                Notification notification = createForegroundNotification("有" + jsonRootBean.getData() + "条消息");
                                //将服务置于启动状态 ,NOTIFICATION_ID指的是创建的通知的ID
                                startForeground(NOTIFICATION_ID, notification);
                            }else {
                                // 获取服务通知
                                Notification notification = createForegroundNotification("暂时没有消息");
                                //将服务置于启动状态 ,NOTIFICATION_ID指的是创建的通知的ID
                                startForeground(NOTIFICATION_ID, notification);
                            }
                        }else{
                            // 获取服务通知
                            Notification notification = createForegroundNotification("暂时没有消息");
                            //将服务置于启动状态 ,NOTIFICATION_ID指的是创建的通知的ID
                            startForeground(NOTIFICATION_ID, notification);
                        }
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "没有网络", Toast.LENGTH_SHORT).show();
            }
        }else if (card==0){
            if (tip){
                Notification notificationtip = createForegroundNotification("没有绑定图书证");
                startForeground(NOTIFICATION_ID, notificationtip);
                tip=false;
            }
        }
    }
}
