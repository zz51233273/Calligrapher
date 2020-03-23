package com.example.hasee.bluecalligrapher.write_field_character;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.utils.ImageUtil;
import com.example.hasee.bluecalligrapher.write_draw_pen.IPenConfig;
import com.example.hasee.bluecalligrapher.write_draw_pen.NewDrawPenView;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;
import com.jrummyapps.android.colorpicker.ColorPickerView;

import static com.example.hasee.bluecalligrapher.write_draw_pen.IPenConfig.STROKE_TYPE_ERASER;


/**
 * @author shiming
 * @version v1.0 create at 2017/8/24
 * @des DrawViewLayout的一些封装  后续优化的点是：页面不初始化，尽量等着用户来选择
 */
public class DrawViewLayout extends FrameLayout implements View.OnClickListener, View.OnLongClickListener{

    private RelativeLayout mShowKeyboard;
    private RelativeLayout mGotoPreviousStep;
    private RelativeLayout mClearCanvas;
    private RelativeLayout mCompare;
    private NewDrawPenView mDrawView;
    private RelativeLayout mSaveBitmap;
    private ViewStub mViewStub;
    private View mChild;
    private Context mContext;
    private ImageView mBox;
    private ImageView mTracingImg;
    private Bitmap mTracingBitmap;
    private LayoutInflater mInflater;
    private int mPenConfig;
    private boolean mIsShowKeyB;
    private ColorPickerView colorPickerViewModel;
    public static final int DIALGE_ID = 0;
    int counts=1;
    public DrawViewLayout(@NonNull Context context) {
        this(context, null);
    }


