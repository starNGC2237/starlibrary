package javaClassYue;

import android.annotation.SuppressLint;
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
import com.e.app_yueshuquan.R;
import com.e.app_yueshuquan.YHomeActivity;
import com.e.mylibrary.Book;
import com.e.mylibrary.JsonPageReview.JsonPaginationReview;
import com.e.mylibrary.JsonPageReview.JsonReview;
import com.e.util.HttpUtil;

import java.util.List;

public class YueHomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_TOOLS = 1;
    private static final int ITEM_SUDI = 2;
    private static final int ITEM_REVIEWS = 3;
    View v;
    private Book mLoadBook;
    JsonPaginationReview review;
    YHomeActivity activity;
    public YueHomeAdapter(Book mLoadBook, JsonPaginationReview review,YHomeActivity activity) {
        this.mLoadBook = mLoadBook;
        this.activity=activity;
        this.review=review;
    }
    //② 创建ViewHolder
    public static class VT extends RecyclerView.ViewHolder{

        public final CardView my_review_Yue;
        public final CardView my_borrowedBook_Yue;
        public final CardView my_favoritesBook_Yue;
        public VT(View v) {
            super(v);
            my_review_Yue = v.findViewById(R.id.my_review_Yue);
            my_borrowedBook_Yue = v.findViewById(R.id.my_borrowedBook_Yue);
            my_favoritesBook_Yue = v.findViewById(R.id.my_favoritesBook_Yue);
        }
    }
    //② 创建ViewHolder
    public static class VS extends RecyclerView.ViewHolder{

        public final TextView allBook_sudi;
        public final CardView bookSuDi1_sudi;
        public final TextView bookSuDi1_bookIntroduction_sudi;
        public final TextView bookSuDi1_bookName_sudi;
        public final TextView bookSuDi1_bookAuthor_sudi;
        public final ImageView bookSuDi1_bookSrc_sudi;
        public VS(View v) {
            super(v);
            allBook_sudi = v.findViewById(R.id.allBook_sudi);
            bookSuDi1_sudi=v.findViewById(R.id.bookSuDi1_sudi);
            bookSuDi1_bookIntroduction_sudi=v.findViewById(R.id.bookSuDi1_bookIntro_sudi);
            bookSuDi1_bookSrc_sudi=v.findViewById(R.id.bookSuDi1_bookSrc_sudi);
            bookSuDi1_bookAuthor_sudi=v.findViewById(R.id.bookSuDi1_bookAuthor_sudi);
            bookSuDi1_bookName_sudi=v.findViewById(R.id.bookSuDi1_bookName_sudi);
        }
    }
    //② 创建ViewHolder
    public static class VP extends RecyclerView.ViewHolder{
        public final ImageView bookIntroductionYue_src;
        public final TextView bookIntroductionYue_intro;
        public final ImageView dianzhan;
        public final TextView bookIntroductionYue_bookName;
        public final TextView bookIntroductionYue_score;
        public final TextView bookIntroductionYue_context;
        public final TextView dianzhanNums;
        public VP(View v) {
            super(v);
            bookIntroductionYue_src=v.findViewById(R.id.bookIntroductionYue_src);
            dianzhan=v.findViewById(R.id.dianzhan);
            bookIntroductionYue_bookName=v.findViewById(R.id.bookIntroductionYue_bookName);
            bookIntroductionYue_score=v.findViewById(R.id.bookIntroductionYue_score);
            bookIntroductionYue_context=v.findViewById(R.id.bookIntroductionYue_context);
            dianzhanNums=v.findViewById(R.id.dianzhanNums);
            bookIntroductionYue_intro=v.findViewById(R.id.bookIntroductionYue_intro);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TOOLS){
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.tools_yue, parent, false);
            return new VT(v);
        } else if (viewType==ITEM_SUDI){
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.sudi_yue, parent, false);
            return new VS(v);
        }else if(viewType==ITEM_REVIEWS) {
            v=LayoutInflater.from(parent.getContext()).inflate(R.layout.shuping_yue, parent, false);
            return new VP(v);
        }else {
            v=null;
            return new VP(v);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof VT){
            ((VT) holder).my_review_Yue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ARouter.getInstance().build("/app_yueshuquan/MyReviewActivity").navigation();
                }
            });
            ((VT) holder).my_favoritesBook_Yue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ARouter.getInstance().build("/app_borrow/FavoritesActivity").navigation();
                }
            });
            ((VT) holder).my_borrowedBook_Yue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ARouter.getInstance().build("/app_borrow/BorrowedBorrowActivity").navigation();
                }
            });
        }else if (holder instanceof VS){
            ((VS) holder).allBook_sudi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ARouter.getInstance().build("/app_yueshuquan/ShowSuDiBooksActivity").navigation();
                }
            });
            ((VS) holder).bookSuDi1_bookName_sudi.setText(mLoadBook.getBookName());
            ((VS) holder).bookSuDi1_bookIntroduction_sudi.setText(mLoadBook.getBookIntroduction());
            ((VS) holder).bookSuDi1_bookAuthor_sudi.setText(mLoadBook.getBookAuthor());
            Glide.with(v).load(mLoadBook.getSrc()).into((((VS) holder).bookSuDi1_bookSrc_sudi));
            ((VS) holder).bookSuDi1_sudi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     ARouter.getInstance().build("/app_book/BookActivity").withInt("bookId",mLoadBook.getBookId()).withString("floor",mLoadBook.getBookPlace()).navigation();
                }
            });
        }else if (holder instanceof VP){
            ((YueHomeAdapter.VP) holder).bookIntroductionYue_bookName.setText(review.getList().get(position-2).getBook().get(0).getBookName());
            ((YueHomeAdapter.VP) holder).bookIntroductionYue_context.setText(review.getList().get(position-2).getContent());
            ((YueHomeAdapter.VP) holder).bookIntroductionYue_intro.setText(review.getList().get(position-2).getBook().get(0).getBookIntroduction());
            Glide.with(v).load(review.getList().get(position-2).getBook().get(0).getSrc()).into(((YueHomeAdapter.VP) holder).bookIntroductionYue_src);
            ((YueHomeAdapter.VP) holder).bookIntroductionYue_score.setText("评分："+review.getList().get(position-2).getScore());
            ((YueHomeAdapter.VP) holder).dianzhanNums.setText(review.getList().get(position-2).getHitNum()+"");
            if(review.getList().get(position-2).getHit()==0){
                Glide.with(v).load(R.drawable.dianzhan).into(((YueHomeAdapter.VP) holder).dianzhan);
            }else if (review.getList().get(position-2).getHit()==1){
                Glide.with(v).load(R.drawable.dianzhan_have).into(((YueHomeAdapter.VP) holder).dianzhan);
            }
            ((YueHomeAdapter.VP) holder).dianzhan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.hit(review.getList().get(position-2).getHit(),review.getList().get(position-2).getAdId());
                    if(review.getList().get(position-2).getHit()==0){
                        review.getList().get(position-2).setHit(1);
                        review.getList().get(position-2).setHitNum(review.getList().get(position-2).getHitNum()+1);
                        Glide.with(v).load(R.drawable.dianzhan_have).into(((YueHomeAdapter.VP) holder).dianzhan);
                        ((YueHomeAdapter.VP) holder).dianzhanNums.setText(review.getList().get(position-2).getHitNum()+"");
                    }else if (review.getList().get(position-2).getHit()==1){
                        review.getList().get(position-2).setHit(0);
                        review.getList().get(position-2).setHitNum(review.getList().get(position-2).getHitNum()-1);
                        Glide.with(v).load(R.drawable.dianzhan).into(((YueHomeAdapter.VP) holder).dianzhan);
                        ((YueHomeAdapter.VP) holder).dianzhanNums.setText(review.getList().get(position-2).getHitNum()+"");
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 2+review.getList().size();
    }
    public int getItemViewType(int position){
        if (position==0){
            return ITEM_TOOLS;
        }else if(position==1) {
            return ITEM_SUDI;
        }else {
            return ITEM_REVIEWS;
        }
    }
}
