package com.example.wuzhihan.finalwork.Signin;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;


public class ScrollLayout extends ViewGroup {
    private static final String TAG = "ScrollLayout";
    private VelocityTracker mVelocityTracker;// 用于判断甩动手势
    private Scroller mScroller;// 滑动控制
    private int mCurScreen;
    private int mDefaultScreen = 1;
    private float mLastMotionX; // 手指移动的时候，或者手指离开屏幕的时候记录下的手指的横坐标
    private float mLastMotionY; // 手指移动的时候，或者手指离开屏幕的时候记录下的手指的纵坐标
    private int mTouchSlop;// 手指移动的最小距离的判断标准
    // 在viewpapper中就是依赖于这个值来判断用户
    // 手指滑动的距离是否达到界面滑动的标准
    private static final int SNAP_VELOCITY = 600;// 默认的滚动速度,之后用于和手指滑动产生的速度比较,获取屏幕滚动的速度
    private static final int TOUCH_STATE_REST = 0;//表示触摸状态为空闲,即没有触摸或者手指离开了
    private static final int TOUCH_STATE_SCROLLING = 1;//表示手指正在移动
    private int mTouchState = TOUCH_STATE_REST;// 当前手指的事件状态
    private String mCurTime;
    private OnViewChangeListener mOnViewChangeListener;

    /**
     * 滑动是否完成
     */
    private boolean mCompleted = false;
    /**
     * 滑动的方向
     */
    private int mOrientation = -1; // 0：向左；1：向右

    public ScrollLayout(Context context) {
        super(context);
        Log.i(TAG, "---ScrollLyout1---");
        init(context);
    }

