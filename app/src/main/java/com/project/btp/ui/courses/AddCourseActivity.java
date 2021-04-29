package com.project.btp.ui.courses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.btp.R;
import com.project.btp.data.model.Course;
import com.project.btp.data.model.LoggedInUser;
import com.project.btp.ui.login.LoginViewModel;

import java.util.HashMap;
import java.util.Map;

public class AddCourseActivity extends AppCompatActivity {

    private String userId;
    private String userType;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    private LoginViewModel loginViewModel;
    private CoursesViewModel coursesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        coursesViewModel = new ViewModelProvider(this).get(CoursesViewModel.class);

        userId = getIntent().getStringExtra("userId");
        userType = getIntent().getStringExtra("userType");

        final EditText courseId = findViewById(R.id.ac_courseId);
        final EditText sem = findViewById(R.id.ac_sem);
        final EditText slot = findViewById(R.id.ac_slot);
        final Button add_btn = findViewById(R.id.ac_submit);

        if (userType.equals("teacher")) {
            add_btn.setText(R.string.form_create_course);
            setTitle(R.string.title_create_course);
        }

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userType.equals("teacher")) {
                    String cId = courseId.getText().toString();
                    Course course = new Course(
                            cId,
                            userId,
                            sem.getText().toString(),
                            slot.getText().toString()
                            );

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("/courses/"+cId, course);
                    updates.put("/users/"+userId+"/courses/"+cId, course);

                    db.getReference().updateChildren(updates);
                    course.setNstudents(0);
                    coursesViewModel.insert(course);
                    Toast.makeText(getApplicationContext(), "Course Created Successfully", Toast.LENGTH_LONG).show();
                    setResult(Activity.RESULT_OK);
                } else if (userType.equals("student")) {
                    final String cId = courseId.getText().toString();

                    db.getReference("users").child(userId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Map<String, Object> updates = new HashMap<>();

                                    LoggedInUser user = snapshot.getValue(LoggedInUser.class);
                                    String key = db.getReference("courses").push().getKey();

                                    updates.put("/courses/"+cId+"/students/"+key, user);
                                    updates.put("/users/"+userId+"/courses/"+cId, true);

                                    db.getReference().updateChildren(updates);
                                    Toast.makeText(getApplicationContext(), "Registered for course", Toast.LENGTH_LONG).show();
                                    setResult(Activity.RESULT_OK);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.w("userFetchFail", error.toException());
                                    setResult(Activity.RESULT_CANCELED);
                                }
                            });

                }
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (userType.equals("student"))
            inflater.inflate(R.menu.menu_student, menu);
        else if (userType.equals("teacher"))
            inflater.inflate(R.menu.menu_teacher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_logout:
                loginViewModel.logout(this);
                finish();
                return true;
            case R.id.action_analytics:
                Intent intent = new Intent(getApplicationContext(), CoursesActivity.class);
                intent.putExtra("user", userId);
                intent.putExtra("userType", userType);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}