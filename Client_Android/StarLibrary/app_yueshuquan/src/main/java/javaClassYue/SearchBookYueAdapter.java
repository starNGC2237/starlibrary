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

import java.util.List;

    public class SearchBookYueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        View v;
        private List<Book> mDatas;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            if(mDatas.size()>0){
                v= LayoutInflater.from(parent.getContext()).inflate(R.layout.book_search_item_book, parent, false);
                return new MyHolder(v);
            }else {
                v= LayoutInflater.from(parent.getContext()).inflate(R.layout.book_search_null_item, parent, false);
                return new VHNULL(v);
            }

        }
        public SearchBookYueAdapter(List<Book> data) {
            this.mDatas = data;
        }
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MyHolder){
                final Book book = mDatas.get(position);
                Glide.with(v).load(book.getSrc()).into(((MyHolder) holder).ivImage);
                String stringBookNumber=book.getBorrowNumber()+"人已借阅";
                ((MyHolder) holder).tvBorrowNumber.setText(stringBookNumber);
                ((MyHolder) holder).tvBookName.setText(book.getBookName());
                ((MyHolder) holder).tvBookAuthor.setText(book.getBookAuthor());
                ((MyHolder) holder).itemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ARouter.getInstance().build("/app_yueshuquan/BookAndCommentsActivity").withInt("bookId",book.getBookId()).navigation();
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
            TextView tvBookName;
            TextView tvBookAuthor;
            TextView tvBorrowNumber;
            public MyHolder(View itemView) {
                super (itemView);
                itemLayout = itemView.findViewById (R.id.book_search_item);
                ivImage = itemView.findViewById (R.id.bookImage_search);
                tvBookName = itemView.findViewById (R.id.bookName_search);
                tvBookAuthor=itemView.findViewById(R.id.bookAuthor_search);
                tvBorrowNumber=itemView.findViewById(R.id.borrowNumber_search);
            }
        }
        static class VHNULL extends RecyclerView.ViewHolder{
            public VHNULL(View v) {
                super(v);
            }
        }
    }
