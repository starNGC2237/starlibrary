package com.e.app_yueshuquan;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.e.mylibrary.Book;
import com.e.mylibrary.Search.JsonMsgSearch;
import com.e.util.BaseTActivity;
import com.e.util.HttpUtil;
import com.e.util.NetWork;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javaClassYue.SearchBookYueAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
@Route(path = "/app_yueshuquan/SearchBookYueActivity")
public class SearchBookYueActivity extends BaseTActivity {
    String info="?tab=3&content=";
    private List<Book> books=new ArrayList<>();
    SearchBookYueAdapter adapter;
    @Autowired
    String content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book_yue);
        // 调用 inject 方法，如果传递过来的参数含有，这样使用 @Autowired 的会自动解析
        ARouter.getInstance().inject(this);
        //软键盘适应
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //设置toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        //点击取消
        TextView back=findViewById(R.id.cancel_book);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //设置tabLayout
        TabLayout tabLayout=findViewById(R.id.tabs);
        final TabLayout.Tab tab1 = tabLayout.newTab();
        tabLayout.addTab(tab1.setText("按名称搜索"));
        final TabLayout.Tab tab2 = tabLayout.newTab();
        tabLayout.addTab(tab2.setText("按著者搜索"));
        final EditText search_input=findViewById(R.id.search_ED);
        search_input.setText(content);
        sendNeed();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab==tab1){
                    info="?tab=3&content=";
                    sendNeed();
                }else if (tab==tab2){
                    info="?tab=4&content=";
                    sendNeed();
                }else {
                    Toast.makeText(SearchBookYueActivity.this,"错误！",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        search_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) SearchBookYueActivity.this
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
        final EditText search_input=findViewById(R.id.search_ED);
        content=search_input.getText().toString();
        if (NetWork.isNetworkConnected(SearchBookYueActivity.this)){
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/book/querybook"+info+content, new Callback() {
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
                                adapter = new SearchBookYueAdapter(books);
                                rv.setAdapter(adapter);
                                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(SearchBookYueActivity.this);
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