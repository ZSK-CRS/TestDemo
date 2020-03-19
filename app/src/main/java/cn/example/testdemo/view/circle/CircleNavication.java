package cn.example.testdemo.view.circle;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;

import cn.example.testdemo.R;

/**
 * Author : ZSK
 * Date : 2020/2/16
 * Description :
 */
public class CircleNavication extends ViewGroup {

    private ListAdapter mAdapter;

    private View centerView; //中央的视图

    private int mRadius;

    /**
     * 该容器中child item的默认尺寸
     */
    private static float radioChildDimen;

    /**
     * 裁单张child的默认尺寸
     */
    private static float radioCenterDimen;

    /**
     * 该容器的内边距，默认无padding，如需使用则设定该值
     */
    private float radioPadding;

    /**
     * 如果移动角度达到该值，则屏蔽点击
     */
    private static final int NOCLICK_VALUE = 3;

    /**
     * 该容器的内边距,无视padding属性，如需边距请用该变量
     */
    private float mPadding;

    /**
     * 布局时的开始角度
     */
    private double mStartAngle = 0;


    /**
     * 检测按下到抬起时旋转的角度
     */
    private float mTmpAngle;

    /**
     * 检测按下到抬起时使用的时间
     */
    private long mDownTime;

    /**
     * 判断是否正在自动滚动
     */
    private boolean isFling;

    /**
     * item点击事件
     */
    private OnNavigationItemClickListener mOnNavigationItemClickListener;


    private VelocityTracker velocityTracker;//速度追踪

    private double mCurrentVelocity;//记录滑动的速度

    private int cl;//中心按钮的左边或上边离边界的距离

    private int cr;//中心按钮的右边或下边离边界的距离

    private boolean isCenterMove;//判断是否对中央施加滑动

    public CircleNavication(Context context) {
        super(context);
    }

