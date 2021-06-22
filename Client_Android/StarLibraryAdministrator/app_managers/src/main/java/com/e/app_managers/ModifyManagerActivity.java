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

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
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

@Route(path = "/app_managers/ModifyManagerActivity")
public class ModifyManagerActivity extends MyActivity {

    @Autowired
    String userName;
    @Autowired
    String nickName;
    @Autowired
    String floor;
    @Autowired
    String passWord;
    @Autowired
    String createTime;
    @Autowired
    String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_manager);
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
        //获得馆藏地址
        final Spinner spinner1 =findViewById(R.id.spinner_floor_modifyManager);
        // 建立数据源
        String[] mItems1 = getResources().getStringArray(R.array.bookPlaces);
        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> adapter1= new ArrayAdapter<>(ModifyManagerActivity.this, android.R.layout.simple_spinner_item, mItems1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        spinner1 .setAdapter(adapter1);
        setText();
        LinearLayout modifyManager_modifyManager=findViewById(R.id.modifyManager_modifyManager);
        modifyManager_modifyManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText phone_edit_modifyManager=findViewById(R.id.phone_edit_modifyManager);
                EditText nickName_edit_modifyManager=findViewById(R.id.nickName_edit_modifyManager);
                EditText userPassword_edit_modifyManager=findViewById(R.id.userPassword_edit_modifyManager);
                if (!floor.equals(spinner1.getSelectedItem().toString())){
                    changeFloor();
                }
                if (!phone.equals(phone_edit_modifyManager.getText().toString())){
                    changePhone();
                }
                if (!nickName.equals(nickName_edit_modifyManager.getText().toString())){
                    changeNickName();
                }
                if (!passWord.equals(userPassword_edit_modifyManager.getText().toString())){
                    changePassWord();
                }
                finish();
            }
        });
        LinearLayout delete_modifyManager=findViewById(R.id.delete_modifyManager);
        delete_modifyManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteManager();
            }
        });
    }
    //修改楼层
    private void changeFloor(){
        Spinner spinner_bookPlace_modifyBook=findViewById(R.id.spinner_floor_modifyManager);
        final String newFloor=spinner_bookPlace_modifyBook.getSelectedItem().toString();
        if (NetWork.isNetworkConnected(this)) {
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/manager/revise?userName="+userName+"&floor="+newFloor+"&oldFloor="+floor, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                    final String responseData = Objects.requireNonNull(response.body()).string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonMsg jsonMsg = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(responseData, JsonMsg.class);
                            if (jsonMsg.getStatus() == 200) {
                                floor=newFloor;
                            }else if (jsonMsg.getStatus()==404){
                                Toast.makeText(ModifyManagerActivity.this,jsonMsg.getMsg(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
        }
    }
    //修改手机号
    private void changePhone(){
        EditText phone_edit_modifyManager=findViewById(R.id.phone_edit_modifyManager);
        final String newPhone=phone_edit_modifyManager.getText().toString();
        if (NetWork.isNetworkConnected(this)) {
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/manager/revise?userName="+userName+"&phone="+newPhone, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                    final String responseData = Objects.requireNonNull(response.body()).string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("AAAAAAAAAAAA", responseData);
                            JsonMsg jsonMsg = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(responseData, JsonMsg.class);
                            if (jsonMsg.getStatus() == 200) {
                                phone=newPhone;
                            }else if (jsonMsg.getStatus()==404){
                                Toast.makeText(ModifyManagerActivity.this,jsonMsg.getMsg(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
        }
    }
    //修改昵称
    private void changeNickName(){
        EditText nickName_edit_modifyManager=findViewById(R.id.nickName_edit_modifyManager);
        final String newNickName=nickName_edit_modifyManager.getText().toString();
        if (NetWork.isNetworkConnected(this)) {
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/manager/revisename?userName="+userName+"&nickName="+newNickName, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                    final String responseData = Objects.requireNonNull(response.body()).string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("AAAAAAAAAAAA", responseData);
                            JsonMsg jsonMsg = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(responseData, JsonMsg.class);
                            if (jsonMsg.getStatus() == 200) {
                                nickName=newNickName;
                            }else if (jsonMsg.getStatus()==404){
                                Toast.makeText(ModifyManagerActivity.this,jsonMsg.getMsg(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
        }
    }
    //修改密码
    private void changePassWord(){
        EditText userPassword_edit_modifyManager=findViewById(R.id.userPassword_edit_modifyManager);
        final String newPassWord=userPassword_edit_modifyManager.getText().toString();
        if (NetWork.isNetworkConnected(this)) {
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/manager/revisepassword?userName="+userName+"&userPassword="+passWord+"&newPassword="+newPassWord, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                    final String responseData = Objects.requireNonNull(response.body()).string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("AAAAAAAAAAAA", responseData);
                            JsonMsg jsonMsg = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(responseData, JsonMsg.class);
                            if (jsonMsg.getStatus() == 200) {
                                passWord=newPassWord;
                            }else if (jsonMsg.getStatus()==404){
                                Toast.makeText(ModifyManagerActivity.this,jsonMsg.getMsg(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
        }
    }
    //删除管理员
    private void deleteManager(){
        if (NetWork.isNetworkConnected(this)) {
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/manager/deletemanager?userName="+userName+"&floor="+floor, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                    final String responseData = Objects.requireNonNull(response.body()).string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("AAAAAAAAAAAA", responseData);
                            JsonMsg jsonMsg = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(responseData, JsonMsg.class);
                            if (jsonMsg.getStatus() == 200) {
                                Toast.makeText(ModifyManagerActivity.this,jsonMsg.getMsg(),Toast.LENGTH_LONG).show();
                                finish();
                            }else if (jsonMsg.getStatus()==404){
                                Toast.makeText(ModifyManagerActivity.this,jsonMsg.getMsg(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
        }
    }
    //显示信息
    private void setText(){
        TextView userName_text_modifyManager=findViewById(R.id.userName_text_modifyManager);
        userName_text_modifyManager.setText(userName);
        EditText nickName_edit_modifyManager=findViewById(R.id.nickName_edit_modifyManager);
        nickName_edit_modifyManager.setText(nickName);
        EditText userPassword_edit_modifyManager=findViewById(R.id.userPassword_edit_modifyManager);
        userPassword_edit_modifyManager.setText(passWord);
        EditText phone_edit_modifyManager=findViewById(R.id.phone_edit_modifyManager);
        phone_edit_modifyManager.setText(phone);
        Spinner spinner_bookPlace_modifyBook=findViewById(R.id.spinner_floor_modifyManager);
        switch (floor) {
            case "1A":
                spinner_bookPlace_modifyBook.setSelection(0, true);
                break;
            case "2B":
                spinner_bookPlace_modifyBook.setSelection(1, true);
                break;
            case "3C":
                spinner_bookPlace_modifyBook.setSelection(2, true);
                break;
            case "4D":
                spinner_bookPlace_modifyBook.setSelection(3, true);
                break;
            case "5E":
                spinner_bookPlace_modifyBook.setSelection(4, true);
                break;
            case "6F":
                spinner_bookPlace_modifyBook.setSelection(5, true);
                break;
        }
        TextView createTime_text_modifyManager=findViewById(R.id.createTime_text_modifyManager);
        createTime_text_modifyManager.setText(createTime);
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