    public DrawViewLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();

    }
    private void initView() {
        mInflater = LayoutInflater.from(getContext());
        mChild = mInflater.inflate(R.layout.write_brush_weight_layout, this, false);
        addView(mChild);
        colorPickerViewModel=new ColorPickerView(getContext());
        mShowKeyboard = (RelativeLayout) findViewById(R.id.rll_show_color_container);
        mGotoPreviousStep = (RelativeLayout) findViewById(R.id.rll_show_box_container);//空格
        mClearCanvas = (RelativeLayout) findViewById(R.id.rll_show_submit_container);
        mSaveBitmap = (RelativeLayout) findViewById(R.id.rll_show_delete_container);
        mCompare=(RelativeLayout)findViewById(R.id.rll_show_compare_container);
        mViewStub = (ViewStub) findViewById(R.id.draw_view);
        mBox=(ImageView)findViewById(R.id.box);
        mTracingImg=(ImageView)findViewById(R.id.tracing_img);
        if(null!=((Activity)mContext).getIntent().getByteArrayExtra("img_bytes")){
            mTracingBitmap= ImageUtil.Bytes2Bimap(((Activity)mContext).getIntent().getByteArrayExtra("img_bytes"));
            mTracingImg.setImageBitmap(mTracingBitmap);
        }
        setOnClickListenerT();
    }


    private void setOnClickListenerT() {
        mShowKeyboard.setOnClickListener(this);
        mGotoPreviousStep.setOnClickListener(this);
        mClearCanvas.setOnClickListener(this);
        mSaveBitmap.setOnClickListener(this);
        mSaveBitmap.setOnLongClickListener(this);
        mCompare.setOnClickListener(this);
        mSaveBitmap.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_UP){
                    Executor.INSTANCE.stop();
                }
                return false;
            }
        });
    }

    private void setDrawViewConfig() {
        mDrawView = (NewDrawPenView) findViewById(R.id.myglsurface_view);
        mDrawView.setCanvasCode(IPenConfig.STROKE_TYPE_PEN);
        mPenConfig=IPenConfig.STROKE_TYPE_PEN;
        mDrawView.setPenconfig(mPenConfig);
        mDrawView.setGetTimeListener(new NewDrawPenView.TimeListener() {
            @Override
            public void getTime(long l) {
                mIActionCallback.getUptime(l);
            }

            @Override
            public void stopTime() {
                mIActionCallback.stopTime();
            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.rll_show_color_container:
                setCharColor();
                break;
            case R.id.rll_show_box_container:
                setBox();
                break;
            case R.id.rll_show_submit_container:
                mIActionCallback.submit();
                clearScreen();
                break;
            case R.id.rll_show_delete_container:
                clearScreen();
                break;
            case R.id.rll_show_compare_container:
                if(null!=mTracingBitmap){
                    mIActionCallback.compareChar(mTracingBitmap,mDrawView.getPenColor());
                }else{
                    Toast.makeText(getContext(), "没有可比对的文字", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    /**
     * 使用Viewstub的在不需要弹出键盘的时候，渲染不占内存不
     */

    private void opeAdvancenDialog() {
        int color = colorPickerViewModel.getColor();
        //传入的默认color
        ColorPickerDialog colorPickerDialog = ColorPickerDialog.newBuilder().setColor(color)
                .setDialogTitle(R.string.color_picker)
                //设置dialog标题
                .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                //设置为自定义模式
                .setShowAlphaSlider(true)
                //设置有透明度模式，默认没有透明度
                .setDialogId(DIALGE_ID)
                //设置Id,回调时传回用于判断
                .setAllowPresets(false)
                //不显示预知模式
                .create();
        //Buider创建
        colorPickerDialog.setColorPickerDialogListener(pickerDialogListener);
        //设置回调，用于获取选择的颜色
        colorPickerDialog.show(((Activity)getContext()).getFragmentManager(), "color-picker-dialog");
    }

    private ColorPickerDialogListener pickerDialogListener = new ColorPickerDialogListener() {
        @Override
        public void onColorSelected(int dialogId, @ColorInt int color) {
            if (dialogId == DIALGE_ID) {
                mDrawView.setPenColor(color);
                colorPickerViewModel.setColor(color);
            }
        }

        @Override
        public void onDialogDismissed(int dialogId) {

        }
    };

    private void setCharColor() {
        opeAdvancenDialog();

    }


    public void clearScreen() {
        if (mDrawView==null)return;
        mDrawView.setCanvasCode(STROKE_TYPE_ERASER);//z注意变量的来源
    }

    public void showBk() {
        if (!getIsShowKeyB()){
            if (mViewStub.getParent() != null) {
                mViewStub.inflate();
            }
            if (mDrawView == null) {
                setDrawViewConfig();
            }
            mIsShowKeyB=true;
            mViewStub.setVisibility(VISIBLE);
            mIActionCallback.showkeyB(true);
            mDrawView.setVisibility(VISIBLE);
        }
    }


    public IActionCallback mIActionCallback;

    public void setActionCallback(IActionCallback a) {
        mIActionCallback = a;
    }

    public boolean getIsShowKeyB() {
        return mIsShowKeyB;
    }


    /**
     *  长按事件的启动定时器
     * @param v
     * @return
     */
    @Override
    public boolean onLongClick(View v) {
        Executor.INSTANCE.setCallback(mIActionCallback);
        Executor.INSTANCE.upData(v.getId());
        return true;
    }

    public NewDrawPenView getSaveBitmap() {
        return mDrawView;
    }

    public int getPenConfig() {
        return mPenConfig;
    }

    public void setBox(){   //切换辅助线风格
        if(counts==0){
            mDrawView.setBackgroundResource(R.drawable.wu_box);
            mBox.setImageResource(R.drawable.wu_box_icon);
            counts++;
        }else if(counts==1){
            mDrawView.setBackgroundResource(R.drawable.tian_box);
            mBox.setImageResource(R.drawable.tian_box_icon);
            counts++;
        }else{
            mDrawView.setBackgroundResource(R.drawable.mi_box);
            mBox.setImageResource(R.drawable.mi_box_icon);
            counts=0;
        }
    }
/*    public void setPenConfig(int penConfig) {
        mDrawView.setCanvasCode(penConfig);
        mPenConfig=penConfig;
    }*/

    public interface IActionCallback {

        void submit();

        void getUptime(long l);

        void stopTime();

        void showkeyB(boolean flag);

        void compareChar(Bitmap bitmap, int color);
    }

}
