package com.e.app_borrow;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.e.mylibrary.JsonMsg;
import com.e.mylibrary.JsonOrder.JsonMsgOrder;
import com.e.mylibrary.UserIn;
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

import javaClassBorrow.BookOrderBorrowAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
@Route(path = "/app_borrow/BorrowedBorrowActivity")
public class BorrowedActivity extends AppCompatActivity {
    SwipeRefreshLayout swipeRefreshLayout;
    int card=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrowed);
        List<UserIn> newsList = LitePal.where("i =?", "1").find(UserIn.class);
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                if (newsList.get(i).getCard()==0){
                    card=0;
                }else {
                    card=newsList.get(i).getCard();
                }
            }
        }else{
            Toast.makeText(this, "出错误了，这一条是不可能被看见的", Toast.LENGTH_SHORT).show();
        }
        TextView toolbar_title_order_clear=findViewById(R.id.toolbar_title_order_clear);
        toolbar_title_order_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearHistory();
            }
        });
        collectiondetail();
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFruits();
            }
        });
    }
    private void refreshFruits() {
        Timer timer=new Timer();
        //创建TimerTask对象，用于实现界面与主界面的跳转
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                collectiondetail();
                swipeRefreshLayout.setRefreshing(false);
            }
        };
        //2s后跳转
        timer.schedule(timerTask,2000);
    }
    @Override
    protected void onResume() {
        super.onResume();
        collectiondetail();
    }

    private void collectiondetail(){
        if (NetWork.isNetworkConnected(this)){
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/bookBorrow/borrowdetail?card="+card+"&type=2", new Callback() {
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
                            JsonMsgOrder jsonMsgOrder = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(responseData, JsonMsgOrder.class);
                            if (jsonMsgOrder.getStatus() == 200) {
                                LinearLayout wait_time_out=findViewById(R.id.wait_borrowed);
                                wait_time_out.setVisibility(View.GONE);
                                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(BorrowedActivity.this);
                                RecyclerView rv =findViewById(R.id.borrowed_recyclerview);
                                BookOrderBorrowAdapter adapter = new BookOrderBorrowAdapter(jsonMsgOrder.getData(),card);
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

    private void clearHistory(){
        if (NetWork.isNetworkConnected(this)){
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/bookBorrow/deletereturn?card="+card+"&type=2", new Callback() {
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
                                Toast.makeText(BorrowedActivity.this, jsonMsg.getMsg(), Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(BorrowedActivity.this, jsonMsg.getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置",Toast.LENGTH_LONG).show();
        }
    }
}