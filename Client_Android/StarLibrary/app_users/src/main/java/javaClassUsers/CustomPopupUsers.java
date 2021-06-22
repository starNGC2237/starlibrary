package javaClassUsers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.e.app_users.R;
import com.e.util.DownloadUtils;
import com.lxj.xpopup.animator.PopupAnimator;
import com.lxj.xpopup.core.CenterPopupView;

@SuppressLint("ViewConstructor")
public class CustomPopupUsers extends CenterPopupView {
    Context context;
    String title;
    String content;
    //注意：自定义弹窗本质是一个自定义View，但是只需重写一个参数的构造，其他的不要重写，所有的自定义弹窗都是这样。
    public CustomPopupUsers(@NonNull Context context, String title, String content) {
        super(context);
        this.context=context;
        this.title=title;
        this.content=content;
    }
    // 返回自定义弹窗的布局
    @Override
    protected int getImplLayoutId() {
        return R.layout.qs;
    }
    // 执行初始化操作，比如：findView，设置点击，或者任何你弹窗内的业务逻辑
    @Override
    protected void onCreate() {
        super.onCreate();
        findViewById(R.id.tv_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // 关闭弹窗
            }
        });
        findViewById(R.id.tv_confirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        TextView tv_content=findViewById(R.id.tv_content);
        tv_content.setText(content);
        TextView tv_title=findViewById(R.id.tv_title);
        tv_title.setText(title);
    }
    // 设置最大宽度，看需要而定，
    @Override
    protected int getMaxWidth() {
        return super.getMaxWidth();
    }
    // 设置最大高度，看需要而定
    @Override
    protected int getMaxHeight() {
        return super.getMaxHeight();
    }
    // 设置自定义动画器，看需要而定
    @Override
    protected PopupAnimator getPopupAnimator() {
        return super.getPopupAnimator();
    }
    /**
     * 弹窗的宽度，用来动态设定当前弹窗的宽度，受getMaxWidth()限制
     *
     * @return
     */
    protected int getPopupWidth() {
        return 0;
    }

    /**
     * 弹窗的高度，用来动态设定当前弹窗的高度，受getMaxHeight()限制
     *
     * @return
     */
    protected int getPopupHeight() {
        return 0;
    }
}