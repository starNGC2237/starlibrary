package com.e.app_book;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.e.mylibrary.JsonMsg;
import com.e.mylibrary.JsonOrder.JsonMsgOrder;
import com.e.mylibrary.SingleBook.JsonSingleBook;
import com.e.mylibrary.UserIn;
import com.e.util.BaseTActivity;
import com.e.util.HttpUtil;
import com.e.util.NetWork;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Route(path = "/app_book/BookActivity")
public class BookActivity extends BaseTActivity {
    int card;
    Boolean had=false;
    @Autowired
    int bookId;
    @Autowired
    String floor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        // 调用 inject 方法，如果传递过来的参数含有，这样使用 @Autowired 的会自动解析
        ARouter.getInstance().inject(this);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        List<UserIn> newsList = LitePal.where("i =?", "1").find(UserIn.class);
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                if (newsList.get(i).getCard()==0){
                    card=0;
                    Toast.makeText(BookActivity.this,"没有绑定图书证，跳转至绑定界面",Toast.LENGTH_SHORT).show();
                    BookActivity.this.finish();
                    ARouter.getInstance().build("/app_users/BindUsersActivity").navigation();
                }else {
                    card=newsList.get(i).getCard();
                }

            }
        }else{
            Toast.makeText(this, "出错误了，这一条是不可能被看见的", Toast.LENGTH_SHORT).show();
        }
        if (card!=0){
            judgec();
            judgeo();
        }
        LinearLayout book_favorites=findViewById(R.id.book_favorites);
        book_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(had){
                    deletecollection();
                    Toast.makeText(BookActivity.this,"已取消收藏",Toast.LENGTH_SHORT).show();
                }else {
                    favorites();
                    Toast.makeText(BookActivity.this,"已收藏",Toast.LENGTH_SHORT).show();
                }

            }
        });
        sendNeed();

        LinearLayout add_to_order=findViewById(R.id.book_addToOrder);
        add_to_order.setClickable(false);
        add_to_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderlist();
            }
        });
        add_to_order.setClickable(false);
        LinearLayout book_borrowNow=findViewById(R.id.book_borrowNow);
        book_borrowNow.setClickable(false);
        book_borrowNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNow();
                LinearLayout add_to_order=findViewById(R.id.book_addToOrder);
                add_to_order.setClickable(false);
                TextView add_to_order_text=findViewById(R.id.book_addToOrder_text);
                add_to_order_text.setText("已预约");
                LinearLayout book_borrowNow=findViewById(R.id.book_borrowNow);
                book_borrowNow.setClickable(false);
                TextView book_borrowNow_text=findViewById(R.id.book_borrowNow_text);
                book_borrowNow_text.setText("已预约");
            }
        });
        book_borrowNow.setClickable(false);
    }
    //加入订单
    private void orderSec(final String orderName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NetWork.isNetworkConnected(BookActivity.this)){
                        HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/bookBorrow/order?card=" + card + "&bookid=" + bookId + "&floor=" + floor + "&orderName=" + orderName, new Callback() {
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
                                        if (jsonMsg.getStatus() == 200) {
                                            Toast.makeText(BookActivity.this, jsonMsg.getMsg(), Toast.LENGTH_SHORT).show();
                                            judgeo();
                                        }
                                    }
                                });
                            }
                        });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }
    //没有订单，创建并加入
    private void orderOne(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NetWork.isNetworkConnected(BookActivity.this)){
                        HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/bookBorrow/order?card="+card+"&bookid="+bookId+"&floor="+floor, new Callback() {
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
                                            Toast.makeText(BookActivity.this,jsonMsg.getMsg(),Toast.LENGTH_SHORT).show();
                                            judgeo();
                                        }
                                    }
                                });
                            }
                        });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }
    //判断借书车中的订单数量
    private void orderlist(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NetWork.isNetworkConnected(BookActivity.this)){
                        HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/bookBorrow/orderlist?card="+card, new Callback() {
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
                                        JsonMsgOrder jsonMsg = new GsonBuilder()
                                                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                                .create()
                                                .fromJson(responseData, JsonMsgOrder.class);
                                        if (jsonMsg.getStatus()==200){
                                            if (jsonMsg.getData()==null||jsonMsg.getData().size()==0){
                                                orderOne();
                                            }else {
                                                // 创建数据
                                                final String[] items = new String[jsonMsg.getData().size()+1];
                                                for (int i=0;i<jsonMsg.getData().size();i++){
                                                    items[i]=jsonMsg.getData().get(i).getOrderName();
                                                }
                                                items[jsonMsg.getData().size()]="创建一个订单并加入";
                                                // 创建对话框构建器
                                                AlertDialog.Builder builder = new AlertDialog.Builder(BookActivity.this);
                                                // 设置参数
                                                builder.setIcon(R.mipmap.ic_launcher).setTitle("加入一个订单")
                                                        .setItems(items, new DialogInterface.OnClickListener() {

                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                if (items[which].equals("创建一个订单并加入")){
                                                                    orderOne();
                                                                }else {
                                                                    orderSec(items[which]);
                                                                }
                                                            }
                                                        });
                                                builder.create().show();
                                            }
                                        }
                                    }
                                });
                            }
                        });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }
    //1判断是否在订单里
    private void judgeo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NetWork.isNetworkConnected(BookActivity.this)){
                        HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/bookBorrow/judgeo?card=" + card + "&bookid=" + bookId, new Callback() {
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
                                        if (jsonMsg.getStatus() == 200) {
                                            LinearLayout add_to_order = findViewById(R.id.book_addToOrder);
                                            add_to_order.setClickable(false);
                                            TextView add_to_order_text = findViewById(R.id.book_addToOrder_text);
                                            add_to_order_text.setText("已在借书车中");
                                            LinearLayout book_borrowNow = findViewById(R.id.book_borrowNow);
                                            book_borrowNow.setClickable(false);
                                            TextView book_borrowNow_text = findViewById(R.id.book_borrowNow_text);
                                            book_borrowNow_text.setText("已在借书车中");
                                        } else if (jsonMsg.getStatus() == 404) {
                                            judgeBook();
                                        }
                                    }
                                });
                            }
                        });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }
    //立即借阅
    private void registerNow(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NetWork.isNetworkConnected(BookActivity.this)){
                        HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/bookBorrow/register?card=" + card + "&bookid=" + bookId + "&floor=" + floor, new Callback() {
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
                                        if (jsonMsg.getStatus() == 200) {
                                            Toast.makeText(BookActivity.this, jsonMsg.getMsg(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        }).start();
    }
    //删除收藏
    private void deletecollection(){
        if (NetWork.isNetworkConnected(BookActivity.this)){
                HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/bookBorrow/deletecollection?card=" + card + "&bookid=" + bookId, new Callback() {
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
                                if (jsonMsg.getStatus() == 200) {
                                    Toast.makeText(BookActivity.this, jsonMsg.getMsg(), Toast.LENGTH_LONG).show();
                                    had = false;
                                    ImageView favorites = findViewById(R.id.book_image_favorites);
                                    Glide.with(BookActivity.this).load(R.drawable.ic_fa).into(favorites);
                                } else {
                                    Toast.makeText(BookActivity.this, "status:" + jsonMsg.getStatus() + "\nmsg:" + jsonMsg.getMsg(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
        }else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
        }
    }
    //判断是否被收藏
    private void judgec(){
        if (NetWork.isNetworkConnected(BookActivity.this)){
                HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/bookBorrow/judgec?card=" + card + "&bookid=" + bookId, new Callback() {
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
                                if (jsonMsg.getStatus() == 200) {
                                    had = true;
                                    ImageView favorites = findViewById(R.id.book_image_favorites);
                                    Glide.with(BookActivity.this).load(R.drawable.ic_fa_had).into(favorites);
                                }
                            }
                        });
                    }
                });
        }else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
        }
    }
    //2判断书籍状态
    private void judgeBook(){
        if (NetWork.isNetworkConnected(BookActivity.this)){
                HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/bookBorrow/judge?card=" + card + "&bookid=" + bookId, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        final String responseData = Objects.requireNonNull(response.body()).string();
                        runOnUiThread(new Runnable() {
                            @SuppressLint("ResourceAsColor")
                            @Override
                            public void run() {
                                JsonMsg jsonMsg = new GsonBuilder()
                                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                        .create()
                                        .fromJson(responseData, JsonMsg.class);
                                if (jsonMsg.getStatus() == 200) {
                                        LinearLayout add_to_order = findViewById(R.id.book_addToOrder);
                                        add_to_order.setClickable(false);
                                        TextView add_to_order_text = findViewById(R.id.book_addToOrder_text);
                                        add_to_order_text.setText(jsonMsg.getMsg());
                                        LinearLayout book_borrowNow = findViewById(R.id.book_borrowNow);
                                        book_borrowNow.setClickable(false);
                                        TextView book_borrowNow_text = findViewById(R.id.book_borrowNow_text);
                                        book_borrowNow_text.setText(jsonMsg.getMsg());
                                }else {
                                    LinearLayout add_to_order = findViewById(R.id.book_addToOrder);
                                    add_to_order.setClickable(true);
                                    LinearLayout book_borrowNow = findViewById(R.id.book_borrowNow);
                                    book_borrowNow.setClickable(true);
                                }
                            }
                        });
                    }
                });
        }else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
        }
    }
    //添加到收藏夹
    private void favorites(){
        if (NetWork.isNetworkConnected(BookActivity.this)){
                HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/bookBorrow/bookcollection?card=" + card + "&bookid=" + bookId, new Callback() {
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
                                if (jsonMsg.getStatus() == 200) {
                                    Toast.makeText(BookActivity.this, jsonMsg.getMsg(), Toast.LENGTH_LONG).show();
                                    had = true;
                                    ImageView favorites = findViewById(R.id.book_image_favorites);
                                    Glide.with(BookActivity.this).load(R.drawable.ic_fa_had).into(favorites);
                                } else {
                                    Toast.makeText(BookActivity.this, "status:" + jsonMsg.getStatus() + "\nmsg:" + jsonMsg.getMsg(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
        }else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
        }
    }
    //根据ID搜索
    private void sendNeed(){
        if (NetWork.isNetworkConnected(this)){
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/book/query?bookId="+bookId, new Callback() {
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
                            JsonSingleBook jsonSingleBook = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(responseData, JsonSingleBook.class);
                            if (jsonSingleBook.getStatus()==200){
                                ImageView imageView=findViewById(R.id.book_image_view);
                                if (!BookActivity.this.isDestroyed()) {
                                    Glide.with(BookActivity.this).load(jsonSingleBook.getData().getSrc()).into(imageView);
                                    CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.coll_book);
                                    collapsingToolbarLayout.setTitle(jsonSingleBook.getData().getBookName());
                                    collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.transparent));
                                    TextView infoBook = findViewById(R.id.book_content_text);
                                    String info = "书籍编号：" + jsonSingleBook.getData().getBookId()+ "\n\n书名：" + jsonSingleBook.getData().getBookName() + "\n\n著者：" + jsonSingleBook.getData().getBookAuthor() + "\n\n出版社：" + jsonSingleBook.getData().getBookPublish() + "\n\n出版日期：" + jsonSingleBook.getData().getPublishTime() + "\n\n库存：" + jsonSingleBook.getData().getBookNumber() + "\n\n页数：" + jsonSingleBook.getData().getBookPage() + "\n\n书籍类型：" + jsonSingleBook.getData().getType() + "\n\n馆藏地址：" + jsonSingleBook.getData().getBookPlace() + "\n\n加入时间：" + jsonSingleBook.getData().getAddTime()  + "\n\n书籍简介：" + jsonSingleBook.getData().getBookIntroduction();
                                    infoBook.setText(info);
                                    if(jsonSingleBook.getData().getBookNumber()<=0){
                                        LinearLayout add_to_order = findViewById(R.id.book_addToOrder);
                                        add_to_order.setClickable(false);
                                        TextView add_to_order_text = findViewById(R.id.book_addToOrder_text);
                                        add_to_order_text.setText("没有库存");
                                        LinearLayout book_borrowNow = findViewById(R.id.book_borrowNow);
                                        book_borrowNow.setClickable(false);
                                        TextView book_borrowNow_text = findViewById(R.id.book_borrowNow_text);
                                        book_borrowNow_text.setText("没有库存");
                                    }
                                    floor=jsonSingleBook.getData().getBookPlace();
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
