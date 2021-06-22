package com.e.app_borrow;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.e.mylibrary.JsonOrder.JsonMsgOrder;
import com.e.util.BaseTActivity;
import com.e.util.HttpUtil;
import com.e.util.NetWork;
import com.e.util.QRCodeUtil;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Route(path = "/app_borrow/OrderActivity")
public class OrderActivity extends BaseTActivity {
    Boolean swwwped=false;
    String interfaceHave;
    @Autowired
    String orderName;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
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
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFruits();
            }
        });
        orderDetail();
    }
    private void refreshFruits() {
        Timer timer=new Timer();
        //创建TimerTask对象，用于实现界面与主界面的跳转
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                orderDetail();
                swipeRefreshLayout.setRefreshing(false);
            }
        };
        //2s后跳转
        timer.schedule(timerTask,2000);
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
                                if (swwwped){
                                    swwwped=false;
                                }
                                if (!OrderActivity.this.isDestroyed()) {
                                    TextView infoOrder = findViewById(R.id.order_content_text);
                                    String orderType = "";
                                    if (jsonMsgOrder.getData().get(0).getType()==0){
                                        orderType="预约";
                                    }else if(jsonMsgOrder.getData().get(0).getType()==1){
                                        orderType="已借";
                                    }else if (jsonMsgOrder.getData().get(0).getType()==2){
                                        orderType="归还";
                                    }else if (jsonMsgOrder.getData().get(0).getType()==3){
                                        orderType="超时";
                                    }
                                    Set<String> set = new HashSet<>();
                                    for(int i=0;i<jsonMsgOrder.getData().size();i++){
                                        if (jsonMsgOrder.getData().get(i).getSweeped()==0){
                                            swwwped=true;
                                        }
                                        set.add(jsonMsgOrder.getData().get(i).getFloor());
                                    }
                                    List<String> list = new ArrayList<>(set);
                                    StringBuilder sb = new StringBuilder();
                                    for (int i = 0; i < list.size(); i++) {
                                        sb.append(list.get(i));
                                        if (i < list.size() - 1) {
                                            sb.append("，");
                                        }
                                    }
                                    String info = "订单名：" + jsonMsgOrder.getData().get(0).getOrderName() + "\n\n借书证号：" + jsonMsgOrder.getData().get(0).getCard() + "\n\n订单状态：" + orderType+ "\n\n包含楼层：" + sb.toString()+ "\n\n预约开始时间：" + getNowDate(jsonMsgOrder.getData().get(0).getRgTime())+ "\n\n预约结束时间：" + getNowDate(jsonMsgOrder.getData().get(0).getUnrgTime())+ "\n\n借阅开始时间：" + getNowDate(jsonMsgOrder.getData().get(0).getBoTime())+ "\n\n借阅结束时间：" + getNowDate(jsonMsgOrder.getData().get(0).getUnboTime());
                                    infoOrder.setText(info);
                                    //生成二维码
                                    ImageView mImageView =findViewById(R.id.ivOrder);
                                    if (swwwped){
                                        if (!(jsonMsgOrder.getData().get(0).getType()==3&&jsonMsgOrder.getData().get(0).getUnboTime()==null)){
                                            interfaceHave="https://starlibrary.online/haopeng/bookBorrow/sweeped";
                                        }
                                    }else {
                                        if (jsonMsgOrder.getData().get(0).getType()==1||(jsonMsgOrder.getData().get(0).getType()==3&&jsonMsgOrder.getData().get(0).getUnboTime()!=null)){
                                            interfaceHave="https://starlibrary.online/haopeng/bookBorrow/registercheck?type=2";
                                        }else {
                                            if (!(jsonMsgOrder.getData().get(0).getType()==3&&jsonMsgOrder.getData().get(0).getUnboTime()==null)){
                                                interfaceHave="https://starlibrary.online/haopeng/bookBorrow/registercheck?type=1";
                                            }

                                        }

                                    }
                                    Log.d("AAAAAAAAAAAAAAAAA", interfaceHave);
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
    public static String getNowDate(Date date) {
        if (date!=null){
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return formatter.format(date);
        }else {
            return null;
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