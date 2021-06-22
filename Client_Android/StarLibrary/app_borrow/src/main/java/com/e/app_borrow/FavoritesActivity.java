package com.e.app_borrow;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.e.mylibrary.Book;
import com.e.mylibrary.JsonCollection.JsonMsgCollection;
import com.e.mylibrary.UserIn;
import com.e.util.HttpUtil;
import com.e.util.NetWork;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javaClassBorrow.BookSearchNewBorrowAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Route(path = "/app_borrow/FavoritesActivity")
public class FavoritesActivity extends AppCompatActivity {
    int card=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = findViewById(R.id.toolbar_favorites);
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
                if (newsList.get(i).getCard()==0){
                    card=0;
                }else {
                    card=newsList.get(i).getCard();
                }
            }
        }else{
            Toast.makeText(this, "出错误了，这一条是不可能被看见的", Toast.LENGTH_SHORT).show();
        }
        collectiondetail();
    }

    @Override
    protected void onResume() {
        super.onResume();
        collectiondetail();
    }

    private void collectiondetail(){
        if (NetWork.isNetworkConnected(this)){
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/bookBorrow/collectiondetail?card="+card, new Callback() {
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
                            JsonMsgCollection jsonMsg = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(responseData, JsonMsgCollection.class);
                            if (jsonMsg.getStatus() == 200) {
                                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(FavoritesActivity.this);
                                RecyclerView rv =findViewById(R.id.favorites_recyclerview);
                                ArrayList<Book> mDatas = new ArrayList<>();
                                if (jsonMsg.getData().size()!=0&&!jsonMsg.getData().isEmpty()){
                                    for (int i=0;i<jsonMsg.getData().size();i++){
                                        mDatas.addAll(jsonMsg.getData().get(i).getBook());
                                    }
                                }
                                BookSearchNewBorrowAdapter adapter = new BookSearchNewBorrowAdapter(mDatas,card);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}