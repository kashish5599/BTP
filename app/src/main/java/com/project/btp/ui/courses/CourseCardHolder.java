package com.project.btp.ui.courses;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.btp.R;
import com.project.btp.data.model.Course;

public class CourseCardHolder extends RecyclerView.ViewHolder {

    private final TextView courseNameView;
    private final TextView studentCountView;
    private final FloatingActionButton uploadBtnView;

    private CourseCardHolder(View itemView) {
        super(itemView);
        courseNameView = itemView.findViewById(R.id.textCourseName);
        studentCountView = itemView.findViewById(R.id.student_count);
        uploadBtnView = itemView.findViewById(R.id.courseUploadBtn);
    }

    @SuppressLint("DefaultLocale")
    public void bind(Course course, CourseListAdapter.OnItemClickListener listener) {
        courseNameView.setText(course.getCourseId());
        studentCountView.setText(String.format("No of students:%d", course.getNstudents()));

//        if (!course.getCanUpload()) {
//            uploadBtnView.setVisibility(View.GONE);
//        }
        uploadBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    listener.onItemClick(course);
                }
        });
    }

    static CourseCardHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.course_card, parent, false);
        return new CourseCardHolder(view);
    }
}
