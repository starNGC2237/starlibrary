package javaclassOrders;

import android.app.Activity;
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
import com.e.JavaClass.Block;
import com.e.JavaClass.UserMangers;
import com.e.app_orders.CustomCaptureAActivity;
import com.e.app_orders.OrderBlocksShowActivity;
import com.e.app_orders.R;
import com.google.zxing.integration.android.IntentIntegrator;

import org.litepal.LitePal;

import java.util.List;

public class BlockOrderShowBlockAdapter extends RecyclerView.Adapter<BlockOrderShowBlockAdapter.ViewHolder>{

    String floor;
    private Context mContext;
    Activity activity;
    private List<Block> mBlockList;

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

    public BlockOrderShowBlockAdapter(List<Block> BookNewList, Activity activity) {
        mBlockList = BookNewList;
        this.activity=activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.block_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Block block = mBlockList.get(position);
        holder.BookNewName.setText(block.getName());
        Glide.with(mContext).load(block.getImageId()).into(holder.BookNewImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<UserMangers> newsList = LitePal.where("i =?", "1").find(UserMangers.class);
                if(newsList != null && newsList.size()==1){
                    for (int i=0;i<newsList.size();i++){
                        floor = newsList.get(i).getFloor();
                    }
                }
                switch (block.getName()) {
                    case "预约中订单准备书籍管理":
                        ARouter.getInstance().build("/app_orders/OrderReadyShowActivity").navigation();
                        break;
                    case "预约中订单取书籍管理":
                        if (activity instanceof OrderBlocksShowActivity){
                            new IntentIntegrator(activity)
                                    .setCaptureActivity(CustomCaptureAActivity.class)
                                    .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)// 扫码的类型,可选：一维码，二维码，一/二维码
                                    .setPrompt("请对准二维码/条码")// 设置提示语
                                    .setCameraId(0)// 选择摄像头,可使用前置或者后置
                                    .setTorchEnabled(true)//是否照明
                                    .setBeepEnabled(true)// 是否开启声音,扫完码之后会"哔"的一声
                                    .setBarcodeImageEnabled(true)// 扫完码之后生成二维码的图片
                                    .initiateScan();// 初始化扫码
                        }
                        break;
                    case "取书完成确认借阅":
                    case "还书":
                    case "直接借阅":
                        if (floor.equals("ALL")){
                            if (activity instanceof OrderBlocksShowActivity){
                                new IntentIntegrator(activity)
                                        .setCaptureActivity(CustomCaptureAActivity.class)
                                        .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)// 扫码的类型,可选：一维码，二维码，一/二维码
                                        .setPrompt("请对准二维码/条码")// 设置提示语
                                        .setCameraId(0)// 选择摄像头,可使用前置或者后置
                                        .setTorchEnabled(true)//是否照明
                                        .setBeepEnabled(true)// 是否开启声音,扫完码之后会"哔"的一声
                                        .setBarcodeImageEnabled(true)// 扫完码之后生成二维码的图片
                                        .initiateScan();// 初始化扫码
                            }
                        }else {
                            if (activity instanceof OrderBlocksShowActivity){
                                ((OrderBlocksShowActivity) activity). toast("权限不足");
                            }
                        }
                        break;
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return mBlockList.size();
    }

}
