package com.example.wuzhihan.finalwork.Signin.sign;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日历控件 功能：获得点选的日期区间
 */
public class CalendarView extends View implements View.OnTouchListener {
    private static final String TAG = "WzhFrame";
    /**
     * 开头时，上个月留下来的格子
     */
    private int mLastMonthDay;

    /**
     * 当前日历显示的月
     */
    private Date curDate;
    /**
     * 今天的日期文字显示红色
     */
    private Date today;
    /**
     * 手指按下状态时临时日期
     */
    private Date downDate;
    /**
     * 日历显示的第一个日期和最后一个日期
     */
    private Date showFirstDate, showLastDate;

    /**
     * 按下的格子索引
     */
    private int downIndex;
    private Calendar calendar;
    private Surface surface;
    /**
     * 日历显示数字
     */
    private int[] date = new int[42];
    /**
     * 当前显示的日历起始的索引
     */
    private int curStartIndex, curEndIndex;
    /**
     * 日期的背景颜色
     */
    private int mDefaultDayBackGround = Color.argb(0x7F, 0xBF, 0xBF, 0xBF);//Color.parseColor("#7FBFBFBF");
    private int mDayBackGround = Color.parseColor("#BFBFBF");
    /**
     * 日期的字体颜色
     */
    private int mDayColor = Color.parseColor("#FFFFFF");
    /**
     * 考勤签到的背景颜色
     */
    private int mSignInGround = Color.parseColor("#51AFE6");
    private int mSignOutGround = Color.parseColor("#27B675");
    private int mSignWrongGround = Color.parseColor("#FC6156");
    private int mSignLeaveGround = Color.parseColor("#D4D4D4");
    private int mSignAskForLeaveGround = Color.parseColor("#F9B01E");
    /**
     * 为false表示只选择了开始日期，true表示结束日期也选择了
     */
    private DisplayMetrics mDisplayMetrics;
    /**
     * 提供正常，考勤异常，外勤接口
     */
    private List<Integer> signOutList;
    private List<Integer> signInList;
    private List<Integer> signWrongList;
    private List<Integer> signLeaveList;
    private List<Integer> signAskForLeaveList;


    /**
     * 给控件设置监听事件
     */
    private OnItemClickListener onItemClickListener;

