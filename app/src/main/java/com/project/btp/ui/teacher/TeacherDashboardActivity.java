package com.project.btp.ui.teacher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.btp.R;
import com.project.btp.data.model.Attendance;
import com.project.btp.data.model.User;
import com.project.btp.ui.courses.AddCourseActivity;
import com.project.btp.ui.courses.CoursesActivity;
import com.project.btp.ui.login.LoginActivity;
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
    private static final int LOC_PERM_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        teacherId = getIntent().getStringExtra("user");

        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        attendanceViewModel = new ViewModelProvider(this).get(AttendanceViewModel.class);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

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

        attendBtn.setOnClickListener(v -> {
            //TODO : Show list of students whose has been taken
            if (processOngoing) {
                timer("END");
//                stateText.setText("");
                mManager.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        attendBtn.setText(R.string.form_take_attendance);
                        processOngoing = false;
                    }

                    @Override
                    public void onFailure(int reason) {

                    }
                });
            } else {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOC_PERM_CODE);
                    return;
                }
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        courseId = course_id.getText().toString();
                        changeDeviceName(courseId);
                        timer("START");
                        attendBtn.setText(R.string.form_attend_btn_stop);
                        getRegisteredStudents();
                        processOngoing = true;
                        Toast.makeText(TeacherDashboardActivity.this, "Attendance process started", Toast.LENGTH_LONG).show();
                        Log.d("Toast", "Started wifi p2p");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.d("Toast", Integer.toString(reason));
                        Toast.makeText(TeacherDashboardActivity.this, "Attendance did not start", Toast.LENGTH_SHORT).show();
                        Toast.makeText(TeacherDashboardActivity.this, "Turn on Location", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);
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

    public void checkPermission(String permission, int requestCode) {
        ActivityCompat.requestPermissions(TeacherDashboardActivity.this,
                new String[] { permission },
                requestCode);
    }

    private void getRegisteredStudents() {
        attendanceViewModel.getStudents(courseId).observe(this, students -> {
            for (User student : students) {
                System.out.println(student.getUserId());
            studentsList.put(student.getUserId(), false);
        }
        });
    }

    public void takeAttendance(HashSet<String> presentStudents) {
        int presenetStudents = 0;
        for (String student : presentStudents)
            if (studentsList.containsKey(student)) {
                studentsList.put(student, true);
                presenetStudents = presenetStudents + 1;
            }
        if (presenetStudents > 0) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String date = dateFormat.format(new Date());

            List<Attendance> attendanceList = new ArrayList<>();

            for (Map.Entry<String, Boolean> entry : studentsList.entrySet()) {
                attendanceList.add(new Attendance(courseId, entry.getKey(), entry.getValue(), date));
            }
            String completeMessage = attendanceList.size() + " students recorded.";
            System.out.println(completeMessage);
            Toast.makeText(TeacherDashboardActivity.this, completeMessage, Toast.LENGTH_LONG).show();
            attendanceViewModel.insert(attendanceList);
        } else {
            Toast.makeText(TeacherDashboardActivity.this, "Failed to record any attendance", Toast.LENGTH_LONG).show();
        }
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
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == LOC_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
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