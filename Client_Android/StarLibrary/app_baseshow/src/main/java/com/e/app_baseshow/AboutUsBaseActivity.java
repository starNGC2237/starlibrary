package com.e.app_baseshow;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.e.mylibrary.JsonMsg;
import com.e.mylibrary.JsonMsgDataVersion;
import com.e.util.BaseTActivity;
import com.e.util.DownloadUtils;
import com.e.util.HttpUtil;
import com.e.util.NetWork;
import com.google.gson.GsonBuilder;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import javaClassBase.CustomPopup;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Route(path = "/app_base/AboutUsActivity")
public class AboutUsBaseActivity extends BaseTActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us_base);
        Toolbar toolbar = findViewById(R.id.toolbar_about);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        TextView version_name_about=findViewById(R.id.version_name_about);
        version_name_about.setText(BuildConfig.VERSION_NAME);
        RelativeLayout check_new_about=findViewById(R.id.check_new_about);
        check_new_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkversion();
            }
        });
        RelativeLayout features_about=findViewById(R.id.features_about);
        features_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentofversion();
            }
        });
    }
    //获得版本信息
    private void contentofversion(){
        if (NetWork.isNetworkConnected(AboutUsBaseActivity.this)) {
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/user/contentofversion?versionId="+BuildConfig.VERSION_NAME+"&role=0", new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String data = Objects.requireNonNull(response.body()).string();
                    runOnUiThread(() -> {
                        JsonMsg jsonMsg = new GsonBuilder()
                                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                .create()
                                .fromJson(data, JsonMsg.class);
                        if (jsonMsg.getStatus() == 200) {
                            if (!AboutUsBaseActivity.this.isDestroyed()){
                                String s=jsonMsg.getMsg();
                                new XPopup.Builder(AboutUsBaseActivity.this).asConfirm(BuildConfig.VERSION_NAME, "\n"+s+"\n",
                                        () -> {})
                                        .show();
                            }
                        } else {
                            Toast.makeText(AboutUsBaseActivity.this, jsonMsg.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }else {
            Toast.makeText(AboutUsBaseActivity.this, "网络连接不可用，请检查您的网络设置", Toast.LENGTH_SHORT).show();
        }
    }
    //检查新版本
    private void checkversion(){
        if (NetWork.isNetworkConnected(AboutUsBaseActivity.this)) {
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/user/checkversion?versionId="+BuildConfig.VERSION_NAME+"&role=0", new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String data = Objects.requireNonNull(response.body()).string();
                    runOnUiThread(() -> {
                        JsonMsgDataVersion jsonMsg = new GsonBuilder()
                                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                .create()
                                .fromJson(data, JsonMsgDataVersion.class);
                        if (jsonMsg.getStatus() == 200) {
                            if (!AboutUsBaseActivity.this.isDestroyed()){
                                new XPopup.Builder(AboutUsBaseActivity.this)
                                        .asCustom(new CustomPopup(AboutUsBaseActivity.this,jsonMsg.getData(),"\n"+jsonMsg.getMsg()+"\n"))
                                        .show();
                            }
                        } else {
                            if (jsonMsg.getMsg().equals("版本号相同")){
                                Toast.makeText(AboutUsBaseActivity.this, "已是最新版本", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }else {
            Toast.makeText(AboutUsBaseActivity.this, "网络连接不可用，请检查您的网络设置", Toast.LENGTH_SHORT).show();
        }
    }
    //设置返回键
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        }
        return true;
    }
}