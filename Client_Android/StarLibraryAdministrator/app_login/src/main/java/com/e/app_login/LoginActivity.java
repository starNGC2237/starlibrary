package com.e.app_login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.e.JavaClass.JsonRootBean;
import com.e.JavaClass.UserMangers;
import com.e.mylibrary.HttpUtil;
import com.e.mylibrary.MyActivity;
import com.e.mylibrary.NetWork;
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
public class LoginActivity extends MyActivity {
    PopupWindow pop=null;
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
        List<UserMangers> newsList = LitePal.where("i =?", "0").find(UserMangers.class);
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
                    List<UserMangers> userins = LitePal
                            .where("username = ? ",accountNeed.getText().toString() )
                            .find(UserMangers.class);
                    ImageView pic=findViewById(R.id.headPict);
                    if(userins.size() == 1){
                        if (userins.get(0).getFloor().equals("ALL")){
                            pic.setImageResource(R.drawable.administrator);
                        }else {
                            pic.setImageResource(R.drawable.manager);
                        }
                    }
                }else {
                    if(pop!=null&&pop.isShowing()){
                        pop.dismiss();
                    }
                }
            }
        });
    }
    /*
     * 解锁密码
     * */
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
    /*
     * 弹出提示
     * */
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
    /*
    * 登录
    * */
    public void login(View view) {
        final ImageView login=findViewById(R.id.login_button);
        login.setClickable(false);
        final CheckBox checkBox = findViewById(R.id.checked_remember);
        EditText accountNeed = findViewById(R.id.editText_userName);
        final String userName = accountNeed.getText().toString();
        EditText passwordNeed = findViewById(R.id.editText_passWord);
        final String userPassword = passwordNeed.getText().toString();
        if (NetWork.isNetworkConnected(LoginActivity.this)){
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/manager/login" +
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
                            final JsonRootBean jsonRootBean = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(responseData, JsonRootBean.class);
                            if (jsonRootBean.getStatus()==200){
                                Connector.getDatabase();
                                UserMangers userin = new UserMangers();
                                List<UserMangers> userins = LitePal
                                        .where("username = ? and userpass = ?", jsonRootBean.getData().getUserName(), jsonRootBean.getData().getPassWord())
                                        .find(UserMangers.class);
                                //如果没有，写入
                                if (userins == null || userins.size() == 0) {
                                    List<UserMangers> userintwo = LitePal
                                            .where("username = ?", jsonRootBean.getData().getUserName())
                                            .find(UserMangers.class);
                                    //有并且密码不同，就改密码
                                    if (userintwo != null && userintwo.size() != 0) {
                                        if (checkBox.isChecked()) {
                                            ContentValues values = new ContentValues();
                                            values.put("userpass", jsonRootBean.getData().getPassWord());
                                            LitePal.updateAll(UserMangers.class, values, "username = ?", jsonRootBean.getData().getUserName());
                                            ContentValues valuetwo = new ContentValues();
                                            valuetwo.put("i", "1");
                                            valuetwo.put("floor",jsonRootBean.getData().getFloor());
                                            LitePal.updateAll(UserMangers.class, valuetwo, "username = ?and i = ?", jsonRootBean.getData().getUserName(), "0");
                                        } else {
                                            ContentValues values = new ContentValues();
                                            values.put("userpass", "");
                                            LitePal.updateAll(UserMangers.class, values, "username = ?", jsonRootBean.getData().getUserName());
                                            ContentValues valuetwo = new ContentValues();
                                            valuetwo.put("i", "1");
                                            valuetwo.put("floor",jsonRootBean.getData().getFloor());
                                            LitePal.updateAll(UserMangers.class, valuetwo, "username = ?and i = ?", jsonRootBean.getData().getUserName(), "0");
                                        }
                                    } else {
                                        userin.setUserName(jsonRootBean.getData().getUserName());
                                        if (checkBox.isChecked()) {
                                            userin.setUserPass(jsonRootBean.getData().getPassWord());
                                        } else {
                                            userin.setUserPass("");
                                        }
                                        userin.setFloor(jsonRootBean.getData().getFloor());
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
                                        LitePal.updateAll(UserMangers.class, values, "username = ?", jsonRootBean.getData().getUserName());
                                    }
                                    ContentValues valuetwo = new ContentValues();
                                    valuetwo.put("i", "1");
                                    valuetwo.put("floor",jsonRootBean.getData().getFloor());
                                    LitePal.updateAll(UserMangers.class, valuetwo, "username = ?and i = ?", jsonRootBean.getData().getUserName(), "0");
                                }
                                Toast.makeText(getApplicationContext(), "欢迎您，" + jsonRootBean.getData().getNickName(), Toast.LENGTH_SHORT).show();
                                ARouter.getInstance().build("/app_base/BaseActivity").navigation();
                                finish();
                            }else if (jsonRootBean.getStatus()==404){
                                login.setClickable(true);
                                Toast.makeText(LoginActivity.this,jsonRootBean.getMsg(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            });
        }else {
            login.setClickable(true);
            Toast.makeText(LoginActivity.this,"网络连接不可用，请检查您的网络设置",Toast.LENGTH_LONG).show();
        }
    }
    /*
    * 展示账号
    * */
    public void showMore(View view) {
        if (pop == null) {
            final List<String> names = new ArrayList<>();
            List<UserMangers> userins = LitePal.findAll(UserMangers.class);
            for (int i = 0; i < userins.size(); i++) {
                names.add(userins.get(i).getUserName());
            }
            final EditText accountNeed = findViewById(R.id.editText_userName);
            final EditText passwordNeed = findViewById(R.id.editText_passWord);
            InputMethodManager imm = (InputMethodManager) LoginActivity.this
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(accountNeed.getWindowToken(), 0);
            View L1 = findViewById(R.id.L1);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    LoginActivity.this, R.layout.simple, names);
            final ListView listView = new ListView(LoginActivity.this);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    accountNeed.setText(names.get(position));
                    List<UserMangers> userintwo = LitePal
                            .where("username = ?", names.get(position))
                            .find(UserMangers.class);
                    for (int i = 0; i < userintwo.size(); i++) {
                        passwordNeed.setText(userintwo.get(i).getUserPass());
                    }
                    pop.dismiss();
                }
            });
            pop = new PopupWindow(listView, L1.getWidth(), 3 * L1.getHeight());
            pop.showAsDropDown(L1);
        } else {
            if (pop.isShowing()) {
                pop.dismiss();
            } else {
                View L1 = findViewById(R.id.L1);
                EditText accountNeed = findViewById(R.id.editText_userName);
                InputMethodManager imm = (InputMethodManager) LoginActivity.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(accountNeed.getWindowToken(), 0);
                pop.showAsDropDown(L1);
            }

        }
    }
}
