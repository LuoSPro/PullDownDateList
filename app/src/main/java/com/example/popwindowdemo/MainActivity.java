package com.example.popwindowdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements DateListAdapter.OnDateItemListener {

    //放时间段数据的集合
    private ArrayList<String> mDateList;
    //记录当前所选时间段的坐标，方便后面进行是否选中的判断
    private int mCurrentSelectedDate;
    //保存popWindow的最底部位置
    private int fromYDelta;
    //popWindow是否已经显示了
    private boolean isPopWindowShowing = false;

    //popWindow
    private PopupWindow mPopupWindow;
    //选择框出来时的灰色背景
    public View mGrayLayout;
    //工具栏
    public RelativeLayout mToolBar;
    public LinearLayout mToolBarBtn;
    public TextView mToolBarDateTv;
    public TextView mShowDateTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化View
        initView();
        //初始化数据
        initData();
        //初始化监听事件
        initListener();
    }

    private void initData() {
        //初始化选择第三个时间段（本月）
        mCurrentSelectedDate = 2;
        showSelectedDate();
    }

    private void initListener() {
        mToolBarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPopWindowShowing) {
                    //如果window已经展示出来，那么就隐藏
                    //否则展示window
                    showPopupWindow();
                }else {
                    mPopupWindow.dismiss();
                }
            }
        });

    }

    private void initView() {
        mGrayLayout = findViewById(R.id.gray_layout);
        mToolBarBtn = findViewById(R.id.tool_bar_ll);
        mToolBar = findViewById(R.id.tool_bar);
        mToolBarDateTv = findViewById(R.id.tool_bar_tv_date);
        mShowDateTv = findViewById(R.id.tv_show_date);
    }

    private void showPopupWindow() {
        initPopupWindowData();
        //popWindow的监听事件
        initPopupWindowListener();
        isPopWindowShowing = true;
    }

    private void initPopupWindowListener() {
        //popWindow消失的监听
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //设置window状态为隐藏
                isPopWindowShowing = false;
                mGrayLayout.setVisibility(View.GONE);
            }
        });
    }

    private void initPopupWindowData() {
        //需要映射的view
        final View contentView = LayoutInflater.from(this).inflate(R.layout.date_select_list, null);
        RecyclerView dateListRv = contentView.findViewById(R.id.budget_center_date_list);
        dateListRv.setLayoutManager(new LinearLayoutManager(this));
        DateListAdapter dateAdapter = new DateListAdapter();
        mDateList = new ArrayList<String>() {{
            add("今天");
            add("本周");
            add("本月");
            add("本季");
            add("本年");
        }};
        dateAdapter.setData(mDateList);
        dateAdapter.setSelectedDate(mDateList.get(mCurrentSelectedDate));
        //设置监听
        dateAdapter.setOnDateItemListener(this);
        dateListRv.setAdapter(dateAdapter);

        mPopupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        //如果设置了这两个属性，那么就不能有收回view的动画了，因为点击事件被popWindow消费了，我们的灰色层再也接收不到点击事件
        //将这两个属性设置为false，使点击popupwindow外面其他地方不会消失(因为点击事件在往下传的时候，会被其他的view所消耗)
        //mPopupWindow.setOutsideTouchable(true);
        //设置popupWindow.setFocusable(true); 这样才能让popupWindow里面的布局控件获得点击的事件，否则就被它的父亲view给拦截了。
        //mPopupWindow.setFocusable(true);
        //灰色背景可见
        mGrayLayout.setVisibility(View.VISIBLE);
        //获取popupwindow高度确定动画开始位置
        int contentHeight = ViewUtils.getViewMeasuredHeight(contentView);
        //在点击内容的下面展示
        mPopupWindow.showAsDropDown(mToolBar, 0, 0);
        fromYDelta = -contentHeight - 50;
        //开始动画
        mPopupWindow.getContentView().startAnimation(AnimationUtil.createInAnimation(this, fromYDelta));
    }

    @Override
    public void onDateItemClick(int position) {
        //隐藏选择（只有这里才能用动画的形式消失，不然其他地方都不能监听到点击事件，进而无法用动画）
        mPopupWindow.getContentView().startAnimation(AnimationUtil.createOutAnimation(this, fromYDelta));
        mPopupWindow.getContentView().postDelayed(new Runnable() {
            @Override
            public void run() {
                //popwindow隐藏
                mPopupWindow.dismiss();
            }
        }, AnimationUtil.ANIMATION_OUT_TIME);
        //如果选择的是上次已经选择了的时间段，就没必要更新
        if (mCurrentSelectedDate == position){
            return;
        }
        //选择了时间段，更新数据
        //更新时间段
        mCurrentSelectedDate = position;
        //更新tv
        mToolBarDateTv.setText(mDateList.get(position));
        showSelectedDate();
    }

    private void showSelectedDate() {
        if (mCurrentSelectedDate == 0){
            //今天
            refreshTV(DateUtils.getTodayStartTime(), DateUtils.getTodayEndTime());
        }else if (mCurrentSelectedDate == 1){
            //本周
            refreshTV(DateUtils.getCurrentWeekStartTimes(), DateUtils.getCurrentWeekEndTimes());
        }else if (mCurrentSelectedDate == 2){
            //本月
            refreshTV(DateUtils.getCurrentMonthStartTimes(), DateUtils.getCurrentMonthEndTimes());
        }else if (mCurrentSelectedDate == 3){
            //本季
            refreshTV(DateUtils.getCurrentQuarterStartTime(),DateUtils.getCurrentQuarterEndTime());
        }else if (mCurrentSelectedDate == 4){
            //本年
            refreshTV(DateUtils.getCurrentYearStartTime(), DateUtils.getCurrentYearEndTime());
        }
    }

    private void refreshTV(Date startTime,Date endTime){
        String startTimeStr = DateUtils.dateToString(startTime);
        String endTimeStr = DateUtils.dateToString(endTime);
        mShowDateTv.setText(startTimeStr + "\t\t\t--->\t\t\t" + endTimeStr);
    }
}
