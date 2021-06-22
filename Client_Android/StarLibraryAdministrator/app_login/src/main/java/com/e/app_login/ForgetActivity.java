package com.e.app_login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.e.JavaClass.JsonMsg;
import com.e.JavaClass.JsonRootBean;
import com.e.mylibrary.HttpUtil;
import com.e.mylibrary.MyActivity;
import com.e.mylibrary.NetWork;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Route(path = "/app_login/ForgetActivity")
public class ForgetActivity extends MyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
    }
    /*
    * 返回登录
    * */
    public void OnBack(View view) {
        ForgetActivity.this.finish();
    }
    /*
    * 忘记密码
    * */
    public void forget(View view) {
        EditText accountNeed=findViewById(R.id.EDuserName_fo);
        String userName=accountNeed.getText().toString();
        EditText passwordNeed=findViewById(R.id.EDuserPassWord_fo);
        String userPassword=passwordNeed.getText().toString();
        EditText phoneNeed=findViewById(R.id.EDuserPhone_fo);
        String userPhone=phoneNeed.getText().toString();
        Pattern pattern = Pattern.compile("[0-9]*");
        if (userPassword.length()<=16&&userPassword.length()>=6&&pattern.matcher(userPhone).matches()) {
            if (NetWork.isNetworkConnected(getApplicationContext())) {
                HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/manager/forgetpassword" +
                        "?userName=" + userName + "&passWord=" + userPassword + "&phone=" + userPhone, new Callback() {
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
                                JsonMsg jsonMsg = new Gson().fromJson(responseData, JsonMsg.class);
                                if (jsonMsg.getStatus() == 200) {
                                    Toast.makeText(getApplicationContext(), jsonMsg.getMsg(), Toast.LENGTH_SHORT).show();
                                    ARouter.getInstance().build("/app_login/LoginActivity").navigation();
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), jsonMsg.getMsg(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
            }
        }else {
            if (userPassword.length()>16||userPassword.length()<6){
                Toast.makeText(getApplicationContext(), "密码应由6-16个字符组成，区分大小写。", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(), "手机号的格式错误！", Toast.LENGTH_SHORT).show();
            }
        }
    }
}