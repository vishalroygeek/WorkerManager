package com.vishalroy.workermanager.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vishalroy.workermanager.Models.AttendanceRange;
import com.vishalroy.workermanager.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class AttendanceRangeAdapter extends RecyclerView.Adapter<AttendanceRangeAdapter.ViewHolder> {

    private List<AttendanceRange> attendanceRangeList;
    private Context context;

    public AttendanceRangeAdapter(List<AttendanceRange> attendanceRangeList){
        this.attendanceRangeList = attendanceRangeList;
    }

    @NonNull
    @Override
    public AttendanceRangeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.attendance_range_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceRangeAdapter.ViewHolder holder, int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        holder.setDate(sdf.format(attendanceRangeList.get(position).getDate()));
    }

    @Override
    public int getItemCount() {
        return attendanceRangeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView date;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            date = view.findViewById(R.id.date);
        }

        public void setDate(String dt){
            date.setText(dt);
        }
    }
}
