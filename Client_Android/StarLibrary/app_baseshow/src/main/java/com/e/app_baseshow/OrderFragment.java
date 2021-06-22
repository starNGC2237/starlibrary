package com.e.app_baseshow;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.mylibrary.Book;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javaClassBase.BookOrderAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class OrderFragment extends Fragment {
    BaseTActivity activity;
    int card = 0;
    private List<Book> books=new ArrayList<>();
    @SuppressLint("InflateParams")
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_order,null);
            //设置toolbar
            Toolbar toolbar=view.findViewById(R.id.toolbar);
            activity = (BaseTActivity) getActivity();
            if (activity != null) {
                activity.setSupportActionBar(toolbar);
                Objects.requireNonNull(activity.getSupportActionBar()).setDisplayShowTitleEnabled(false);
                ActionBar actionBar=activity.getSupportActionBar();
                if(actionBar!=null){
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    actionBar.setHomeAsUpIndicator(R.drawable.menu);
                }
            }
            searchBook();
            return view;
        }

    @Override
    public void onResume() {
        searchBook();
        super.onResume();
    }

    //提交订单
    public void appointment(String orderName){
        Toast.makeText(activity,"请稍等，订单正在提交",Toast.LENGTH_LONG).show();
        if (NetWork.isNetworkConnected(activity)){
            Thread one=new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/bookBorrow/register?orderName="+orderName, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            e.printStackTrace();
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            final String responseData = Objects.requireNonNull(response.body()).string();
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    JsonMsg jsonMsg = new GsonBuilder()
                                            .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                            .create()
                                            .fromJson(responseData, JsonMsg.class);
                                    if (jsonMsg.getStatus()==200){
                                        Toast.makeText(activity,jsonMsg.getMsg(),Toast.LENGTH_SHORT).show();
                                        searchBook();
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
            Toast.makeText(OrderFragment.this.getContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
        }
    }

    public void searchBook() {
            if (NetWork.isNetworkConnected(activity)){
                List<UserIn> newsList = LitePal.where("i =?", "1").find(UserIn.class);
                if(newsList != null && newsList.size()==1){
                    for (int i=0;i<newsList.size();i++){
                        if (newsList.get(i).getCard()!=0){
                            card=newsList.get(i).getCard();
                        }else {
                            card = 0;
                        }
                    }
                }else{
                    Toast.makeText(activity, "error1", Toast.LENGTH_SHORT).show();
                }
                HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/bookBorrow/orderdetail?card="+card, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        final String responseData = Objects.requireNonNull(response.body()).string();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                JsonMsgOrder jsonMsgOrder = new GsonBuilder()
                                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                        .create()
                                        .fromJson(responseData, JsonMsgOrder.class);
                                if (jsonMsgOrder.getStatus()==200){
                                    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(activity);
                                    RecyclerView rv =activity.findViewById(R.id.recycler_view_order);
                                    BookOrderAdapter adapter = new BookOrderAdapter(jsonMsgOrder.getData(),OrderFragment.this,card);
                                    rv.setAdapter(adapter);
                                    rv.setLayoutManager(linearLayoutManager);
                                    rv.setItemAnimator(new DefaultItemAnimator());
                                }
                            }
                        });
                    }
                });
            }else {
                Toast.makeText(OrderFragment.this.getContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
            }
        }
}