    public ScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG, "---ScrollLyout2---");
        init(context);
    }

    public ScrollLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.i(TAG, "---ScrollLyout3---");
        init(context);
    }

    private void init(Context context) {
        mCurScreen = mDefaultScreen;
        mScroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop(); // 使用系统默认的值
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i(TAG, "---onLayout---");
        // 为每一个孩子设置它们的位置
        int width = 0;
        int childLeft = 0;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = getChildAt(i);
            if (childView.getVisibility() != View.GONE) {
                // 此处获取到的宽度就是在onMeasure中设置的值
                final int childWidth = childView.getMeasuredWidth();
                width = childWidth;
                // 为每一个子View布局
                childView.layout(childLeft, 0, childLeft + childWidth, childView.getMeasuredHeight());
                childLeft = childLeft + childWidth;
            }
        }
        // 需要测试
        scrollTo(mCurScreen * width, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(TAG, "---onMeasure---");
        // 在onlayout之前执行，获取View申请的大小，把它们保存下来，方面后面使用
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException(
                    "ScrollLayout only can run at EXACTLY mode!");
        }

        final int hightModed = MeasureSpec.getMode(heightMeasureSpec);
        if (hightModed != MeasureSpec.EXACTLY) {
            throw new IllegalStateException(
                    "ScrollLayout only can run at EXACTLY mode!");
        }

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    /**
     * 让界面跟着手指移动到手指移动的地点
     */
    public void snapToDestination() {
        Log.i(TAG, "---snapToDestination---");
        final int screenWidth = getWidth();// 子view的宽度，此例中为他适配的父view的宽度
        Log.i(TAG, "screenWidth = " + screenWidth);
        final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;// 某个算法吧
        Log.i(TAG, "[destScreen] : " + destScreen);// getScroolX()值为
        snapToScreen(destScreen);
    }

    /**
     * 滚动到指定screen
     */
    public void snapToScreen(int whichScreen) {
        Log.i(TAG, "---snapToDestScreen---");
        Log.i(TAG, "Math.min(destScreen, getChildCount() - 1) = " + (Math.min(whichScreen, getChildCount() - 1)));
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));// 获取要滚动到的目标screen
        Log.i(TAG, "whichScreen = " + whichScreen + " getScrollX = " + getScrollX() + " whichScreen * getWidth() = " + whichScreen * getWidth());
        if (getScrollX() != (whichScreen * getWidth())) {
            final int delta = whichScreen * getWidth() - getScrollX();// 获取屏幕移到目的view还需要移动多少距离
            Log.i(TAG, "[getScrollX()] : " + getScrollX());
            Log.i(TAG, "[delta] : " + delta);
            Log.i(TAG, "[getScrollX要走到的位置为] : " + (getScrollX() + delta));
            mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);// 使用Scroller辅助滚动，让滚动变得更平滑
            mCurScreen = whichScreen;
            invalidate();// 重绘界面
        }
    }

    @Override
    public void computeScroll() {
        Log.i(TAG, "---computeScroll---");
        if (mScroller.computeScrollOffset()) {//computeScrollOffset  方法会一直返回false，但当动画执行完成后会返回返加true.
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        } else {
            if (mCompleted) {
                mCompleted = !mCompleted;
                if (mOrientation == 0) { // 向左，删除最后一个view，添加到最前面
                    int ori = mOrientation;
                    mOrientation = -1;
                    mCurScreen = 1;
                    if (mOnViewChangeListener != null) {
                        mOnViewChangeListener.OnViewChange(ori);
                    }
//                    View view = getChildAt(2);
//                    removeViewAt(2);
//                    addView(view, 0);
                } else if (mOrientation == 1) { // 向右，删除第一个view,添加到最后
                    int ori = mOrientation;
                    mOrientation = -1;
                    mCurScreen = 1;
                    if (mOnViewChangeListener != null) {
                        mOnViewChangeListener.OnViewChange(ori);
                    }
//                    View view = getChildAt(0);
//                    removeViewAt(0);
//                    addView(view);
                }
            }

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "---onTouchEvent---");
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
//        removeViewAt(2);
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN://1,终止滚动2,获取最后一次事件的x值
                Log.i(TAG, "onTouchEvent:ACTION_DOWN");
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                    mVelocityTracker.addMovement(event);
                }
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE://1,获取最后一次事件的x值2,滚动到指定位置
                Log.i(TAG, "onTouchEvent:ACTION_MOVE");
                int deltaX = (int) (mLastMotionX - x);
                if (IsCanMove(deltaX)) {
                    if (mVelocityTracker != null) {
                        mVelocityTracker.addMovement(event);
                    }
                    mLastMotionX = x;
                    scrollBy(deltaX, 0);
                }
                break;
            case MotionEvent.ACTION_UP://1,计算手指移动的速度并得出我们需要的速度2,选择不同情况下滚动到哪个screen
                Log.i(TAG, "onTouchEvent:ACTION_UP");
                int velocityX = 0;
                if (mVelocityTracker != null) {
                    mVelocityTracker.addMovement(event);
                    mVelocityTracker.computeCurrentVelocity(1000);// 设置属性为计算1秒运行多少个像素
                    // computeCurrentVelocity(int units, float maxVelocity)上面的1000即为此处的units。
                    // maxVelocity必须为正，表示当计算出的速率大于maxVelocity时为maxVelocity,小于maxVelocity就为计算出的速率
                    velocityX = (int) mVelocityTracker.getXVelocity();
                    Log.i(TAG, "[velocityX] : " + velocityX);
                }
                if (velocityX > SNAP_VELOCITY && mCurScreen > 0)//如果速度为正，则表示向右滑动。需要指定mCurScreen大于0，才能滑，不然就不准确啦
                {
                    // Fling enough to move left
                    Log.i(TAG, "snap left");
                    Log.i(TAG, "速度为正且-->：当前mCurScreen = " + mCurScreen);

                    mCompleted = true;
                    mOrientation = 0;
                    snapToScreen(mCurScreen - 1);
                } else if (velocityX < -SNAP_VELOCITY && mCurScreen < getChildCount() - 1)//如果速度为负，则表示手指向左滑动。需要指定mCurScreen小于最后一个子view的id，才能滑，不然就不准确啦
                {
                    // Fling enough to move right
                    Log.i(TAG, "snap right");
                    Log.i(TAG, "速度为fu且《--：当前mCurScreen = " + mCurScreen);
                    Log.i(TAG, "要走到的：mCurScreen = " + (mCurScreen + 1));
                    mCompleted = true;
                    mOrientation = 1;
                    snapToScreen(mCurScreen + 1);
                } else//速度小于我们规定的达标速度，那么就让界面跟着手指滑动显示。最后显示哪个screen再做计算（方法中有计算）
                {
                    Log.i(TAG, "速度的绝对值小于规定速度,走snapToDestination方法");
                    snapToDestination();
                }
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                //mTouchState = TOUCH_STATE_REST;		//为什么这里要设置？？？
                break;
            case MotionEvent.ACTION_CANCEL://1,设置触摸事件状态为空闲
                Log.i(TAG, "onTouchEvent:ACTION_CANCEL");
                mTouchState = TOUCH_STATE_REST;
                break;
            default:
                break;
        }
        return true;
    }

    private boolean IsCanMove(int deltaX) {
        if (getScrollX() <= 0 && deltaX < 0) {
            return false;
        }
        if (getScrollX() >= (getChildCount() - 1) * getWidth() && deltaX > 0) {
            return false;
        }
        return true;
    }

    public void SetOnViewChangeListener(OnViewChangeListener listener) {
        mOnViewChangeListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.i(TAG, "---onInterceptTouchEvent---");
        final int action = ev.getAction();
        // 如果
        if ((action == MotionEvent.ACTION_MOVE)
                && mTouchState != TOUCH_STATE_REST) {
            return true;
        }

        final float x = ev.getX();
        final float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:// 判断滚动是否停止
                Log.i(TAG, "onInterceptTouchEvent:ACTION_DOWN");
                mLastMotionX = x;
                mLastMotionY = y;
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
                        : TOUCH_STATE_SCROLLING;

                break;
            case MotionEvent.ACTION_MOVE:// 判断是否达成滚动条件
                Log.i(TAG, "onInterceptTouchEvent:ACTION_MOVE");
                final int xDiff = (int) Math.abs(mLastMotionX - x);
                int velocityX = 0;
                if (mVelocityTracker != null) {
                    mVelocityTracker.addMovement(ev);
                    mVelocityTracker.computeCurrentVelocity(1000);// 设置属性为计算1秒运行多少个像素
                    // computeCurrentVelocity(int units, float maxVelocity)上面的1000即为此处的units。
                    // maxVelocity必须为正，表示当计算出的速率大于maxVelocity时为maxVelocity,小于maxVelocity就为计算出的速率
                    velocityX = (int) mVelocityTracker.getXVelocity();
                    Log.i(TAG, "[velocityX] : " + velocityX);
                }
                if (velocityX < -SNAP_VELOCITY && mCurScreen < getChildCount() - 1)//如果速度为负，则表示手指向左滑动。需要指定mCurScreen小于最后一个子view的id，才能滑，不然就不准确啦
                {
                    // Fling enough to move right
                    mCompleted = true;
                    mOrientation = 1;
                    snapToScreen(mCurScreen + 1);
                }
                if (xDiff > mTouchSlop) {// 如果该值大于我们规定的最小移动距离则表示界面在滚动
                    mTouchState = TOUCH_STATE_SCROLLING;
                }
                break;
            case MotionEvent.ACTION_UP:// 把状态调整为空闲
                Log.i(TAG, "onInterceptTouchEvent:ACTION_UP");
                mTouchState = TOUCH_STATE_REST;
                break;

        }
        // 如果屏幕没有在滚动那么就不消耗这个touch事件
        return mTouchState != TOUCH_STATE_REST;
    }

    public void clickLeftMonth() {
        Log.d(TAG,"clickLeftMonth#mCurScreen = " + mCurScreen);
        mCompleted = true;
        mOrientation = 0;
        snapToScreen(mCurScreen - 1);
    }

    public void clickRightMonth() {
        mCompleted = true;
        mOrientation = 1;
        snapToScreen(mCurScreen + 1);
    }

    public int getmCurScreen() {
        return mCurScreen;
    }
    public String getmCurTime() {
        return mCurTime;
    }

    public void setmCurTime(String mCurTime) {
        this.mCurTime = mCurTime;
    }
}
