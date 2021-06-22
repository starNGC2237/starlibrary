package com.e.app_yueshuquan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.e.mylibrary.JsonMsg;
import com.e.mylibrary.JsonPageReview.JsonMsgReview;
import com.e.mylibrary.JsonPageReview.JsonReview;
import com.e.mylibrary.SingleBook.JsonSingleBook;
import com.e.mylibrary.UserIn;
import com.e.util.BaseTActivity;
import com.e.util.HttpUtil;
import com.e.util.NetWork;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javaClassYue.CommentsBookAdapter;
import javaClassYue.YueHomeAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Route(path = "/app_yueshuquan/BookAndCommentsActivity")
public class BookAndCommentsActivity extends BaseTActivity {
    CommentsBookAdapter adapter;
    String userName;
    @Autowired
    int bookId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_and_comments);
        List<UserIn> newsList = LitePal.where("i =?", "1").find(UserIn.class);
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                userName=newsList.get(0).getUserName();
            }
        }else if(newsList != null && newsList.size()>=2){
            Toast.makeText(getApplicationContext(), "错误！", Toast.LENGTH_SHORT).show();
        }
        // 调用 inject 方法，如果传递过来的参数含有，这样使用 @Autowired 的会自动解析
        ARouter.getInstance().inject(this);
        final Toolbar toolbar = findViewById(R.id.toolbar_c);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        sendNeed();
        sp();
    }
    //根据ID搜索
    private void sendNeed(){
        if (NetWork.isNetworkConnected(this)){
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/book/query?bookId="+bookId, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                    final String responseData = Objects.requireNonNull(response.body()).string();
                    runOnUiThread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            JsonSingleBook jsonSingleBook = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(responseData, JsonSingleBook.class);
                            if (jsonSingleBook.getStatus()==200){
                                ImageView imageView=findViewById(R.id.book_image_view_yue);
                                TextView book_bookName_view_yue=findViewById(R.id.book_bookName_view_yue);
                                TextView book_bookAuthor_view_yue=findViewById(R.id.book_bookAuthor_view_yue);
                                TextView book_bookTime_view_yue=findViewById(R.id.book_bookTime_view_yue);
                                TextView book_bookIntro_view_yue=findViewById(R.id.book_bookIntro_view_yue);
                                if (!BookAndCommentsActivity.this.isDestroyed()) {
                                    Glide.with(BookAndCommentsActivity.this).load(jsonSingleBook.getData().getSrc()).into(imageView);
                                    book_bookName_view_yue.setText(jsonSingleBook.getData().getBookName());
                                    book_bookAuthor_view_yue.setText("著者："+jsonSingleBook.getData().getBookAuthor());
                                    book_bookTime_view_yue.setText(jsonSingleBook.getData().getPublishTime()+"出版");
                                    book_bookIntro_view_yue.setText(jsonSingleBook.getData().getBookIntroduction());
                                }
                            }
                        }
                    });
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
        }
    }
    public void hit(int hit, int adid){
        if (NetWork.isNetworkConnected(BookAndCommentsActivity.this)){
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/book/hit?adid="+adid+"&userName="+userName+"&hit="+hit, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String responseData = Objects.requireNonNull(response.body()).string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonMsg j = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(responseData, JsonMsg.class);
                            if (j.getStatus() == 200) {
                                Toast.makeText(BookAndCommentsActivity.this,j.getMsg(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置",Toast.LENGTH_LONG).show();
        }
    }
    //ID书评
    private  void sp(){
        if (NetWork.isNetworkConnected(this)){
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/book/alladvise?bookId="+bookId+"&userName="+userName, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                    final String responseData = Objects.requireNonNull(response.body()).string();
                    runOnUiThread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            JsonMsgReview json = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(responseData, JsonMsgReview.class);
                            if (json.getStatus()==200){
                                RecyclerView rv =findViewById(R.id.ss_reviews);
                                adapter = new CommentsBookAdapter(json.getData(),BookAndCommentsActivity.this);
                                rv.setAdapter(adapter);
                                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(BookAndCommentsActivity.this);
                                rv.setLayoutManager(linearLayoutManager);
                                rv.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    });
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        }
        return true;
    }
}