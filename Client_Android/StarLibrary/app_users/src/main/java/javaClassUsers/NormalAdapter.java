package javaClassUsers;

import android.content.ContentValues;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.e.app_users.R;
import com.e.mylibrary.UserIn;
import com.e.util.ActivityCollector;

import org.litepal.LitePal;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NormalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int ITEM_USER_INFO = 0;
    private static final int ITEM_USER_EXIT = 1;
    private List<UserIn> mDatas;
    private Boolean deleteMode=false;
    View v;


    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder{

        public final TextView title;
        public final ImageView image;
        public final ImageView imageyes;
        public VH(View v) {
            super(v);
            title = v.findViewById(R.id.option_text);
            image=v.findViewById(R.id.option_image);
            imageyes=v.findViewById(R.id.option_imageYes);

        }
    }
    public static class VHEXIT extends RecyclerView.ViewHolder{
        public final TextView exit;
        public VHEXIT(View v) {
            super(v);
            exit=v.findViewById(R.id.exit_users);
        }
    }

    public NormalAdapter(List<UserIn> data) {
        this.mDatas = data;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            if(holder instanceof VH) {
                final UserIn userIn = mDatas.get(position);
                List<UserIn> newsList = LitePal.where("i =?", "1").find(UserIn.class);
                if (deleteMode){
                    ((VH) holder).imageyes.setImageResource(R.drawable.ic_action_clear);
                    ((VH) holder).imageyes.setVisibility(View.VISIBLE);
                }else {
                    ((VH) holder).imageyes.setImageResource(R.drawable.ic_yes);
                    if(newsList != null && newsList.size()==1){
                        for (int i=0;i<newsList.size();i++){
                            if(userIn.getUserName().equals(newsList.get(i).getUserName())){
                                ((VH) holder).imageyes.setVisibility(View.VISIBLE);
                            }else {
                                ((VH) holder).imageyes.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }
                String text = userIn.getNickName() + "\n用户名：" + userIn.getUserName();
                ((VH) holder).title.setText(text);
                Glide.with(v).load(userIn.getSrc()).into(((VH) holder).image);
                if (deleteMode){
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDatas.remove(position);
                            ContentValues values = new ContentValues();
                            if (userIn.getI()==1){
                                values.put("i", 3);
                            }else if(userIn.getI()==0){
                                values.put("i", 2);
                            }
                            LitePal.updateAll(UserIn.class, values, "username = ?",userIn.getUserName());
                            //删除动画
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, mDatas.size()+2);
                        }
                    });
                }else {
                    ((VH) holder).itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            List<UserIn> newsList = LitePal.where("i =?", "1").find(UserIn.class);
                            if(newsList != null && newsList.size()==1){
                                for (int i=0;i<newsList.size();i++){
                                    ContentValues values = new ContentValues();
                                    values.put("i", 0);
                                    LitePal.updateAll(UserIn.class, values, "i = ?", "1");
                                }
                            }
                            ContentValues values = new ContentValues();
                            values.put("i", 1);
                            LitePal.updateAll(UserIn.class, values, "username = ?",userIn.getUserName());
                            notifyDataSetChanged();
                        }
                    });
                }
            }
            else  if(holder instanceof VHEXIT){
                if (position == getItemCount()-1){
                    ((VHEXIT) holder).exit.setText(R.string.exit_APP_users);
                }else {
                    ((VHEXIT) holder).exit.setText("退出登录");
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position == getItemCount()-1) {
                            ActivityCollector.finishAll();
                        } else {
                            List<UserIn> newsList = LitePal.where("i =?", "1").find(UserIn.class);
                            if (newsList != null && !newsList.isEmpty()) {
                                for (int i = 0; i < newsList.size(); i++) {
                                    ContentValues values = new ContentValues();
                                    values.put("i", 0);
                                    LitePal.updateAll(UserIn.class, values, "i = ?", "1");
                                }
                            } else {
                                Toast.makeText(v.getContext(), "错误", Toast.LENGTH_SHORT).show();
                            }
                            ActivityCollector.finishAll();
                            ARouter.getInstance().build("/app/WelcomeActivity").navigation();
                        }
                    }
                });
            }

    }
    public int getItemViewType(int position){
        if (position>=getItemCount()-2){
            return ITEM_USER_EXIT;
        }else {
            return ITEM_USER_INFO;
        }
    }
    //进入或退出删除模式
    public void deleteMode(){
        deleteMode= !deleteMode;
        notifyDataSetChanged();
    }
    //检查用户，如果没有就切换
    public void checkUsers(){
        List<UserIn> newsList = LitePal.where("i =?", "2").find(UserIn.class);
        List<UserIn> newsList2 = LitePal.where("i =?", "3").find(UserIn.class);
        if ((newsList != null&&!newsList.isEmpty())||(newsList2!=null&&!newsList2.isEmpty()) ){
            LitePal.deleteAll(UserIn.class, "i= ?","2");
            LitePal.deleteAll(UserIn.class, "i= ?","3");
            if (mDatas.size()!=0){
                UserIn userIn = mDatas.get(0);
                if (userIn!=null){
                    ContentValues values = new ContentValues();
                    values.put("i", 1);
                    LitePal.updateAll(UserIn.class, values, "username = ?", userIn.getUserName());
                }else {
                    Toast.makeText(v.getContext(),"错误1",Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(v.getContext(),"没有账号被保存，即将跳转至登录",Toast.LENGTH_SHORT).show();
                Timer timer=new Timer();
                TimerTask timerTask=new TimerTask() {
                    @Override
                    public void run() {
                        ActivityCollector.finishAll();
                        ARouter.getInstance().build("/app_login/LoginActivity").navigation();
                    }
                };
                timer.schedule(timerTask,1000);

            }
        }
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return mDatas.size()+2;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_USER_INFO) {
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout, parent, false);
            return new VH(v);
        } else if (viewType==ITEM_USER_EXIT){
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout2, parent, false);
            return new VHEXIT(v);
        }else {
            v=null;
            return new VH(v);
        }
        
    }


}