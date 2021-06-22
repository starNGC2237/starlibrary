package com.e.app_managers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.e.JavaClass.JsonDataBookId;
import com.e.JavaClass.JsonMsg;
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

@Route(path = "/app_managers/AddManagerActivity")
public class AddManagerActivity extends MyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_manager);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        //获得馆藏地址
        Spinner spinner1 =findViewById(R.id.spinner_floor_addManager);
        // 建立数据源
        String[] mItems1 = getResources().getStringArray(R.array.bookPlaces);
        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> adapter1= new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mItems1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        spinner1 .setAdapter(adapter1);
        LinearLayout cancel_addBook=findViewById(R.id.cancel_addManager);
        cancel_addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        LinearLayout addManager_addManager=findViewById(R.id.addManager_addManager);
        addManager_addManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addManager();
            }
        });
    }
    private void addManager(){
        if (NetWork.isNetworkConnected(AddManagerActivity.this)){
            EditText userName_edit_addManager=findViewById(R.id.userName_edit_addManager);
            String userName=userName_edit_addManager.getText().toString();
            EditText nickName_edit_addManager=findViewById(R.id.nickName_edit_addManager);
            String nickName=nickName_edit_addManager.getText().toString();
            EditText userPassword_edit_addManager=findViewById(R.id.userPassword_edit_addManager);
            String userPassword=userPassword_edit_addManager.getText().toString();
            EditText phone_edit_addManager=findViewById(R.id.phone_edit_addManager);
            int phone=Integer.parseInt(phone_edit_addManager.getText().toString());
            Spinner spinner1 =findViewById(R.id.spinner_floor_addManager);
            String floor = spinner1.getSelectedItem().toString();
            String s="https://starlibrary.online/haopeng/manager/addmanager"+"?userName="+userName+"&nickName="+nickName+"&userPassword="+userPassword+"&phone="+phone+"&floor="+floor;
            HttpUtil.sendOkHttpRequest(s, new Callback() {
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
                            JsonMsg jsonMsg = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(responseData, JsonMsg.class);
                            if (jsonMsg.getStatus() == 200) {
                                Toast.makeText(AddManagerActivity.this, jsonMsg.getMsg(), Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                Toast.makeText(AddManagerActivity.this, jsonMsg.getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
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