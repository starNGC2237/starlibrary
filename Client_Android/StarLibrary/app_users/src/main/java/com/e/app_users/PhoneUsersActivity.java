package com.e.app_users;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.e.mylibrary.UserIn;
import com.e.util.BaseTActivity;

import org.litepal.LitePal;

import java.util.List;
import java.util.Objects;

@Route(path = "/app_users/PhoneUsersActivity")
public class PhoneUsersActivity extends BaseTActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_users);
        //设置toolbar和返回键
        Toolbar toolbar = findViewById(R.id.toolbar_nickname);
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
                TextView phone=findViewById(R.id.users_phone_black);
                String userTip="手机号："+newsList.get(i).getUserPhone();
                phone.setText(userTip);
            }
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