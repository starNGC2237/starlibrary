package com.e.app_baseshow;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.e.mylibrary.Book;
import com.e.mylibrary.Search.JsonMsgSearch;
import com.e.util.BaseTActivity;
import com.e.util.HttpUtil;
import com.e.util.NetWork;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javaClassBase.BookSearchNewAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class SearchFragment extends Fragment {
        BaseTActivity activity;
        BookSearchNewAdapter adapter=null;
        String info="?tab=1";
        private List<Book> books=new ArrayList<>();
        @SuppressLint("InflateParams")
        @Nullable
        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_search, null);
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
            TabLayout tabLayout=view.findViewById(R.id.tabs_search_base);
            final TabLayout.Tab tab1 = tabLayout.newTab();
            tabLayout.addTab(tab1.setText("全部"));
            final TabLayout.Tab tab2 = tabLayout.newTab();
            tabLayout.addTab(tab2.setText("工具书"));
            final TabLayout.Tab tab3 = tabLayout.newTab();
            tabLayout.addTab(tab3.setText("人物传记"));
            final TabLayout.Tab tab4 = tabLayout.newTab();
            tabLayout.addTab(tab4.setText("想象文学"));
            final TabLayout.Tab tab5 = tabLayout.newTab();
            tabLayout.addTab(tab5.setText("咨询类书籍"));
            final TabLayout.Tab tab6 = tabLayout.newTab();
            tabLayout.addTab(tab6.setText("论说类书籍"));
            final TabLayout.Tab tab7 = tabLayout.newTab();
            tabLayout.addTab(tab7.setText("说明规则类书籍"));
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab==tab1){
                        info="?tab=1";
                        searchBook();
                    }else if (tab==tab2){
                        info="?tab=2&type="+"工具书";
                        searchBook();
                    }else if (tab==tab3){
                        info="?tab=2&type="+"人物传记书籍";
                        searchBook();
                    }else if (tab==tab4){
                        info="?tab=2&type="+"想象文学书籍";
                        searchBook();
                    }else if (tab==tab5){
                        info="?tab=2&type="+"咨询类书籍";
                        searchBook();
                    }else if (tab==tab6){
                        info="?tab=2&type="+"论说类书籍";
                        searchBook();
                    }else if (tab==tab7){
                        info="?tab=2&type="+"说明规则类书籍";
                        searchBook();
                    }else {
                        Toast.makeText(getContext(),"错误！",Toast.LENGTH_LONG).show();
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
            //跳转到搜索书籍
            LinearLayout linearLayout=view.findViewById(R.id.toSearchBook);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ARouter.getInstance().build("/app_book/SearchBookActivity").navigation();
                }
            });
            searchBook();
            return view;

        }
    private void searchBook() {
        if (NetWork.isNetworkConnected(activity)){
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/book/querybook"+info, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String data = Objects.requireNonNull(response.body()).string();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonMsgSearch jsonMsgPagination = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(data, JsonMsgSearch.class);
                            if (jsonMsgPagination.getStatus()==200){
                                books=jsonMsgPagination.getData();
                                RecyclerView rv =activity.findViewById(R.id.recycler_view_search);
                                adapter = new BookSearchNewAdapter(books);
                                rv.setAdapter(adapter);
                                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(activity);
                                rv.setLayoutManager(linearLayoutManager);
                                rv.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    });
                }
            });
        }else {
            Toast.makeText(SearchFragment.this.getContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
        }
    }
}