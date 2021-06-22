package com.e.app_base;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.e.JavaClass.JsonMsg;
import com.e.JavaClass.JsonOrderInterface;
import com.e.JavaClass.UserMangers;
import com.e.mylibrary.MyActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.litepal.LitePal;

import java.util.List;
import java.util.regex.Pattern;

@Route(path = "/app_base/BaseActivity")
public class BaseActivity extends MyActivity {
    private long mExitTime;
    private DrawerLayout mDrawerLayout;
    //当前的Fragment
    private Fragment mCurFragment = new Fragment();
    //初始化其他的Fragment
    private HomeFragment mHomeFragment = new HomeFragment();
    private BookFragment mSearchFragment = new BookFragment();
    private OrderFragment mOrderFragment = new OrderFragment();
    private UserFragment mUserFragment  = new UserFragment();
    private NavigationView navigationView;
    String floor;
    @Override
    protected void onResume() {
        super.onResume();
        TextView userNickName = navigationView.getHeaderView(0).findViewById(R.id.user_nickName_base);
        ImageView headPic_base=navigationView.getHeaderView(0).findViewById(R.id.headPic_base);
        List<UserMangers> newsList = LitePal.where("i =?", "1").find(UserMangers.class);
        if (newsList != null && newsList.size() == 1) {
            for (int i = 0; i < newsList.size(); i++) {
                if (newsList.get(i).getFloor().equals("ALL")){
                    headPic_base.setImageResource(R.drawable.administrator);
                }else {
                    headPic_base.setImageResource(R.drawable.manager);
                }
                userNickName.setText(newsList.get(i).getNickName());
            }
        } else if (newsList != null && newsList.size() >= 2) {
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
        //跳转信息

        RelativeLayout userInfo_base=navigationView.getHeaderView(0).findViewById(R.id.user_info_base);
        // TODO 可能还要添加
        userInfo_base.setOnClickListener(v -> ARouter.getInstance().build("/app_users/UserInfoActivity").navigation());
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId()==R.id.nav_account){
                //账号管理
                ARouter.getInstance().build("/app_users/UsersActivity").navigation();
            }else if (item.getItemId()==R.id.nav_about){
                //关于我们
                ARouter.getInstance().build("/app_base/AboutUsActivity").navigation();
            }else if(item.getItemId()==R.id.nav_book){
                //书籍管理
                ARouter.getInstance().build("/app_bookmanagement/ShowBookActivity").navigation();
            }else if (item.getItemId()==R.id.nav_order){
                //订单管理
                ARouter.getInstance().build("/app_orders/OrderBlocksShowActivity").navigation();
            }
            return false;
        });
        //设置头像和信息
        TextView userNickName=navigationView.getHeaderView(0).findViewById(R.id.user_nickName_base);
        ImageView headPic_base=navigationView.getHeaderView(0).findViewById(R.id.headPic_base);
        List<UserMangers> newsList = LitePal.where("i =?", "1").find(UserMangers.class);
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                userNickName.setText(newsList.get(i).getNickName());
                if (newsList.get(i).getFloor().equals("ALL")){
                    headPic_base.setImageResource(R.drawable.administrator);
                }else {
                    headPic_base.setImageResource(R.drawable.manager);
                }
            }
        }else if(newsList != null && newsList.size()>=2){
            Toast.makeText(BaseActivity.this, "错误！", Toast.LENGTH_SHORT).show();
        }
        //对底部栏设置监听器
        BottomNavigationView bottomNaviView = findViewById(R.id.bottom_navigation);
        bottomNaviView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
                //主页
            if (itemId == R.id.page_1) {
                switchToHome();
                //书籍
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
    public void toast(String str){
        Toast.makeText(BaseActivity.this,str,Toast.LENGTH_LONG).show();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.scan) {
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "扫码被取消了", Toast.LENGTH_SHORT).show();
            } else {
                Pattern pattern = Pattern.compile("[0-9]*");
                if (!pattern.matcher(result.getContents()).matches()){
                    JsonOrderInterface jsonMsg = new Gson()
                            .fromJson(result.getContents(), JsonOrderInterface.class);
                    List<UserMangers> newsList = LitePal.where("i =?", "1").find(UserMangers.class);
                    if (newsList != null && newsList.size() == 1) {
                        for (int i = 0; i < newsList.size(); i++) {
                            floor = newsList.get(i).getFloor();
                        }
                    } else {
                        Toast.makeText(BaseActivity.this, "错误！", Toast.LENGTH_LONG).show();
                    }
                    switch (jsonMsg.getInterfaceHave()) {
                        case "https://starlibrary.online/haopeng/bookBorrow/sweeped":
                            ARouter.getInstance().build("/app_orders/OrderSweepedActivity")
                                    .withString("orderName", jsonMsg.getOrderName())
                                    .withString("interfaceHave", jsonMsg.getInterfaceHave())
                                    .navigation();
                            break;
                        case "https://starlibrary.online/haopeng/bookBorrow/registercheck?type=1": {
                            if (floor.equals("ALL")) {
                                ARouter.getInstance().build("/app_orders/OrderBorrowOutActivity")
                                        .withString("orderName", jsonMsg.getOrderName())
                                        .withString("interfaceHave", jsonMsg.getInterfaceHave())
                                        .navigation();
                            } else {
                                Toast.makeText(BaseActivity.this, "权限不足,订单已经全部取书，需要总管理员确认", Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case "https://starlibrary.online/haopeng/bookBorrow/registercheck?type=2": {
                            if (floor.equals("ALL")) {
                                ARouter.getInstance().build("/app_orders/OrderBorrowInActivity")
                                        .withString("orderName", jsonMsg.getOrderName())
                                        .withString("interfaceHave", jsonMsg.getInterfaceHave())
                                        .navigation();
                            } else {
                                Toast.makeText(BaseActivity.this, "权限不足,订单需要总管理员确认还书", Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case "https://starlibrary.online/haopeng/bookBorrow/directlyregister": {
                            if (floor.equals("ALL")) {
                                ARouter.getInstance().build("/app_orders/OrderDirectlyActivity")
                                        .withString("orderName", jsonMsg.getOrderName())
                                        .withString("interfaceHave", jsonMsg.getInterfaceHave())
                                        .navigation();
                            } else {
                                Toast.makeText(BaseActivity.this, "权限不足,订单需要总管理员确认直接借阅", Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                    }

                }else {
                    ARouter.getInstance().build("/app_bookmanagement/BookActivity").withInt("bookId", Integer.parseInt(result.getContents())).navigation();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
}
