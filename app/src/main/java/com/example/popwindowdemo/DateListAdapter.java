package com.example.popwindowdemo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DateListAdapter extends RecyclerView.Adapter<DateListAdapter.InnerHolder> {
    private List<String> mDateList = new ArrayList<>();
    private String selectedDate;
    private OnDateItemListener mDateItemListener;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_select_list, parent, false);
        return new InnerHolder(itemView);
    }

    //记录上一次的icon，方便隐藏
    private ImageView mLastIcon;

    @Override
    public void onBindViewHolder(@NonNull final InnerHolder holder, final int position) {
        //根据上次选择了的内容，将该项的icon设为可见(被选中)
        if (mDateList.get(position).equals(selectedDate)){
            holder.mDateIcon.setVisibility(View.VISIBLE);
            mLastIcon = holder.mDateIcon;
        }
        holder.setData(mDateList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastIcon != null){
                    mLastIcon.setVisibility(View.GONE);
                }
                mDateItemListener.onDateItemClick(position);
                holder.mDateIcon.setVisibility(View.VISIBLE);
                mLastIcon = holder.mDateIcon;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDateList.size();
    }

    public void setData(List<String> dateList) {
        this.mDateList.clear();
        this.mDateList.addAll(dateList);
        notifyDataSetChanged();
    }

    public void setSelectedDate(String date) {
        this.selectedDate = date;
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        //@BindView(R.id.item_budget_center_date_list_tv)
        public TextView mDateTitle;

        //@BindView(R.id.item_budget_center_date_list_iv)
        public ImageView mDateIcon;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            mDateTitle = itemView.findViewById(R.id.item_budget_center_date_list_tv);
            mDateIcon = itemView.findViewById(R.id.item_budget_center_date_list_iv);
            //ButterKnife.bind(this,itemView);
        }

        public void setData(String date) {
            mDateTitle.setText(date);
            mDateIcon.setImageResource(R.mipmap.date_selected_icon);
        }
    }

    public void setOnDateItemListener(OnDateItemListener listener){
        this.mDateItemListener = listener;
    }

    public interface OnDateItemListener {
        void onDateItemClick(int position);
    }
}
