package javaclassManagers;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.e.JavaClass.Block;
import com.e.JavaClass.UserMangers;
import com.e.app_managers.R;
import com.e.app_managers.ShowBlockManagerActivity;

import org.litepal.LitePal;

import java.util.List;

public class Block2Adapter extends RecyclerView.Adapter<Block2Adapter.ViewHolder>{

    private String floor;
    private Context mContext;
    private List<Block> mBlockList;
    ShowBlockManagerActivity showBlockManagerActivity;
    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView BookNewImage;
        TextView BookNewName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            BookNewImage = view.findViewById(R.id.bookImage);
            BookNewName = view.findViewById(R.id.bookName);
        }
    }

    public Block2Adapter(List<Block> BookNewList, ShowBlockManagerActivity s) {
        mBlockList = BookNewList;
        showBlockManagerActivity=s;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.block_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Block block = mBlockList.get(position);
        holder.BookNewName.setText(block.getName());
        Glide.with(mContext).load(block.getImageId()).into(holder.BookNewImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                List<UserMangers> newsList = LitePal.where("i =?", "1").find(UserMangers.class);
                if(newsList != null && newsList.size()==1){
                    for (int i=0;i<newsList.size();i++){
                        floor=newsList.get(i).getFloor();
                    }
                }else {
                    showBlockManagerActivity.aaa("错误！");
                }
                switch (block.getName()) {

                    case "添加管理员":
                        if (floor.equals("ALL")){
                            ARouter.getInstance().build("/app_managers/AddManagerActivity").navigation();
                        }else {
                            showBlockManagerActivity.aaa("权限不足！");
                        }
                        break;
                    case "修改管理员":
                    case "删除管理员":
                        if (floor.equals("ALL")){
                            ARouter.getInstance().build("/app_managers/ShowManagersActivity").navigation();
                        }else {
                            showBlockManagerActivity.aaa("权限不足！");
                        }
                        break;
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mBlockList.size();
    }

}
