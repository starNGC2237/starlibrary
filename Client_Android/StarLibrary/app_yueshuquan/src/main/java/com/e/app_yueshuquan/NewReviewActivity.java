package com.e.app_yueshuquan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.e.mylibrary.Book;
import com.e.mylibrary.JsonMsg;
import com.e.mylibrary.JsonOrder.JsonMsgOrder;
import com.e.mylibrary.JsonPageReview.JsonMsgReview;
import com.e.mylibrary.UserIn;
import com.e.util.BaseTActivity;
import com.e.util.HttpUtil;
import com.e.util.NetWork;
import com.google.gson.GsonBuilder;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javaClassYue.CustomPopupYue;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Route(path = "/app_yueshuquan/NewReviewActivity")
public class NewReviewActivity extends BaseTActivity {
    int card=0;
    String userName=null;
    String content=null;
    double score=8.0;
    ArrayList<Book> booklistclasss=new ArrayList<>();
    int bookSectId=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_review);
        final Toolbar toolbar = findViewById(R.id.toolbar_new_review);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        List<UserIn> newsList = LitePal.where("i =?", "1").find(UserIn.class);
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                if (newsList.get(i).getCard()==0){
                    card=0;
                    Toast.makeText(NewReviewActivity.this,"?????????????????????????????????????????????",Toast.LENGTH_SHORT).show();
                    ARouter.getInstance().build("/app_users/BindUsersActivity").navigation();
                }else {
                    userName=newsList.get(i).getUserName();
                    card=newsList.get(i).getCard();
                }
            }
        }else{
            Toast.makeText(this, "????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
        }
        ImageView sectorBook_newReview=findViewById(R.id.sectorBook_newReview_src);
        sectorBook_newReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectiondetail();
            }
        });
        TextView sectorBook_newReview_stars=findViewById(R.id.sectorBook_newReview_stars);
        sectorBook_newReview_stars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new XPopup.Builder(NewReviewActivity.this)
                        .asCustom(new CustomPopupYue(NewReviewActivity.this,"?????????????????????",NewReviewActivity.this,score))
                        .show();
            }
        });
        Button review_new_save=findViewById(R.id.review_new_save);
        review_new_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newReview();
            }
        });
    }
    public void setScore(double stars){
        score=stars*2;
        TextView sectorBook_newReview_stars=findViewById(R.id.sectorBook_newReview_stars);
        sectorBook_newReview_stars.setText("?????????"+score);
    }
    //TODO ????????????
    public void newReview(){
        EditText sectorBook_newReview_content=findViewById(R.id.sectorBook_newReview_content);
        content=sectorBook_newReview_content.getText().toString();
        if (content.equals("")||score==0||userName==null||card==0||bookSectId==0){
            String s="";
            if (content.equals("")){
                s="???????????????";
            }
            if (score==0){
                s="???????????????";
            }
            if (userName==null){
                s="????????????";
            }
            if (card==0){
                s="?????????????????????";
            }
            if (bookSectId==0){
                s="??????????????????";
            }
            Toast.makeText(NewReviewActivity.this,s,Toast.LENGTH_LONG).show();
        }else {
            if (NetWork.isNetworkConnected(NewReviewActivity.this)){
                String s="https://starlibrary.online/haopeng/book/addadvise?bookId="+bookSectId+"&userName="+userName+"&score="+score+"&content="+content;
                HttpUtil.sendOkHttpRequest(s, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        final String data = Objects.requireNonNull(response.body()).string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                JsonMsg json = new GsonBuilder()
                                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                        .create()
                                        .fromJson(data, JsonMsg.class);
                                if (json.getStatus() == 200) {
                                    Toast.makeText(NewReviewActivity.this,json.getMsg(),Toast.LENGTH_SHORT).show();
                                    finish();
                                }else {
                                    Toast.makeText(NewReviewActivity.this,json.getMsg(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }else {
                Toast.makeText(NewReviewActivity.this,"???????????????????????????????????????????????????",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void collectiondetail(){
        if (NetWork.isNetworkConnected(this)){
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/bookBorrow/borrowdetail?card="+card+"&type=2", new Callback() {
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
                            JsonMsgOrder jsonMsgOrder = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(responseData, JsonMsgOrder.class);
                            if (jsonMsgOrder.getStatus() == 200) {
                                int books=0;
                                if (booklistclasss.size()==0){
                                    //???????????????List
                                    for(int i=0;i<jsonMsgOrder.getData().size();i++){
                                        for(int a=0;a<jsonMsgOrder.getData().get(i).getBook().size();a++){
                                            books+=1;
                                            booklistclasss.add(jsonMsgOrder.getData().get(i).getBook().get(a));
                                        }
                                    }
                                }
                                //List???String
                                String[] str = new String[booklistclasss.size()];
                                for(int i=0;i<booklistclasss.size();i++){
                                    str[i]=booklistclasss.get(i).getBookName();
                                }
                                if (books>=0){
                                    new XPopup.Builder(NewReviewActivity.this)
                                            //.maxWidth(600)
                                            .asCenterList("?????????????????????", str,
                                                    new OnSelectListener() {
                                                        @Override
                                                        public void onSelect(int position, String text) {
                                                            setBook(booklistclasss.get(position));
                                                        }
                                                    })
                                            .show();
                                }else {
                                    Toast.makeText(NewReviewActivity.this,"??????????????????",Toast.LENGTH_LONG).show();
                                }

                            }
                        }
                    });
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "???????????????????????????????????????????????????",Toast.LENGTH_LONG).show();
        }
    }
    public void setBook(Book book){
        ImageView sectorBook_newReview=findViewById(R.id.sectorBook_newReview_src);
        TextView sectorBook_newReview_intro=findViewById(R.id.sectorBook_newReview_intro);
        TextView sectorBook_newReview_bookName=findViewById(R.id.sectorBook_newReview_bookName);
        sectorBook_newReview_intro.setText(book.getBookIntroduction());
        Glide.with(NewReviewActivity.this).load(book.getSrc()).into(sectorBook_newReview);
        sectorBook_newReview_bookName.setText(book.getBookName());
        bookSectId=book.getBookId();
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