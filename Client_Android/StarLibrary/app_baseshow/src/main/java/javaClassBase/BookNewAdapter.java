package javaClassBase;

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
import com.e.app_baseshow.R;
import com.e.mylibrary.Book;

import java.util.List;

public class BookNewAdapter extends RecyclerView.Adapter<BookNewAdapter.ViewHolder>{


    private Context mContext;

    private List<Book> mBookNewList;

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

    public BookNewAdapter(List<Book> BookNewList) {
        mBookNewList = BookNewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.book_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Book book = mBookNewList.get(position);
        holder.BookNewName.setText(book.getBookName());
        Glide.with(mContext).load(book.getSrc()).into(holder.BookNewImage);
        holder.itemView.setOnClickListener(v -> ARouter.getInstance().build("/app_book/BookActivity").withInt("bookId",book.getBookId()).withString("floor",book.getBookPlace()).navigation());
    }

    @Override
    public int getItemCount() {
        return mBookNewList.size();
    }

}
