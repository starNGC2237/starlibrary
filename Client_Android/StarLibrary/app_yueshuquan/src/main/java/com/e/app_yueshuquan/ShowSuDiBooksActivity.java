package com.e.app_yueshuquan;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.e.mylibrary.Book;
import com.e.mylibrary.Pagination.JsonMsgPagination;
import com.e.util.BaseTActivity;
import com.e.util.HttpUtil;
import com.e.util.NetWork;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

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

@Route(path = "/app_yueshuquan/ShowSuDiBooksActivity")
public class ShowSuDiBooksActivity extends BaseTActivity {
    BookSuDiAdapter adapter;
    private List<Book> bookNews=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_sudi_books);
        final Toolbar toolbar = findViewById(R.id.toolbar_sbs);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        suDiToDo();
    }
    public void suDiToDo(){
        if (NetWork.isNetworkConnected(ShowSuDiBooksActivity.this)) {
                HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/book/querybook?tab=0", new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseData = Objects.requireNonNull(response.body()).string();
                        showResponseDate(responseData);
                    }
                    private void showResponseDate(final String responseData) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                JsonMsgPagination jsonMsgPagination = new GsonBuilder()
                                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                        .create()
                                        .fromJson(responseData, JsonMsgPagination.class);
                                if (jsonMsgPagination.getStatus() == 200) {
                                    bookNews = jsonMsgPagination.getData().getList();
                                    RecyclerView rv =findViewById(R.id.recyclerView_sbs);
                                    adapter = new BookSuDiAdapter(bookNews);
                                    rv.setAdapter(adapter);
                                    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(ShowSuDiBooksActivity.this);
                                    rv.setLayoutManager(linearLayoutManager);
                                    rv.setItemAnimator(new DefaultItemAnimator());
                                }
                            }
                        });
                    }
                });
            } else {
             Toast.makeText(ShowSuDiBooksActivity.this, "网络连接不可用，请检查您的网络设置", Toast.LENGTH_SHORT).show();
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