package com.e.app_bookmanagement;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.e.JavaClass.JsonSingleBook;
import com.e.mylibrary.HttpUtil;
import com.e.mylibrary.MyActivity;
import com.e.mylibrary.NetWork;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
@Route(path = "/app_bookmanagement/BookActivity")
public class BookActivity extends MyActivity {
    @Autowired
    int bookId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        // 调用 inject 方法，如果传递过来的参数含有，这样使用 @Autowired 的会自动解析
        ARouter.getInstance().inject(this);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        sendNeed();
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
                        @Override
                        public void run() {
                            JsonSingleBook jsonSingleBook = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(responseData, JsonSingleBook.class);
                            if (jsonSingleBook.getStatus()==200){
                                ImageView imageView=findViewById(R.id.book_image_view);
                                if (!BookActivity.this.isDestroyed()) {
                                    Glide.with(BookActivity.this).load(jsonSingleBook.getData().getSrc()).into(imageView);
                                    CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.coll_book);
                                    collapsingToolbarLayout.setTitle(jsonSingleBook.getData().getBookName());
                                    collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.transparent));
                                    TextView infoBook = findViewById(R.id.book_content_text);
                                    String info =  "书籍编号：" + jsonSingleBook.getData().getBookId()+"\n\n书名：" + jsonSingleBook.getData().getBookName() + "\n\n著者：" + jsonSingleBook.getData().getBookAuthor() + "\n\n出版社：" + jsonSingleBook.getData().getBookPublish() + "\n\n出版日期：" + jsonSingleBook.getData().getPublishTime() + "\n\n库存：" + jsonSingleBook.getData().getBookNumber() + "\n\n页数：" + jsonSingleBook.getData().getBookPage() + "\n\n书籍类型：" + jsonSingleBook.getData().getType() + "\n\n馆藏地址：" + jsonSingleBook.getData().getBookPlace() + "\n\n加入时间：" + jsonSingleBook.getData().getAddTime()  + "\n\n书籍简介：" + jsonSingleBook.getData().getBookIntroduction();
                                    infoBook.setText(info);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}