package com.project.btp.ui.courses;

import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.project.btp.R;

public class CoursesActivity extends AppCompatActivity {

    ScrollView coursesListContainer;
    LinearLayout coursesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.stud_dash_toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        // TODO : Add course button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_course);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        coursesListContainer = findViewById(R.id.coursesListContainer);
        coursesList = findViewById(R.id.coursesList);

        showCourses();
    }

    protected void showCourses() {
        // TODO : make api call to show current courses
        int n_courses = 2;

        for (int i=0; i<n_courses; i++) {
            CardView course = newCourse();
            coursesList.addView(course);
        }
    }

    protected CardView newCourse() {
        CardView course = new CardView(getApplicationContext());
        return course;
    }
}