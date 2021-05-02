package com.project.btp.ui.courses;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.project.btp.R;
import com.project.btp.data.model.Attendance;
import com.project.btp.ui.login.LoginViewModel;
import com.project.btp.ui.student.StudentDashboardActivity;
import com.project.btp.ui.teacher.TeacherDashboardActivity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CoursesActivity extends AppCompatActivity {

    private CoursesViewModel coursesViewModel;
    private LoginViewModel loginViewModel;

    String userType;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        FirebaseDatabase db = FirebaseDatabase.getInstance();

        coursesViewModel = new ViewModelProvider(this).get(CoursesViewModel.class);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        userType = getIntent().getStringExtra("userType");
        userId = getIntent().getStringExtra("user");

        RecyclerView coursesListView = findViewById(R.id.coursesList);
        final CourseListAdapter adapter = new CourseListAdapter(
                new CourseListAdapter.CourseDiff(),
                course -> {
                    coursesViewModel.getAttendance(course.getCourseId()).observe(this, attendance -> {
                        if (attendance != null) {
                            if (attendance.size() == 0) {
                                Toast.makeText(this, "Uploaded", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this, "Uploading", Toast.LENGTH_SHORT).show();

                                Map<String, Object> updates = new HashMap<>();
                                HashSet<String> uniqDates = new HashSet<>();
                                String courseId = course.getCourseId();

                                for (Attendance attendanceRow : attendance) {
                                    uniqDates.add(attendanceRow.getDate());
                                }
                                for (String date : uniqDates) {
                                    Map<String, Boolean> studentAttendance = new HashMap<>();
                                    for (Attendance attendanceRow : attendance)
                                        if (date.equals(attendanceRow.getDate()))
                                            studentAttendance.put(attendanceRow.getStudentId(), attendanceRow.getPresent());

                                    String dbAttendKey = db.getReference("attendance").child(courseId).push().getKey();
                                    String dbCourseKey = db.getReference("courses").child(courseId).child("attendance").push().getKey();

                                    updates.put("/attendance/" + courseId + "/" + dbAttendKey + "/students", studentAttendance);
                                    updates.put("/attendance/" + courseId + "/" + dbAttendKey + "/time", date);
                                    updates.put("/courses/" + courseId + "/attendance/" + dbCourseKey + "/attendId", dbAttendKey);
                                    updates.put("/courses/" + courseId + "/attendance/" + dbCourseKey + "/time", date);

                                    for (Map.Entry<String, Boolean> entry : studentAttendance.entrySet()) {
                                        String key = entry.getKey();
                                        Boolean value = entry.getValue();
                                        updates.put("/users/" + key + "/attendance/" + dbCourseKey + "/" + dbAttendKey, value);
                                    }
                                }
                                db.getReference().updateChildren(updates);

                                coursesViewModel.deleteAttendance(courseId);
                            }
                        }
                    });
                });
        coursesListView.setAdapter(adapter);
        coursesListView.setLayoutManager(new LinearLayoutManager(this));

        // Update cached copy of words in adapter
        coursesViewModel.getCourses().observe(this, adapter::submitList);
    }

    private void updateCourses() {
        coursesViewModel.getCourses(this, userId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_course, menu);
        return true;
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        Intent i = null;
        if (userType.equals("students")) {
            i = new Intent(this, StudentDashboardActivity.class);
        } else i = new Intent(this, TeacherDashboardActivity.class);

        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra("user", userId);

        return i;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_logout:
                loginViewModel.logout(this);
                finish();
                return true;
            case R.id.action_refresh:
                updateCourses();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}