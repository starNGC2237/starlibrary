package com.e.app_message;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.e.mylibrary.JsonMsg;
import com.e.mylibrary.JsonOrder.JsonMsgOrder;
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

import javaclassMessage.OrderMessageAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Route(path = "/app_message/MessageActivity")
public class MessageActivity extends BaseTActivity {
    int card;
    OrderMessageAdapter adapter;
    //重新刷新
    @Override
    protected void onResume() {
        super.onResume();
        borrowdetail();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        final Toolbar toolbar = findViewById(R.id.toolbar_message);
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
                    Toast.makeText(MessageActivity.this,"没有绑定图书证，跳转至绑定界面",Toast.LENGTH_SHORT).show();
                    MessageActivity.this.finish();
                    ARouter.getInstance().build("/app_users/BindUsersActivity").navigation();
                }else {
                    card=newsList.get(i).getCard();
                }
            }
        }else{
            Toast.makeText(this, "出错误了，这一条是不可能被看见的", Toast.LENGTH_SHORT).show();
        }
        borrowdetail();
    }

    private void borrowdetail(){
        if (NetWork.isNetworkConnected(MessageActivity.this)){
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/bookBorrow/borrowdetail?card="+card+"&type=0&flag=0", new Callback() {
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
                            if (jsonMsgOrder.getStatus()==200){
                                RecyclerView rv =findViewById(R.id.message_listview);
                                adapter = new OrderMessageAdapter(jsonMsgOrder.getData(),MessageActivity.this);
                                rv.setAdapter(adapter);
                                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(MessageActivity.this);
                                rv.setLayoutManager(linearLayoutManager);
                                rv.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    });
                }
            });
        }else {
            Toast.makeText(MessageActivity.this,"网络连接不可用，请检查您的网络设置",Toast.LENGTH_LONG).show();
        }
    }
    public void readed(String orderName){
        if (NetWork.isNetworkConnected(MessageActivity.this)){
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/bookBorrow/readed?orderName="+orderName, new Callback() {
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
                            if (jsonMsg.getStatus()==200){
                                Log.d("MSG", jsonMsg.getMsg());
                            }else {
                                Log.d("MSG", jsonMsg.getMsg());
                            }
                        }
                    });
                }
            });
        }else {
            Toast.makeText(MessageActivity.this,"网络连接不可用，请检查您的网络设置",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            this.finish();
            ARouter.getInstance().build("/app_base/BaseActivity").navigation();
        }
        return true;
    }
    //再点一次退出
    @Override
    public void onBackPressed() {
        finish();
        ARouter.getInstance().build("/app_base/BaseActivity").navigation();
    }
}
