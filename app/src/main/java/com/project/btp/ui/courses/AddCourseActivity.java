package com.project.btp.ui.courses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.btp.R;
import com.project.btp.data.model.LoggedInUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddCourseActivity extends AppCompatActivity {

    private String userId;
    private String userType;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

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
                            sem.getText().toString(),
                            slot.getText().toString(),
                            userId);

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("/courses/"+cId, course);
                    updates.put("/users/"+userId+"/courses/"+cId, course);

                    db.getReference().updateChildren(updates);

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

}