package com.example.wuzhihan.finalwork.Signin.sign;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.wuzhihan.finalwork.R;
import com.example.wuzhihan.finalwork.Signin.singModel.SignListByMonth;
import com.example.wuzhihan.finalwork.Util;
import com.example.wuzhihan.finalwork.model.SignIn;
import com.example.wuzhihan.finalwork.model.SignOut;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MonthCalendarActivity extends Activity implements OnClickListener, OnViewChangeListener, CalendarView.OnItemClickListener {
    private static final String TAG = "MonthCalendarActivity";
//    private MonthCalendarResponse mResponse;

    private Map<String, List<SignListByMonth>> mSignListByMonthInfos = new HashMap<String, List<SignListByMonth>>();
    /**
     * 横向滑动
     */
    private ScrollLayout mScrollLayout;
    private int count;
    /**
     * 自定义头部
     */
//    private TopBarView mTopBarView;
    private CalendarView calendaLeft;
    private CalendarView calendar;
    private CalendarView calendaRight;
    private ImageButton calendarLeft;
    private TextView calendarCenter;
    private ImageButton calendarRight;
    private String lastSearchDate;
    private String searchDate;
    private String yearAndmonth;
    private String account;
    private int year;
    private int month;
    private List<String> signListByMonthList;
    List<Integer> signInTimeList = new ArrayList<>();
    List<Integer> signOutTimeList = new ArrayList<>();
    List<Integer> signExceptionTimeList = new ArrayList<>();
    List<Integer> signLeaveTimeList = new ArrayList<>();
    List<Integer> signAskForLeaveList = new ArrayList<>();

    List<SignListByMonth> signListByMonthListCopy = new ArrayList<>();
    List<SignListByMonth> signListByMonthListCopy2 = new ArrayList<>();

    private Handler calendarHandler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Date date = null;
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-mm");
                date = format.parse(bundle.getString("searchDate"));

            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar.setCalendarData(date);
            signListByMonthList.clear();
            super.handleMessage(msg);
        }
    };

    private String mUsername;
