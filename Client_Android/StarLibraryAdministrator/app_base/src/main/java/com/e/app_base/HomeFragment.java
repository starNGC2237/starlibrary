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


public class HomeFragment extends Fragment {
    MyActivity activity;
    View view;
    GridLayoutManager layoutManager=null;
    BlockAdapter adapter;
    private List<Block> blocks=new ArrayList<>();
    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //定义view用来设置fragment的layout
        view = inflater.inflate(R.layout.fragment_home, null);
        //设置toolbar
        Toolbar toolbar=view.findViewById(R.id.toolbar);
        activity= (MyActivity) getActivity();
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
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BlockAdapter(blocks,activity);
        recyclerView.setAdapter(adapter);
        return view;
    }
    private void initData() {
        Block book = new Block("书籍管理", R.drawable.book_manager);
        blocks.add(book);
        Block order = new Block("订单管理", R.drawable.ic_order_block);
        blocks.add(order);
        Block mangers = new Block("总管理员管理", R.drawable.manager);
        blocks.add(mangers);
        Block aboutus = new Block("关于我们", R.drawable.ic_about_black);
        blocks.add(aboutus);

    }
}
