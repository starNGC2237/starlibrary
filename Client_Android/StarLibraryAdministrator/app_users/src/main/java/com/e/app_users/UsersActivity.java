package com.e.app_users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.e.JavaClass.UserMangers;
import com.e.mylibrary.MyActivity;

import org.litepal.LitePal;

import java.util.List;
import java.util.Objects;

import javaClassUsers.NormalAdapter;

@Route(path = "/app_users/UsersActivity")
public class UsersActivity extends MyActivity {
    NormalAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        Toolbar toolbar = findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        final ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        List<UserMangers> users_list= LitePal.findAll(UserMangers.class);
        RecyclerView rv =findViewById(R.id.RecylerView_users);
        adapter=new NormalAdapter(users_list);
        rv.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());

        final TextView delete_users=findViewById(R.id.delete_users);
        delete_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (delete_users.getText().equals("删除")){
                    delete_users.setText("保存");
                    adapter.deleteMode();
                }else if (delete_users.getText().equals("保存")){
                    adapter.checkUsers();
                    delete_users.setText("删除");
                    adapter.deleteMode();
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        List<UserMangers> newsList = LitePal.where("i =?", "2").find(UserMangers.class);
        List<UserMangers> newsList2 = LitePal.where("i =?", "3").find(UserMangers.class);
        if ((newsList!= null&&!newsList.isEmpty())||(newsList2!=null&&!newsList2.isEmpty()) ){
            if (newsList!= null&&!newsList.isEmpty()) {
                for (int i = 0; i < newsList.size(); i++) {
                    ContentValues values = new ContentValues();
                    values.put("i", 0);
                    LitePal.updateAll(UserMangers.class, values, "i = ?", "2");
                }
            }
            if (newsList2!=null&&!newsList2.isEmpty()){
                for (int i = 0; i < newsList2.size(); i++) {
                    ContentValues values = new ContentValues();
                    values.put("i", 1);
                    LitePal.updateAll(UserMangers.class, values, "i = ?", "3");
                }
            }
        }
    }
}