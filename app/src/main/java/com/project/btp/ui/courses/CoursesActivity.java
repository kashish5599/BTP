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

import com.project.btp.R;
import com.project.btp.ui.login.LoginViewModel;
import com.project.btp.ui.student.StudentDashboardActivity;
import com.project.btp.ui.teacher.TeacherDashboardActivity;

public class CoursesActivity extends AppCompatActivity {

    private CoursesViewModel coursesViewModel;
    private LoginViewModel loginViewModel;

    String userType;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

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
                course -> coursesViewModel.upload(getApplicationContext(), course));
        coursesListView.setAdapter(adapter);
        coursesListView.setLayoutManager(new LinearLayoutManager(this));

        // Update cached copy of words in adapter
        coursesViewModel.getCourses().observe(this, adapter::submitList);
    }

    private void updateCourses() {
        coursesViewModel.getCourses(this);
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