    public CircleNavication(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleNavication);
        radioChildDimen = typedArray.getFloat(R.styleable.CircleNavication_radioChildDimen, 1 / 4f);
        radioCenterDimen = typedArray.getFloat(R.styleable.CircleNavication_radioCenterDimen, 1 / 3f);
        radioPadding = typedArray.getFloat(R.styleable.CircleNavication_radioPadding, 1 / 12f);
        typedArray.recycle();
        //无视padding
        setPadding(0, 0, 0, 0);
    }

    public CircleNavication(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAdapter != null) {
            buildItem();
        }
        if (centerView != null) {
            addView(centerView);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量自身尺寸
        measureMyself(widthMeasureSpec, heightMeasureSpec);
        //测量子View的尺寸
        measureAllChild();
        mPadding = radioPadding * mRadius;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int layoutRadius = mRadius;

        int left, top;

        //子View的尺寸
        int cWidth = (int) (layoutRadius * radioChildDimen);

        //根据view的个数,计算角度
        float angleDelay = 360 / mAdapter.getCount();

        //遍历去设置view的位置
        for (int i = 0; i < mAdapter.getCount(); i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            mStartAngle %= 360;
            //计算，中心点到view中心的距离
            float tmp = layoutRadius / 2f - cWidth / 2 - mPadding;
            left = layoutRadius / 2 + (int) Math.round(tmp * Math.cos(Math.toRadians(mStartAngle)) - 1 / 2f * cWidth);
            top = layoutRadius / 2 + (int) Math.round(tmp * Math.sin(Math.toRadians(mStartAngle)) - 1 / 2f * cWidth);
            child.layout(left, top, left + cWidth, top + cWidth);
            // 叠加尺寸
            mStartAngle += angleDelay;
        }
        if (centerView != null) {
            //设置center view位置
            cl = layoutRadius / 2 - centerView.getMeasuredWidth() / 2;
            cr = cl + centerView.getMeasuredWidth();
            centerView.layout(cl, cl, cr, cr);
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        boolean isIncenter = centerView != null ? cl < x && x < cr && cl < y && y < cr : false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handleDown(x, y, isIncenter);
                break;
            case MotionEvent.ACTION_MOVE:
                handleMove(x, y, isIncenter, event);
                break;
            case MotionEvent.ACTION_UP:
                handleUp(x, y);
                break;

        }
        return super.dispatchTouchEvent(event);
    }


    /**
     * 记录上一次的x，y坐标
     */
    private float mLastX;
    private float mLastY;

    /**
     * 自动滚动的Runnable
     */
    private AutoFlingRunnable mFlingRunnable;

    private boolean handleDown(float x, float y, boolean isIncenter) {
        velocityTracker = VelocityTracker.obtain();
        isCenterMove = false;
        mLastX = x;
        mLastY = y;
        mDownTime = System.currentTimeMillis();
        mTmpAngle = 0;

        //如果当前已经在快速滑动
        if (isFling) {
            //移除快速滚动的回调，立刻停止
            removeCallbacks(mFlingRunnable);
            isFling = false;

            //点在中央不响应
            if (!isIncenter) {
                return true;
            }
        }
        return false;
    }

    private boolean handleMove(float x, float y, boolean isIncenter, MotionEvent event) {
        //获取开始的角度
        float start = (float) (180 / Math.PI * Math.atan2((double) (mLastY - mRadius / 2f), (double) (mLastX - mRadius / 2f)));
        //获得当前的角度
        float end = (float) (180 / Math.PI * Math.atan2((double) (y - mRadius / 2f), (double) (x - mRadius / 2f)));
        int factor = 0;
        //处理第2，3象限-180度和180度重合的问题
        if (getQuadrant(mLastX, mLastY) == 3 && getQuadrant(x, y) == 2) {
            factor = 1;
        } else if (getQuadrant(mLastX, mLastY) == 2 && getQuadrant(x, y) == 3) {
            factor = -1;
        }
        mStartAngle += end - start + factor * 360;
        mTmpAngle += end - start + factor * 360;
        //判断一定时间内的差值，判断是加速还是减速
        velocityTracker.addMovement(event);
        velocityTracker.computeCurrentVelocity(100);
        mCurrentVelocity = Math.hypot(velocityTracker.getXVelocity(), velocityTracker.getYVelocity());

        //如果当前旋转角度超过NOCLICK_VALUE,则判定为界面滑动
        if (Math.abs(mTmpAngle) > NOCLICK_VALUE) {
            //在中心按钮上滑动时，屏蔽滑动
            if (isIncenter) {
                isCenterMove = true;
                return false;
            }
            requestLayout();
        }
        mLastX = x;
        mLastY = y;
        return false;
    }

    private boolean handleUp(float x, float y) {
        velocityTracker.recycle();
        //计算每秒移动的角度
        float anglePerSecond = mTmpAngle * 1000 / (System.currentTimeMillis() - mDownTime);
        //防止速度过快 体验不好
        int mark = (int) (anglePerSecond / Math.abs(anglePerSecond));
        anglePerSecond = Math.abs(anglePerSecond) > 900 ? 900 * mark : anglePerSecond;
        //最后滑动的速度大于一定值判断为快速滑动
        if (mCurrentVelocity > 50 && !isFling) {
            //post一个任务 去自行滚动
            post(mFlingRunnable = new AutoFlingRunnable(anglePerSecond));
        }
        if (Math.abs(mTmpAngle) > NOCLICK_VALUE)
            return true;
        return false;
    }


    /**
     * 根据当前位置计算象限
     *
     * @param x
     * @param y
     * @return
     */
    private int getQuadrant(float x, float y) {
        int tmpX = (int) (x - mRadius / 2);
        int tmpY = (int) (y - mRadius / 2);
        if (tmpX >= 0) {
            return tmpY >= 0 ? 4 : 1;
        } else {
            return tmpY >= 0 ? 3 : 2;
        }
    }

    private void measureAllChild() {
        //获得半径
        mRadius = Math.max(getMeasuredWidth(), getMeasuredHeight());

        //子View的数量
        final int count = getChildCount();
        //子view的尺寸
        int childSize = (int) (mRadius * radioCenterDimen);

        //设定子View的模式
        int childMode = MeasureSpec.EXACTLY;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            //计算子View的尺寸
            int makeMeasureSpaec = -1;
            if (child == centerView) {
                makeMeasureSpaec = MeasureSpec.makeMeasureSpec((int) (mRadius * radioCenterDimen), childMode);
            } else {
                makeMeasureSpaec = MeasureSpec.makeMeasureSpec(childSize, childMode);
            }
            child.measure(makeMeasureSpaec, makeMeasureSpaec);
        }
    }

    private void measureMyself(int widthMeasureSpec, int heightMeasureSpec) {

        int resWidth, resHeight;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY) {
            //设置为背景图的高度
            resWidth = getSuggestedMinimumWidth();
            resWidth = resWidth == 0 ? getDefaultWidth() : resWidth;

            resHeight = getSuggestedMinimumHeight();
            resHeight = resHeight == 0 ? getDefaultWidth() : resHeight;

        } else {
            resWidth = resHeight = Math.min(width, height);
        }
        setMeasuredDimension(resWidth, resHeight);
    }

    /**
     * 构建菜单项
     */
    private void buildItem() {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View itemView = mAdapter.getView(i, null, this);
            final int position = i;
            itemView.setOnClickListener(view -> {
                if (mOnNavigationItemClickListener != null) {
                    mOnNavigationItemClickListener.itemClick(view, position);
                }
            });
            addView(itemView);
        }
    }

    /**
     * 获得默认该layout的尺寸
     *
     * @return
     */
    private int getDefaultWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return Math.min(outMetrics.widthPixels, outMetrics.heightPixels);
    }

    /*****************************************属性功能设置****************************************/

    /**
     * 数据
     *
     * @param adapter
     */
    public void setAdapter(ListAdapter adapter) {
        this.mAdapter = adapter;
    }

    /**
     * 设置中心视图
     *
     * @param centerView
     */
    public void setCenterView(View centerView) {
        this.centerView = centerView;
    }

    /**
     * 设置item点击事件
     */
    public void setOnNavigationItemClickListener(OnNavigationItemClickListener clickListener) {
        this.mOnNavigationItemClickListener = clickListener;
    }

    /*****************************************点击事件********************************************/

    /**
     * item的点击事件
     */
    public interface OnNavigationItemClickListener {
        void itemClick(View view, int position);
    }


    /*****************************************************************************************/

    private class AutoFlingRunnable implements Runnable {

        private float angelPerSecond;

        public AutoFlingRunnable(float velocity) {
            this.angelPerSecond = velocity;
        }

        @Override
        public void run() {
            //如果小于20则停止
            if (Math.abs(angelPerSecond) < 30) {
                isFling = false;
                return;
            }
            isFling = true;
            //不断改变mStartAngle ,让其滚动 /30避免滚动太快
            mStartAngle+= (angelPerSecond/30);
            //逐渐减小
            angelPerSecond/=1.1f;
            postDelayed(this,30);
            //重新布局
            requestLayout();
        }
    }
}
