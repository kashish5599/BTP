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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void upload(Context context, Course course) {
        String courseId = course.getCourseId();
        List<String> uniqDates = mRepository.getAttendanceDates(courseId);

        if (uniqDates.size() == 0) {
            Toast.makeText(context, "Already uploaded", Toast.LENGTH_SHORT).show();
        } else {
            Map<String, Object> updates = new HashMap<>();

            for (String date : uniqDates) {
                List<Attendance> attendanceList = mRepository.getAttendance(course.getCourseId(), date);
                Map<String, Boolean> studentAttendance = new HashMap<>();
                for (Attendance attendance : attendanceList)
                    studentAttendance.put(attendance.getStudentId(), attendance.getPresent());

                String dbAttendKey = db.getReference("attendance").child(courseId).push().getKey();
                String dbCourseKey = db.getReference("courses").child(courseId).child("attendance").push().getKey();

                updates.put("/attendance/" + courseId + "/" + dbAttendKey + "/students", studentAttendance);
                updates.put("/attendance/" + courseId + "/" + dbAttendKey + "/time", date);
                updates.put("/courses/" + courseId + "/attendance/" + dbCourseKey + "/attendId", dbAttendKey);
                updates.put("/courses/" + courseId + "/attendance/" + dbCourseKey + "/time", date);
                for (Attendance attendance : attendanceList)
                    updates.put("/users/" + attendance.getStudentId() + "/attendance/" + dbCourseKey + "/" + dbAttendKey, attendance.getPresent());
            }
            db.getReference().updateChildren(updates);

            mRepository.deleteAttendance(courseId);

            Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public void getCourses(Context context) {
        DatabaseReference coursesRef = db.getReference("courses");
        coursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Course course = childSnapshot.getValue(Course.class);
                    assert course != null;
                    course.setNstudents(childSnapshot.child("students").getChildrenCount());
                    insert(course);

                    for (DataSnapshot studentSnapshot : childSnapshot.child("students").getChildren()) {
                        User student = studentSnapshot.getValue(User.class);
                        assert student != null;
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

//    public List<String> getSavedCourses(String teacherId) {
//        return mRepository.getSavedCourses(teacherId);
//    }
}
