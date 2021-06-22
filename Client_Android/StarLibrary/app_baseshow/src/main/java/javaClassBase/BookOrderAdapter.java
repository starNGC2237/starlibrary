package javaClassBase;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.e.app_baseshow.OrderFragment;
import com.e.app_baseshow.R;
import com.e.mylibrary.Book;
import com.e.mylibrary.JsonOrder.JsonOrder;

import java.util.HashMap;
import java.util.List;

public class BookOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_ORDER_HEADER = 0;
    private static final int ITEM_ORDER_BODY = 1;
    private static final int ITEM_ORDER_EMPTY =2 ;
    View v;
    private List<JsonOrder> mDatas;
    int orderNum=0;
    int bookNum=0;
    int orderNumBook=0;
    OrderFragment fragment;
    int card;
    public BookOrderAdapter(List<JsonOrder> mDatas, OrderFragment fragment,int card) {
        this.mDatas = mDatas;
        this.fragment=fragment;
        this.card=card;
    }
    HashMap<Integer, String> headers = new HashMap<>();
    HashMap<Integer, Book> bodyers = new HashMap<>();
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType==ITEM_ORDER_HEADER){
                v= LayoutInflater.from(parent.getContext()).inflate(R.layout.base_book_order_item_header, parent, false);
                return new HeaderHolder(v);
            }else if (viewType==ITEM_ORDER_BODY){
                v= LayoutInflater.from(parent.getContext()).inflate(R.layout.base_book_odrer_item_body, parent, false);
                return new BodyHolder(v);
            }else if(viewType==ITEM_ORDER_EMPTY) {
                v= LayoutInflater.from(parent.getContext()).inflate(R.layout.my_null_item, parent, false);
                return new VHNULL(v);
            }else {
                v=null;
                return new HeaderHolder(v);
            }

    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder){
            if (orderNum<mDatas.size()){
                Log.d("IIIIIIIIIIIIIIIIIIII", orderNum+"");
                headers.put(position,mDatas.get(orderNum).getOrderName());
                ((HeaderHolder) holder).tvOrder.setText("订单名："+mDatas.get(orderNum).getOrderName());
                holder.itemView.setOnClickListener(v -> {
                    ARouter.getInstance().build("/app_borrow/OrderDirectlyActivity")
                            .withString("orderName",headers.get(position))
                            .navigation();
                });
                ((HeaderHolder) holder).cbBookHeader.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (((HeaderHolder) holder).cbBookHeader.isChecked()){
                        ((HeaderHolder) holder).tvOrderSubmit.setVisibility(View.VISIBLE);
                        ((HeaderHolder) holder).tvOrderDirectlyregister.setVisibility(View.VISIBLE);
                    }else {
                        ((HeaderHolder) holder).tvOrderSubmit.setVisibility(View.INVISIBLE);
                        ((HeaderHolder) holder).tvOrderDirectlyregister.setVisibility(View.INVISIBLE);
                    }
                });
                ((HeaderHolder) holder).tvOrderSubmit.setOnClickListener(v -> {
                    String h=headers.get(position);
                    headers.clear();
                    bodyers.clear();
                    orderNum=0;
                    bookNum=0;
                    orderNumBook=0;
                    fragment.appointment(h);
                });
                ((HeaderHolder) holder).tvOrderDirectlyregister.setOnClickListener(v -> {
                    ARouter.getInstance().build("/app_borrow/OrderDirectlyActivity")
                            .withString("orderName",headers.get(position))
                            .navigation();
                });
                orderNum+=1;
            }else {
                Log.d("pppppp", headers.toString());
                ((HeaderHolder) holder).tvOrder.setText("订单名："+headers.get(position));
                holder.itemView.setOnClickListener(v -> {
                    ARouter.getInstance().build("/app_borrow/OrderDirectlyActivity")
                            .withString("orderName",headers.get(position))
                            .navigation();
                });
                ((HeaderHolder) holder).cbBookHeader.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (((HeaderHolder) holder).cbBookHeader.isChecked()){
                        ((HeaderHolder) holder).tvOrderSubmit.setVisibility(View.VISIBLE);
                        ((HeaderHolder) holder).tvOrderDirectlyregister.setVisibility(View.VISIBLE);
                    }else {
                        ((HeaderHolder) holder).tvOrderSubmit.setVisibility(View.INVISIBLE);
                        ((HeaderHolder) holder).tvOrderDirectlyregister.setVisibility(View.INVISIBLE);
                    }
                });
                ((HeaderHolder) holder).tvOrderSubmit.setOnClickListener(v -> {
                    String h=headers.get(position);
                    headers.clear();
                    bodyers.clear();
                    orderNum=0;
                    bookNum=0;
                    orderNumBook=0;
                    fragment.appointment(h);
                });
                ((HeaderHolder) holder).tvOrderDirectlyregister.setOnClickListener(v -> {
                    ARouter.getInstance().build("/app_borrow/OrderDirectlyActivity")
                            .withString("orderName",headers.get(position))
                            .navigation();
                });
            }

        }else if (holder instanceof BodyHolder){
            if (orderNumBook<mDatas.size()){
                Log.d("BBBBBBBBBBBBBBBBBBB", orderNumBook+"");
                Log.d("AAAAAAAAAAAAAAAAA", bookNum+"");
                bodyers.put(position,mDatas.get(orderNumBook).getBook().get(bookNum));
                Book book = mDatas.get(orderNumBook).getBook().get(bookNum);
                ((BodyHolder) holder).tvBookName.setText(book.getBookName());
                ((BodyHolder) holder).tvBookAuthor.setText(book.getBookAuthor());
                Glide.with(v).load(book.getSrc()).into(((BodyHolder) holder).ivBookSrc);
                bookNum+=1;
                if(bookNum>=mDatas.get(orderNumBook).getBook().size()){
                    orderNumBook+=1;
                    bookNum=0;
                }
                holder.itemView.setOnClickListener(v ->
                        ARouter.getInstance().build("/app_book/BookActivity").withInt("bookId",book.getBookId()).withString("floor",book.getBookPlace()).navigation());
            }else {
                Book book = bodyers.get(position);
                assert book != null;
                ((BodyHolder) holder).tvBookName.setText(book.getBookName());
                ((BodyHolder) holder).tvBookAuthor.setText(book.getBookAuthor());
                Glide.with(v).load(book.getSrc()).into(((BodyHolder) holder).ivBookSrc);
                holder.itemView.setOnClickListener(v -> ARouter.getInstance().build("/app_book/BookActivity").withInt("bookId",book.getBookId()).withString("floor",book.getBookPlace()).navigation());
            }
        }else if (holder instanceof VHNULL){
            if (card!=0){
                ((VHNULL) holder).tvNull.setText("没有待提交的订单哦");
            }else {
                ((VHNULL) holder).tvNull.setText("没有绑定借书证哦");
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
        CheckBox cbBookHeader;
        TextView tvOrder;
        TextView tvOrderSubmit;
        TextView tvOrderDirectlyregister;
        public HeaderHolder(View itemView) {
            super (itemView);
            itemLayout = itemView.findViewById (R.id.base_book_order_header);
            cbBookHeader = itemView.findViewById (R.id.base_book_order_header_orderCheck);
            tvOrder = itemView.findViewById (R.id.base_book_order_header_orderName);
            tvOrderSubmit=itemView.findViewById(R.id.base_book_order_header_submit);
            tvOrderDirectlyregister=itemView.findViewById(R.id.base_book_order_header_directlyregister);
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
        TextView tvNull;
        public VHNULL(View v) {
            super(v);
            tvNull=v.findViewById(R.id.tvNull);
        }
    }
}
