package com.project.btp.ui.teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.btp.R;
import com.project.btp.data.model.LoggedInUser;
import com.project.btp.ui.courses.AddCourseActivity;
import com.project.btp.ui.wifiDirect.WifiDirectBroadcastReceiver;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class TeacherDashboardActivity extends AppCompatActivity {

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;

    private final IntentFilter mIntentFilter = new IntentFilter();

    FirebaseDatabase db = FirebaseDatabase.getInstance();

    private String teacherId;
    private HashSet<String> allStudentsList = new HashSet<String>();
    public static String startTime;
    public static String endTime;
    public boolean processOngoing = false;
    public List allPeers = new ArrayList();
    public TreeSet<String> studentsList = new TreeSet<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        final EditText courseId = findViewById(R.id.teach_courseId);
        final Button attendBtn = findViewById(R.id.teach_attendBtn);
        final FloatingActionButton addCourseBtn = findViewById(R.id.teach_addCourseBtn);
        final TextView stateText = findViewById(R.id.stateText);

        teacherId = getIntent().getStringExtra("user");

        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddCourseActivity.class);
                intent.putExtra("userId", teacherId);
                intent.putExtra("userType", "teacher");
                startActivity(intent);
            }
        });

        attendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO : Show list of students whose has been taken
                if (processOngoing) {
                    timer("END");
                    stateText.setText("");
                    attendBtn.setText(R.string.form_attend_btn_uploading);

                    takeAttendance(courseId.getText().toString(), teacherId);
                } else {
                    changeDeviceName(courseId.getText().toString());
                    timer("START");
                    stateText.setText(R.string.ongoing_attendance);
                    attendBtn.setText(R.string.form_attend_btn_stop);
                    getRegisteredStudents(courseId.getText().toString());
                    processOngoing = true;

                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFailure(int reason) {
                            Log.d("Toast", Integer.toString(reason));
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private void changeDeviceName(String name) {
        try {
            Method method = mManager.getClass().getMethod("setDeviceName",
                    WifiP2pManager.Channel.class, String.class, WifiP2pManager.ActionListener.class);

            method.invoke(mManager, mChannel, name, new WifiP2pManager.ActionListener() {
                public void onSuccess() {}

                public void onFailure(int reason) {}
            });
        } catch (Exception e)   {
            e.printStackTrace();
        }
    }

    private void getRegisteredStudents(String courseId) {
        db.getReference("courses").child(courseId).child("students")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allStudentsList.clear();
                        for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                            String userId = childSnapshot.getValue(LoggedInUser.class).getUserId();
                            allStudentsList.add(userId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("Students read failed: " + error.getMessage());
                    }
                });
    }

    private void takeAttendance(String courseId, String teacherId) {
        Map<String, Boolean> students = new HashMap<>();
        for (String student : studentsList) {
            if (allStudentsList.contains(student)) students.put(student, true);
        }

        Map<String, Object> updates = new HashMap<>();
        long time = (new Date()).getTime();
        String attendanceKey = db.getReference("attendance").child(courseId).push().getKey();
        String courseKey = db.getReference("courses").child(courseId).child("attendance").push().getKey();

        updates.put("/attendance/"+courseId+"/"+attendanceKey+"/students", students);
        updates.put("/attendance/"+courseId+"/"+attendanceKey+"/time", time);

        updates.put("/courses/"+courseId+"/attendance/"+courseKey+"/attendId", attendanceKey);
        updates.put("/courses/"+courseId+"/attendance/"+courseKey+"/teacherId", teacherId);
        updates.put("/courses/"+courseId+"/attendance/"+courseKey+"/time", time);

        for (String student : students.keySet()) {
            updates.put("/users/"+student+"/attendance/"+courseKey+"/"+attendanceKey, true);
        }

        db.getReference().updateChildren(updates);
    }

    public void timer(String state){

        Calendar now = Calendar.getInstance();
        int second = now.get(Calendar.SECOND);
        int millis = now.get(Calendar.MILLISECOND);

        String timer = second + "." + millis;
        Log.d("Toast", "Get Time: " + timer);

        if(state.equals("START")){
            startTime = timer;
        }
        else if(state.equals("END")){
            endTime = timer;
        }

    }
}