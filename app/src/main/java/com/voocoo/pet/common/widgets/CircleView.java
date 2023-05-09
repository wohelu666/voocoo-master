package com.voocoo.pet.common.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.voocoo.pet.R;
import com.voocoo.pet.common.utils.LogUtil;


public class CircleView extends View {

    /**
     * 默认波长
     */
    private static final int DEFAULT_RADIUS = 100;

    /**
     * 默认波峰和波谷的高度
     */
    private static final int DEFAULT_WAVE_HEIGHT = 5;

    /**
     * 默认的最大的进度
     */
    private static final int DEFAULT_MAX_PROGRESS = 100;

    /**
     * 默认边框宽度
     */
    private static final int DEFAULT_BORDER_WIDTH = 2;

    /**
     * 默认的进度字体大小
     */
    private static final int DEFAULT_TEXT_SIZE = 16;

    //进度
    private int mProgress;
    //半径
    private int mRadius = DEFAULT_RADIUS;
    //进度条的高度
    private int mProgressHeight;
    //文字的大小
    private int mTextSize;
    //波高
    private int mWaveHeight;
    //文字颜色
    private int mTextColor;
    //波浪的颜色
    private int mWaveColor=0xFFFDBE9B;
    //圆形边框的颜色
    private int mBorderColor=0xFFF2F3FA;
    //圆形边框的宽度
    private int borderWidth;
    //是否隐藏进度文字
    private boolean isHideProgressText = false;
    //进度条的贝塞尔曲线
    private Path mBerzierPath;
    //用于裁剪的Path
    private Path mCirclePath;
    // 画圆的画笔
    private Paint mCirclePaint;
    // 画文字的笔
    private Paint mTextPaint;
    // 画波浪的笔
    private Paint mWavePaint;
    // 文字的区域
    private Rect mTextRect;

