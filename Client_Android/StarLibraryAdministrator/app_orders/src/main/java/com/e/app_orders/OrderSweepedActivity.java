package com.e.app_orders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.e.JavaClass.JsonMsg;
import com.e.JavaClass.JsonOrder.JsonMsgOrder;
import com.e.JavaClass.UserMangers;
import com.e.mylibrary.HttpUtil;
import com.e.mylibrary.MyActivity;
import com.e.mylibrary.NetWork;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javaclassOrders.OrderSweepedOrderAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Route(path = "/app_orders/OrderSweepedActivity")
public class OrderSweepedActivity extends MyActivity {
    String floor;
    @Autowired
    String orderName;
    @Autowired
    String interfaceHave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_sweeped);
        // 调用 inject 方法，如果传递过来的参数含有，这样使用 @Autowired 的会自动解析
        ARouter.getInstance().inject(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        List<UserMangers> newsList = LitePal.where("i =?", "1").find(UserMangers.class);
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                floor=newsList.get(i).getFloor();
            }
        }else {
            Toast.makeText(OrderSweepedActivity.this,"错误！",Toast.LENGTH_LONG).show();
        }
        TextView book_order_header_orderName=findViewById(R.id.book_order_header_orderName);
        book_order_header_orderName.setText("订单名："+orderName);
        orderDetail();
    }
    public void sweeped(int borrowid){
        if (NetWork.isNetworkConnected(this)){
            String q=interfaceHave+"?borrowid="+borrowid;
            HttpUtil.sendOkHttpRequest(q, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                    final String responseData = Objects.requireNonNull(response.body()).string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonMsg jsonMsg = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(responseData, JsonMsg.class);
                            if (jsonMsg.getStatus()==200){
                                if (!OrderSweepedActivity.this.isDestroyed()) {
                                    Toast.makeText(OrderSweepedActivity.this,jsonMsg.getMsg(),Toast.LENGTH_LONG).show();
                                    orderDetail();
                                }
                            }
                        }
                    });
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
        }
    }
    public void toast(String str){
        Toast.makeText(OrderSweepedActivity.this,str,Toast.LENGTH_LONG).show();
    }
    private void orderDetail(){
        if (NetWork.isNetworkConnected(this)){
            String q="https://starlibrary.online/haopeng/bookBorrow/detail?orderName="+orderName;
            Log.d("AAAAAAAAAAAAAAAAA", q);
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/bookBorrow/detail?orderName="+orderName, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                    final String responseData = Objects.requireNonNull(response.body()).string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonMsgOrder jsonMsgOrder = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(responseData, JsonMsgOrder.class);
                            if (jsonMsgOrder.getStatus()==200){
                                if (!OrderSweepedActivity.this.isDestroyed()) {
                                    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(OrderSweepedActivity.this);
                                    RecyclerView rv =findViewById(R.id.recycler_view);
                                    OrderSweepedOrderAdapter adapter = new OrderSweepedOrderAdapter(jsonMsgOrder.getData(),OrderSweepedActivity.this);
                                    rv.setAdapter(adapter);
                                    rv.setLayoutManager(linearLayoutManager);
                                    rv.setItemAnimator(new DefaultItemAnimator());
                                }
                            }
                        }
                    });
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
        }
    }
    //设置返回键
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        }
        return true;
    }
}