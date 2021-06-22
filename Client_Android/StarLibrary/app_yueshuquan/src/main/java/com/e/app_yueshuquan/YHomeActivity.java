package com.e.app_yueshuquan;


import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.e.mylibrary.Book;
import com.e.mylibrary.JsonMsg;
import com.e.mylibrary.JsonMsgDataVersion;
import com.e.mylibrary.JsonPageReview.JsonMsgReview;
import com.e.mylibrary.Pagination.JsonMsgPagination;
import com.e.mylibrary.UserIn;
import com.e.util.BaseTActivity;
import com.e.util.HttpUtil;
import com.e.util.NetWork;
import com.google.gson.GsonBuilder;


import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javaClassYue.BookSuDiAdapter;
import javaClassYue.YueHomeAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Route(path = "/app_yueshuquan/YHomeActivity")
public class YHomeActivity extends BaseTActivity {
    YueHomeAdapter adapter;
    Book loadBook;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_y_home);
        List<UserIn> newsList = LitePal.where("i =?", "1").find(UserIn.class);
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                userName=newsList.get(0).getUserName();
            }
        }else if(newsList != null && newsList.size()>=2){
            Toast.makeText(getApplicationContext(), "错误！", Toast.LENGTH_SHORT).show();
        }
        load();
        final EditText search_input=findViewById(R.id.search_ED);
        search_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) YHomeActivity.this
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(search_input.getWindowToken(), 0);
                    ARouter.getInstance().build("/app_yueshuquan/SearchBookYueActivity")
                            .withString("content",search_input.getText().toString())
                            .navigation();
                    return true;
                }
                return false;
            }
        });
    }
    public void hit(int hit, int adid){
        if (NetWork.isNetworkConnected(YHomeActivity.this)){
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
                                Toast.makeText(YHomeActivity.this,j.getMsg(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置",Toast.LENGTH_LONG).show();
        }
    }
    public void load(){
        if (NetWork.isNetworkConnected(YHomeActivity.this)){
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/book/querybook?tab=0", new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String data = Objects.requireNonNull(response.body()).string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonMsgPagination jsonMsg = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(data, JsonMsgPagination.class);
                            if (jsonMsg.getStatus() == 200) {
                                loadBook=jsonMsg.getData().getList().get(0);
                                if (NetWork.isNetworkConnected(YHomeActivity.this)){
                                    HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/book/advise?userName="+userName, new Callback() {
                                        @Override
                                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                            e.printStackTrace();
                                        }

                                        @Override
                                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                            final String data = Objects.requireNonNull(response.body()).string();
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    JsonMsgReview jsonMsg = new GsonBuilder()
                                                            .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                                            .create()
                                                            .fromJson(data, JsonMsgReview.class);
                                                    if (jsonMsg.getStatus() == 200) {
                                                        RecyclerView rv =findViewById(R.id.y_home_rec);
                                                        adapter = new YueHomeAdapter(loadBook,jsonMsg.getData(),YHomeActivity.this);
                                                        rv.setAdapter(adapter);
                                                        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(YHomeActivity.this);
                                                        rv.setLayoutManager(linearLayoutManager);
                                                        rv.setItemAnimator(new DefaultItemAnimator());
                                                    }else {
                                                        Toast.makeText(YHomeActivity.this,"error123",Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }else {
                                    Toast.makeText(YHomeActivity.this,"网络连接不可用，请检查您的网络设置",Toast.LENGTH_LONG).show();
                                }
                            }else {
                                Toast.makeText(YHomeActivity.this,"error123",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
        }else {
            Toast.makeText(YHomeActivity.this,"网络连接不可用，请检查您的网络设置",Toast.LENGTH_LONG).show();
        }
    }
}
