package com.e.starlibrary;


import android.content.ContentValues;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.e.javaBean.JsonRootBean;
import com.e.mylibrary.UserIn;
import com.e.util.BaseTActivity;
import com.e.util.HttpUtil;
import com.e.util.NetWork;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Route(path = "/app/WelcomeActivity")
public class WelcomeActivity extends BaseTActivity {

    private static final boolean IS_DUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LitePal.initialize(this.getApplication());
        if (IS_DUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this.getApplication());
        setContentView(R.layout.activity_main);
        Timer timer=new Timer();
        //创建TimerTask对象，用于实现界面与主界面的跳转
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                //如果数据库有，就自动登录从启动界面到主界面
                List<UserIn> userins = LitePal
                        .where("i= ?","1")
                        .find(UserIn.class);
                if (userins == null || userins.size() == 0) {
                    //没有登陆过就跳转
                    ARouter.getInstance().build("/app_login/LoginActivity").navigation();
                    finish();
                }else if(userins.size() == 1){
                    String userName=userins.get(0).getUserName();
                    String userPassword=userins.get(0).getUserPass();
                    String nickName=userins.get(0).getNickName();
                    if (NetWork.isNetworkConnected(getApplicationContext())){
                        HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/user/login" +
                                "?userName=" + userName + "&passWord=" + userPassword, new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                e.printStackTrace();
                            }
                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                final String responseData = Objects.requireNonNull(response.body()).string();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        JsonRootBean jsonRootBean = new GsonBuilder()
                                                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                                .create()
                                                .fromJson(responseData, JsonRootBean.class);
                                        if (jsonRootBean.getStatus() == 200) {
                                            if (!nickName.equals(jsonRootBean.getData().getNickName())){
                                                ContentValues value1 = new ContentValues();
                                                value1.put("nickname",jsonRootBean.getData().getNickName());
                                                LitePal.updateAll(UserIn.class, value1, "username = ?and i = ?", jsonRootBean.getData().getUserName(), "1");
                                            }
                                            ARouter.getInstance().build("/app_base/BaseActivity").navigation();
                                            finish();
                                        } else {
                                            //没有就显示登陆失败，密码错误
                                            Toast.makeText(getApplicationContext(), "登录失败，密码错误", Toast.LENGTH_SHORT).show();
                                            //没有登陆过就跳转
                                            ARouter.getInstance().build("/app_login/LoginActivity").navigation();
                                            finish();
                                        }
                                    }
                                });


                            }
                        });
                    }else {
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
                        ARouter.getInstance().build("/app_login/LoginActivity").navigation();
                        finish();
                        Looper.loop();
                    }
                }else{
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "错误1！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }


            }
        };
        //2s后跳转
        timer.schedule(timerTask,2000);
    }
}