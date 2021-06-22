package com.e.app_yueshuquan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.e.mylibrary.JsonMsg;
import com.e.mylibrary.JsonOrder.JsonMsgOrder;
import com.e.mylibrary.JsonPageReview.JsonMsgReview;
import com.e.mylibrary.JsonPageReview.JsonReview;
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
import java.util.Timer;
import java.util.TimerTask;

import javaClassYue.MyReviewsAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Route(path = "/app_yueshuquan/MyReviewActivity")
public class MyReviewActivity extends BaseTActivity {

    String userName;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_review);
        final Toolbar toolbar = findViewById(R.id.toolbar_my_review);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        ImageView NewReview_my_review=findViewById(R.id.NewReview_my_review);
        NewReview_my_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ARouter.getInstance().build("/app_yueshuquan/NewReviewActivity").navigation();
            }
        });
        List<UserIn> newsList = LitePal.where("i =?", "1").find(UserIn.class);
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                userName=newsList.get(0).getUserName();
            }
        }else if(newsList != null && newsList.size()>=2){
            Toast.makeText(getApplicationContext(), "错误！", Toast.LENGTH_SHORT).show();
        }
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFruits();
            }
        });
        load();
    }
    private void refreshFruits() {
        Timer timer=new Timer();
        //创建TimerTask对象，用于实现界面与主界面的跳转
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                load();
                swipeRefreshLayout.setRefreshing(false);
            }
        };
        //2s后跳转
        timer.schedule(timerTask,2000);
    }
    private void load(){
        if (NetWork.isNetworkConnected(MyReviewActivity.this)){
            Log.d("TAG2", "load: "+userName);
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/book/myadvise?userName="+userName, new Callback() {
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
                            JsonMsgReview jsonMsgReview = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(responseData, JsonMsgReview.class);
                            if (jsonMsgReview.getStatus() == 200) {
                                Log.d("TAG", jsonMsgReview.getData().getList().size()+"");
                                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(MyReviewActivity.this);
                                RecyclerView rv =findViewById(R.id.recycler_view_my_review);
                                MyReviewsAdapter adapter = new MyReviewsAdapter(jsonMsgReview,MyReviewActivity.this);
                                rv.setAdapter(adapter);
                                rv.setLayoutManager(linearLayoutManager);
                                rv.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    });
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置",Toast.LENGTH_LONG).show();
        }
    }
    public void hit(int hit, int adid){
        if (NetWork.isNetworkConnected(MyReviewActivity.this)){
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
                                Toast.makeText(MyReviewActivity.this,j.getMsg(),Toast.LENGTH_SHORT).show();
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