    private int mMoveX = 0;

    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(attrs);
        initPaint();
        initPath();
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CircleView);
        mRadius = ta.getDimensionPixelSize(R.styleable.CircleView_c_radius, DEFAULT_RADIUS);
        mProgressHeight = mRadius * 2;
        mTextColor = ta.getResourceId(R.styleable.CircleView_textColor, 0);
        //mWaveColor = ta.getResourceId(R.styleable.CircleView_waveColor, 0);
        //mBorderColor = ta.getResourceId(R.styleable.CircleView_borderColor, 0);
        borderWidth = ta.getDimensionPixelOffset(R.styleable.CircleView_borderWidth, dp2px(DEFAULT_BORDER_WIDTH));
        mTextSize = ta.getDimensionPixelSize(R.styleable.CircleView_textSize, sp2px(DEFAULT_TEXT_SIZE));
        mWaveHeight = ta.getDimensionPixelSize(R.styleable.CircleView_waveHeight, dp2px(DEFAULT_WAVE_HEIGHT));
        mProgress = ta.getInteger(R.styleable.CircleView_progress, 0);
        isHideProgressText = ta.getBoolean(R.styleable.CircleView_hideText, false);
        ta.recycle();
        invalidate();
    }

    private void initPath() {
        mBerzierPath = new Path();
        mCirclePath = new Path();
        mCirclePath.addCircle(mRadius, mRadius, mRadius, Path.Direction.CCW);
    }

    private void initPaint() {
        mWavePaint = new Paint();
        mWavePaint.setColor(mWaveColor);
        mWavePaint.setStyle(Paint.Style.FILL);
        mWavePaint.setAntiAlias(true);

        mCirclePaint = new Paint();
        mCirclePaint.setStyle(Paint.Style.FILL);// 空心画笔
        mCirclePaint.setColor(mBorderColor);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStrokeWidth(borderWidth);

        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.FILL);// 空心画笔
        mTextPaint.setColor(mTextColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(10);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //圆形的进度条，正好是正方形的内切圆(这边暂时没有考虑padding的影响)
        setMeasuredDimension(mProgressHeight, mProgressHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBerzierPath.reset();
        //画曲线
        mBerzierPath.moveTo(0, getWaveY());
        mBerzierPath.lineTo(getWidth(), getWaveY());
        mBerzierPath.lineTo(getWidth(), getHeight());
        mBerzierPath.lineTo(0, getHeight());
        mBerzierPath.close();

        //画圆
        canvas.drawCircle(mRadius, mRadius, mRadius, mCirclePaint);

        //裁剪一个圆形的区域
        canvas.clipPath(mCirclePath);
        canvas.drawPath(mBerzierPath, mWavePaint);

        canvas.drawLine(getWidth() / 2 - 50, getWaveY() + 5, getWidth() / 2 + 50, getWaveY() + 5, mTextPaint);
    }

    private int getWaveY() {
        float scale = mProgress * 1f / DEFAULT_MAX_PROGRESS * 1f;
        if (scale < 0.05) {
            scale = (float) 0.05;
        }
       // LogUtil.d("scale->" + scale);
        if (scale >= 1) {
            return 0;
        } else {
            int height = (int) (scale * mProgressHeight);
            return mProgressHeight - height;
        }
    }


    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        if (progress > 1000) {
            this.mProgress = 1000;
        } else if (progress < 0) {
            this.mProgress = 0;
        } else {
            this.mProgress = progress;
        }
        postInvalidate();
    }

    /**
     * 设置字体的颜色
     *
     * @param color
     */
    public void setTextColor(int color) {
        mTextPaint.setColor(color);
    }

    /**
     * 设置波浪的颜色
     *
     * @param color
     */
    public void setWaveColor(int color) {
        mWavePaint.setColor(color);
    }

    /**
     * 设置
     *
     * @param color
     */
    public void setBorderColor(int color) {
        mCirclePaint.setColor(color);
    }

    /**
     * 设置隐藏进度文字
     *
     * @param flag
     */
    public void hideProgressText(boolean flag) {
        isHideProgressText = flag;
    }

    /**
     * 获取当前进度
     *
     * @return
     */
    public int getProgress() {
        return mProgress;
    }

    //dp to px
    protected int dp2px(int dpval) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpval, getResources().getDisplayMetrics());
    }

    //sp to px
    protected int sp2px(int dpval) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dpval, getResources().getDisplayMetrics());
    }

    private int downX = 0;
    private int downY = 0;
    private int upX = 0;
    private int upY = 0;
    private int moveX = 0;
    private int moveY = 0;

   /* @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                responseTouch(downX, downY);
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = (int) event.getX();
                moveY = (int) event.getY();
                responseTouch(moveX, moveY);
                break;
            case MotionEvent.ACTION_UP:
                upX = (int) event.getX();
                upY = (int) event.getY();
                responseTouch(upX, upY);
                if (progressChangeListener != null)
                    progressChangeListener.onProgressChange(mProgress);
                // responseOnTouch.onTouchResponse(cur_sections);
                break;
        }
        return true;
    }*/

    private void responseTouch(int x, int y) {
        /*if (validateTouch(x, y)) {
            LogUtil.d("okok");
        }*/
        setProgress(1000 - (int) ((float) y / getHeight() * 1000));
    }

    /**
     * 判断触摸是否有效
     *
     * @param xPos x
     * @param yPos y
     * @return is validate touch
     */
    private boolean validateTouch(float xPos, float yPos) {
        //获取控件在屏幕的位置
        int[] location = new int[2];
        getLocationOnScreen(location);

        //控件相对于屏幕的x与y坐标
        int x = location[0];
        int y = location[1];


        //圆半径 通过左右坐标计算获得getLeft
        int r = (getRight() - getLeft()) / 2;

        //圆心坐标
        int vCenterX = x + r;
        int vCenterY = y + r;

        //点击位置x坐标与圆心的x坐标的距离
        int distanceX = (int) Math.abs(vCenterX - xPos);
        //点击位置y坐标与圆心的y坐标的距离
        int distanceY = (int) Math.abs(vCenterY - yPos);
        //点击位置与圆心的直线距离
        int distanceZ = (int) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));

        //如果点击位置与圆心的距离大于圆的半径，证明点击位置没有在圆内
        if (distanceZ > r) {
            return false;
        }

        return true;
    }

    public ProgressChangeListener getProgressChangeListener() {
        return progressChangeListener;
    }

    public void setProgressChangeListener(ProgressChangeListener progressChangeListener) {
        this.progressChangeListener = progressChangeListener;
    }

    ProgressChangeListener progressChangeListener;


    public interface ProgressChangeListener {
        void onProgressChange(int progress);
    }

    public void setmWaveColor(int mWaveColor) {
        this.mWaveColor = mWaveColor;
        invalidate();
    }

    public void setmBorderColor(int mBorderColor) {
        this.mBorderColor = mBorderColor;
        invalidate();
    }
}