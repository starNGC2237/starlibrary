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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.e.mylibrary.Book;
import com.e.mylibrary.Pagination.JsonMsgPagination;
import com.e.util.BaseTActivity;
import com.e.util.HttpUtil;
import com.e.util.NetWork;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javaClassBase.BookNewAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class HomeFragment extends Fragment {

        //定义以goodsentity实体类为对象的数据集合
        private SwipeRefreshLayout swipeRefreshLayout;
        private List<Book> bookNews=new ArrayList<>();
        private BaseTActivity activity;
        private BookNewAdapter adapter;
        GridLayoutManager layoutManager=null;
        View view;
        @SuppressLint("InflateParams")
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            //定义view用来设置fragment的layout
            view = inflater.inflate(R.layout.fragment_home, null);
            initData();
            //设置toolbar
            Toolbar toolbar=view.findViewById(R.id.toolbar);
            activity= (BaseTActivity) getActivity();
            if (activity != null) {
                activity.setSupportActionBar(toolbar);
                Objects.requireNonNull(activity.getSupportActionBar()).setDisplayShowTitleEnabled(false);
                ActionBar actionBar=activity.getSupportActionBar();
                if(actionBar!=null){
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    actionBar.setHomeAsUpIndicator(R.drawable.menu);
                }
            }
            swipeRefreshLayout =view.findViewById(R.id.swipe_refresh);
            swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
            swipeRefreshLayout.setOnRefreshListener(this::refreshFruits);
            return view;
        }

        private void refreshFruits() {
            new Thread(() -> {
                Thread thread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                activity.runOnUiThread(() -> {
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                });
            }).start();
        }

        private void initData() {
            if (NetWork.isNetworkConnected(view.getContext())) {
                HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/book/querybook?tab=0", new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseData = Objects.requireNonNull(response.body()).string();
                        showResponseDate(responseData);
                    }
                    private void showResponseDate(final String responseData) {
                        activity.runOnUiThread(() -> {
                            JsonMsgPagination jsonMsgPagination = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(responseData, JsonMsgPagination.class);
                            if (jsonMsgPagination.getStatus() == 200) {
                                bookNews=jsonMsgPagination.getData().getList();
                                if(bookNews.size()>0){
                                    ArrayList<Book> randomList = new ArrayList<>(bookNews.size());
                                    do{
                                        int randomIndex = Math.abs( new Random( ).nextInt( bookNews.size() ) );
                                        randomList.add( bookNews.remove( randomIndex ) );
                                    }while( bookNews.size( ) > 0 );
                                    layoutManager = new GridLayoutManager(view.getContext(), 2);
                                    RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
                                    recyclerView.setLayoutManager(layoutManager);
                                    //adapter = new BookNewAdapter(bookNewsList);
                                    adapter = new BookNewAdapter(randomList);
                                    recyclerView.setAdapter(adapter);
                                }else {
                                    layoutManager = new GridLayoutManager(view.getContext(), 2);
                                    RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
                                    recyclerView.setLayoutManager(layoutManager);
                                    //adapter = new BookNewAdapter(bookNewsList);
                                    adapter = new BookNewAdapter(jsonMsgPagination.getData().getList());
                                    recyclerView.setAdapter(adapter);
                                }

                            }
                        });
                    }
                });
            } else {
                activity.runOnUiThread(() -> Toast.makeText(view.getContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_SHORT).show());
            }
        }
}