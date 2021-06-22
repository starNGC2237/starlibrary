package com.e.app_baseshow;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.e.mylibrary.Image;
import com.e.mylibrary.UserIn;
import com.e.util.BaseTActivity;
import com.e.util.NetWork;
import com.google.gson.GsonBuilder;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javaClassBase.Option;
import javaClassBase.OptionAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class UserFragment extends Fragment {
    private static String TAG= UserFragment.class.getSimpleName();
    private List<Option> optionList=new ArrayList<>();
        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            Log.d(TAG,"onAttach");
        }
        @SuppressLint("InflateParams")
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.d(TAG,"onCreateView");
            initOptions();
            View view = inflater.inflate(R.layout.fragment_user, container, false);
            //设置toolbar
            Toolbar toolbar=view.findViewById(R.id.toolbar);
            BaseTActivity activity = (BaseTActivity) getActivity();
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
            List<UserIn> newsList = LitePal.where("i =?", "1").find(UserIn.class);
            if(newsList != null && newsList.size()==1){
                for (int i=0;i<newsList.size();i++){
                    userName.setText(newsList.get(i).getNickName());
                    setUrlImage(newsList.get(i).getUserName(),view);
                }
            }else if(newsList != null && newsList.size()>=2){
                Toast.makeText(view.getContext(), "错误！", Toast.LENGTH_SHORT).show();
            }
            RecyclerView mRecyclerView = view.findViewById (R.id.list_options);
            OptionAdapter optionAdapter = new OptionAdapter (getActivity (),optionList);
            mRecyclerView.setLayoutManager (new LinearLayoutManager (getActivity (),LinearLayoutManager.VERTICAL,false));
            mRecyclerView.setItemAnimator (new DefaultItemAnimator());
            mRecyclerView.setAdapter (optionAdapter);
            mRecyclerView.addItemDecoration (new DividerItemDecoration(Objects.requireNonNull(getActivity()),DividerItemDecoration.VERTICAL));
            return view;
        }

    private void setUrlImage(final String userN, final View v) {
        if (NetWork.isNetworkConnected(getActivity())) {
            OkHttpClient mOkHttpClient = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("userName", userN)
                    .build();
            Request request = new Request.Builder()
                    .url("https://starlibrary.online/haopeng/user/reimage")
                    .post(formBody)
                    .build();
            Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                    final String data = Objects.requireNonNull(response.body()).string();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Image jsonBeanImage = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(data, Image.class);
                            int resultCode = jsonBeanImage.getStatus();
                            if (resultCode == 200) {
                                final ImageView pic = v.findViewById(R.id.user_pic);
                                pic.post(() -> {
                                    ContentValues values = new ContentValues();
                                    values.put("src", jsonBeanImage.getData().getSrc());
                                    LitePal.updateAll(UserIn.class, values, "username = ?",userN);
                                    RequestOptions userAvatarOptions = new RequestOptions()
                                            .signature(new ObjectKey(jsonBeanImage.getData().getSrc()));
                                    if(!UserFragment.this.isDetached()){
                                        Glide.with(v)
                                                .applyDefaultRequestOptions(userAvatarOptions)
                                                .load(jsonBeanImage.getData().getSrc())
                                                .into(pic);
                                    }

                                });
                            } else {
                                Toast.makeText(getActivity(), "错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).start();
                }
            });
        }else {
            Toast.makeText(getActivity(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_SHORT).show();
        }
    }

    private void initOptions() {
        Option account = new Option("账号管理", R.drawable.ic_account);
        optionList.add(account);
        Option reserving = new Option("预约中的订单", R.drawable.ic_order_reserving);
        optionList.add(reserving);
        Option borrowing = new Option("借阅中的订单", R.drawable.ic_order_borrowing);
        optionList.add(borrowing);
        Option returned = new Option("已还的订单", R.drawable.ic_order_finish);
        optionList.add(returned);
        Option timeout = new Option("超时的订单", R.drawable.ic_order_overtime);
        optionList.add(timeout);
        Option seeCar = new Option("查看收藏", R.drawable.ic_seecar);
        optionList.add(seeCar);
        Option bind = new Option("绑定图书证", R.drawable.ic_bind);
        optionList.add(bind);
        Option yue = new Option("阅书圈", R.drawable.ic_edityue);
        optionList.add(yue);
        Option about = new Option("关于我们", R.drawable.ic_about);
        optionList.add(about);
    }

    @Override
    public void onResume(){
        super.onResume();
        View view = this.getView();
        TextView userName= Objects.requireNonNull(view).findViewById(R.id.username);
        List<UserIn> newsList = LitePal.where("i =?", "1").find(UserIn.class);
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                userName.setText(newsList.get(i).getNickName());
                setUrlImage(newsList.get(i).getUserName(),view);
            }
        }else if(newsList != null && newsList.size()>=2){
            Toast.makeText(view.getContext(), "错误！", Toast.LENGTH_SHORT).show();
        }
     }


}