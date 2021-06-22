package com.e.app_login;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.e.javaBean.Image;
import com.e.javaBean.JsonRootBean;
import com.e.mylibrary.UserIn;
import com.e.util.BaseTActivity;
import com.e.util.HttpUtil;
import com.e.util.NetWork;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Route(path = "/app_login/LoginActivity")
public class LoginActivity extends BaseTActivity {
    PopupWindow pop = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //软键盘适应
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        final EditText accountNeed=findViewById(R.id.editText_userName);
        final EditText passwordNeed=findViewById(R.id.editText_passWord);
        //配置忘记跳转
        TextView Forget=findViewById(R.id.retrievePassword);
        Forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,ForgetActivity.class);
                startActivity(intent);
            }
        });
        //配置注册跳转
        TextView Register=findViewById(R.id.register_login);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        List<UserIn> newsList = LitePal.where("i =?", "0").find(UserIn.class);
        if(newsList != null && newsList.size()>0){
            accountNeed.setText(newsList.get(0).getUserName());
            passwordNeed.setText(newsList.get(0).getUserPass());
        }
        accountNeed.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    // 此处为失去焦点时的处理内容
                    if(pop!=null&&pop.isShowing()){
                        pop.dismiss();
                    }
                    List<UserIn> userins = LitePal
                            .where("username = ? ",accountNeed.getText().toString() )
                            .find(UserIn.class);
                    if(userins.size() == 1){
                        if (NetWork.isNetworkConnected(getApplicationContext())) {
                            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/user/reimage" +
                                    "?userName=" + userins.get(0).getUserName(), new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    e.printStackTrace();
                                }
                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    String responseData = Objects.requireNonNull(response.body()).string();
                                    showResponseDate(responseData);
                                }
                                private void showResponseDate(final String responseData) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Image jsonBeanImage = new Gson()
                                                    .fromJson(responseData, Image.class);
                                            if (jsonBeanImage.getStatus() == 200) {
                                                ImageView pic=findViewById(R.id.headPict);
                                                RequestOptions userAvatarOptions = new RequestOptions()
                                                        .signature(new ObjectKey(jsonBeanImage.getData().getSrc()));
                                                Glide.with(LoginActivity.this)
                                                        .applyDefaultRequestOptions(userAvatarOptions)
                                                        .load(jsonBeanImage.getData().getSrc())
                                                        .into(pic);
                                            }
                                        }
                                    });
                                }
                            });
                        }else {
                            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        ImageView pic=findViewById(R.id.headPict);
                        String url="https://starlibrary.online/image/user.png";
                        RequestOptions userAvatarOptions = new RequestOptions()
                                .signature(new ObjectKey("users"));
                        Glide.with(LoginActivity.this)
                                .applyDefaultRequestOptions(userAvatarOptions)
                                .load(url)
                                .into(pic);
                    }
                }else {
                    if(pop!=null&&pop.isShowing()){
                        pop.dismiss();
                    }
                }
            }
        });
    }
    //登录
    public void login(View view) {
        final ImageView login=findViewById(R.id.login_button);
        login.setClickable(false);
        final CheckBox checkBox = findViewById(R.id.checked_remember);
        EditText accountNeed = findViewById(R.id.editText_userName);
        final String userName = accountNeed.getText().toString();
        EditText passwordNeed = findViewById(R.id.editText_passWord);
        final String userPassword = passwordNeed.getText().toString();
        if (NetWork.isNetworkConnected(getApplicationContext())) {
            Thread loginTh=new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/user/login" +
                            "?userName=" + userName + "&passWord=" + userPassword, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            e.printStackTrace();
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String responseData = Objects.requireNonNull(response.body()).string();
                            showResponseDate(responseData);
                        }
                        private void showResponseDate(final String responseData) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    JsonRootBean jsonRootBean = new GsonBuilder()
                                            .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                            .create()
                                            .fromJson(responseData, JsonRootBean.class);
                                    if (jsonRootBean.getStatus() == 200) {
                                        Connector.getDatabase();
                                        UserIn userin = new UserIn();
                                        List<UserIn> userins = LitePal
                                                .where("username = ? and userpass = ?", jsonRootBean.getData().getUserName(), jsonRootBean.getData().getPassWord())
                                                .find(UserIn.class);
                                        //如果没有，写入
                                        if (userins == null || userins.size() == 0) {
                                            List<UserIn> userintwo = LitePal
                                                    .where("username = ?", jsonRootBean.getData().getUserName())
                                                    .find(UserIn.class);
                                            //有并且密码不同，就改密码
                                            if (userintwo != null && userintwo.size() != 0) {
                                                if (checkBox.isChecked()) {
                                                    ContentValues values = new ContentValues();
                                                    values.put("userpass", jsonRootBean.getData().getPassWord());
                                                    LitePal.updateAll(UserIn.class, values, "username = ?", jsonRootBean.getData().getUserName());
                                                    ContentValues valuetwo = new ContentValues();
                                                    valuetwo.put("i", "1");
                                                    valuetwo.put("card", jsonRootBean.getData().getCard());
                                                    LitePal.updateAll(UserIn.class, valuetwo, "username = ?and i = ?", jsonRootBean.getData().getUserName(), "0");
                                                } else {
                                                    ContentValues values = new ContentValues();
                                                    values.put("userpass", "");
                                                    LitePal.updateAll(UserIn.class, values, "username = ?", jsonRootBean.getData().getUserName());
                                                    ContentValues valuetwo = new ContentValues();
                                                    valuetwo.put("i", "1");
                                                    valuetwo.put("card", jsonRootBean.getData().getCard());
                                                    LitePal.updateAll(UserIn.class, valuetwo, "username = ?and i = ?", jsonRootBean.getData().getUserName(), "0");
                                                }
                                            } else {
                                                userin.setUserName(jsonRootBean.getData().getUserName());
                                                if (checkBox.isChecked()) {
                                                    userin.setUserPass(jsonRootBean.getData().getPassWord());
                                                } else {
                                                    userin.setUserPass("");
                                                }
                                                userin.setCard(jsonRootBean.getData().getCard());
                                                userin.setNickName(jsonRootBean.getData().getNickName());
                                                userin.setCreateTime(jsonRootBean.getData().getCreateTime().toString());
                                                userin.setUserPhone(jsonRootBean.getData().getPhone());
                                                userin.setI(1);
                                                userin.saveThrows();
                                            }
                                        } else {
                                            //如果有
                                            //有密码相同看checkBox
                                            if (!checkBox.isChecked()) {
                                                ContentValues values = new ContentValues();
                                                values.put("userpass", "");
                                                LitePal.updateAll(UserIn.class, values, "username = ?", jsonRootBean.getData().getUserName());
                                            }
                                            ContentValues valuetwo = new ContentValues();
                                            valuetwo.put("i", "1");
                                            valuetwo.put("card", jsonRootBean.getData().getCard());
                                            LitePal.updateAll(UserIn.class, valuetwo, "username = ?and i = ?", jsonRootBean.getData().getUserName(), "0");
                                        }
                                        ContentValues value1 = new ContentValues();
                                        value1.put("nickname",jsonRootBean.getData().getNickName());
                                        LitePal.updateAll(UserIn.class, value1, "username = ?and i = ?", jsonRootBean.getData().getUserName(), "1");
                                        Toast.makeText(getApplicationContext(), "欢迎您，" + jsonRootBean.getData().getNickName(), Toast.LENGTH_SHORT).show();
                                        ARouter.getInstance().build("/app_base/BaseActivity").navigation();
                                        finish();
                                    } else {
                                        login.setClickable(true);
                                        Toast.makeText(getApplicationContext(), jsonRootBean.getMsg(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            });
            if (!loginTh.isAlive()){
                loginTh.start();
            }
        } else {
            login.setClickable(true);
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
        }
    }
    //？提示
    public void Tips(View view) {
        AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setCancelable(true)
                .setMessage("\n每个用户拥有唯一的用户名\n\n"+"密码（6-16个字符组成，区分大小写）\n")//内容
                .setIcon(R.mipmap.ic_launcher)//图标
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setNeutralButton("在网页上反馈", new DialogInterface.OnClickListener() {//添加普通按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent(Intent.ACTION_VIEW);
                        //TODO 反馈页面没有适配
                        intent.setData(Uri.parse("https://starlibrary.online/feedBack.html"));
                        startActivity(intent);
                    }
                })
                .create();
        alertDialog1.show();
    }
    //解锁密码
    public void unlock(View view) {
        if(view.getId()==R.id.lock){
            ImageView lock=findViewById(R.id.lock);
            lock.setImageResource(R.drawable.unlock_login);
            lock.setId(R.id.unlock);
            EditText passwordNeed=findViewById(R.id.editText_passWord);
            passwordNeed.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordNeed.setSelection(passwordNeed.getText().length());
        }else if(view.getId()==R.id.unlock){
            ImageView lock=findViewById(R.id.unlock);
            lock.setImageResource(R.drawable.lock_login);
            lock.setId(R.id.lock);
            EditText passwordNeed=findViewById(R.id.editText_passWord);
            passwordNeed.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordNeed.setSelection(passwordNeed.getText().length());
        }else {
            Toast.makeText(getApplicationContext(), "错误！", Toast.LENGTH_SHORT).show();
        }

    }
    //展示更多
    public void showMore(View view) {
        if (pop==null){
            final List<String> names = new ArrayList<>();
            List<UserIn> userins=LitePal.findAll(UserIn.class);
            for (int i=0;i<userins.size();i++){
                names.add(userins.get(i).getUserName());
            }
            final EditText accountNeed=findViewById(R.id.editText_userName);
            final EditText passwordNeed=findViewById(R.id.editText_passWord);
            InputMethodManager imm = (InputMethodManager) LoginActivity.this
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(accountNeed.getWindowToken(), 0);
            View L1=findViewById(R.id.L1);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    LoginActivity.this, R.layout.simple, names);
            final ListView listView = new ListView(LoginActivity.this);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    accountNeed.setText(names.get(position));
                    List<UserIn> userintwo = LitePal
                            .where("username = ?",names.get(position))
                            .find(UserIn.class);
                    for (int i=0;i<userintwo.size();i++){
                        passwordNeed.setText(userintwo.get(i).getUserPass());
                    }
                    pop.dismiss();
                }
            });
            pop= new PopupWindow(listView, L1.getWidth(), 3* L1.getHeight());
            pop.showAsDropDown(L1);
        }else {
            if (pop.isShowing()){
                pop.dismiss();
            }else {
                View L1=findViewById(R.id.L1);
                EditText accountNeed=findViewById(R.id.editText_userName);
                InputMethodManager imm = (InputMethodManager) LoginActivity.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(accountNeed.getWindowToken(), 0);
                pop.showAsDropDown(L1);
            }

        }
    }
}