    public CalendarView(Context context) {
        super(context);
        init();
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
//        curDate = mSelectedStartDate = selectedEndDate = today = new Date();
        curDate = today = new Date();
        mDisplayMetrics = getResources().getDisplayMetrics();
        calendar = Calendar.getInstance();
        calendar.setTime(curDate);
        surface = new Surface();
        surface.mDensity = getResources().getDisplayMetrics().density;
        setBackgroundColor(surface.bgColor);
        setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        surface.width = getResources().getDisplayMetrics().widthPixels;
        surface.height = getResources().getDisplayMetrics().heightPixels * 2 / 4;
        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(surface.width,
                View.MeasureSpec.EXACTLY);
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(surface.height,
                View.MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        if (changed) {
            surface.init();
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /**
         * 星期
         */
        float mWeekTextY = surface.mWeekHeight * 3 / 4f;
        for (int i = 0; i < surface.mWeekText.length; i++) {
            float mWeekTextX = i
                    * surface.mCellWidth
                    + (surface.mCellWidth - surface.weekPaint
                    .measureText(surface.mWeekText[i])) / 2f;
            canvas.drawText(surface.mWeekText[i], mWeekTextX, mWeekTextY,
                    surface.weekPaint);
        }
        /**
         * 计算日期
         */
        calculateDate();
//        /**
//         * 按下状态，选择状态背景色
//         */
//        drawDownOrSelectedBg(canvas);
        /**
         * write date number,today index
         */
        int todayIndex = -1;
        calendar.setTime(curDate);
        Log.d(TAG,"CalendarView#curDate=" + curDate);
        String curYearAndMonth = calendar.get(Calendar.YEAR) + ""
                + calendar.get(Calendar.MONTH);
        calendar.setTime(today);
        Log.d(TAG,"CalendarView#today=" + today);
        String todayYearAndMonth = calendar.get(Calendar.YEAR) + ""
                + calendar.get(Calendar.MONTH);
        if (curYearAndMonth.equals(todayYearAndMonth)) {
            int todayNumber = calendar.get(Calendar.DAY_OF_MONTH);
            todayIndex = curStartIndex + todayNumber - 1;
        }
        mLastMonthDay = 0;
        for (int i = 0; i < 42; i++) {
            /**
             * 给日期添加背景
             * */
            int color = mDayColor;
            if (isLastMonth(i)) {
                color = surface.borderColor;
                drawDefaultBackGround(canvas, i);
                mLastMonthDay++;
            } else if (isNextMonth(i)) {
                color = surface.borderColor;
                drawDefaultBackGround(canvas, i);
            } else {
                drawBackGround(canvas, i);
            }

            if (signOutList != null && signOutList.size() > 0) {
                if (signOutList.contains(i + 1 - mLastMonthDay)) {
                    drawSignOutBackGround(canvas, i);
                }
            }
            if (signInList != null && signInList.size() > 0) {
                if (signInList.contains(i + 1 - mLastMonthDay)) {
                    drawSignInBackGround(canvas, i);
                }
            }
            if (signWrongList != null && signWrongList.size() > 0) {
                if (signWrongList.contains(i + 1 - mLastMonthDay)) {
                    drawSignWrongGround(canvas, i);
                }
            }
            if (signLeaveList != null && signLeaveList.size() > 0) {
                if (signLeaveList.contains(i + 1 - mLastMonthDay)) {
                    drawSignLeaveGround(canvas, i);
                }
            }
            if (signAskForLeaveList != null && signAskForLeaveList.size() > 0) {
                if (signAskForLeaveList.contains(i + 1 - mLastMonthDay)) {
                    drawSignAskForLeaveGround(canvas, i);
                }
            }
            if (todayIndex != -1 && i == todayIndex) {
                int todayColor = surface.todayNumberColor;
                drawTodayCellText(canvas, i, date[i] + "", todayColor);
            }

            drawCellText(canvas, i, date[i] + "", color);
        }
        super.onDraw(canvas);
    }


    private void drawBackGround(Canvas canvas, int index) {
        surface.datePaint.setColor(mDayBackGround);
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        float circlex = surface.mCellWidth * (x - 1) + surface.mCellWidth / 2;
        float circley = surface.mCellHeight * y + surface.mCellHeight / 2;
        canvas.drawCircle(circlex, circley, surface.mCellWidth / 4, surface.datePaint);
    }

    private void drawDefaultBackGround(Canvas canvas, int index) {
        surface.datePaint.setColor(mDefaultDayBackGround);
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        float circlex = surface.mCellWidth * (x - 1) + surface.mCellWidth / 2;
        float circley = surface.mCellHeight * y + surface.mCellHeight / 2;
        canvas.drawCircle(circlex, circley, surface.mCellWidth / 4, surface.datePaint);
    }

    private void drawSignOutBackGround(Canvas canvas, int index) {
        surface.datePaint.setColor(mSignOutGround);
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        float circlex = surface.mCellWidth * (x - 1) + surface.mCellWidth / 2;
        float circley = surface.mCellHeight * y + surface.mCellHeight / 2;
        canvas.drawCircle(circlex, circley, surface.mCellWidth / 4, surface.datePaint);
    }

    private void drawSignWrongGround(Canvas canvas, int index) {
        surface.datePaint.setColor(mSignWrongGround);
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        float circlex = surface.mCellWidth * (x - 1) + surface.mCellWidth / 2;
        float circley = surface.mCellHeight * y + surface.mCellHeight / 2;
        canvas.drawCircle(circlex, circley, surface.mCellWidth / 4, surface.datePaint);
    }

    private void drawSignInBackGround(Canvas canvas, int index) {
        surface.datePaint.setColor(mSignInGround);
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        float circlex = surface.mCellWidth * (x - 1) + surface.mCellWidth / 2;
        float circley = surface.mCellHeight * y + surface.mCellHeight / 2;
        canvas.drawCircle(circlex, circley, surface.mCellWidth / 4, surface.datePaint);
    }

    private void drawSignLeaveGround(Canvas canvas, int index) {
        surface.datePaint.setColor(mSignLeaveGround);
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        float circlex = surface.mCellWidth * (x - 1) + surface.mCellWidth / 2;
        float circley = surface.mCellHeight * y + surface.mCellHeight / 2;
        canvas.drawCircle(circlex, circley, surface.mCellWidth / 4, surface.datePaint);
    }

    private void drawSignAskForLeaveGround(Canvas canvas, int index) {
        surface.datePaint.setColor(mSignAskForLeaveGround);
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        float circlex = surface.mCellWidth * (x - 1) + surface.mCellWidth / 2;
        float circley = surface.mCellHeight * y + surface.mCellHeight / 2;
        canvas.drawCircle(circlex, circley, surface.mCellWidth / 4, surface.datePaint);
    }

    private void calculateDate() {
        Log.d(TAG,"calculateDate#curDates=" + curDate + " " + getCurDate());
        calendar.setTime(curDate);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int monthStart = dayInWeek;
        if (monthStart == 1) {
            monthStart = 8;
        }
        /**
         * 以日为开头-1，以星期一为开头-2
         */
        monthStart -= 1;
        curStartIndex = monthStart;
        date[monthStart] = 1;
        /**
         * last month
         */
        Log.d(TAG,"calculateDate#next month curStartIndex=" + curStartIndex);
        if (monthStart > 0) {
            calendar.set(Calendar.DAY_OF_MONTH, 0);
            int dayInmonth = calendar.get(Calendar.DAY_OF_MONTH);
            for (int i = monthStart - 1; i >= 0; i--) {
                date[i] = dayInmonth;
                dayInmonth--;
            }
            calendar.set(Calendar.DAY_OF_MONTH, date[0]);
        }
        showFirstDate = calendar.getTime();
        /**
         * this month
         */
        Log.d(TAG,"calculateDate#this month curDate=" + curDate);
        calendar.setTime(curDate);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        int monthDay = calendar.get(Calendar.DAY_OF_MONTH);
        for (int i = 1; i < monthDay; i++) {
            date[monthStart + i] = i + 1;
        }
        Log.d(TAG,"calculateDate#next month monthDay=" + monthDay);
        curEndIndex = monthStart + monthDay;
        Log.d(TAG,"calculateDate#this month curEndIndex=" + curEndIndex);
        /**
         * next month
         */
        Log.d(TAG,"calculateDate#next month curEndIndex=" + curEndIndex);
        for (int i = monthStart + monthDay; i < 42; i++) {
            date[i] = i - (monthStart + monthDay) + 1;
        }
        Log.d(TAG,"calculateDate#date[]=" + date[0] + " " + date[41]);

        if (curEndIndex < 42) {
            /**
             * 显示了下一月的
             */
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        calendar.set(Calendar.DAY_OF_MONTH, date[41]);
        showLastDate = calendar.getTime();
        Log.d(TAG,"calculateDate#next month showLastDate=" + showLastDate);

    }

    /**
     * @param canvas
     * @param index
     * @param text
     */
    private void drawCellText(Canvas canvas, int index, String text, int color) {
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        surface.datePaint.setColor(color);
        float cellY = surface.mWeekHeight + (y - 1)
                * surface.mCellHeight + surface.mCellHeight * 7 / 10f;
        float cellX = (surface.mCellWidth * (x - 1))
                + (surface.mCellWidth - surface.datePaint.measureText(text))
                / 2f;
        canvas.drawText(text, cellX, cellY, surface.datePaint);
    }

    /**
     * @param canvas
     * @param index
     * @param text   绘制今天
     */
    private void drawTodayCellText(Canvas canvas, int index, String text, int color) {
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        surface.datePaint.setColor(color);
        float cellY = surface.mWeekHeight + (y - 1)
                * surface.mCellHeight + surface.mCellHeight * 3 / 4f;
        float cellX = (surface.mCellWidth * (x - 1))
                + (surface.mCellWidth - surface.datePaint.measureText(text))
                / 2f;
        float circlex = surface.mCellWidth * (x - 1) + surface.mCellWidth / 2;
        float circley = (float) (surface.mCellHeight * y + surface.mCellHeight / 2 + surface.mCellHeight / 2.5);
        canvas.drawCircle(circlex, circley, surface.mCellWidth / 20, surface.datePaint);
//        canvas.drawText(text, cellX, cellY, surface.datePaint);
    }

    /**
     * @param canvas
     * @param index
     * @param color
     */
    private void drawCellBg(Canvas canvas, int index, int color) {
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        surface.cellBgPaint.setColor(color);
        float left = surface.mCellWidth * (x - 1) + surface.mBorderWidth;
        /**
         * 修改背景方框高度，大约了一下
         */
        float top = surface.mWeekHeight + (y - 1) * surface.mCellHeight + surface.mBorderWidth + surface.mCellHeight / 8;
        canvas.drawRect(left, top, left + surface.mCellWidth
                - surface.mBorderWidth, top + surface.mCellHeight
                - surface.mBorderWidth, surface.cellBgPaint);
    }

//    private void drawDownOrSelectedBg(Canvas canvas) {
//        /**
//         *  down and not up
//         */
//        if (downDate != null) {
//            drawCellBg(canvas, downIndex, surface.cellDownColor);
//        }
//
//    }


    private boolean isLastMonth(int i) {
        if (i < curStartIndex) {
            return true;
        }
        return false;
    }

    private boolean isNextMonth(int i) {
        if (i >= curEndIndex) {
            return true;
        }
        return false;
    }

    private int getXByIndex(int i) {
        return i % 7 + 1;
    }

    private int getYByIndex(int i) {
        return i / 7 + 1;
    }

    /**
     * 获得当前应该显示的年月
     */
    public String getYearAndmonth() {
        calendar.setTime(curDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        return year + "-" + month;
    }

    /**
     * 上一月
     */
    public String clickLeftMonth(Date curDates) {
        Log.d(TAG,"CalendarView#clickLeftMonth curDate=" + curDate);
        calendar.setTime(curDates);
        calendar.add(Calendar.MONTH, -1);
        curDate = calendar.getTime();
        Log.d(TAG,"CalendarView#calendar.getTime =" + curDate);
        invalidate();
        return getYearAndmonth();
    }

    /**
     * 下一月
     */
    public String clickRightMonth(Date curDates) {
        Log.d(TAG,"CalendarView#clickRightMonth curDate=" + curDates);
        calendar.setTime(curDates);
        calendar.add(Calendar.MONTH, 1);
        curDate = calendar.getTime();
        invalidate();
        return getYearAndmonth();
    }

    /**
     * 设置日历时间
     */
    public void setCalendarData(Date date) {
        calendar.setTime(date);
        invalidate();
    }


    private void setSelectedDateByCoor(float x, float y) {

        /**
         * cell click down
         */
        if (y > surface.mWeekHeight) {
            int m = (int) (Math.floor(x / surface.mCellWidth) + 1);
            int n = (int) (Math
                    .floor((y - (surface.mWeekHeight))
                            / Float.valueOf(surface.mCellHeight)) + 1);
            downIndex = (n - 1) * 7 + m - 1;
            calendar.setTime(curDate);
            if (isLastMonth(downIndex)) {
                calendar.add(Calendar.MONTH, -1);
            } else if (isNextMonth(downIndex)) {
                calendar.add(Calendar.MONTH, 1);
            }
            calendar.set(Calendar.DAY_OF_MONTH, date[downIndex]);
            downDate = calendar.getTime();
        }
        invalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setSelectedDateByCoor(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG,"CalendarView# Action_Up downDate = " + downDate + " listener = " + onItemClickListener);
                if (downDate != null) {
                    /**r
                     * 响应监听事件
                     */
                    onItemClickListener.OnItemClick(downDate);
                    invalidate();
                }

                break;
        }
        return true;
    }

    /**
     * 给控件设置监听事件
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 监听接口
     */
    public interface OnItemClickListener {
        void OnItemClick(Date downDate);
    }

    /**
     * 1. 布局尺寸 2. 文字颜色，大小 3. 当前日期的颜色，选择的日期颜色
     */
    private class Surface {
        public float mDensity;
        /**
         * 整个控件的宽度
         */
        public int width;
        /**
         * 整个控件的高度
         */
        public int height;
        /**
         * 显示星期的高度
         */
        public float mWeekHeight;
        /**
         * 日期方框宽度
         */
        public float mCellWidth;
        /**
         * 日期方框高度
         */
        public float mCellHeight;
        public float mBorderWidth;
        public int bgColor = Color.parseColor("#FFFFFF");
        private int textColor = Color.parseColor("#BFBFBF");
        private int dateTextColor = Color.parseColor("#FFFFFF");
        private int borderColor = Color.parseColor("#FFFFFF");
        public int todayNumberColor = Color.RED;
        //        public int cellDownColor = Color.parseColor("#8B1A1A");
        public int cellSelectedColor = Color.parseColor("#FFC0CB");
        public Paint weekPaint;
        public Paint datePaint;
        public Paint cellBgPaint;
        public String[] mWeekText = {"日", "一", "二", "三", "四", "五", "六"};

        public void init() {
            float temp = height / 7f;
            mWeekHeight = (float) ((temp + temp * 0.3f) * 0.7);
            mCellHeight = (height - mWeekHeight) / 6f;
            mCellWidth = width / 7f;
            mBorderWidth = (float) (0.5 * mDensity);
            mBorderWidth = mBorderWidth < 1 ? 1 : mBorderWidth;
            weekPaint = new Paint();
            weekPaint.setColor(textColor);
            weekPaint.setAntiAlias(true);
            weekPaint.setTextSize(mDisplayMetrics.scaledDensity * 12);
            weekPaint.setTypeface(Typeface.DEFAULT_BOLD);
            datePaint = new Paint();
            datePaint.setColor(dateTextColor);
            datePaint.setAntiAlias(true);
            datePaint.setTextSize(mDisplayMetrics.scaledDensity * 14);
            datePaint.setTypeface(Typeface.DEFAULT_BOLD);
            cellBgPaint = new Paint();
            cellBgPaint.setAntiAlias(true);
            cellBgPaint.setStyle(Paint.Style.FILL);
            cellBgPaint.setColor(cellSelectedColor);
        }
    }

    /**
     * 设置外勤天数
     *
     * @param signOutList
     */
    public void setSignOutList(List<Integer> signOutList) {
        this.signOutList = signOutList;
    }

    /**
     * 设置考勤正常天数
     *
     * @param signInList
     */
    public void setSignInList(List<Integer> signInList) {
        this.signInList = signInList;
    }

    /**
     * 设置考勤缺勤天数
     *
     * @param signLeaveList
     */
    public void setSignLeaveList(List<Integer> signLeaveList) {
        this.signLeaveList = signLeaveList;
    }

    /**
     * 设置外勤异常天数
     *
     * @param signWrongList
     */
    public void setSignWrongList(List<Integer> signWrongList) {
        this.signWrongList = signWrongList;
    }

    /**
     * 设置考勤请假天数
     *
     * @param signAskForLeaveList
     */
    public void setSignAskForLeaveList(List<Integer> signAskForLeaveList) {
        this.signAskForLeaveList = signAskForLeaveList;
    }

    public int getmLastMonthDay() {
        return mLastMonthDay;
    }

    public List<Integer> getSignWrongList() {
        return signWrongList;
    }

    public List<Integer> getSignInList() {
        return signInList;
    }

    public List<Integer> getSignOutList() {
        return signOutList;
    }

    public List<Integer> getSignLeaveList() {
        return signLeaveList;
    }

    public int getmSignAskForLeaveGround() {
        return mSignAskForLeaveGround;
    }

    /***
     * 当前时间
     */
    public Date getCurDate() {
        return curDate;
    }

    public void setCurDate(Date curDate) {
        this.curDate = curDate;
    }

    /**
     * 按下格子的索引
     */
    public int getDownIndex() {
        return downIndex;
    }

    public void setDownIndex(int downIndex) {
        this.downIndex = downIndex;
    }

    public String moveToMonth(Date curDates) {
        Log.d(TAG,"CalendarView#moveToMonth curDate=" + curDates);
        curDate = curDates;
        invalidate();
        return getYearAndmonth();
    }

}
