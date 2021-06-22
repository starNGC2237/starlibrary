package com.e.app_managers;


import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.e.JavaClass.Block;
import com.e.mylibrary.MyActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javaclassManagers.Block2Adapter;

@Route(path = "/app_managers/ShowBlockManagerActivity")
public class ShowBlockManagerActivity extends MyActivity {
    private List<Block> blocks=new ArrayList<>();
    GridLayoutManager layoutManager=null;
    Block2Adapter adapter;

    public void aaa(String s) {
        Toast.makeText(ShowBlockManagerActivity.this,s,Toast.LENGTH_LONG).show();
    }

    public void aaa() {
        Toast.makeText(ShowBlockManagerActivity.this,"",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_manager);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        initData();
        layoutManager = new GridLayoutManager(ShowBlockManagerActivity.this, 2);
        RecyclerView recyclerView = findViewById(R.id.list_options);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new Block2Adapter(blocks, ShowBlockManagerActivity.this);
        recyclerView.setAdapter(adapter);
    }
    private void initData() {
        Block addBook = new Block("添加管理员", R.drawable.add_manager);
        blocks.add(addBook);
        Block editBook = new Block("修改管理员", R.drawable.edit_manager);
        blocks.add(editBook);
        Block deleteBook = new Block("删除管理员", R.drawable.delete_manager);
        blocks.add(deleteBook);
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
