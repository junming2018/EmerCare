package com.example.emercare.resource;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emercare.R;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
    Activity mActivity;
    public List<CourseData> courseData;

    public CourseAdapter(List<CourseData> courseData, Activity mActivity) {
        this.mActivity = mActivity;
        this.courseData = courseData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_course_item, parent, false);
        return new ViewHolder(view, mActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAdapter.ViewHolder holder, int position) {
        holder.course = courseData.get(position).getCourse();
        holder.imgCourse.setImageResource(courseData.get(position).getCourseIcon());
        holder.tvName.setText(courseData.get(position).getCourseName());
    }

    @Override
    public int getItemCount() {
        return courseData.size();
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Activity mActivity;
        public ImageView imgCourse;
        public TextView tvName;
        public String course;

        public ViewHolder(View itemView, Activity mActivity) {
            super(itemView);
            this.mActivity = mActivity;
            imgCourse = itemView.findViewById(R.id.resource_list_img_course);
            tvName = itemView.findViewById(R.id.resource_list_tv_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), ResourceCourse.class);
            intent.putExtra("Course Name", tvName.getText().toString().trim());
            intent.putExtra("Course", course);
            view.getContext().startActivity(intent);
            mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }
}