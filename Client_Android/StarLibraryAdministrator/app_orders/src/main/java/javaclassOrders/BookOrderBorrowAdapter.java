package javaclassOrders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.e.JavaClass.Book;
import com.e.JavaClass.UserMangers;
import com.e.app_orders.OrderReadyShowActivity;
import com.e.app_orders.R;
import com.e.JavaClass.JsonOrder.JsonOrder;

import org.litepal.LitePal;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class BookOrderBorrowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_ORDER_HEADER = 0;
    private static final int ITEM_ORDER_BODY = 1;
    private static final int ITEM_ORDER_EMPTY =2 ;
    View v;
    private List<JsonOrder> mDatas;
    Activity activity;
    int orderNum=0;
    int bookNum=0;
    int orderNumBook=0;
    String floor;
    public BookOrderBorrowAdapter(List<JsonOrder> mDatas, Activity activity) {
        this.mDatas = mDatas;
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
                ((HeaderHolder) holder).tvOrderTimeOrOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (((HeaderHolder) holder).tvOrderTimeOrOk.getText().toString().equals("确认已准备")){
                            if (activity instanceof OrderReadyShowActivity){
                                String h= Objects.requireNonNull(headers.get(position)).getOrderName();
                                headers.clear();
                                bodyers.clear();
                                orderNum=0;
                                bookNum=0;
                                orderNumBook=0;
                                ((OrderReadyShowActivity) activity).readybook(h);
                            }
                        }
                    }
                });
                if(mDatas.get(orderNum)!=null){
                    if (mDatas.get(orderNum).getUnrgTime()!=null&&mDatas.get(orderNum).getUnboTime()==null){
                        long q=mDatas.get(orderNum).getUnrgTime().getTime()-System.currentTimeMillis();
                        if (q<1000*24*60*60&&q>0){
                            ((HeaderHolder) holder).tvOrderTimeOrOk.setText(q/(1000*60*60)+"h后到期");
                        }else if (q<0){
                            ((HeaderHolder) holder).tvOrderTimeOrOk.setText("预约已超时");
                            ((HeaderHolder) holder).tvOrderTimeOrOk.setTextColor(R.color.colorAccent);
                        }else {
                            ((HeaderHolder) holder).tvOrderTimeOrOk.setText("正在预约");
                        }
                    }else if(mDatas.get(orderNum).getUnrgTime()!=null&&mDatas.get(orderNum).getUnboTime()!=null){
                        ((HeaderHolder) holder).tvOrder.setText("订单名："+mDatas.get(orderNum).getOrderName());
                        long q=mDatas.get(orderNum).getUnboTime().getTime()-System.currentTimeMillis();
                        if (q<1000*24*60*60&&q>0){
                            ((HeaderHolder) holder).tvOrderTimeOrOk.setText(q/(1000*24*60*60)+"天后借阅到期");
                        }else if (q<0){
                            ((HeaderHolder) holder).tvOrderTimeOrOk.setText("借阅已超时");
                            ((HeaderHolder) holder).tvOrderTimeOrOk.setTextColor(R.color.colorAccent);
                        }else {
                            ((HeaderHolder) holder).tvOrderTimeOrOk.setText("正在借阅");
                        }
                    }
                    if (mDatas.get(orderNum).getReadyed()==1){
                        ((HeaderHolder) holder).cbOrderCheck.setChecked(true);
                        ((HeaderHolder) holder).tvOrderTimeOrOk.setText("已确认");
                    }
                }
                orderNum+=1;

            }else {
                Log.d("pppppp", headers.toString());
                final JsonOrder jsonOrder=headers.get(position);
                if (jsonOrder!=null){
                    ((HeaderHolder) holder).tvOrderTimeOrOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (((HeaderHolder) holder).tvOrderTimeOrOk.getText().toString().equals("确认已准备")){
                                if (activity instanceof OrderReadyShowActivity){
                                    String h=jsonOrder.getOrderName();
                                    headers.clear();
                                    bodyers.clear();
                                    orderNum=0;
                                    bookNum=0;
                                    orderNumBook=0;
                                    ((OrderReadyShowActivity) activity).readybook(h);
                                }
                            }
                        }
                    });
                    if (jsonOrder.getUnboTime()==null&&jsonOrder.getUnrgTime()!=null){
                        ((HeaderHolder) holder).tvOrder.setText("订单名："+jsonOrder.getOrderName());
                        long q=jsonOrder.getUnrgTime().getTime()-System.currentTimeMillis();
                        if (q<1000*24*60*60&&q>0){
                            ((HeaderHolder) holder).tvOrderTimeOrOk.setText(q/(1000*24*60*60)+"h后到期");
                        }else if (q<0){
                            ((HeaderHolder) holder).tvOrderTimeOrOk.setText("预约已超时");
                            ((HeaderHolder) holder).tvOrderTimeOrOk.setTextColor(R.color.colorAccent);
                        }else {
                            ((HeaderHolder) holder).tvOrderTimeOrOk.setText("正在预约");
                        }
                    }else if(jsonOrder.getUnrgTime()!=null&&jsonOrder.getUnboTime()!=null){
                        ((HeaderHolder) holder).tvOrder.setText("订单名："+jsonOrder.getOrderName());
                        long q=jsonOrder.getUnboTime().getTime()-System.currentTimeMillis();
                        if (q<1000*24*60*60&&q>0){
                            ((HeaderHolder) holder).tvOrderTimeOrOk.setText(q/(1000*24*60*60)+"天后借阅到期");
                        }else if (q<0){
                            ((HeaderHolder) holder).tvOrderTimeOrOk.setText("借阅已超时");
                            ((HeaderHolder) holder).tvOrderTimeOrOk.setTextColor(R.color.colorAccent);
                        }else {
                            ((HeaderHolder) holder).tvOrderTimeOrOk.setText("正在借阅");
                        }
                    }
                    if (jsonOrder.getReadyed()==1){
                        ((HeaderHolder) holder).cbOrderCheck.setChecked(true);
                        ((HeaderHolder) holder).tvOrderTimeOrOk.setText("已确认");
                    }
                }
            }
            ((HeaderHolder) holder).cbOrderCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    List<UserMangers> newsList = LitePal.where("i =?", "1").find(UserMangers.class);
                    if(newsList != null && newsList.size()==1){
                        for (int i=0;i<newsList.size();i++){
                            floor = newsList.get(i).getFloor();
                        }
                    }
                    if (!floor.equals("ALL")){
                        if (Objects.requireNonNull(headers.get(position)).getReadyed()==0){
                            if (((HeaderHolder) holder).cbOrderCheck.isChecked()) {
                                ((HeaderHolder) holder).tvOrderTimeOrOk.setText("确认已准备");
                            }else {
                                ((HeaderHolder) holder).tvOrderTimeOrOk.setText("");
                            }
                        }else {
                            ((HeaderHolder) holder).cbOrderCheck.setChecked(true);
                            ((HeaderHolder) holder).tvOrderTimeOrOk.setText("已确认");
                        }
                    }else {
                        ((HeaderHolder) holder).cbOrderCheck.setChecked(!((HeaderHolder) holder).cbOrderCheck.isChecked());
                        if (activity instanceof OrderReadyShowActivity){
                            ((OrderReadyShowActivity) activity).toast("不是对应楼层管理员操作！");
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
                        ARouter.getInstance().build("/app_bookmanagement/BookActivity").withInt("bookId",book.getBookId()).navigation();
                    }
                });
            }else {
                final Book book = bodyers.get(position);
                if (book!=null){
                    ((BodyHolder) holder).tvBookName.setText(book.getBookName());
                    ((BodyHolder) holder).tvBookAuthor.setText(book.getBookAuthor());
                    Glide.with(v).load(book.getSrc()).into(((BodyHolder) holder).ivBookSrc);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ARouter.getInstance().build("/app_bookmanagement/BookActivity").withInt("bookId",book.getBookId()).navigation();
                        }
                    });
                }
            }
        }else if (holder instanceof VHNULL){
            ((VHNULL) holder).textView.setText("没有待处理订单");
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
        TextView tvOrderTimeOrOk;
        CheckBox cbOrderCheck;
        public HeaderHolder(View itemView) {
            super (itemView);
            itemLayout = itemView.findViewById (R.id.book_order_header);
            tvOrder = itemView.findViewById (R.id.book_order_header_orderName);
            tvOrderTimeOrOk=itemView.findViewById(R.id.book_order_header_timeOrOk);
            cbOrderCheck=itemView.findViewById(R.id.book_order_header_orderCheck);
        }
    }
    static class BodyHolder extends RecyclerView.ViewHolder {
        LinearLayout itemLayout;
        ImageView ivBookSrc;
        TextView tvBookName;
        TextView tvBookAuthor;
        TextView tvBookId;
        public BodyHolder(View itemView) {
            super (itemView);
            itemLayout = itemView.findViewById (R.id.book_order_body);
            ivBookSrc=itemView.findViewById(R.id.book_order_body_image);
            tvBookName = itemView.findViewById (R.id.book_order_body_info_bookName);
            tvBookAuthor=itemView.findViewById(R.id.book_order_body_bookAuthor);
            tvBookId=itemView.findViewById(R.id.book_order_body_bookId);
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
