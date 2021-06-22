package com.e.app_bookmanagement;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.e.JavaClass.JsonMsg;
import com.e.JavaClass.JsonSingleBook;
import com.e.mylibrary.HttpUtil;
import com.e.mylibrary.MyActivity;
import com.e.mylibrary.NetWork;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Route(path = "/app_bookmanagement/ModifyBookActivity")
public class ModifyBookActivity extends MyActivity {

    int bookNumber;
    String bookPlace;
    int number=0;
    @Autowired
    int bookId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_book);
        // 调用 inject 方法，如果传递过来的参数含有，这样使用 @Autowired 的会自动解析
        ARouter.getInstance().inject(this);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        //获得馆藏地址
        Spinner spinner1 =findViewById(R.id.spinner_bookPlace_modifyBook);
        // 建立数据源
        String[] mItems1 = getResources().getStringArray(R.array.bookPlaces);
        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> adapter1= new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mItems1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        spinner1 .setAdapter(adapter1);
        sendNeed();
        LinearLayout delete_modifyBook=findViewById(R.id.delete_modifyBook);
        delete_modifyBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBook();
            }
        });
        LinearLayout modifyBook_modifyBook=findViewById(R.id.modifyBook_modifyBook);
        modifyBook_modifyBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revise();
            }
        });
        RelativeLayout bookNumber_button1_modifyBook=findViewById(R.id.bookNumber_button1_modifyBook);
        bookNumber_button1_modifyBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookNumber+number>=1){
                    number-=1;
                    TextView bookNumber_edit_modifyBook=findViewById(R.id.bookNumber_edit_modifyBook);
                    if (number<0){
                        bookNumber_edit_modifyBook.setText(bookNumber+"  -  "+Math.abs(number));
                    }else {
                        bookNumber_edit_modifyBook.setText(bookNumber+"  +  "+number);
                    }

                }else {
                    Toast.makeText(ModifyBookActivity.this,"库存不能小于0",Toast.LENGTH_LONG).show();
                }

            }
        });
        RelativeLayout bookNumber_button2_modifyBook=findViewById(R.id.bookNumber_button2_modifyBook);
        bookNumber_button2_modifyBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number+=1;
                TextView bookNumber_edit_modifyBook=findViewById(R.id.bookNumber_edit_modifyBook);
                if (number<0){
                    bookNumber_edit_modifyBook.setText(bookNumber+"  -  "+Math.abs(number));
                }else {
                    bookNumber_edit_modifyBook.setText(bookNumber+"  +  "+number);
                }
            }
        });
    }
    //根据ID搜索
    private void sendNeed() {
        if (NetWork.isNetworkConnected(this)) {
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/book/query?bookId=" + bookId, new Callback() {
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
                            if (jsonSingleBook.getStatus() == 200) {
                                ImageView imageView = findViewById(R.id.bookpic_modifyBook);
                                if (!ModifyBookActivity.this.isDestroyed()) {
                                    Glide.with(ModifyBookActivity.this).load(jsonSingleBook.getData().getSrc()).into(imageView);
                                    TextView bookid_text_modifyBook=findViewById(R.id.bookid_text_modifyBook);
                                    bookid_text_modifyBook.setText(jsonSingleBook.getData().getBookId()+"");
                                    TextView bookName_text_modifyBook=findViewById(R.id.bookName_text_modifyBook);
                                    bookName_text_modifyBook.setText(jsonSingleBook.getData().getBookName());
                                    TextView bookAuthor_text_modifyBook=findViewById(R.id.bookAuthor_text_modifyBook);
                                    bookAuthor_text_modifyBook.setText(jsonSingleBook.getData().getBookAuthor());
                                    TextView bookPublish_text_modifyBook=findViewById(R.id.bookPublish_text_modifyBook);
                                    bookPublish_text_modifyBook.setText(jsonSingleBook.getData().getBookPublish());
                                    TextView publishTimeSelector_modifyBook=findViewById(R.id.publishTimeSelector_modifyBook);
                                    publishTimeSelector_modifyBook.setText(jsonSingleBook.getData().getPublishTime());
                                    TextView bookNumber_edit_modifyBook=findViewById(R.id.bookNumber_edit_modifyBook);
                                    bookNumber=jsonSingleBook.getData().getBookNumber();
                                    bookNumber_edit_modifyBook.setText(jsonSingleBook.getData().getBookNumber()+"");
                                    TextView bookPage_edit_modifyBook=findViewById(R.id.bookPage_edit_modifyBook);
                                    bookPage_edit_modifyBook.setText(jsonSingleBook.getData().getBookPage()+"");
                                    Spinner spinner_bookPlace_modifyBook=findViewById(R.id.spinner_bookPlace_modifyBook);
                                    bookPlace=jsonSingleBook.getData().getBookPlace();
                                    switch (jsonSingleBook.getData().getBookPlace()) {
                                        case "1A":
                                            spinner_bookPlace_modifyBook.setSelection(0, true);
                                            break;
                                        case "2B":
                                            spinner_bookPlace_modifyBook.setSelection(1, true);
                                            break;
                                        case "3C":
                                            spinner_bookPlace_modifyBook.setSelection(2, true);
                                            break;
                                        case "4D":
                                            spinner_bookPlace_modifyBook.setSelection(3, true);
                                            break;
                                        case "5E":
                                            spinner_bookPlace_modifyBook.setSelection(4, true);
                                            break;
                                        case "6F":
                                            spinner_bookPlace_modifyBook.setSelection(5, true);
                                            break;
                                    }
                                    TextView spinner_type_modifyBook=findViewById(R.id.spinner_type_modifyBook);
                                    spinner_type_modifyBook.setText(jsonSingleBook.getData().getType());
                                    TextView bookIntroduction_text_modifyBook=findViewById(R.id.bookIntroduction_text_modifyBook);
                                    bookIntroduction_text_modifyBook.setText(jsonSingleBook.getData().getBookIntroduction());                                }
                            }
                        }
                    });
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
        }
    }
    //删除书籍
    private void deleteBook(){
        if (NetWork.isNetworkConnected(this)) {
            String s="https://starlibrary.online/haopeng/book/deletebook?bookId=" + bookId;
            Log.d("QQQQQQQQQQQQQ", s);
            HttpUtil.sendOkHttpRequest(s, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }
                @Override
                            public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                                final String responseData = Objects.requireNonNull(response.body()).string();
                                Log.d("AAAAAAAAAAAAA", responseData);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        JsonMsg jsonMsg = new GsonBuilder()
                                                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                                .create()
                                                .fromJson(responseData, JsonMsg.class);
                            if (jsonMsg.getStatus() == 200) {
                                Toast.makeText(ModifyBookActivity.this,jsonMsg.getMsg(),Toast.LENGTH_LONG).show();
                                finish();
                            }else if (jsonMsg.getStatus()==404){
                                Toast.makeText(ModifyBookActivity.this,jsonMsg.getMsg(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
        }
    }
    //修改书籍
    private void revise(){
        Spinner spinner_bookPlace_modifyBook=findViewById(R.id.spinner_bookPlace_modifyBook);
        TextView bookNumber_edit_modifyBook=findViewById(R.id.bookNumber_edit_modifyBook);
        if (spinner_bookPlace_modifyBook.getSelectedItem().toString().equals(bookPlace)&&number==0){
            Toast.makeText(ModifyBookActivity.this,"没有修改",Toast.LENGTH_LONG).show();
        }else {
            String s = null;
            if (!spinner_bookPlace_modifyBook.getSelectedItem().toString().equals(bookPlace)&&number==0){
                s="https://starlibrary.online/haopeng/book/revise?bookId=" + bookId+"&bookPlace="+spinner_bookPlace_modifyBook.getSelectedItem().toString();
            }else if (!spinner_bookPlace_modifyBook.getSelectedItem().toString().equals(bookPlace)&&number!=0){
                if (number>0){
                    s="https://starlibrary.online/haopeng/book/revise?bookId=" + bookId+"&bookNumber="+number+"&bookPlace="+spinner_bookPlace_modifyBook.getSelectedItem().toString()+"&type=1";
                }else {
                    s="https://starlibrary.online/haopeng/book/revise?bookId=" + bookId+"&bookNumber="+Math.abs(number)+"&bookPlace="+spinner_bookPlace_modifyBook.getSelectedItem().toString()+"&type=0";
                }

            }else if(spinner_bookPlace_modifyBook.getSelectedItem().toString().equals(bookPlace)&&number!=0){
                if (number>0){
                    s="https://starlibrary.online/haopeng/book/revise?bookId=" + bookId+"&bookNumber="+number+"&type=1";
                }else {
                    s="https://starlibrary.online/haopeng/book/revise?bookId=" + bookId+"&bookNumber="+Math.abs(number)+"&type=0";
                }
            }
            if (NetWork.isNetworkConnected(this)) {
                HttpUtil.sendOkHttpRequest(s, new Callback() {
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
                                Log.d("AAAAAAAAAAAAA", responseData);
                                JsonMsg jsonMsg = new GsonBuilder()
                                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                        .create()
                                        .fromJson(responseData, JsonMsg.class);
                                if (jsonMsg.getStatus() == 200) {
                                    Toast.makeText(ModifyBookActivity.this,jsonMsg.getMsg(),Toast.LENGTH_LONG).show();
                                    finish();
                                }else if (jsonMsg.getStatus()==404){
                                    Toast.makeText(ModifyBookActivity.this,jsonMsg.getMsg(),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
            }
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