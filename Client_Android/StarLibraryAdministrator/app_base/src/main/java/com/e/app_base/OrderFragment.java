package com.e.app_base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.JavaClass.Block;
import com.e.mylibrary.MyActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javaclass.BlockAdapter;

public class OrderFragment extends Fragment {
    MyActivity activity;
    View view;
    private List<Block> blocks=new ArrayList<>();
    GridLayoutManager layoutManager=null;
    BlockAdapter adapter;
    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //定义view用来设置fragment的layout
        view = inflater.inflate(R.layout.fragment_order, null);
        //设置toolbar
        Toolbar toolbar=view.findViewById(R.id.toolbar);
        activity = (MyActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            Objects.requireNonNull(activity.getSupportActionBar()).setDisplayShowTitleEnabled(false);
            ActionBar actionBar=activity.getSupportActionBar();
            if(actionBar!=null){
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.menu);
            }
        }
        initData();
        layoutManager = new GridLayoutManager(view.getContext(), 2);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_order);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BlockAdapter(blocks,activity);
        recyclerView.setAdapter(adapter);
        return view;
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
}
