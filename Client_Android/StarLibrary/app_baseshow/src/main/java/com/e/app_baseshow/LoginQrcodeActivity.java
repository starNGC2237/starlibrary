package com.e.app_baseshow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.e.mylibrary.JsonMsg;
import com.e.mylibrary.UserIn;
import com.e.util.BaseTActivity;
import com.e.util.HttpUtil;
import com.e.util.NetWork;
import com.google.gson.GsonBuilder;
import com.lxj.xpopup.XPopup;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Route(path = "/app_base/LoginQrcodeActivity")
public class LoginQrcodeActivity extends BaseTActivity {
    String userName;
    @Autowired
    String uuid;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_qrcode);
        // 调用 inject 方法，如果传递过来的参数含有，这样使用 @Autowired 的会自动解析
        ARouter.getInstance().inject(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        ImageView img_q=findViewById(R.id.img_q);
        TextView userName_q=findViewById(R.id.userName_q);
        List<UserIn> newsList = LitePal.where("i =?", "1").find(UserIn.class);
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                userName=newsList.get(i).getUserName();
                Glide.with(LoginQrcodeActivity.this).load(newsList.get(i).getSrc()).into(img_q);
                userName_q.setText("即将登录的账号是：\n"+newsList.get(i).getUserName());
            }
        }else if(newsList != null && newsList.size()>=2){
            Toast.makeText(LoginQrcodeActivity.this, "错误！", Toast.LENGTH_SHORT).show();
        }
        Button button_q=findViewById(R.id.button_q);
        button_q.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginQ();
            }
        });
    }
    private void loginQ(){
        if (NetWork.isNetworkConnected(LoginQrcodeActivity.this)) {
            String s="https://starlibrary.online/haopeng/user/revisestatus?uuid="+uuid+"&status=2&userName="+userName;
            HttpUtil.sendOkHttpRequest(s, new Callback() {
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
                            Toast.makeText(LoginQrcodeActivity.this, jsonMsg.getMsg(), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(LoginQrcodeActivity.this, jsonMsg.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }else {
            Toast.makeText(LoginQrcodeActivity.this, "网络连接不可用，请检查您的网络设置", Toast.LENGTH_SHORT).show();
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