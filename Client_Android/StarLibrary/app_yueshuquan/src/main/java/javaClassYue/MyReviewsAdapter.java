package javaClassYue;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.e.app_yueshuquan.MyReviewActivity;
import com.e.app_yueshuquan.R;
import com.e.app_yueshuquan.ShuPingClickActivity;
import com.e.mylibrary.Book;
import com.e.mylibrary.JsonPageReview.JsonMsgReview;
import com.e.mylibrary.JsonPageReview.JsonReview;
import com.google.gson.Gson;

public class MyReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    JsonMsgReview mReviews;
    MyReviewActivity activity;
    View v;
    public MyReviewsAdapter(JsonMsgReview mReviews,MyReviewActivity activity) {
        this.mReviews = mReviews;
        this.activity=activity;
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
    //② 创建ViewHolder
    public static class VN extends RecyclerView.ViewHolder{
        public VN(View v) {
            super(v);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mReviews.getData().getList().size()!=0&&mReviews.getData().getList()!=null){
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.shuping_yue, parent, false);
            return new MyReviewsAdapter.VP(v);
        }else {
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.shuping_null, parent, false);
            return new MyReviewsAdapter.VN(v);
        }

    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyReviewsAdapter.VP){
            ((VP) holder).bookIntroductionYue_bookName.setText(mReviews.getData().getList().get(position).getBook().get(0).getBookName());
            ((VP) holder).bookIntroductionYue_context.setText(mReviews.getData().getList().get(position).getContent());
            ((VP) holder).bookIntroductionYue_intro.setText(mReviews.getData().getList().get(position).getBook().get(0).getBookIntroduction());
            Glide.with(v).load(mReviews.getData().getList().get(position).getBook().get(0).getSrc()).into(((VP) holder).bookIntroductionYue_src);
            ((VP) holder).bookIntroductionYue_score.setText("评分："+mReviews.getData().getList().get(position).getScore());
            ((VP) holder).dianzhanNums.setText(mReviews.getData().getList().get(position).getHitNum()+"");
            if(mReviews.getData().getList().get(position).getHit()==0){
                Glide.with(v).load(R.drawable.dianzhan).into(((VP) holder).dianzhan);
            }else if (mReviews.getData().getList().get(position).getHit()==1){
                Glide.with(v).load(R.drawable.dianzhan_have).into(((VP) holder).dianzhan);
            }
            ((VP) holder).dianzhan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.hit(mReviews.getData().getList().get(position).getHit(),mReviews.getData().getList().get(position).getAdId());
                    if(mReviews.getData().getList().get(position).getHit()==0){
                        mReviews.getData().getList().get(position).setHit(1);
                        mReviews.getData().getList().get(position).setHitNum(mReviews.getData().getList().get(position).getHitNum()+1);
                        Glide.with(v).load(R.drawable.dianzhan_have).into(((VP) holder).dianzhan);
                        ((MyReviewsAdapter.VP) holder).dianzhanNums.setText(mReviews.getData().getList().get(position-1).getHitNum()+"");
                    }else if (mReviews.getData().getList().get(position).getHit()==1){
                        mReviews.getData().getList().get(position).setHit(0);
                        mReviews.getData().getList().get(position).setHitNum(mReviews.getData().getList().get(position).getHitNum()-1);
                        Glide.with(v).load(R.drawable.dianzhan).into(((VP) holder).dianzhan);
                        ((MyReviewsAdapter.VP) holder).dianzhanNums.setText(mReviews.getData().getList().get(position-1).getHitNum()+"");
                    }
                }
            });
            ((VP) holder).bookIntroductionYue_context.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(activity, ShuPingClickActivity.class);
                    intent.putExtra("json",new Gson().toJson(mReviews.getData().getList().get(position)));
                    activity.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        Log.d("TAG1", "getItemCount: "+mReviews.getData().getList().size());
        return Math.max(mReviews.getData().getList().size(), 1);

    }
}
