package javaClassYue;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.e.app_yueshuquan.BookAndCommentsActivity;
import com.e.app_yueshuquan.R;
import com.e.mylibrary.Book;
import com.e.mylibrary.JsonPageReview.JsonPaginationReview;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class CommentsBookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_NULL = 0;
    private static final int ITEM_TOOLS = 1;
    private static final int ITEM_REVIEWS = 2;
    View v;
    BookAndCommentsActivity activity;
    JsonPaginationReview review;
    public CommentsBookAdapter(JsonPaginationReview review, BookAndCommentsActivity activity) {
        this.review = review;
        this.activity=activity;
    }
    //② 创建ViewHolder
    public static class VT extends RecyclerView.ViewHolder{

        public final TextView tool_comments_book_numbs;
        public VT(View v) {
            super(v);
            tool_comments_book_numbs = v.findViewById(R.id.tool_comments_book_numbs);
        }
    }
    //② 创建ViewHolder
    public static class VP extends RecyclerView.ViewHolder{
        public final RoundedImageView user_pic_c_reviews;
        public final TextView nickName_reviews;
        public final ImageView dianzhan_reviews;
        public final TextView comments_reviews;
        public final TextView scores_reviews;
        public final TextView date_reviews;
        public final TextView dianzhanNums_reviews;
        public VP(View v) {
            super(v);
            user_pic_c_reviews=v.findViewById(R.id.user_pic_c_reviews);
            dianzhan_reviews=v.findViewById(R.id.dianzhan_reviews);
            comments_reviews=v.findViewById(R.id.comments_reviews);
            scores_reviews=v.findViewById(R.id.scores_reviews);
            date_reviews=v.findViewById(R.id.date_reviews);
            dianzhanNums_reviews=v.findViewById(R.id.dianzhanNums_reviews);
            nickName_reviews=v.findViewById(R.id.nickName_reviews);
        }
    }
    //② 创建ViewHolder
    public static class VN extends RecyclerView.ViewHolder{
        public VN(View v) {
            super(v);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_TOOLS){
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.tool_comments_book, parent, false);
            return new CommentsBookAdapter.VT(v);
        } else if (viewType==ITEM_NULL){
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.shuping_null, parent, false);
            return new CommentsBookAdapter.VN(v);
        }else if(viewType==ITEM_REVIEWS) {
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews_comments_book, parent, false);
            return new CommentsBookAdapter.VP(v);
        }else {
            v=null;
            return new CommentsBookAdapter.VP(v);
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof CommentsBookAdapter.VP){
            ((VP) holder).date_reviews.setText(review.getList().get(position-1).getAddTime());
            ((VP) holder).nickName_reviews.setText(review.getList().get(position-1).getNickName());
            ((VP) holder).comments_reviews.setText(review.getList().get(position-1).getContent());
            Glide.with(v).load(review.getList().get(position-1).getUsrc().trim()).into(((VP) holder).user_pic_c_reviews);
            ((VP) holder).scores_reviews.setText("评分："+review.getList().get(position-1).getScore());
            ((VP) holder).dianzhanNums_reviews.setText(review.getList().get(position-1).getHitNum()+"");
            if(review.getList().get(position-1).getHit()==0){
                Glide.with(v).load(R.drawable.dianzhan).into(((VP) holder).dianzhan_reviews);
            }else if (review.getList().get(position-1).getHit()==1){
                Glide.with(v).load(R.drawable.dianzhan_have).into(((VP) holder).dianzhan_reviews);
            }
            ((VP) holder).dianzhan_reviews.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View view) {
                    activity.hit(review.getList().get(position-1).getHit(),review.getList().get(position-1).getAdId());
                    if(review.getList().get(position-1).getHit()==0){
                        review.getList().get(position-1).setHit(1);
                        review.getList().get(position-1).setHitNum(review.getList().get(position-1).getHitNum()+1);
                        Glide.with(v).load(R.drawable.dianzhan_have).into(((VP) holder).dianzhan_reviews);
                        ((VP) holder).dianzhanNums_reviews.setText(review.getList().get(position-1).getHitNum()+"");
                    }else if (review.getList().get(position-1).getHit()==1){
                        review.getList().get(position-1).setHit(0);
                        review.getList().get(position-1).setHitNum(review.getList().get(position-1).getHitNum()-1);
                        Glide.with(v).load(R.drawable.dianzhan).into(((VP) holder).dianzhan_reviews);
                        ((VP) holder).dianzhanNums_reviews.setText(review.getList().get(position-1).getHitNum()+"");
                    }
                }
            });
        }else if (holder instanceof VT){
            ((VT) holder).tool_comments_book_numbs.setText("全部  "+review.getList().size());
        }
    }

    @Override
    public int getItemCount() {
        if (review.getList().size()==0&&review.getList()==null){
            return 1;
        }else {
            return review.getList().size()+1;
        }
    }
    public int getItemViewType(int position){
        if (review.getList().size()==0&&review.getList()==null){
            return ITEM_NULL;
        }else {
            if (position==0){
                return ITEM_TOOLS;
            }else {
                return ITEM_REVIEWS;
            }
        }

    }
}
