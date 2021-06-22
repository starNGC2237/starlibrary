package com.e.app_users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.UserManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.e.JavaClass.JsonMsg;
import com.e.JavaClass.UserMangers;
import com.e.mylibrary.HttpUtil;
import com.e.mylibrary.MyActivity;
import com.e.mylibrary.NetWork;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
@Route(path = "/app_users/NickNameUsersActivity")
public class NickNameUsersActivity extends MyActivity {
    String userName = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nick_name_users);
        //设置toolbar和返回键
        Toolbar toolbar = findViewById(R.id.toolbar_nickname);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        //设置按钮并设置点击事件
        final Button save=findViewById(R.id.users_nickname_save);
        save.setEnabled(false);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<UserMangers> newsList = LitePal.where("i =?", "1").find(UserMangers.class);
                EditText nickNameED=findViewById(R.id.users_nickname_nicknameED);
                final String nickName=nickNameED.getText().toString();
                if(newsList != null && newsList.size()==1){
                    for (int i=0;i<newsList.size();i++){
                        userName=newsList.get(i).getUserName();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "错误", Toast.LENGTH_SHORT).show();
                }
                if (userName!=null){
                    if (NetWork.isNetworkConnected(getApplicationContext())) {
                        HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/manager/revisename" +
                                "?userName=" + userName + "&nickName=" + nickName, new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                String responseData = Objects.requireNonNull(response.body()).string();
                                showResponseDate(responseData);
                            }

                            private void showResponseDate(final String responseData) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        JsonMsg jsonMsg = new Gson()
                                                .fromJson(responseData, JsonMsg.class);
                                        if (jsonMsg.getStatus() == 200) {
                                            Toast.makeText(getApplicationContext(), jsonMsg.getMsg(), Toast.LENGTH_SHORT).show();
                                            ContentValues values = new ContentValues();
                                            values.put("nickname", nickName);
                                            LitePal.updateAll(UserMangers.class, values, "username = ?", userName);
                                            finish();
                                        } else if (jsonMsg.getStatus() == 404) {
                                            Toast.makeText(getApplicationContext(), jsonMsg.getMsg(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), jsonMsg.getMsg(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        });
                    }else {
                        Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        //监听edit的改变
        final EditText change=findViewById(R.id.users_nickname_nicknameED);
        List<UserMangers> newsList = LitePal.where("i =?", "1").find(UserMangers.class);
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                change.setText(newsList.get(i).getNickName());
                final String nickName=newsList.get(i).getNickName();
                change.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (change.getText().toString().equals(nickName)){
                            save.setEnabled(false);
                        }else {
                            save.setEnabled(true);
                        }

                    }
                });
            }
        }else if(newsList != null && newsList.size()>=2){
            Toast.makeText(getApplicationContext(), "错误！", Toast.LENGTH_SHORT).show();
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