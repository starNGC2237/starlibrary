package com.e.app_users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.os.Bundle;
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
@Route(path = "/app_users/PassUsersActivity")
public class PassUsersActivity extends MyActivity {
    String userPasswordT=null;
    String userName=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_users);
        Toolbar toolbar = findViewById(R.id.toolbar_pass);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        final EditText past=findViewById(R.id.users_pass_past);
        final EditText now=findViewById(R.id.users_pass_now);
        //设置按钮并设置点击事件
        final Button save=findViewById(R.id.users_pass_save);
        save.setEnabled(false);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userPassword=past.getText().toString();
                List<UserMangers> newsList = LitePal.where("i =?", "1").find(UserMangers.class);
                if(newsList != null && newsList.size()==1) {
                    for (int i = 0; i < newsList.size(); i++) {
                        userName = newsList.get(i).getUserName();
                        userPasswordT=newsList.get(i).getUserPass();
                    }
                }
                final String newPassword=now.getText().toString();
                if (userName!=null&&userPasswordT!=null&&userPasswordT.equals(userPassword)) {
                    if (NetWork.isNetworkConnected(getApplicationContext())) {
                        HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/manager/revisepassword"
                                + "?userName=" + userName
                                + "&userPassword=" + userPassword
                                + "&newPassword=" + newPassword, new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "网络连接失败！请检查网络", Toast.LENGTH_SHORT).show();
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
                                            values.put("userpass", newPassword);
                                            LitePal.updateAll(UserMangers.class, values, "username = ?", userName);
                                            finish();
                                        } else if (jsonMsg.getStatus() == 404) {
                                            Toast.makeText(getApplicationContext(), "原密码错误！", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "原密码错误！", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        });
                    }else {
                        Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"原密码错误！",Toast.LENGTH_LONG).show();
                }
            }
        });
        //检查是否都填了
        now.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!past.getText().toString().equals("")){
                    if(!now.getText().toString().equals("")){
                        if (now.getText().toString().length()<6&&past.getText().toString().length()>16){
                            save.setEnabled(false);
                            Toast.makeText(getApplicationContext(), "密码应由6-16个字符组成，区分大小写。", Toast.LENGTH_SHORT).show();
                        }else {
                            save.setEnabled(true);
                        }

                    }else {
                        save.setEnabled(false);
                    }
                }else {
                    save.setEnabled(false);
                }
            }
        });
        past.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!past.getText().toString().equals("")){
                    if(!now.getText().toString().equals("")){
                        save.setEnabled(true);
                    }else {
                        save.setEnabled(false);
                    }
                }else {
                    save.setEnabled(false);
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        }
        return true;
    }
}