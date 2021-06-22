package javaClassBorrow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
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
import com.e.app_borrow.OrderDirectlyActivity;
import com.e.app_borrow.R;
import com.e.mylibrary.Book;
import com.e.mylibrary.JsonOrder.JsonOrder;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class BookOrderBorrowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_ORDER_HEADER = 0;
    private static final int ITEM_ORDER_BODY = 1;
    private static final int ITEM_ORDER_EMPTY =2 ;
    View v;
    private List<JsonOrder> mDatas;
    int orderNum=0;
    int bookNum=0;
    int orderNumBook=0;
    int card;
    Activity activity;
    public BookOrderBorrowAdapter(List<JsonOrder> mDatas,int card) {
        this.mDatas = mDatas;
        this.card=card;
    }
    public BookOrderBorrowAdapter(List<JsonOrder> mDatas,int card,Activity activity) {
        this.mDatas = mDatas;
        this.card=card;
        this.activity=activity;
    }
    HashMap<Integer, JsonOrder> headers = new HashMap<>();
    HashMap<Integer, Book> bodyers = new HashMap<>();
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType==ITEM_ORDER_HEADER){
                v= LayoutInflater.from(parent.getContext()).inflate(R.layout.borrow_order_item_header, parent, false);
                return new HeaderHolder(v);
            }else if (viewType==ITEM_ORDER_BODY){
                v= LayoutInflater.from(parent.getContext()).inflate(R.layout.borrow_odrer_item_body, parent, false);
                return new BodyHolder(v);
            }else if(viewType==ITEM_ORDER_EMPTY) {
                v= LayoutInflater.from(parent.getContext()).inflate(R.layout.borrow_null_item, parent, false);
                return new VHNULL(v);
            }else {
                v=null;
                return new HeaderHolder(v);
            }
    }
    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderHolder){
            if (orderNum<mDatas.size()){
                Log.d("IIIIIIIIIIIIIIIIIIII", orderNum+"");
                headers.put(position,mDatas.get(orderNum));
                ((HeaderHolder) holder).tvOrder.setText("订单名："+mDatas.get(orderNum).getOrderName());
                if(mDatas.get(orderNum)!=null){
                    if(mDatas.get(orderNum).getType()==0){
                        long q=mDatas.get(orderNum).getUnrgTime().getTime()-System.currentTimeMillis();
                        if (q<1000*24*60*60&&q>0){
                            ((HeaderHolder) holder).tvOrderTime.setText(q/(1000*60*60)+"h后预约到期");
                        }else if (q<0){
                            ((HeaderHolder) holder).tvOrderTime.setText("预约已超时");
                            ((HeaderHolder) holder).tvOrderTime.setTextColor(R.color.colorAccent);
                        }else {
                            ((HeaderHolder) holder).tvOrderTime.setText("正在预约");
                        }
                    }else if (mDatas.get(orderNum).getType()==1){
                        long q=mDatas.get(orderNum).getUnboTime().getTime()-System.currentTimeMillis();
                        if (q<1000*24*60*60&&q>0){
                            ((HeaderHolder) holder).tvOrderTime.setText(q/(1000*24*60*60)+"天后借阅到期");
                        }else if (q<0){
                            ((HeaderHolder) holder).tvOrderTime.setText("借阅已超时");
                            ((HeaderHolder) holder).tvOrderTime.setTextColor(R.color.colorAccent);
                        }else {
                            ((HeaderHolder) holder).tvOrderTime.setText("正在借阅");
                        }
                    }else if(mDatas.get(orderNum).getType()==2){
                        ((HeaderHolder) holder).tvOrderTime.setText("已还书");
                    }else if (mDatas.get(orderNum).getType()==3){
                        if (mDatas.get(orderNum).getUnboTime()!=null){
                            long q=mDatas.get(orderNum).getUnboTime().getTime()-System.currentTimeMillis();
                            if (q<1000*24*60*60&&q>0){
                                ((HeaderHolder) holder).tvOrderTime.setText(q/(1000*24*60*60)+"天后借阅到期");
                            }else if (q<0){
                                ((HeaderHolder) holder).tvOrderTime.setText("借阅已超时"+Math.abs(q/(1000*24*60*60))+"天");
                                ((HeaderHolder) holder).tvOrderTime.setTextColor(R.color.red);
                            }else {
                                ((HeaderHolder) holder).tvOrderTime.setText("正在借阅");
                            }
                        }else {
                            ((HeaderHolder) holder).tvOrderTime.setTextColor(R.color.red);
                            ((HeaderHolder) holder).tvOrderTime.setText("预约超时");
                        }

                    }
                }
                orderNum+=1;
            }else {
                Log.d("pppppp", headers.toString());
                JsonOrder jsonOrder=headers.get(position);
                if (jsonOrder!=null){
                    if(jsonOrder.getType()==0){
                        long q=jsonOrder.getUnrgTime().getTime()-System.currentTimeMillis();
                        if (q<1000*24*60*60&&q>0){
                            ((HeaderHolder) holder).tvOrderTime.setText(q/(1000*60*60)+"h后预约到期");
                        }else if (q<0){
                            ((HeaderHolder) holder).tvOrderTime.setText("预约已超时");
                            ((HeaderHolder) holder).tvOrderTime.setTextColor(R.color.colorAccent);
                        }else {
                            ((HeaderHolder) holder).tvOrderTime.setText("正在预约");
                        }
                    }else if (jsonOrder.getType()==1){
                        long q=jsonOrder.getUnrgTime().getTime()-System.currentTimeMillis();
                        if (q<1000*24*60*60&&q>0){
                            ((HeaderHolder) holder).tvOrderTime.setText(q/(1000*24*60*60)+"天后借阅到期");
                        }else if (q<0){
                            ((HeaderHolder) holder).tvOrderTime.setText("借阅已超时");
                            ((HeaderHolder) holder).tvOrderTime.setTextColor(R.color.colorAccent);
                        }else {
                            ((HeaderHolder) holder).tvOrderTime.setText("正在借阅");
                        }
                    }else if(jsonOrder.getType()==2){
                        ((HeaderHolder) holder).tvOrderTime.setText("已还书");
                    }else if (jsonOrder.getType()==3){
                        long q=jsonOrder.getUnrgTime().getTime()-System.currentTimeMillis();
                        if (q<1000*24*60*60&&q>0){
                            ((HeaderHolder) holder).tvOrderTime.setText(q/(1000*24*60*60)+"天后借阅到期");
                        }else if (q<0){
                            ((HeaderHolder) holder).tvOrderTime.setText("借阅已超时"+Math.abs(q/(1000*24*60*60))+"天");
                            ((HeaderHolder) holder).tvOrderTime.setTextColor(R.color.colorAccent);
                        }else {
                            ((HeaderHolder) holder).tvOrderTime.setText("正在借阅");
                        }
                    }
                }
            }
            ((HeaderHolder) holder).tvOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Objects.requireNonNull(headers.get(position)).getType()==1||Objects.requireNonNull(headers.get(position)).getType()==3){
                        if (Objects.requireNonNull(headers.get(position)).getUnboTime()!=null){
                            ARouter.getInstance().build("/app_borrow/OrderActivity")
                                    .withString("orderName", Objects.requireNonNull(headers.get(position)).getOrderName())
                                    .navigation();
                        }

                    }
                }
            });
        }else if (holder instanceof BodyHolder){
            if (orderNumBook<mDatas.size()){
                Log.d("BBBBBBBBBBBBBBBBBBB", orderNumBook+"");
                Log.d("AAAAAAAAAAAAAAAAA", bookNum+"");
                bodyers.put(position,mDatas.get(orderNumBook).getBook().get(bookNum));
                final Book book = mDatas.get(orderNumBook).getBook().get(bookNum);
                ((BodyHolder) holder).tvBookName.setText(book.getBookName());
                ((BodyHolder) holder).tvBookAuthor.setText(book.getBookAuthor());
                Glide.with(v).load(book.getSrc()).into(((BodyHolder) holder).ivBookSrc);
                bookNum+=1;
                if(bookNum>=mDatas.get(orderNumBook).getBook().size()){
                    orderNumBook+=1;
                    bookNum=0;
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ARouter.getInstance().build("/app_book/BookActivity").withInt("bookId",book.getBookId()).withString("floor",book.getBookPlace()).navigation();
                    }
                });
                if (activity instanceof OrderDirectlyActivity){
                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            new XPopup.Builder(activity).asConfirm("删除书籍","书籍编号："+book.getBookId()+"\n确定从订单中删除书籍吗" ,
                                    new OnConfirmListener() {
                                        @Override
                                        public void onConfirm() {
                                            Log.d("TAG：", mDatas.get(0).getBook().size()+"");
                                            ((OrderDirectlyActivity) activity).deleteorderbynameandid(mDatas.get(0).getOrderName(),card,book.getBookId(),mDatas.get(0).getBook().size());
                                        }
                                    })
                                    .show();
                            return true;
                        }
                    });
                }
            }else {
                final Book book = bodyers.get(position);
                if (book!=null){
                    ((BodyHolder) holder).tvBookName.setText(book.getBookName());
                    ((BodyHolder) holder).tvBookAuthor.setText(book.getBookAuthor());
                    Glide.with(v).load(book.getSrc()).into(((BodyHolder) holder).ivBookSrc);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ARouter.getInstance().build("/app_book/BookActivity").withInt("bookId",book.getBookId()).withString("floor",book.getBookPlace()).navigation();
                        }
                    });
                    if (activity instanceof OrderDirectlyActivity){
                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                new XPopup.Builder(activity).asConfirm("删除书籍","书籍编号："+book.getBookId()+"\n确定从订单中删除书籍吗" ,
                                        new OnConfirmListener() {
                                            @Override
                                            public void onConfirm() {
                                                Log.d("TAG：", mDatas.get(0).getBook().size()+"");
                                                ((OrderDirectlyActivity) activity).deleteorderbynameandid(mDatas.get(0).getOrderName(),card,book.getBookId(),mDatas.get(0).getBook().size());
                                            }
                                        })
                                        .show();
                                return true;
                            }
                        });
                    }

                }
            }
        }else if (holder instanceof VHNULL){
            if (card!=0){
                ((VHNULL) holder).textView.setText("没有找到书籍哦");
            }else {
                ((VHNULL) holder).textView.setText("没有绑定图书证哦");
            }
        }
    }

    @Override
    public int getItemCount() {
        if(mDatas.size()>0){
            int count=0;
            for(int i=0;i<mDatas.size();i++){
                count += mDatas.get(i).getBook().size();
            }
            return count+mDatas.size();
        }else {
            return 1;
        }

    }
    public int getItemViewType(int position){
        if(mDatas.size()>0){
            int count=0;
            for(int i=0;i<mDatas.size();i++){
                if(position==count){
                    return ITEM_ORDER_HEADER;
                }
                count += mDatas.get(i).getBook().size()+1;
            }
            return ITEM_ORDER_BODY;
        }else {
            return ITEM_ORDER_EMPTY;
        }

    }

    static class HeaderHolder extends RecyclerView.ViewHolder {
        LinearLayout itemLayout;
        TextView tvOrder;
        TextView tvOrderTime;
        public HeaderHolder(View itemView) {
            super (itemView);
            itemLayout = itemView.findViewById (R.id.book_order_header);
            tvOrder = itemView.findViewById (R.id.book_order_header_orderName);
            tvOrderTime=itemView.findViewById(R.id.book_order_header_time);
        }
    }
    static class BodyHolder extends RecyclerView.ViewHolder {
        LinearLayout itemLayout;
        ImageView ivBookSrc;
        TextView tvBookName;
        TextView tvBookAuthor;
        public BodyHolder(View itemView) {
            super (itemView);
            itemLayout = itemView.findViewById (R.id.book_order_body);
            ivBookSrc=itemView.findViewById(R.id.book_order_body_image);
            tvBookName = itemView.findViewById (R.id.book_order_body_info_bookName);
            tvBookAuthor=itemView.findViewById(R.id.book_order_body_bookAuthor);
        }
    }
    static class VHNULL extends RecyclerView.ViewHolder{
        TextView textView;
        public VHNULL(View v) {
            super(v);
            textView=v.findViewById(R.id.tvNull);
        }
    }
}
