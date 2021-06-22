package com.e.app_users;

import android.content.ContentValues;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.e.mylibrary.JsonMsg;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Route(path = "/app_users/BindUsersActivity")
public class BindUsersActivity extends BaseTActivity {
    int card=0;
    String userName=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_users);
        final Toolbar toolbar = findViewById(R.id.toolbar_bind);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        List<UserIn> newsList = LitePal.where("i =?", "1").find(UserIn.class);
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                userName=newsList.get(i).getUserName();
                card=newsList.get(i).getCard();
                if (card != 0) {
                    EditText bindED = findViewById(R.id.users_bind_bindED);
                    bindED.setVisibility(View.INVISIBLE);
                    Button button = findViewById(R.id.users_bind_save);
                    button.setVisibility(View.INVISIBLE);
                    TextView bindcard = findViewById(R.id.users_bind_black);
                    String cardstring = "图书证号：" + card;
                    bindcard.setText(cardstring);
                } else {
                    RelativeLayout bind = findViewById(R.id.bind);
                    bind.setVisibility(View.INVISIBLE);
                    Button button = findViewById(R.id.users_bind_save);
                    button.setEnabled(false);
                }
            }
        }else{
            Toast.makeText(getApplicationContext(), "错误", Toast.LENGTH_SHORT).show();
        }

        final EditText bindED=findViewById(R.id.users_bind_bindED);
        bindED.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!bindED.getText().toString().equals("")){
                    Button button=findViewById(R.id.users_bind_save);
                    button.setEnabled(true);
                }else {
                    Button button=findViewById(R.id.users_bind_save);
                    button.setEnabled(false);
                }
            }
        });
        Button button=findViewById(R.id.users_bind_save);
        button.setEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card=Integer.parseInt(bindED.getText().toString());
                if (NetWork.isNetworkConnected(getApplicationContext())) {
                    HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/user/bindCard" + "?userName=" + userName + "&card=" + card, new Callback() {
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
                                    JsonMsg jsonmsg = new GsonBuilder()
                                            .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                            .create()
                                            .fromJson(responseData, JsonMsg.class);
                                    if (jsonmsg.getStatus() == 200) {
                                        Toast.makeText(getApplicationContext(), jsonmsg.getMsg(), Toast.LENGTH_SHORT).show();
                                        ContentValues valuetwo = new ContentValues();
                                        valuetwo.put("i", "1");
                                        valuetwo.put("card",card );
                                        LitePal.updateAll(UserIn.class, valuetwo, "username = ?", userName);
                                        finish();
                                    } else if (jsonmsg.getStatus() == 404) {
                                        Toast.makeText(getApplicationContext(), jsonmsg.getMsg(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), jsonmsg.getMsg(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });
                }else {
                    Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
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