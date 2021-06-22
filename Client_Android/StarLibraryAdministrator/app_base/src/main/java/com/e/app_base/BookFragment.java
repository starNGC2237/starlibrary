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

public class BookFragment extends Fragment {
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
        view = inflater.inflate(R.layout.fragment_book, null);
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
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_book);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BlockAdapter(blocks,activity);
        recyclerView.setAdapter(adapter);
        return view;
    }
    private void initData() {
        Block addBook = new Block("添加书籍", R.drawable.add_book);
        blocks.add(addBook);
        Block editBook = new Block("修改书籍", R.drawable.edit_book);
        blocks.add(editBook);
        Block deleteBook = new Block("删除书籍", R.drawable.delete_book);
        blocks.add(deleteBook);
    }
}
