package com.e.app_base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.e.JavaClass.UserMangers;
import com.e.mylibrary.MyActivity;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javaclass.Option;
import javaclass.OptionAdapter;

public class UserFragment extends Fragment {
    MyActivity activity;
    View view;
    private List<Option> optionList=new ArrayList<>();
    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //定义view用来设置fragment的layout
        view = inflater.inflate(R.layout.fragment_user, null);
        initOptions();
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
        TextView userName=view.findViewById(R.id.username);
        RelativeLayout userinfo=view.findViewById(R.id.user_info);
        userinfo.setOnClickListener(v -> ARouter.getInstance().build("/app_users/UserInfoActivity").navigation());
        List<UserMangers> newsList = LitePal.where("i =?", "1").find(UserMangers.class);
        ImageView pic=view.findViewById(R.id.user_pic);
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                userName.setText(newsList.get(i).getNickName());
                if (newsList.get(i).getFloor().equals("ALL")){
                    pic.setImageResource(R.drawable.administrator);
                }else {
                    pic.setImageResource(R.drawable.manager);
                }
            }
        }else if(newsList != null && newsList.size()>=2){
            Toast.makeText(view.getContext(), "错误！", Toast.LENGTH_SHORT).show();
        }
        RecyclerView mRecyclerView = view.findViewById (R.id.list_options);
        OptionAdapter optionAdapter = new OptionAdapter(getActivity (),optionList);
        mRecyclerView.setLayoutManager (new LinearLayoutManager(getActivity (),LinearLayoutManager.VERTICAL,false));
        mRecyclerView.setItemAnimator (new DefaultItemAnimator());
        mRecyclerView.setAdapter (optionAdapter);
        mRecyclerView.addItemDecoration (new DividerItemDecoration(Objects.requireNonNull(getActivity()),DividerItemDecoration.VERTICAL));
        return view;
    }
    private void initOptions() {
        Option account = new Option("账号管理", R.drawable.ic_account);
        optionList.add(account);
        Option book = new Option("书籍管理", R.drawable.book_manager);
        optionList.add(book);
        Option order = new Option("订单管理", R.drawable.ic_order_block);
        optionList.add(order);
        Option about = new Option("关于我们", R.drawable.ic_about);
        optionList.add(about);
    }
    @Override
    public void onResume(){
        super.onResume();
        View view = this.getView();
        TextView userName= Objects.requireNonNull(view).findViewById(R.id.username);
        List<UserMangers> newsList = LitePal.where("i =?", "1").find(UserMangers.class);
        ImageView pic=view.findViewById(R.id.user_pic);
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                userName.setText(newsList.get(i).getNickName());

                if (newsList.get(i).getFloor().equals("ALL")){
                    pic.setImageResource(R.drawable.administrator);
                }else {
                    pic.setImageResource(R.drawable.manager);
                }
            }
        }else if(newsList != null && newsList.size()>=2){
            Toast.makeText(view.getContext(), "错误！", Toast.LENGTH_SHORT).show();
        }
    }
}
