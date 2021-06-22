package com.e.app_login;


import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.e.javaBean.MessageBean;
import com.e.javaBean.RegisterResult;
import com.e.util.BaseTActivity;
import com.e.util.HttpUtil;
import com.e.util.NetWork;
import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends BaseTActivity {
    int numcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
    //关闭页面
    public void OnBack(View view) { RegisterActivity.this.finish(); }
    //填写不能注册
    public void openBrowser(View view) {
        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://47.98.179.191:8080/example/Folder/RegisterFail.html"));
        startActivity(intent);
    }

    public void register(View view) {
        EditText passwordNeed=findViewById(R.id.editRE_userPass);
        String userPassword=passwordNeed.getText().toString();
        EditText userPhoneNeed=findViewById(R.id.editRE_userPhone);
        String userPhone=userPhoneNeed.getText().toString();
        if (userPassword.length()<=16&&userPassword.length()>=6&&isMobileNO(userPhone)) {
            numcode = (int) ((Math.random() * 9 + 1) * 100000);
            if (NetWork.isNetworkConnected(RegisterActivity.this)){
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        EditText userPhoneNeed=findViewById(R.id.editRE_userPhone);
                        String userPhone=userPhoneNeed.getText().toString();
                        String host = "https://feginesms.market.alicloudapi.com";// 【1】请求地址 支持http 和 https 及 WEBSOCKET
                        String path = "/codeNotice";// 【2】后缀
                        String appcode = "93157d3eb11a4c7bb74f60053b98416c"; // 【3】开通服务后 买家中心-查看AppCode
                        String sign = "1"; // 【4】请求参数，详见文档描述
                        String skin = "1"; // 【4】请求参数，详见文档描述
                        // 【4】请求参数，详见文档描述
                        // 【4】请求参数，详见文档描述
                        String urlSend = host + path + "?sign=" + sign + "&skin=" + skin+ "&param=" + numcode+ "&phone=" + userPhone; // 【5】拼接请求链接
                        try {
                            URL url = new URL(urlSend);
                            HttpURLConnection httpURLCon = (HttpURLConnection) url.openConnection();
                            httpURLCon.setRequestProperty("Authorization", "APPCODE " + appcode);// 格式Authorization:APPCODE
                            // (中间是英文空格)
                            int httpCode = httpURLCon.getResponseCode();
                            if (httpCode == 200) {
                                new XPopup.Builder(RegisterActivity.this).asInputConfirm("请输入收到的", "验证码",
                                        new OnInputConfirmListener() {
                                            @Override
                                            public void onConfirm(String text) {
                                                if (text.equals(String.valueOf(numcode))){
                                                    registerLast();
                                                }else {
                                                    Toast.makeText(getApplicationContext(), "验证码错误！", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .show();
                            } else {
                                Map<String, List<String>> map = httpURLCon.getHeaderFields();
                                String error = map.get("X-Ca-Error-Message").get(0);
                                if (httpCode == 400 && error.equals("Invalid AppCode `not exists`")) {
                                    Toast.makeText(getApplicationContext(), "AppCode错误！", Toast.LENGTH_SHORT).show();
                                } else if (httpCode == 400 && error.equals("Invalid Url")) {
                                    Toast.makeText(getApplicationContext(), "请求的 Method、Path 或者环境错误", Toast.LENGTH_SHORT).show();
                                } else if (httpCode == 400 && error.equals("Invalid Param Location")) {
                                    Toast.makeText(getApplicationContext(), "参数错误", Toast.LENGTH_SHORT).show();
                                } else if (httpCode == 403 && error.equals("Unauthorized")) {
                                    Toast.makeText(getApplicationContext(), "服务未被授权（或URL和Path不正确）", Toast.LENGTH_SHORT).show();
                                } else if (httpCode == 403 && error.equals("Quota Exhausted")) {
                                    Toast.makeText(getApplicationContext(), "套餐包次数用完", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "参数名错误 或 其他错误", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (MalformedURLException e) {
                            Toast.makeText(getApplicationContext(), "URL格式错误", Toast.LENGTH_SHORT).show();
                        } catch (UnknownHostException e) {
                            Toast.makeText(getApplicationContext(), "URL地址错误", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            // 打开注释查看详细报错异常信息
                            e.printStackTrace();
                        }
                    }
                }).start();

            }else {
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


    public void registerLast(){
        EditText accountNeed=findViewById(R.id.editRE_userName);
        String userName=accountNeed.getText().toString();
        EditText passwordNeed=findViewById(R.id.editRE_userPass);
        String userPassword=passwordNeed.getText().toString();
        EditText userPhoneNeed=findViewById(R.id.editRE_userPhone);
        String userPhone=userPhoneNeed.getText().toString();
        if (NetWork.isNetworkConnected(getApplicationContext())) {
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/user/register"+"?userName=" + userName + "&userPassword=" + userPassword + "&userPhone=" + userPhone, new Callback() {
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
                            RegisterResult jsonRootBean = new Gson().fromJson(responseData, RegisterResult.class);
                            if (jsonRootBean.getStatus() == 200) {
                                Toast.makeText(getApplicationContext(), jsonRootBean.getMsg(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (jsonRootBean.getStatus() == 404) {
                                Toast.makeText(getApplicationContext(), jsonRootBean.getMsg(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "错误！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "网络连接不可用，请检查您的网络设置", Toast.LENGTH_LONG).show();
        }

    }
    private boolean isMobileNO(String mobiles){
        String telRegex =  "^1([38][0-9]|4[579]|5[^4]|6[6]|7[0135678]|9[89])\\d{8}$";
        return !TextUtils.isEmpty(mobiles) && mobiles.matches(telRegex);
    }
}