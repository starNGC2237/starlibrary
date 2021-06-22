package javaclass;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.e.JavaClass.Block;
import com.e.JavaClass.UserMangers;
import com.e.app_base.BaseActivity;
import com.e.app_base.CustomCaptureActivity;
import com.e.app_base.R;
import com.google.zxing.integration.android.IntentIntegrator;

import org.litepal.LitePal;

import java.util.List;

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.ViewHolder>{

    String floor;
    private Context mContext;
    private List<Block> mBlockList;
    private Activity activity;
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

    public BlockAdapter(List<Block> BookNewList,Activity activity) {
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
        Block block = mBlockList.get(position);
        holder.BookNewName.setText(block.getName());
        Glide.with(mContext).load(block.getImageId()).into(holder.BookNewImage);
        holder.itemView.setOnClickListener(v -> {
            List<UserMangers> newsList = LitePal.where("i =?", "1").find(UserMangers.class);
            if(newsList != null && newsList.size()==1){
                for (int i=0;i<newsList.size();i++){
                    floor = newsList.get(i).getFloor();
                }
            }
            switch (block.getName()) {
                case "关于我们":
                    ARouter.getInstance().build("/app_base/AboutUsActivity").navigation();
                    break;
                case "总管理员管理":
                    if (floor.equals("ALL")){
                        ARouter.getInstance().build("/app_managers/ShowBlockManagerActivity").navigation();
                    }else {
                        if (activity instanceof BaseActivity){
                            ((BaseActivity) activity). toast("权限不足");
                        }
                    }
                    break;
                case "取书完成确认借阅":
                case "还书":
                case "直接借阅":
                    if (floor.equals("ALL")){
                        if (activity instanceof BaseActivity){
                            new IntentIntegrator(activity)
                                    .setCaptureActivity(CustomCaptureActivity.class)
                                    .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)// 扫码的类型,可选：一维码，二维码，一/二维码
                                    .setPrompt("请对准二维码/条码")// 设置提示语
                                    .setCameraId(0)// 选择摄像头,可使用前置或者后置
                                    .setTorchEnabled(true)//是否照明
                                    .setBeepEnabled(true)// 是否开启声音,扫完码之后会"哔"的一声
                                    .setBarcodeImageEnabled(true)// 扫完码之后生成二维码的图片
                                    .initiateScan();// 初始化扫码
                        }
                    }else {
                        if (activity instanceof BaseActivity){
                            ((BaseActivity) activity). toast("权限不足");
                        }
                    }
                    break;
                case "添加书籍":
                    ARouter.getInstance().build("/app_bookmanagement/AddBookActivity").navigation();
                    break;
                case "修改书籍":
                    ARouter.getInstance().build("/app_bookmanagement/ShowBookActivity").navigation();
                    break;
                case "删除书籍":
                    ARouter.getInstance().build("/app_bookmanagement/ShowBookActivity").navigation();
                    break;
                case "书籍管理":
                    ARouter.getInstance().build("/app_bookmanagement/ShowBookActivity").navigation();
                    break;
                case "预约中订单准备书籍管理":
                    ARouter.getInstance().build("/app_orders/OrderReadyShowActivity").navigation();
                    break;
                case "预约中订单取书籍管理":
                    if (activity instanceof BaseActivity){
                        new IntentIntegrator(activity)
                                .setCaptureActivity(CustomCaptureActivity.class)
                                .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)// 扫码的类型,可选：一维码，二维码，一/二维码
                                .setPrompt("请对准二维码/条码")// 设置提示语
                                .setCameraId(0)// 选择摄像头,可使用前置或者后置
                                .setTorchEnabled(true)//是否照明
                                .setBeepEnabled(true)// 是否开启声音,扫完码之后会"哔"的一声
                                .setBarcodeImageEnabled(true)// 扫完码之后生成二维码的图片
                                .initiateScan();// 初始化扫码
                    }
                    break;
                case "订单管理":
                    ARouter.getInstance().build("/app_orders/OrderBlocksShowActivity").navigation();
                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBlockList.size();
    }

}
