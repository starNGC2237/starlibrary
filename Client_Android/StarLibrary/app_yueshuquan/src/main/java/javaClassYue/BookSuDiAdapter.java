package javaClassYue;

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
import com.e.app_yueshuquan.R;
import com.e.mylibrary.Book;
import com.e.mylibrary.JsonOrder.JsonOrder;

import java.util.List;

public class BookSuDiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    View v;
    private List<Book> mDatas;
    public BookSuDiAdapter(List<Book> mDatas) {
        this.mDatas = mDatas;
    }
    //② 创建ViewHolder
    public static class MyHolder extends RecyclerView.ViewHolder{
        public final LinearLayout book_sudi_item;
        public final ImageView bookImage_sudi;
        public final TextView bookName_sudi;
        public final TextView bookAuthor_sudi;
        public final TextView reviewNumber_sudi;
        public MyHolder(View v) {
            super(v);
            book_sudi_item=v.findViewById(R.id.book_sudi_item);
            bookImage_sudi = v.findViewById(R.id.bookImage_sudi);
            bookName_sudi = v.findViewById(R.id.bookName_sudi);
            bookAuthor_sudi = v.findViewById(R.id.bookAuthor_sudi);
            reviewNumber_sudi = v.findViewById(R.id.reviewNumber_sudi);
        }
    }
    //② 创建ViewHolder
    public static class VHNULL extends RecyclerView.ViewHolder{
        public VHNULL(View v) {
            super(v);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mDatas.size()>0){
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.book_sudi_item_book, parent, false);
            return new MyHolder(v);
        }else {
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.book_sbs_null_item, parent, false);
            return new VHNULL(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder){
            final Book book = mDatas.get(position);
            Glide.with(v).load(book.getSrc()).into(((MyHolder) holder).bookImage_sudi);
            String stringBookNumber=book.getBorrowNumber()+"人已借阅";
            ((MyHolder) holder).reviewNumber_sudi.setText(stringBookNumber);
            ((MyHolder) holder).bookName_sudi.setText(book.getBookName());
            ((MyHolder) holder).bookAuthor_sudi.setText(book.getBookAuthor());
            ((MyHolder) holder).book_sudi_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ARouter.getInstance().build("/app_book/BookActivity").withInt("bookId",book.getBookId()).withString("floor",book.getBookPlace()).navigation();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
}
