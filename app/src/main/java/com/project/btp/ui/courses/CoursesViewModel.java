package com.project.btp.ui.courses;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.btp.data.AppDataRepository;
import com.project.btp.data.model.Attendance;
import com.project.btp.data.model.Course;
import com.project.btp.data.model.User;

import java.util.List;

public class CoursesViewModel extends AndroidViewModel {
    private AppDataRepository mRepository;

    private final LiveData<List<Course>> mCourses;

    FirebaseDatabase db = FirebaseDatabase.getInstance();

    public CoursesViewModel (Application application) {
        super(application);
        mRepository = new AppDataRepository(application);
        mCourses = mRepository.getAllCourses();
    }

    public LiveData<List<Course>> getCourses() { return mCourses; }

    public void insert(Course course) {
        mRepository.insertCourse(course);
    }

    public LiveData<List<String>> getAttendanceDates(String courseId) {
        return mRepository.getAttendanceDates(courseId);
    }

    public LiveData<List<Attendance>> getAttendance(String courseId) {
        return mRepository.getAttendance(courseId);
    }

    public void getCourses(Context context, String userId) {
        DatabaseReference coursesRef = db.getReference("courses");
        coursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Course course = childSnapshot.getValue(Course.class);
                    assert course != null;
                    course.setNstudents(childSnapshot.child("students").getChildrenCount());
                    if (!course.getTeacherId().equals(userId)) continue;
                    insert(course);

                    for (DataSnapshot studentSnapshot : childSnapshot.child("students").getChildren()) {
                        User student = studentSnapshot.getValue(User.class);
                        assert student != null;
                        System.out.println(student.getUserId());

                        student.setCourseId(course.getCourseId());
                        mRepository.insertStudent(student);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteAttendance(String courseId) {
        mRepository.deleteAttendance(courseId);
    }

    //    public List<String> getSavedCourses(String teacherId) {
//        return mRepository.getSavedCourses(teacherId);
//    }
}
