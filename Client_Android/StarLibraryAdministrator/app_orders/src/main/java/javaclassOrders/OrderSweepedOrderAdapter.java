package javaclassOrders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.e.JavaClass.Book;
import com.e.JavaClass.JsonOrder.JsonOrder;
import com.e.JavaClass.UserMangers;
import com.e.app_orders.OrderBorrowInActivity;
import com.e.app_orders.OrderBorrowOutActivity;
import com.e.app_orders.OrderSweepedActivity;
import com.e.app_orders.R;

import org.litepal.LitePal;

import java.util.List;

public class OrderSweepedOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<JsonOrder> mDatas;
    View v;
    String floor;
    Activity activity;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mDatas.size()>0){
            if (activity instanceof OrderBorrowOutActivity) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_order_sweeped_item, parent, false);
                return new MyHolder(v);
            }else if(activity instanceof OrderBorrowInActivity){
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.borrow_odrer_item_body, parent, false);
                return new MyInHolder(v);
            }else if (activity instanceof OrderSweepedActivity){
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_order_sweeped_item, parent, false);
                return new MyHolder(v);
            }else {
                v= LayoutInflater.from(parent.getContext()).inflate(R.layout.borrow_null_item, parent, false);
                return new VHNULL(v);
            }
        }else {
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.borrow_null_item, parent, false);
            return new VHNULL(v);
        }
    }
    public OrderSweepedOrderAdapter(List<JsonOrder> data,Activity activity) {
        this.mDatas = data;
        this.activity=activity;
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyHolder){
            Glide.with(v).load(mDatas.get(position).getBook().get(0).getSrc()).into(((MyHolder) holder).ivImage);
            String stringBookId="bookid："+mDatas.get(position).getBook().get(0).getBookId();
            ((MyHolder) holder).tvBookId.setText(stringBookId);
            ((MyHolder) holder).tvBookName.setText(mDatas.get(position).getBook().get(0).getBookName());
            ((MyHolder) holder).tvBookFloor.setText("floor："+mDatas.get(position).getBook().get(0).getBookPlace());
            if (mDatas.get(position).getSweeped()==0){
                ((MyHolder) holder).cb.setChecked(false);
            }else if(mDatas.get(position).getSweeped()==1) {
                ((MyHolder) holder).cb.setChecked(true);
            }
            if (activity instanceof OrderSweepedActivity) {
                ((MyHolder) holder).cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        List<UserMangers> newsList = LitePal.where("i =?", "1").find(UserMangers.class);
                        if (newsList != null && newsList.size() == 1) {
                            for (int i = 0; i < newsList.size(); i++) {
                                floor = newsList.get(i).getFloor();
                            }
                        }
                        if (mDatas.get(position).getFloor().equals(floor)) {
                            if (mDatas.get(position).getSweeped() == 0) {
                                if (activity instanceof OrderSweepedActivity) {
                                    ((OrderSweepedActivity) activity).sweeped(mDatas.get(position).getBorrowId());
                                }
                            } else {
                                ((MyHolder) holder).cb.setChecked(!((MyHolder) holder).cb.isChecked());
                            }

                        } else {
                            if (activity instanceof OrderSweepedActivity) {
                                if (floor.equals("ALL")) {
                                    ((OrderSweepedActivity) activity).toast("请刷新二维码");
                                    ((MyHolder) holder).cb.setChecked(!((MyHolder) holder).cb.isChecked());
                                } else {
                                    ((OrderSweepedActivity) activity).toast("请点击关于您的楼层的书籍");
                                    ((MyHolder) holder).cb.setChecked(!((MyHolder) holder).cb.isChecked());
                                }
                            }

                        }
                    }
                });
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ARouter.getInstance().build("/app_bookmanagement/BookActivity").withInt("bookId",mDatas.get(position).getBook().get(0).getBookId()).navigation();
                }
            });
        }else if(holder instanceof VHNULL){
            ((VHNULL) holder).tvNull.setText("订单中没有书籍");
        }else if (holder instanceof MyInHolder){
            ((MyInHolder) holder).tvBookName.setText(mDatas.get(position).getBook().get(0).getBookName());
            ((MyInHolder) holder).tvBookAuthor.setText(mDatas.get(0).getBook().get(0).getBookAuthor());
            Glide.with(v).load(mDatas.get(position).getBook().get(0).getSrc()).into(((MyInHolder) holder).ivBookSrc);
            ((MyInHolder) holder).tvBookId.setText("bookid："+mDatas.get(0).getBook().get(0).getBookId());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ARouter.getInstance().build("/app_bookmanagement/BookActivity").withInt("bookId",mDatas.get(position).getBook().get(0).getBookId()).navigation();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
    static class MyHolder extends RecyclerView.ViewHolder {
        LinearLayout itemLayout;
        CheckBox cb;
        ImageView ivImage;
        TextView tvBookName;
        TextView tvBookFloor;
        TextView tvBookId;
        public MyHolder(View itemView) {
            super (itemView);
            itemLayout = itemView.findViewById (R.id.order_order_sweeped_item);
            cb=itemView.findViewById(R.id.order_order_item_cb);
            ivImage=itemView.findViewById(R.id.order_order_item_image);
            tvBookName=itemView.findViewById(R.id.order_order_item_info_bookName);
            tvBookFloor=itemView.findViewById(R.id.order_order_item_bookFloor);
            tvBookId=itemView.findViewById(R.id.order_order_item_bookId);
        }
    }
    static class MyInHolder extends RecyclerView.ViewHolder {
        LinearLayout itemLayout;
        ImageView ivBookSrc;
        TextView tvBookName;
        TextView tvBookAuthor;
        TextView tvBookId;
        public MyInHolder(View itemView) {
            super (itemView);
            itemLayout = itemView.findViewById (R.id.book_order_body);
            ivBookSrc=itemView.findViewById(R.id.book_order_body_image);
            tvBookName = itemView.findViewById (R.id.book_order_body_info_bookName);
            tvBookAuthor=itemView.findViewById(R.id.book_order_body_bookAuthor);
            tvBookId=itemView.findViewById(R.id.book_order_body_bookId);
        }
    }
    static class VHNULL extends RecyclerView.ViewHolder{
        TextView tvNull;
        public VHNULL(View itemView) {
            super(itemView);
            tvNull=itemView.findViewById(R.id.tvNull);
        }
    }
}
