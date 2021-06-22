package com.e.app_baseshow;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.e.mylibrary.Image;
import com.e.mylibrary.JsonMsg;
import com.e.mylibrary.JsonOrder.JsonMsgOrder;
import com.e.mylibrary.JsonUuidInterface;
import com.e.mylibrary.UserIn;
import com.e.util.BaseTActivity;
import com.e.util.HttpUtil;
import com.e.util.NetWork;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Route(path = "/app_base/BaseActivity")
public class BaseActivity extends BaseTActivity {
    private long mExitTime;
    private int card;
    private DrawerLayout mDrawerLayout;
    //当前的Fragment
    private Fragment mCurFragment = new Fragment();
    //初始化其他的Fragment
    private HomeFragment mHomeFragment = new HomeFragment();
    private SearchFragment mSearchFragment = new SearchFragment();
    private OrderFragment mOrderFragment = new OrderFragment();
    private UserFragment mUserFragment  = new UserFragment();
    private NavigationView navigationView;

    Intent mForegroundService;

    @Override
    protected void onResume() {
        super.onResume();
        TextView userNickName=navigationView.getHeaderView(0).findViewById(R.id.user_nickName_base);
        List<UserIn> newsList = LitePal.where("i =?", "1").find(UserIn.class);
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                setUrlImage(newsList.get(i).getUserName());
                userNickName.setText(newsList.get(i).getNickName());
                card=newsList.get(i).getCard();
            }
        }else if(newsList != null && newsList.size()>=2){
            Toast.makeText(BaseActivity.this, "错误！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        switchToHome();
        mDrawerLayout=findViewById(R.id.drawerLayout);
        navigationView=findViewById(R.id.nav_base);
        //启动服务
        if (!MessageService.serviceIsLive) {
            // Android 8.0使用startForegroundService在前台启动新服务
            mForegroundService = new Intent(this, MessageService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(mForegroundService);
            } else {
                startService(mForegroundService);
            }
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId()==R.id.nav_account){
                //账号管理
                ARouter.getInstance().build("/app_users/UsersActivity").navigation();
            }else if (item.getItemId()==R.id.nav_reserving){
                //预约的订单
                ARouter.getInstance().build("/app_borrow/ReservingActivity").navigation();
            }else if (item.getItemId()==R.id.nav_borrowing){
                //已借的订单
                ARouter.getInstance().build("/app_borrow/BorrowingBorrowActivity").navigation();
            }else if (item.getItemId()==R.id.nav_finish){
                // 已还的订单
                ARouter.getInstance().build("/app_borrow/BorrowedBorrowActivity").navigation();
            }else if (item.getItemId()==R.id.nav_overtime){
                // 超时的订单
                ARouter.getInstance().build("/app_borrow/TimeOutActivity").navigation();
            }else if (item.getItemId()==R.id.nav_bind){
                //绑定图书证
                ARouter.getInstance().build("/app_users/BindUsersActivity").navigation();
            }else if (item.getItemId()==R.id.nav_seecar){
                // 查看收藏
                ARouter.getInstance().build("/app_borrow/FavoritesActivity").navigation();
            }else if (item.getItemId()==R.id.nav_about){
                //关于我们
                ARouter.getInstance().build("/app_base/AboutUsActivity").navigation();
            }else if (item.getItemId()==R.id.nav_yue){
                //阅书圈
                ARouter.getInstance().build("/app_yueshuquan/YHomeActivity").navigation();
            }
            return false;
        });
        TextView userNickName=navigationView.getHeaderView(0).findViewById(R.id.user_nickName_base);
        List<UserIn> newsList = LitePal.where("i =?", "1").find(UserIn.class);
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                setUrlImage(newsList.get(i).getUserName());
                userNickName.setText(newsList.get(i).getNickName());
                card=newsList.get(i).getCard();
            }
        }else if(newsList != null && newsList.size()>=2){
            Toast.makeText(BaseActivity.this, "错误！", Toast.LENGTH_SHORT).show();
        }
        RelativeLayout userInfo_base=navigationView.getHeaderView(0).findViewById(R.id.user_info_base);
        userInfo_base.setOnClickListener(v -> ARouter.getInstance().build("/app_users/UserInfoActivity").navigation());
        expedit();
        //对底部栏设置监听器
        BottomNavigationView bottomNaviView = findViewById(R.id.bottom_navigation);
        bottomNaviView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();//主页
            if (itemId == R.id.page_1) {
                switchToHome();
                //查询
            } else if (itemId == R.id.page_2) {
                switchToSearch();
                //订单
            } else if (itemId == R.id.page_3) {
                switchToOrder();
                //我的
            } else if (itemId == R.id.page_4) {
                switchToUser();
            }
            return true;
        });
    }
    private void setUrlImage(final String userN) {
        if (NetWork.isNetworkConnected(BaseActivity.this)) {
            HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/user/reimage?userName=" + userN, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String data = Objects.requireNonNull(response.body()).string();
                    runOnUiThread(() -> {
                         final Image jsonBeanImage = new GsonBuilder()
                                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                .create()
                                .fromJson(data, Image.class);
                        int resultCode = jsonBeanImage.getStatus();
                        if (resultCode == 200) {
                            final ImageView pic = findViewById(R.id.headPic_base);
                            ContentValues values = new ContentValues();
                            values.put("src", jsonBeanImage.getData().getSrc());
                            LitePal.updateAll(UserIn.class, values, "username = ?",userN);
                            RequestOptions userAvatarOptions = new RequestOptions()
                                    .signature(new ObjectKey(jsonBeanImage.getData().getSrc()));
                            if (!BaseActivity.this.isDestroyed()){
                                Glide.with(BaseActivity.this)
                                        .applyDefaultRequestOptions(userAvatarOptions)
                                        .load(jsonBeanImage.getData().getSrc())
                                        .into(pic);
                            }

                        } else {
                            Toast.makeText(BaseActivity.this, "错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }else {
            Toast.makeText(BaseActivity.this, "网络连接不可用，请检查您的网络设置", Toast.LENGTH_SHORT).show();
        }
    }
    private void expedit() {
        if (NetWork.isNetworkConnected(BaseActivity.this)) {
            if (card!=0) {
                HttpUtil.sendOkHttpRequest("https://starlibrary.online/haopeng/bookBorrow/expedit?card=" + card, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        final String data = Objects.requireNonNull(response.body()).string();
                        runOnUiThread(() -> {
                            JsonMsgOrder jsonMsgOrder = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create()
                                    .fromJson(data, JsonMsgOrder.class);
                            int resultCode = jsonMsgOrder.getStatus();
                            if (resultCode == 200) {
                                if (jsonMsgOrder.getData()!=null&&(jsonMsgOrder.getData().size()!=0)) {
                                    if (!BaseActivity.this.isDestroyed()) {
                                        StringBuilder s = new StringBuilder("\n");
                                        for (int i = 0; i < jsonMsgOrder.getData().size(); i++) {
                                            s.append("订单号：")
                                                    .append(jsonMsgOrder.getData().get(i).getOrderName())
                                                    .append("\n")
                                                    .append("到期时间：")
                                                    .append(getNowDate(jsonMsgOrder.getData().get(i).getUnboTime()))
                                                    .append("\n\n");
                                        }
                                        AlertDialog alertDialog1 = new AlertDialog.Builder(BaseActivity.this)
                                                .setTitle("订单即将或已超时")
                                                .setCancelable(true)
                                                .setMessage(s)//内容
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
                                                .create();
                                        alertDialog1.show();
                                    }
                                }

                            } else {
                                Toast.makeText(BaseActivity.this, "错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }else {
            Toast.makeText(BaseActivity.this, "网络连接不可用，请检查您的网络设置", Toast.LENGTH_SHORT).show();
        }
    }
    public static String getNowDate(Date date) {
        if (date!=null){
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return formatter.format(date);
        }else {
            return null;
        }

    }
    private void switchFragment(Fragment targetFragment){
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if (!targetFragment.isAdded()) {//如果要显示的targetFragment没有添加过
            transaction
                    .hide(mCurFragment)//隐藏当前Fragment
                    .add(R.id.frame_content, targetFragment,targetFragment.getClass().getName())//添加targetFragment
                    .commit();
        } else {//如果要显示的targetFragment已经添加过
            transaction//隐藏当前Fragment
                    .hide(mCurFragment)
                    .show(targetFragment)//显示targetFragment
                    .commit();
        }
        //更新当前Fragment为targetFragment
        mCurFragment = targetFragment;

    }

    private void switchToUser() {
        switchFragment(mUserFragment);
    }
    private void switchToOrder() {
        switchFragment(mOrderFragment);
    }
    private void switchToSearch() {
        switchFragment(mSearchFragment);
    }
    private void switchToHome() {
        switchFragment(mHomeFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.message) {
            ARouter.getInstance().build("/app_message/MessageActivity").navigation();
        } else if (itemId == R.id.scan) {
            new IntentIntegrator(this)
                    .setCaptureActivity(CustomCaptureActivity.class)
                    .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)// 扫码的类型,可选：一维码，二维码，一/二维码
                    .setPrompt("请对准二维码/条码")// 设置提示语
                    .setCameraId(0)// 选择摄像头,可使用前置或者后置
                    .setTorchEnabled(true)//是否照明
                    .setBeepEnabled(true)// 是否开启声音,扫完码之后会"哔"的一声
                    .setBarcodeImageEnabled(true)// 扫完码之后生成二维码的图片
                    .initiateScan();// 初始化扫码
        }else if (itemId == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            mCurFragment.onStop();
        }
        return true;
    }
    //再点一次退出
    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(BaseActivity.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            moveTaskToBack(true);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "扫码被取消了", Toast.LENGTH_SHORT).show();
            } else {
                Pattern pattern = Pattern.compile("[0-9]*");
                if (!pattern.matcher(result.getContents()).matches()){
                    JsonUuidInterface jsonMsg = new Gson()
                            .fromJson(result.getContents(), JsonUuidInterface.class);
                    if (jsonMsg.getInterfaceHave().equals("https://starlibrary.online/haopeng/user/revisestatus")){
                        String s=jsonMsg.getInterfaceHave() + "?uuid="+jsonMsg.getUuid()+"&status=1";
                        if (NetWork.isNetworkConnected(BaseActivity.this)) {
                            HttpUtil.sendOkHttpRequest(s, new Callback() {
                                    @Override
                                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                        final String data = Objects.requireNonNull(response.body()).string();
                                        runOnUiThread(() -> {
                                            final JsonMsg jsonMsg1 = new GsonBuilder()
                                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                                    .create()
                                                    .fromJson(data, JsonMsg.class);
                                            if (jsonMsg1.getStatus() == 200) {
                                                Toast.makeText(BaseActivity.this, "扫码成功", Toast.LENGTH_SHORT).show();
                                                ARouter.getInstance().build("/app_base/LoginQrcodeActivity")
                                                        .withString("uuid",jsonMsg.getUuid())
                                                        .navigation();
                                            } else {
                                                Toast.makeText(BaseActivity.this, "错误", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        }else {
                            Toast.makeText(BaseActivity.this, "网络连接不可用，请检查您的网络设置", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    ARouter.getInstance().build("/app_book/BookActivity").withInt("bookId", Integer.parseInt(result.getContents())).navigation();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
