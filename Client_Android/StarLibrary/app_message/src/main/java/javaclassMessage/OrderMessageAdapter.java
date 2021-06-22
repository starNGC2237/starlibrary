package javaclassMessage;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.e.app_message.MessageActivity;
import com.e.app_message.R;
import com.e.mylibrary.JsonOrder.JsonOrder;

import java.util.List;

public class OrderMessageAdapter extends RecyclerView.Adapter<OrderMessageAdapter.MyHolder> {

    View v;
    private List<JsonOrder> mDatas;
    Activity activity;
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
            return new MyHolder(v);
    }
    public OrderMessageAdapter(List<JsonOrder> data,Activity activity) {
        this.mDatas = data;
        this.activity=activity;
    }
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            final JsonOrder order = mDatas.get(position);
            holder.tvMessageTitle.setText("订单"+order.getOrderName()+"正在等待取书\n点击获取订单二维码");
            if (order.getReaded()==1){
                holder.ivMessageDian.setVisibility(View.INVISIBLE);
            }else {
                holder.ivMessageDian.setVisibility(View.VISIBLE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activity instanceof MessageActivity){
                        ((MessageActivity) activity).readed(order.getOrderName());
                    }
                    ARouter.getInstance().build("/app_borrow/OrderActivity")
                            .withString("orderName",order.getOrderName())
                            .navigation();
                }
            });
    }

    @Override
    public int getItemCount() {
        Log.d("AAAAAAAAAAAAAAA", "getItemCount: "+mDatas.size());
        return mDatas.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        LinearLayout itemLayout;
        TextView tvMessageTitle;
        ImageView ivMessageDian;
        public MyHolder(View itemView) {
            super (itemView);
            itemLayout = itemView.findViewById (R.id.message_item);
            tvMessageTitle=itemView.findViewById(R.id.message_title);
            ivMessageDian=itemView.findViewById(R.id.message_dian);
        }
    }
}
