package com.e.app_yueshuquan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.e.mylibrary.JsonMsg;
import com.e.mylibrary.JsonPageReview.JsonReview;
import com.e.mylibrary.UserIn;
import com.e.util.BaseTActivity;
import com.e.util.HttpUtil;
import com.e.util.NetWork;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Route(path = "/app_yueshuquan/ShuPingClickActivity")
public class ShuPingClickActivity extends BaseTActivity {
    String userName;
    JsonReview json;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shu_ping_click);
        String bookJson=getIntent().getStringExtra("json");
        json=new Gson().fromJson(bookJson,JsonReview.class);
        final Toolbar toolbar = findViewById(R.id.toolbar_my_review_click);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        TextView shupingYue_click_intro=findViewById(R.id.shupingYue_click_intro);
        shupingYue_click_intro.setText(json.getBook().get(0).getBookIntroduction());
        ImageView shupingYue_click_src=findViewById(R.id.shupingYue_click_src);
        Glide.with(ShuPingClickActivity.this).load(json.getBook().get(0).getSrc()).into(shupingYue_click_src);
        TextView shupingYue_click_bookName=findViewById(R.id.shupingYue_click_bookName);
        shupingYue_click_bookName.setText(json.getBook().get(0).getBookName());
        TextView shupingYue_click_score=findViewById(R.id.shupingYue_click_score);
        shupingYue_click_score.setText("评分："+json.getScore());
        final ImageView shupingYue_click_dianzhan=findViewById(R.id.shupingYue_click_dianzhan);
        shupingYue_click_dianzhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hit(json.getHit(),json.getAdId());
                if(json.getHit()==0){
                    json.setHit(1);
                    json.setHitNum(json.getHitNum()+1);
                    Glide.with(ShuPingClickActivity.this).load(R.drawable.dianzhan_have).into(shupingYue_click_dianzhan);
                }else if (json.getHit()==1){
                    json.setHit(0);
                    json.setHitNum(json.getHitNum()-1);
                    Glide.with(ShuPingClickActivity.this).load(R.drawable.dianzhan).into(shupingYue_click_dianzhan);
                }
            }
        });
        if(json.getHit()==0){
            Glide.with(ShuPingClickActivity.this).load(R.drawable.dianzhan).into(shupingYue_click_dianzhan);
        }else if (json.getHit()==1){
            Glide.with(ShuPingClickActivity.this).load(R.drawable.dianzhan_have).into(shupingYue_click_dianzhan);
        }
        List<UserIn> newsList = LitePal.where("i =?", "1").find(UserIn.class);
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                userName=newsList.get(0).getUserName();
            }
        }else if(newsList != null && newsList.size()>=2){
            Toast.makeText(getApplicationContext(), "错误！", Toast.LENGTH_SHORT).show();
        }
        TextView shupingYue_click_dianzhanNums=findViewById(R.id.shupingYue_click_dianzhanNums);
        shupingYue_click_dianzhanNums.setText(json.getHitNum()+"");
        TextView shupingYue_click_content=findViewById(R.id.shupingYue_click_content);
        shupingYue_click_content.setText(json.getContent());
        TextView toolbar_my_review_click_delete=findViewById(R.id.toolbar_my_review_click_delete);
        toolbar_my_review_click_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete();
            }
        });
    }
    public void hit(int hit, int adid){
        if (NetWork.isNetworkConnected(ShuPingClickActivity.this)){
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/book/hit?adid="+adid+"&userName="+userName+"&hit="+hit, new Callback() {
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
                            JsonMsg j = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(responseData, JsonMsg.class);
                            if (j.getStatus() == 200) {
                                Toast.makeText(ShuPingClickActivity.this,j.getMsg(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置",Toast.LENGTH_LONG).show();
        }
    }
    private void delete(){
        if (NetWork.isNetworkConnected(ShuPingClickActivity.this)){
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/book/deleteadvise?adid="+json.getAdId(), new Callback() {
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
                            JsonMsg j = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(responseData, JsonMsg.class);
                            if (j.getStatus() == 200) {
                                Toast.makeText(ShuPingClickActivity.this,j.getMsg(),Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置",Toast.LENGTH_LONG).show();
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