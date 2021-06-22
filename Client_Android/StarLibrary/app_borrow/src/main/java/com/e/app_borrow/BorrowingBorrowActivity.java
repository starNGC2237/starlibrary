package com.e.app_borrow;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
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

import javaClassBorrow.BookOrderBorrowAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Route(path = "/app_borrow/BorrowingBorrowActivity")
public class BorrowingBorrowActivity extends AppCompatActivity {
    int card=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrowing_borrow);
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
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/bookBorrow/borrowdetail?card="+card+"&type=1", new Callback() {
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
                                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(BorrowingBorrowActivity.this);
                                RecyclerView rv =findViewById(R.id.borrowing_recyclerview);
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
}
