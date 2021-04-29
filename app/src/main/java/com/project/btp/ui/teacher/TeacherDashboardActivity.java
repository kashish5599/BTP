package com.project.btp.ui.teacher;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.btp.R;
import com.project.btp.data.model.Attendance;
import com.project.btp.data.model.User;
import com.project.btp.ui.courses.AddCourseActivity;
import com.project.btp.ui.courses.CoursesActivity;
import com.project.btp.ui.login.LoginViewModel;
import com.project.btp.ui.wifiDirect.WifiDirectBroadcastReceiver;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class TeacherDashboardActivity extends AppCompatActivity {

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;

    private final IntentFilter mIntentFilter = new IntentFilter();

//    FirebaseDatabase db = FirebaseDatabase.getInstance();
    private LoginViewModel loginViewModel;
    private AttendanceViewModel attendanceViewModel;

    private String teacherId;
    public static String startTime;
    public static String endTime;
    public boolean processOngoing = false;
    private Map<String, Boolean> studentsList = new HashMap<>();
    private String courseId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        teacherId = getIntent().getStringExtra("user");

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);

        attendanceViewModel = new ViewModelProvider(this).get(AttendanceViewModel.class);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
//        CoursesViewModel coursesViewModel = new ViewModelProvider(this).get(CoursesViewModel.class);
//
//        Spinner spinner = (Spinner) findViewById(R.id.teach_course_id);
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                R.layout.support_simple_spinner_dropdown_item,
//                coursesViewModel.getSavedCourses(this.teacherId).toArray(new String[0]));
//        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//        adapter.add("CourseId");
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(this);
        final EditText course_id = findViewById(R.id.teach_courseId);
        final Button attendBtn = findViewById(R.id.teach_attendBtn);
        final FloatingActionButton addCourseBtn = findViewById(R.id.teach_addCourseBtn);
        final TextView stateText = findViewById(R.id.stateText);

        addCourseBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddCourseActivity.class);
            intent.putExtra("userId", teacherId);
            intent.putExtra("userType", "teacher");
            startActivity(intent);
        });

//        attendBtn.setOnClickListener(v -> {
//            //TODO : Show list of students whose has been taken
//            if (processOngoing) {
//                timer("END");
//                stateText.setText("");
//                attendBtn.setText(R.string.form_attend_btn_recording);
//                mManager.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
//                    @Override
//                    public void onSuccess() {
//
//                    }
//
//                    @Override
//                    public void onFailure(int reason) {
//
//                    }
//                });
//            } else {
//                courseId = course_id.getText().toString();
//                changeDeviceName(courseId);
//                timer("START");
//                stateText.setText(R.string.ongoing_attendance);
//                attendBtn.setText(R.string.form_attend_btn_stop);
//                getRegisteredStudents();
//                processOngoing = true;
//
//                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }
//                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
//                    @Override
//                    public void onSuccess() {
//
//                    }
//
//                    @Override
//                    public void onFailure(int reason) {
//                        Log.d("Toast", Integer.toString(reason));
//                    }
//                });
//            }
//        });
    }

//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        if (position != 0) {
//            this.courseId = (String) parent.getSelectedItem();
//        }
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }

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

    private void getRegisteredStudents() {
        for (User student : attendanceViewModel.getStudents(courseId)) {
            studentsList.put(student.getUserId(), false);
        }
//        db.getReference("courses").child(courseId).child("students")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        allStudentsList.clear();
//                        for (DataSnapshot childSnapshot: snapshot.getChildren()) {
//                            String userId = childSnapshot.getValue(LoggedInUser.class).getUserId();
//                            allStudentsList.add(userId);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        System.out.println("Students read failed: " + error.getMessage());
//                    }
//                });
    }

    public void takeAttendance(HashSet<String> presentStudents) {
        for (String student : presentStudents)
            if (studentsList.containsKey(student)) studentsList.put(student, true);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = dateFormat.format(new Date());

        List<Attendance> attendanceList = new ArrayList<>();

        for (Map.Entry<String, Boolean> entry : studentsList.entrySet()) {
            attendanceList.add(new Attendance(courseId, entry.getKey(), entry.getValue(), date));
        }
        attendanceViewModel.insert(attendanceList);

//        Map<String, Boolean> students = new HashMap<>();
//        for (String student : studentsList) {
//            if (allStudentsList.contains(student)) students.put(student, true);
//        }
//
//        Map<String, Object> updates = new HashMap<>();
//        long time = (new Date()).getTime();
//        String attendanceKey = db.getReference("attendance").child(courseId).push().getKey();
//        String courseKey = db.getReference("courses").child(courseId).child("attendance").push().getKey();
//
//        updates.put("/attendance/"+courseId+"/"+attendanceKey+"/students", students);
//        updates.put("/attendance/"+courseId+"/"+attendanceKey+"/time", time);
//
//        updates.put("/courses/"+courseId+"/attendance/"+courseKey+"/attendId", attendanceKey);
//        updates.put("/courses/"+courseId+"/attendance/"+courseKey+"/teacherId", teacherId);
//        updates.put("/courses/"+courseId+"/attendance/"+courseKey+"/time", time);
//
//        for (String student : students.keySet()) {
//            updates.put("/users/"+student+"/attendance/"+courseKey+"/"+attendanceKey, true);
//        }
//
//        db.getReference().updateChildren(updates);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
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
                intent.putExtra("user", this.teacherId);
                intent.putExtra("userType", "teacher");
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}