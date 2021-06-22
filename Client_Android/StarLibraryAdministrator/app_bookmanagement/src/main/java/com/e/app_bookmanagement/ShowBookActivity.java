package com.e.app_bookmanagement;


import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.e.JavaClass.Book;
import com.e.JavaClass.JsonMsgSearch;
import com.e.JavaClass.JsonRootBean;
import com.e.JavaClass.UserMangers;
import com.e.mylibrary.HttpUtil;
import com.e.mylibrary.MyActivity;
import com.e.mylibrary.NetWork;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import javaClassBook.BookSearchNewBookAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Route(path = "/app_bookmanagement/ShowBookActivity")
public class ShowBookActivity extends MyActivity {
    String content="";
    String floor;
    private List<Book> books=new ArrayList<>();
    BookSearchNewBookAdapter adapter;

    @Override
    protected void onResume() {
        super.onResume();
        Timer timer=new Timer();
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                sendNeed();
            }
        };
        //1s后跳转
        timer.schedule(timerTask,1000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_book);
        //软键盘适应
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //设置toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_showBook);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        //点击取消
        TextView back=findViewById(R.id.cancel_showBook);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sendNeed();
        final EditText search_input=findViewById(R.id.search_ED);
        search_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) ShowBookActivity.this
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(search_input.getWindowToken(), 0);
                    sendNeed();
                    return true;
                }
                return false;
            }
        });
    }
    private void sendNeed() {
        List<UserMangers> newsList = LitePal.where("i =?", "1").find(UserMangers.class);
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                floor=newsList.get(i).getFloor();
            }
        }else {
            Toast.makeText(getApplicationContext(), "错误！", Toast.LENGTH_LONG).show();
        }
        final EditText search_input=findViewById(R.id.search_ED);
        content=search_input.getText().toString();
        if (NetWork.isNetworkConnected(ShowBookActivity.this)){
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/book/quertbookoffloor?floor="+floor+"&content="+content, new Callback() {
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
                            JsonMsgSearch jsonMsgPagination = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(responseData, JsonMsgSearch.class);
                            if (jsonMsgPagination.getStatus()==200){
                                books=jsonMsgPagination.getData();
                                RecyclerView rv =findViewById(R.id.list_options);
                                adapter = new BookSearchNewBookAdapter(books);
                                rv.setAdapter(adapter);
                                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(ShowBookActivity.this);
                                rv.setLayoutManager(linearLayoutManager);
                                rv.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    });
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
        }
    }
}