package com.e.app_base;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.e.JavaClass.JsonMsg;
import com.e.JavaClass.JsonMsgDataVersion;
import com.e.mylibrary.DownloadUtils;
import com.e.mylibrary.HttpUtil;
import com.e.mylibrary.MyActivity;
import com.e.mylibrary.NetWork;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Route(path = "/app_base/AboutUsActivity")
public class AboutUsActivity extends MyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
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
        check_new_about.setOnClickListener(v -> {
            checkversion();
        });
        RelativeLayout features_about=findViewById(R.id.features_about);
        features_about.setOnClickListener(v -> {
            contentofversion();
        });
    }
    //??????????????????
    private void contentofversion(){
        if (NetWork.isNetworkConnected(AboutUsActivity.this)) {
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/user/contentofversion?versionId="+BuildConfig.VERSION_NAME+"&role=1", new Callback() {
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
                            if (!AboutUsActivity.this.isDestroyed()){
                                String s=jsonMsg.getMsg();
                                AlertDialog alertDialog1 = new AlertDialog.Builder(AboutUsActivity.this)
                                        .setTitle(BuildConfig.VERSION_NAME)
                                        .setCancelable(true)
                                        .setMessage("\n"+s+"\n")//??????
                                        .setIcon(R.mipmap.ic_launcher)//??????
                                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {//??????"Yes"??????
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                            }
                                        })
                                        .setNegativeButton("??????", new DialogInterface.OnClickListener() {//????????????
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                            }
                                        })
                                        .create();
                                alertDialog1.show();
                            }
                        } else {
                            Toast.makeText(AboutUsActivity.this, "??????", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }else {
            Toast.makeText(AboutUsActivity.this, "???????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
        }
    }
    //???????????????
    private void checkversion(){
        if (NetWork.isNetworkConnected(AboutUsActivity.this)) {
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
                            if (!AboutUsActivity.this.isDestroyed()){
                                AlertDialog alertDialog1 = new AlertDialog.Builder(AboutUsActivity.this)
                                        .setTitle(jsonMsg.getData())
                                        .setCancelable(true)
                                        .setMessage("\n"+jsonMsg.getMsg()+"\n")//??????
                                        .setIcon(R.mipmap.ic_launcher)//??????
                                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {//??????"Yes"??????
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Toast.makeText(AboutUsActivity.this,"????????????starLibraray",Toast.LENGTH_LONG).show();
                                                new DownloadUtils(AboutUsActivity.this,"https://starlibrary.online/StarLibraryAdministrator/"+jsonMsg.getData()+".apk", "app2.apk");
                                            }
                                        })
                                        .setNegativeButton("??????", new DialogInterface.OnClickListener() {//????????????
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                            }
                                        })
                                        .create();
                                alertDialog1.show();
                            }
                        } else {
                            if (jsonMsg.getMsg().equals("???????????????")){
                                Toast.makeText(AboutUsActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }else {
            Toast.makeText(AboutUsActivity.this, "???????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
        }
    }
    //???????????????
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        }
        return true;
    }
}