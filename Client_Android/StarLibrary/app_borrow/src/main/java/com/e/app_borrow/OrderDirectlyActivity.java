package com.e.app_borrow;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.e.mylibrary.JsonMsg;
import com.e.mylibrary.JsonOrder.JsonMsgOrder;
import com.e.util.BaseTActivity;
import com.e.util.HttpUtil;
import com.e.util.NetWork;
import com.e.util.QRCodeUtil;
import com.google.gson.GsonBuilder;
import com.lxj.xpopup.XPopup;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import javaClassBorrow.BookOrderBorrowAdapter;
import javaClassBorrow.CustomPopupBorrow;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


@Route(path = "/app_borrow/OrderDirectlyActivity")
public class OrderDirectlyActivity extends BaseTActivity {

    String interfaceHave;
    @Autowired
    String orderName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_directly);
        // 调用 inject 方法，如果传递过来的参数含有，这样使用 @Autowired 的会自动解析
        ARouter.getInstance().inject(this);
        final Toolbar toolbar = findViewById(R.id.toolbar_order);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        orderDetail();
        ImageView toolbar_edit_order=findViewById(R.id.toolbar_edit_order);
        toolbar_edit_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new XPopup.Builder(OrderDirectlyActivity.this)
                        .asCustom(new CustomPopupBorrow(OrderDirectlyActivity.this,"","长摁订单中的书籍可以从待提交订单中删除该书籍"))
                        .show();
            }
        });
    }
    private void orderDetail(){
        if (NetWork.isNetworkConnected(this)){
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/bookBorrow/oneorderdetail?orderName="+orderName, new Callback() {
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
                                if (!OrderDirectlyActivity.this.isDestroyed()) {
                                    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(OrderDirectlyActivity.this);
                                    RecyclerView rv =findViewById(R.id.recyclerview_list);
                                    BookOrderBorrowAdapter adapter = new BookOrderBorrowAdapter(jsonMsgOrder.getData(),jsonMsgOrder.getData().get(0).getCard(),OrderDirectlyActivity.this);
                                    rv.setAdapter(adapter);
                                    rv.setLayoutManager(linearLayoutManager);
                                    rv.setItemAnimator(new DefaultItemAnimator());
                                    //生成二维码
                                    ImageView mImageView =findViewById(R.id.ivOrder);
                                    interfaceHave="https://starlibrary.online/haopeng/bookBorrow/directlyregister";
                                    Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap("{\"orderName\":\""+orderName+"\",\"interfaceHave\":\""+interfaceHave+"\"}", 480, 480);
                                    mImageView.setImageBitmap(mBitmap);
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
    //提交订单
    public void deleteorderbynameandid(final String orderName, final int card, final int bookid, final int bookNum){
        Toast.makeText(OrderDirectlyActivity.this,"请稍等，订单书籍正在删除",Toast.LENGTH_LONG).show();
        if (NetWork.isNetworkConnected(OrderDirectlyActivity.this)){
            Thread one=new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/bookBorrow/deleteorderbynameandid?card="+card+"&orderName="+orderName+"&bookid="+bookid, new Callback() {
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
                                        if (bookNum==1){
                                            Toast.makeText(OrderDirectlyActivity.this,"订单中已经没有书籍",Toast.LENGTH_SHORT).show();
                                            finish();
                                        }else {
                                            Toast.makeText(OrderDirectlyActivity.this,jsonMsg.getMsg(),Toast.LENGTH_SHORT).show();
                                            orderDetail();
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
            });
            if (!one.isAlive()){
                one.start();
            }
        }else {
            Toast.makeText(OrderDirectlyActivity.this, "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
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