package com.e.app_users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.e.JavaClass.UserMangers;
import com.e.mylibrary.MyActivity;
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
public class UserInfoActivity extends MyActivity {
    //重新刷新
    @Override
    protected void onResume() {
        super.onResume();
        List<UserMangers> newsList = LitePal.where("i =?", "1").find(UserMangers.class);
        TextView userName=findViewById(R.id.user_username_info);
        TextView nickName=findViewById(R.id.user_nickname_info);
        TextView phone=findViewById(R.id.user_phone_info);
        TextView pass=findViewById(R.id.user_pass_info);
        TextView floor=findViewById(R.id.user_floor_info);
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                userName.setText(newsList.get(i).getUserName());
                nickName.setText(newsList.get(i).getNickName());
                phone.setText(newsList.get(i).getUserPhone());
                ImageView pic=findViewById(R.id.user_pic_info);
                if (newsList.get(i).getFloor().equals("ALL")){
                    pic.setImageResource(R.drawable.administrator);
                }else {
                    pic.setImageResource(R.drawable.manager);
                }
                pass.setText(newsList.get(i).getUserPass());
                floor.setText(newsList.get(i).getFloor());
            }
        }else if(newsList != null && newsList.size()>=2){
            Toast.makeText(getApplicationContext(), "错误！", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar = findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        List<UserMangers> newsList = LitePal.where("i =?", "1").find(UserMangers.class);
        TextView userName=findViewById(R.id.user_username_info);
        TextView nickName=findViewById(R.id.user_nickname_info);
        TextView phone=findViewById(R.id.user_phone_info);
        TextView pass=findViewById(R.id.user_pass_info);
        TextView floor=findViewById(R.id.user_floor_info);
        //写入信息
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                userName.setText(newsList.get(i).getUserName());
                nickName.setText(newsList.get(i).getNickName());
                phone.setText(newsList.get(i).getUserPhone());
                ImageView pic=findViewById(R.id.user_pic_info);
                if (newsList.get(i).getFloor().equals("ALL")){
                    pic.setImageResource(R.drawable.administrator);
                }else {
                    pic.setImageResource(R.drawable.manager);
                }
                pass.setText(newsList.get(i).getUserPass());
                floor.setText(newsList.get(i).getFloor());
            }
        } else if(newsList != null && newsList.size()>=2){
            Toast.makeText(getApplicationContext(), "错误！", Toast.LENGTH_SHORT).show();
        }
        //改密码的跳转
        LinearLayout changeuserpass=findViewById(R.id.userInfo_changePass);
        changeuserpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app_users/PassUsersActivity").navigation();
            }
        });
        //改密码的跳转
        LinearLayout changeNickname=findViewById(R.id.userInfo_changeNickname);
        changeNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app_users/NickNameUsersActivity").navigation();
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
