package com.project.btp.ui.courses;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.project.btp.data.model.Course;

public class CourseListAdapter extends ListAdapter<Course, CourseCardHolder> {


    public interface OnItemClickListener {
        void onItemClick(Course course);
    }

    private final OnItemClickListener listener;

    public CourseListAdapter(@NonNull DiffUtil.ItemCallback<Course> diffCallback, OnItemClickListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return CourseCardHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseCardHolder holder, int position) {
        Course current = getItem(position);
        holder.bind(current, listener);
    }

    static class CourseDiff extends DiffUtil.ItemCallback<Course> {

        @Override
        public boolean areItemsTheSame(@NonNull Course oldItem, @NonNull Course newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Course oldItem, @NonNull Course newItem) {
            return oldItem.getCourseId().equals(newItem.getCourseId());
        }
    }
}
