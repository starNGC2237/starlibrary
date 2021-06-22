package javaclassManagers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.e.JavaClass.Book;
import com.e.JavaClass.Manager;
import com.e.app_managers.R;

import java.util.List;

public class ManagerSearchNewManagersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    View v;
    private List<Manager> mDatas;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(mDatas.size()>0){
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.manager_managers_item, parent, false);
            return new MyHolder(v);
        }else {
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.manager_managers_null_item, parent, false);
            return new VHNULL(v);
        }

    }
    public ManagerSearchNewManagersAdapter(List<Manager> data) {
        this.mDatas = data;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder){
            final Manager manager = mDatas.get(position);
            Glide.with(v).load(R.drawable.manager).into(((MyHolder) holder).ivImage);
            ((MyHolder) holder).tvUserName.setText("用户名："+manager.getUserName());
            ((MyHolder) holder).tvPhone.setText("手机号："+manager.getPhone());
            ((MyHolder) holder).tvFloor.setText("楼层："+manager.getFloor());
            ((MyHolder) holder).itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ARouter.getInstance().build("/app_managers/ModifyManagerActivity")
                            .withString("floor",manager.getFloor())
                            .withString("userName",manager.getUserName())
                            .withString("nickName",manager.getNickName())
                            .withString("passWord",manager.getPassWord())
                            .withString("createTime",manager.getCreateTime())
                            .withString("phone",manager.getPhone())
                            .navigation();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (mDatas.size()>0){
            return mDatas.size();
        }else {
            return 1;
        }

    }

    static class MyHolder extends RecyclerView.ViewHolder {
        LinearLayout itemLayout;
        ImageView ivImage;
        TextView tvUserName;
        TextView tvPhone;
        TextView tvFloor;
        public MyHolder(View itemView) {
            super (itemView);
            itemLayout = itemView.findViewById (R.id.book_bookmanagerment_item);
            ivImage = itemView.findViewById (R.id.managerImage_managers);
            tvUserName = itemView.findViewById (R.id.userName_managers);
            tvPhone=itemView.findViewById(R.id.phone_managers);
            tvFloor=itemView.findViewById(R.id.floor_managers);
        }
    }
    static class VHNULL extends RecyclerView.ViewHolder{
        public VHNULL(View v) {
            super(v);
        }
    }
}
