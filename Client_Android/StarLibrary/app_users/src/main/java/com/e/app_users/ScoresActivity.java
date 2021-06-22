package com.e.app_users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.e.util.BaseTActivity;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;

import java.util.Objects;

import javaClassUsers.CustomPopupUsers;

@Route(path = "/app_users/ScoresActivity")
public class ScoresActivity extends BaseTActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        final Toolbar toolbar = findViewById(R.id.toolbar_scores);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        ImageView help_tips_img=findViewById(R.id.help_tips_img);
        help_tips_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new XPopup.Builder(ScoresActivity.this)
                        .asCustom(new CustomPopupUsers(ScoresActivity.this,"信誉分数规则","用户初始分数为90分\n信誉扣分规则：\n1.完成借阅预约后取书超时：-15\n2.书籍借阅超时：-1/天\n信誉加分规则：\n1.按时还书：+1\n信誉最大借阅容量规则：\n90-100 一共可以借5本，80-90 4本，70-80 2本，65-70 1本，小于65还清全部书前无法借阅，全部还清后分数变为65"))
                        .show();
            }
        });
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