package com.e.app_users;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.e.mylibrary.Image;
import com.e.mylibrary.UserIn;
import com.e.util.BaseTActivity;
import com.e.util.NetWork;
import com.google.gson.Gson;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Route(path = "/app_users/UserInfoActivity")
public class UserInfoActivity extends BaseTActivity {
    //重新刷新
    @Override
    protected void onResume() {
        super.onResume();
        List<UserIn> newsList = LitePal.where("i =?", "1").find(UserIn.class);
        TextView userName=findViewById(R.id.user_username_info);
        TextView nickName=findViewById(R.id.user_nickname_info);
        TextView phone=findViewById(R.id.user_phone_info);
        TextView pass=findViewById(R.id.user_pass_info);
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                setUrlImage(newsList.get(i).getUserName());
                userName.setText(newsList.get(i).getUserName());
                nickName.setText(newsList.get(i).getNickName());
                StringBuffer stringPhone=new StringBuffer();
                if (newsList.get(i).getUserPhone().length()>4){
                    for (int b=0;b<newsList.get(i).getUserPhone().length()-4;b++){
                        stringPhone.append("*");
                    }
                    stringPhone.append(newsList.get(i).getUserPhone().substring(newsList.get(i).getUserPhone().length()-4));
                }else {
                    for (int b=0;b<newsList.get(i).getUserPhone().length();b++){
                        stringPhone.append("*");
                    }
                }
                phone.setText(stringPhone);
                StringBuffer stringBuffer = new StringBuffer();
                for (int a=0;a<newsList.get(i).getUserPass().length();a++){
                    stringBuffer.append("*");
                }
                pass.setText(stringBuffer);
            }
        }else if(newsList != null && newsList.size()>=2){
            Toast.makeText(getApplicationContext(), "错误！", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        Toolbar toolbar = findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        List<UserIn> newsList = LitePal.where("i =?", "1").find(UserIn.class);
        TextView userName=findViewById(R.id.user_username_info);
        TextView nickName=findViewById(R.id.user_nickname_info);
        TextView phone=findViewById(R.id.user_phone_info);
        TextView pass=findViewById(R.id.user_pass_info);
        //改昵称的跳转
        LinearLayout changenickname=findViewById(R.id.userInfo_changeNickname);
        changenickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app_users/NickNameUsersActivity").navigation();
            }
        });
        //改密码的跳转
        LinearLayout changeuserpass=findViewById(R.id.userInfo_changePass);
        changeuserpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app_users/PassUsersActivity").navigation();
            }
        });
        //改用户名（用户名改不了）
        LinearLayout changeusername=findViewById(R.id.userInfo_changeUserName);
        changeusername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app_users/UserNameUsersActivity").navigation();
            }
        });
        //改手机号
        LinearLayout changeuserphone=findViewById(R.id.userInfo_changePhone);
        changeuserphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app_users/PhoneUsersActivity").navigation();
            }
        });
        //图书证绑定
        LinearLayout bind=findViewById(R.id.userInfo_bind);
        bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app_users/BindUsersActivity").navigation();
            }
        });
        //头像
        LinearLayout headPic=findViewById(R.id.userInfo_headPic);
        headPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app_users/HeadpicUsersActivity").navigation();
            }
        });
        //信誉分数
        LinearLayout scores=findViewById(R.id.userInfo_scores);
        scores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app_users/ScoresActivity").navigation();
            }
        });

        //写入信息
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                userName.setText(newsList.get(i).getUserName());
                nickName.setText(newsList.get(i).getNickName());

                StringBuffer stringPhone=new StringBuffer();
                if (newsList.get(i).getUserPhone().length()>4){
                    for (int b=0;b<newsList.get(i).getUserPhone().length()-4;b++){
                        stringPhone.append("*");
                    }
                    stringPhone.append(newsList.get(i).getUserPhone().substring(newsList.get(i).getUserPhone().length()-4));
                }else {
                    for (int b=0;b<newsList.get(i).getUserPhone().length();b++){
                        stringPhone.append("*");
                    }
                }
                phone.setText(stringPhone);
                setUrlImage(newsList.get(i).getUserName());
                StringBuffer stringBuffer = new StringBuffer();
                for (int a=0;a<newsList.get(i).getUserPass().length();a++){
                    stringBuffer.append("*");
                }
                pass.setText(stringBuffer);
            }
        }else if(newsList != null && newsList.size()>=2){
            Toast.makeText(getApplicationContext(), "错误！", Toast.LENGTH_SHORT).show();
        }

    }
    //设置图片
    private void setUrlImage(final String userN) {
        if (NetWork.isNetworkConnected(getApplicationContext())) {
            OkHttpClient mOkHttpClient = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("userName", userN)
                    .build();
            Request request = new Request.Builder()
                    .url("https://starlibrary.online/haopeng/user/reimage")
                    .post(formBody)
                    .build();
            Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Toast.makeText(getApplicationContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                    final String data = Objects.requireNonNull(response.body()).string();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ImageView userPicInfo = findViewById(R.id.user_pic_info);
                                    Image jsonBeanImage = new Gson()
                                            .fromJson(data, Image.class);
                                    if (jsonBeanImage.getStatus() == 200) {
                                        ContentValues values = new ContentValues();
                                        values.put("src", jsonBeanImage.getData().getSrc());
                                        LitePal.updateAll(UserIn.class, values, "username = ?",userN);
                                        RequestOptions userAvatarOptions = new RequestOptions()
                                                .signature(new ObjectKey(jsonBeanImage.getData().getSrc()));
                                        if(!UserInfoActivity.this.isDestroyed()){
                                            Glide.with(UserInfoActivity.this)
                                                    .applyDefaultRequestOptions(userAvatarOptions)
                                                    .load(jsonBeanImage.getData().getSrc())
                                                    .into(userPicInfo);
                                        }

                                    }
                                }
                            });
                        }
                    }).start();
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
        }
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