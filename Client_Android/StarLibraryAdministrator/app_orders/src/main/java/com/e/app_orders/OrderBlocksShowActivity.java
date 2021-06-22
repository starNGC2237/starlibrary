package com.e.app_orders;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.e.JavaClass.Block;
import com.e.JavaClass.JsonOrderInterface;
import com.e.JavaClass.UserMangers;
import com.e.mylibrary.MyActivity;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import javaclassOrders.BlockOrderShowBlockAdapter;

@Route(path = "/app_orders/OrderBlocksShowActivity")
public class OrderBlocksShowActivity extends MyActivity {
    String floor;
    GridLayoutManager layoutManager=null;
    BlockOrderShowBlockAdapter adapter;
    private List<Block> blocks=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_blocks_show);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        initData();
        layoutManager = new GridLayoutManager(OrderBlocksShowActivity.this, 2);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BlockOrderShowBlockAdapter(blocks,OrderBlocksShowActivity.this);
        recyclerView.setAdapter(adapter);
    }
    private void initData() {
        Block addBook = new Block("预约中订单准备书籍管理", R.drawable.order_ready);
        blocks.add(addBook);
        Block editBook = new Block("预约中订单取书籍管理", R.drawable.order_for);
        blocks.add(editBook);
        Block borrowOut = new Block("取书完成确认借阅", R.drawable.ic_order_ok);
        blocks.add(borrowOut);
        Block borrowIn = new Block("还书", R.drawable.ic_order_ok);
        blocks.add(borrowIn);
        Block directly = new Block("直接借阅", R.drawable.ic_order_ok);
        blocks.add(directly);
    }
    public void toast(String str){
        Toast.makeText(OrderBlocksShowActivity.this,str,Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "扫码被取消了", Toast.LENGTH_SHORT).show();
            } else {
                Pattern pattern = Pattern.compile("[0-9]*");
                if (!pattern.matcher(result.getContents()).matches()){
                    JsonOrderInterface jsonMsg = new Gson()
                            .fromJson(result.getContents(), JsonOrderInterface.class);
                    List<UserMangers> newsList = LitePal.where("i =?", "1").find(UserMangers.class);
                    if (newsList != null && newsList.size() == 1) {
                        for (int i = 0; i < newsList.size(); i++) {
                            floor = newsList.get(i).getFloor();
                        }
                    } else {
                        Toast.makeText(OrderBlocksShowActivity.this, "错误！", Toast.LENGTH_LONG).show();
                    }
                    switch (jsonMsg.getInterfaceHave()) {
                        case "https://starlibrary.online/haopeng/bookBorrow/sweeped":
                            ARouter.getInstance().build("/app_orders/OrderSweepedActivity")
                                    .withString("orderName", jsonMsg.getOrderName())
                                    .withString("interfaceHave", jsonMsg.getInterfaceHave())
                                    .navigation();
                            break;
                        case "https://starlibrary.online/haopeng/bookBorrow/registercheck?type=1": {
                            if (floor.equals("ALL")) {
                                ARouter.getInstance().build("/app_orders/OrderBorrowOutActivity")
                                        .withString("orderName", jsonMsg.getOrderName())
                                        .withString("interfaceHave", jsonMsg.getInterfaceHave())
                                        .navigation();
                            } else {
                                Toast.makeText(OrderBlocksShowActivity.this, "权限不足,订单已经全部取书，需要总管理员确认", Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case "https://starlibrary.online/haopeng/bookBorrow/registercheck?type=2": {
                            if (floor.equals("ALL")) {
                                ARouter.getInstance().build("/app_orders/OrderBorrowInActivity")
                                        .withString("orderName", jsonMsg.getOrderName())
                                        .withString("interfaceHave", jsonMsg.getInterfaceHave())
                                        .navigation();
                            } else {
                                Toast.makeText(OrderBlocksShowActivity.this, "权限不足,订单需要总管理员确认还书", Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case "https://starlibrary.online/haopeng/bookBorrow/directlyregister": {
                            if (floor.equals("ALL")) {
                                ARouter.getInstance().build("/app_orders/OrderDirectlyActivity")
                                        .withString("orderName", jsonMsg.getOrderName())
                                        .withString("interfaceHave", jsonMsg.getInterfaceHave())
                                        .navigation();
                            } else {
                                Toast.makeText(OrderBlocksShowActivity.this, "权限不足,订单需要总管理员确认直接借阅", Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                    }
                }else {
                    ARouter.getInstance().build("/app_bookmanagement/BookActivity").withInt("bookId", Integer.parseInt(result.getContents())).navigation();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
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