package javaClassBase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.e.app_baseshow.R;

import java.util.List;

public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.MyHolder> {
    Context context;
    private List<Option> list;
    public OptionAdapter(Context context, List<Option> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.option_users_item,parent,false);
        final MyHolder myHolder = new MyHolder(view);
        myHolder.itemView.setOnClickListener(v -> {
            int position=myHolder.getAdapterPosition();
            Option option=list.get(position);
            if ("账号管理".equals(option.getName())) {
                ARouter.getInstance().build("/app_users/UsersActivity").navigation();
            }else if ("预约中的订单".equals(option.getName())){
                ARouter.getInstance().build("/app_borrow/ReservingActivity").navigation();
            }else if ("借阅中的订单".equals(option.getName())){
                ARouter.getInstance().build("/app_borrow/BorrowingBorrowActivity").navigation();
            }else if ("已还的订单".equals(option.getName())){
                //已还
                ARouter.getInstance().build("/app_borrow/BorrowedBorrowActivity").navigation();
            }else if ("超时的订单".equals(option.getName())){
                // 超时
                ARouter.getInstance().build("/app_borrow/TimeOutActivity").navigation();
            }else if ("绑定图书证".equals(option.getName())){
                ARouter.getInstance().build("/app_users/BindUsersActivity").navigation();
            }else if ("查看收藏".equals(option.getName())){
                // 跳转到查看收藏
                ARouter.getInstance().build("/app_borrow/FavoritesActivity").navigation();
            }else if ("关于我们".equals(option.getName())){
                ARouter.getInstance().build("/app_base/AboutUsActivity").navigation();
            }else if ("阅书圈".equals(option.getName())){
                ARouter.getInstance().build("/app_yueshuquan/YHomeActivity").navigation();
            }else{
                Toast.makeText(view.getContext(),"错误",Toast.LENGTH_LONG).show();
            }
        });
        return myHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Option item = list.get (position);
        Glide.with (context).load (item.getImageId()).into (holder.ivImage);
        holder.tvTitle.setText (item.getName());
    }
    @Override
    public int getItemCount() {
        return list.size ();
    }
    static class MyHolder extends RecyclerView.ViewHolder {
        LinearLayout itemLayout;
        ImageView ivImage;
        TextView tvTitle;
        public MyHolder(View itemView) {
            super (itemView);
            itemLayout = itemView.findViewById (R.id.line1);
            ivImage = itemView.findViewById (R.id.option_image);
            tvTitle = itemView.findViewById (R.id.option_text);
        }
    }

}