//    private String mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carlendar_layout);
        initView();
        initData();
        //获取日历中年月 ya[0]为年，ya[1]为月（格式大家可以自行在日历控件中改）
        yearAndmonth = calendar.getYearAndmonth();
        String[] ya = yearAndmonth.split("-");
        calendarCenter.setText(ya[0] + "年" + ya[1] + "月");
    }

    private void initView() {
        mScrollLayout = (ScrollLayout) findViewById(R.id.scrollLayout);
        count = mScrollLayout.getChildCount();
        Log.d(TAG,"MonthCalendarActivity#count=" + count);
        calendarLeft = (ImageButton) findViewById(R.id.calendarLeft);
        calendarCenter = (TextView) findViewById(R.id.calendarCenter);
        calendarRight = (ImageButton) findViewById(R.id.calendarRight);
        calendar = (CalendarView) findViewById(R.id.calendar);
        calendaLeft = (CalendarView) findViewById(R.id.calendar_left);
        calendaRight = (CalendarView) findViewById(R.id.calendar_right);
//        mTopBarView = (TopBarView) findViewById(R.id.center_top_bar);
        calendaLeft.setOnItemClickListener(this);
        calendar.setOnItemClickListener(this);
        calendaRight.setOnItemClickListener(this);
        calendarLeft.setOnClickListener(this);
        calendarRight.setOnClickListener(this);
        mScrollLayout.SetOnViewChangeListener(this);

    }

    private void initData() {
        /**
         * 获取account数据
         * */
        mUsername = getIntent().getStringExtra("username");

        signListByMonthList = new ArrayList<>();
        Calendar calendars = Calendar.getInstance();
        year = calendars.get(Calendar.YEAR);
        month = calendars.get(Calendar.MONTH) + 1;
        searchDate = CorrectDate(String.valueOf(year), String.valueOf(month));//把Date型日期变成String  x-y
        signExceptionTimeList.clear();
        signLeaveTimeList.clear();
        signOutTimeList.clear();
        signInTimeList.clear();
        signAskForLeaveList.clear();
        signListByMonthListCopy.clear();
        getList(searchDate);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.calendarLeft) {
//            getList();
            signInTimeList.clear();
            signOutTimeList.clear();
            signLeaveTimeList.clear();
            signAskForLeaveList.clear();
            signExceptionTimeList.clear();
            signListByMonthListCopy.clear();
            String leftYearAndmonth;
            View rightView = mScrollLayout.getChildAt(2);
            mScrollLayout.removeView(rightView);
            mScrollLayout.addView(rightView, 0);
            calendaLeft = (CalendarView) mScrollLayout.getChildAt(0);
            calendar = (CalendarView) mScrollLayout.getChildAt(1);
            calendaRight = (CalendarView) mScrollLayout.getChildAt(2);
            Date curDate = StrToDate(yearAndmonth);
            leftYearAndmonth = calendar.clickLeftMonth(curDate);
            yearAndmonth = leftYearAndmonth;
            String[] ya = leftYearAndmonth.split("-");
            String searchLeftDate = CorrectDate(ya[0], ya[1]);
            calendarCenter.setText(ya[0] + "年" + ya[1] + "月");
            //左滑获取存储在Map中的list数据
            List<SignListByMonth> signListByMonthList = mSignListByMonthInfos.get(searchLeftDate);
//            bianse();
            if (signListByMonthList != null && signListByMonthList.size() > 0) {
                DateToCalender(signListByMonthList);
            } else {
                getList(searchLeftDate);
            }
        } else if (i == R.id.calendarRight) {
            signInTimeList.clear();
            signOutTimeList.clear();
            signLeaveTimeList.clear();
            signAskForLeaveList.clear();
            signExceptionTimeList.clear();
            signListByMonthListCopy.clear();
            //点击下一月
            String rightYearAndmonth;
            //向右滑动,删除最左测得view,在最右侧添加一个view
            View leftView = mScrollLayout.getChildAt(0);
            mScrollLayout.removeView(leftView);
            mScrollLayout.addView(leftView, 2);
            calendaLeft = (CalendarView) mScrollLayout.getChildAt(0);
            calendar = (CalendarView) mScrollLayout.getChildAt(1);
            calendaRight = (CalendarView) mScrollLayout.getChildAt(2);
            Date curDate = StrToDate(yearAndmonth);
            rightYearAndmonth = calendar.clickRightMonth(curDate);
            yearAndmonth = rightYearAndmonth;
            String[] ya = rightYearAndmonth.split("-");
            String searchRightDate = CorrectDate(ya[0], ya[1]);
            calendarCenter.setText(ya[0] + "年" + ya[1] + "月");
            //右滑获取存储在Map中的list数据
            List<SignListByMonth> signListByMonthList = mSignListByMonthInfos.get(searchRightDate);
//            bianse();
            if (signListByMonthList != null && signListByMonthList.size() > 0) {
                DateToCalender(signListByMonthList);
            } else {
                getList(searchRightDate);
            }
        }
    }

    @Override
    public void OnViewChange(int view) {
        Log.d(TAG,"OnViewChange#view = " + view);
        signInTimeList.clear();
        signOutTimeList.clear();
        signLeaveTimeList.clear();
        signAskForLeaveList.clear();
        signExceptionTimeList.clear();
        signListByMonthListCopy.clear();


        if (view == 0) {

            //向左滑动,删除最右测得view,在最左侧添加一个view
            View rightView = mScrollLayout.getChildAt(2);
            mScrollLayout.removeView(rightView);
            mScrollLayout.addView(rightView, 0);
            calendaLeft = (CalendarView) mScrollLayout.getChildAt(0);
            calendar = (CalendarView) mScrollLayout.getChildAt(1);
            calendaRight = (CalendarView) mScrollLayout.getChildAt(2);
            String[] yearAndmonths = yearAndmonth.split("-");
            if (!"1".equals(yearAndmonths[1])) {
                int month = Integer.valueOf(yearAndmonths[1]);
                yearAndmonths[1] = String.valueOf(month - 1);
                yearAndmonth = yearAndmonths[0] + "-" + yearAndmonths[1];
            } else {
                yearAndmonths[1] = String.valueOf(12);
                int year = Integer.valueOf(yearAndmonths[0]) - 1;
                yearAndmonths[0] = String.valueOf(year);
                yearAndmonth = yearAndmonths[0] + "-" + yearAndmonths[1];
            }
            calendarCenter.setText(yearAndmonths[0] + "年" + yearAndmonths[1] + "月");
            Date curDate = StrToDate(yearAndmonth);
            calendar.moveToMonth(curDate);
            final String searchMoveLeftDate = CorrectDate(yearAndmonths[0], yearAndmonths[1]);
            //左滑获取存储在Map中的list数据
            List<SignListByMonth> signListByMonthList = mSignListByMonthInfos.get(searchMoveLeftDate);
//            bianse();
            if (signListByMonthList != null && signListByMonthList.size() > 0) {
                DateToCalender(signListByMonthList);
            } else {
                getList(searchMoveLeftDate);
            }
        } else if (view == 1) {
            String[] yearAndmonths = yearAndmonth.split("-");
            //向右滑动,删除最左测得view,在最右侧添加一个view
            View leftView = mScrollLayout.getChildAt(0);
            mScrollLayout.removeView(leftView);
            mScrollLayout.addView(leftView, 2);
            calendaLeft = (CalendarView) mScrollLayout.getChildAt(0);
            calendar = (CalendarView) mScrollLayout.getChildAt(1);
            calendaRight = (CalendarView) mScrollLayout.getChildAt(2);
            if (!"12".equals(yearAndmonths[1])) {
                int month = Integer.valueOf(yearAndmonths[1]);
                yearAndmonths[1] = String.valueOf(month + 1);
                yearAndmonth = yearAndmonths[0] + "-" + yearAndmonths[1];
            } else {
                yearAndmonths[1] = String.valueOf(1);
                int year = Integer.valueOf(yearAndmonths[0]) + 1;
                yearAndmonths[0] = String.valueOf(year);
                yearAndmonth = yearAndmonths[0] + "-" + yearAndmonths[1];
            }
            calendarCenter.setText(yearAndmonths[0] + "年" + yearAndmonths[1] + "月");
            Date curDate = StrToDate(yearAndmonth);
            calendar.moveToMonth(curDate);
            String searchMoveRightDate = CorrectDate(yearAndmonths[0], yearAndmonths[1]);
            List<SignListByMonth> signListByMonthList = mSignListByMonthInfos.get(searchMoveRightDate);
//            bianse();
            //右滑获取存储在Map中的list数据
            if (signListByMonthList != null && signListByMonthList.size() > 0) {
                DateToCalender(signListByMonthList);

            }else {
                getList(searchMoveRightDate);
            }
        }
    }

    public String CorrectDate(String year, String month) {
        String searchDate = "";
        StringBuilder sb = new StringBuilder();
        if (Integer.valueOf(month) < 10) {
            sb.append(year);
            sb.append("-0");
            sb.append(month);
            searchDate = sb.toString();
        } else {
            sb.append(year);
            sb.append("-");
            sb.append(month);
            searchDate = sb.toString();
        }
        return searchDate;
    }

    public static Date StrToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public void getList(final String date){
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        String username = bundle.getString("username");
        BmobQuery<SignIn> query = new BmobQuery<SignIn>();
        query.addWhereEqualTo("username",username);
        query.addWhereEqualTo("YearAndMonth",date);
        query.findObjects(new FindListener<SignIn>() {
            @Override
            public void done(List<SignIn> xxlist, BmobException e) {
                if (e==null){
                    for (SignIn signIn : xxlist){
                        SignListByMonth sighListByMonth = new SignListByMonth();
                        String signDate = signIn.getInTime();
                        sighListByMonth.setSignDate(signDate);
                        Integer signState = signIn.getSignState();
                        sighListByMonth.setSignStatus(signState);
                        signListByMonthListCopy.add(sighListByMonth);
                    }
                    Intent i = getIntent();
                    Bundle bundle = i.getExtras();
                    String username = bundle.getString("username");
                    BmobQuery<SignOut> query = new BmobQuery<SignOut>();
                    query.addWhereEqualTo("username",username);
                    query.addWhereEqualTo("YearAndMonth",date);
                    query.findObjects(new FindListener<SignOut>() {
                        @Override
                        public void done(List<SignOut> xlist, BmobException e) {
                            if (e==null){
                                for (SignOut signout : xlist){
                                    SignListByMonth signListByMonth = new SignListByMonth();
                                    String signDate = signout.getOutTime();
                                    signListByMonth.setSignDate(signDate);
                                    Integer signState = signout.getSignState();
                                    signListByMonth.setSignStatus(signState);
                                    signListByMonthListCopy2.add(signListByMonth);
                                }
                                if (signListByMonthListCopy.get(0).getSignStatus()==1&&signListByMonthListCopy2.get(0).getSignStatus()==1){
                                    signListByMonthListCopy.get(0).setSignStatus(1);
                                    DateToCalender(signListByMonthListCopy);
                                }else{
                                    signListByMonthListCopy.get(0).setSignStatus(2);
                                    DateToCalender(signListByMonthListCopy);
                                }
                            }
                        }
                    });
//                    DateToCalender(signListByMonthListCopy);
                }
            }
        });
    }

    public void DateToCalender(List<SignListByMonth> signListByMonthList) {
//        getList();
        for (int i = 0; i < signListByMonthList.size(); i++) {
            if (signListByMonthList.get(i).getSignStatus()==1) {
                signInTimeList.add(Integer.valueOf(signListByMonthList.get(i).getSignDate().substring(signListByMonthList.get(i).getSignDate().length() - 2, signListByMonthList.get(i).getSignDate().length())));
            } else if (signListByMonthList.get(i).getSignStatus()==2) {
                signExceptionTimeList.add(Integer.valueOf(signListByMonthList.get(i).getSignDate().substring(signListByMonthList.get(i).getSignDate().length() - 2, signListByMonthList.get(i).getSignDate().length())));
            } else if (signListByMonthList.get(i).getSignStatus()==3) {
                signOutTimeList.add(Integer.valueOf(signListByMonthList.get(i).getSignDate().substring(signListByMonthList.get(i).getSignDate().length() - 2, signListByMonthList.get(i).getSignDate().length())));
            } else if (signListByMonthList.get(i).getSignStatus()==4) {
                signLeaveTimeList.add(Integer.valueOf(signListByMonthList.get(i).getSignDate().substring(signListByMonthList.get(i).getSignDate().length() - 2, signListByMonthList.get(i).getSignDate().length())));
            } else if (signListByMonthList.get(i).getSignStatus()==5) {
                signAskForLeaveList.add(Integer.valueOf(signListByMonthList.get(i).getSignDate().substring(signListByMonthList.get(i).getSignDate().length() - 2, signListByMonthList.get(i).getSignDate().length())));
            }
        }
        calendar.setSignWrongList(signExceptionTimeList);
        calendar.setSignOutList(signOutTimeList);
        calendar.setSignInList(signInTimeList);
        calendar.setSignLeaveList(signLeaveTimeList);
        calendar.setSignAskForLeaveList(signAskForLeaveList);
        Log.d(TAG,"MonthCalendarActivity#signListByMonthList= " + signListByMonthList + " " + calendaLeft.getYearAndmonth());
        final String finalSearchDate = searchDate;
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putString("searchDate", finalSearchDate);
        message.setData(bundle);
        calendarHandler.sendMessage(message);
    }

    @Override
    public void OnItemClick(Date downDate) {
        if (Util.isNetWorkAvailable(this)) {
            Log.d(TAG,"MonthCalendarActivity #OnItemClick Date downDate = " + downDate);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//            System.out.println(signInTimeList);
//            System.out.print(signListByMonthListCopy);
//            Intent intent = new Intent(this, SignListByDayActivity.class);
//            intent.putExtra("signDay", format.format(downDate));
//            intent.putExtra("account", mUserId);
//            intent.putExtra("name", mName);
//            startActivity(intent);
        }
    }
}
