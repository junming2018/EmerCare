package com.example.emercare.home;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emercare.R;

import java.util.ArrayList;

public class RecordParentAdapter extends RecyclerView.Adapter<RecordParentAdapter.ViewHolder> {
    Activity activity;
    ArrayList<RecordParentData> recordParentData;
    ArrayList<RecordChildData> recordChildData, recordGlucoseChildData = new ArrayList<>(), recordPressureChildData = new ArrayList<>();

    public RecordParentAdapter(Activity activity, ArrayList<RecordParentData> recordParentData, ArrayList<RecordChildData> recordChildData) {
        this.activity = activity;
        this.recordParentData = recordParentData;
        this.recordChildData = recordChildData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_record_parent_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvDate.setText(recordParentData.get(position).getRecordDate());

        String parentDate = recordParentData.get(position).getRecordDate();

        recordGlucoseChildData.clear();
        recordPressureChildData.clear();

        for (int i = 0; i < recordChildData.size(); i++) {
            String childDate = recordChildData.get(i).getRecordDate();

            if (childDate.equals(parentDate)) {

                if (recordChildData.get(i).getChildRecord().equals("Weight")) {
                    RecordChildAdapter recordChildAdapter = new RecordChildAdapter(recordChildData.get(i), activity);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
                    holder.rvChild.setLayoutManager(linearLayoutManager);
                    holder.rvChild.setAdapter(recordChildAdapter);
                }

                if (recordChildData.get(i).getChildRecord().equals("Blood Glucose")) {
                    if (recordChildData.get(i).getRecordPeriod().equals("Before Breakfast")) {
                        recordGlucoseChildData.add(recordChildData.get(i));
                    }
                    if (recordChildData.get(i).getRecordPeriod().equals("After Breakfast")) {
                        recordGlucoseChildData.add(recordChildData.get(i));
                    }
                    if (recordChildData.get(i).getRecordPeriod().equals("Before Lunch")) {
                        recordGlucoseChildData.add(recordChildData.get(i));
                    }
                    if (recordChildData.get(i).getRecordPeriod().equals("After Lunch")) {
                        recordGlucoseChildData.add(recordChildData.get(i));
                    }
                    if (recordChildData.get(i).getRecordPeriod().equals("Before Dinner")) {
                        recordGlucoseChildData.add(recordChildData.get(i));
                    }
                    if (recordChildData.get(i).getRecordPeriod().equals("After Dinner")) {
                        recordGlucoseChildData.add(recordChildData.get(i));
                    }
                    if (recordChildData.get(i).getRecordPeriod().equals("At Bedtime")) {
                        recordGlucoseChildData.add(recordChildData.get(i));
                    }
                    createChildRecyclerView(holder, recordGlucoseChildData, activity);
                }

                if (recordChildData.get(i).getChildRecord().equals("Blood Pressure")) {
                    if (recordChildData.get(i).getRecordPeriod().equals("Before Breakfast")) {
                        recordPressureChildData.add(recordChildData.get(i));
                    }
                    if (recordChildData.get(i).getRecordPeriod().equals("After Breakfast")) {
                        recordPressureChildData.add(recordChildData.get(i));
                    }
                    if (recordChildData.get(i).getRecordPeriod().equals("Before Lunch")) {
                        recordPressureChildData.add(recordChildData.get(i));
                    }
                    if (recordChildData.get(i).getRecordPeriod().equals("After Lunch")) {
                        recordPressureChildData.add(recordChildData.get(i));
                    }
                    if (recordChildData.get(i).getRecordPeriod().equals("Before Dinner")) {
                        recordPressureChildData.add(recordChildData.get(i));
                    }
                    if (recordChildData.get(i).getRecordPeriod().equals("After Dinner")) {
                        recordPressureChildData.add(recordChildData.get(i));
                    }
                    if (recordChildData.get(i).getRecordPeriod().equals("At Bedtime")) {
                        recordPressureChildData.add(recordChildData.get(i));
                    }
                    createChildRecyclerView(holder, recordPressureChildData, activity);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return recordParentData.size();
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        RecyclerView rvChild;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.home_list_tv_date);
            rvChild = itemView.findViewById(R.id.home_list_rv_child);
        }
    }

    public void createChildRecyclerView(ViewHolder holder, ArrayList<RecordChildData> recordChildDataList, Activity activity) {
        RecordChildAdapter recordChildAdapter = new RecordChildAdapter(recordChildDataList, activity);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        holder.rvChild.setLayoutManager(linearLayoutManager);
        holder.rvChild.setAdapter(recordChildAdapter);
    }
}