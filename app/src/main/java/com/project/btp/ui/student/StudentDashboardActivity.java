package com.project.btp.ui.student;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.project.btp.R;
import com.project.btp.ui.courses.AddCourseActivity;
import com.project.btp.ui.login.LoginViewModel;
import com.project.btp.ui.teacher.TeacherDashboardActivity;
import com.project.btp.ui.wifiDirect.WifiDirectBroadcastReceiver;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class StudentDashboardActivity extends AppCompatActivity {

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;

    private final IntentFilter mIntentFilter = new IntentFilter();

    FirebaseDatabase db = FirebaseDatabase.getInstance();

    public String studentId;
    public List allPeers = new ArrayList();
    public MutableLiveData<Integer> processState = new MutableLiveData<>();
    private LoginViewModel loginViewModel;
    private static final int LOC_PERM_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);
        Toolbar toolbar = findViewById(R.id.stud_dash_toolbar);
        setSupportActionBar(toolbar);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        final EditText courseId = findViewById(R.id.stud_courseId);
        final Button attendBtn = findViewById(R.id.stud_attendBtn);

        studentId = getIntent().getStringExtra("user");
        processState.setValue(0);

        processState.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == 2) {
                    mManager.stopPeerDiscovery(mChannel, null);
                    Toast.makeText(getApplicationContext(), "Attendance Recorded Successfully", Toast.LENGTH_LONG).show();
                }
            }
        });

        FloatingActionButton addCourseBtn = findViewById(R.id.add_course);
        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddCourseActivity.class);
                intent.putExtra("userId", studentId);
                intent.putExtra("userType", "student");
                startActivity(intent);
            }
        });

        attendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                    checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOC_PERM_CODE);
                    return;
                }
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        changeDeviceName(studentId);
                        processState.setValue(1);
                        Toast.makeText(StudentDashboardActivity.this, "Attendance process started", Toast.LENGTH_LONG).show();
                        Log.d("Toast", "Started wifi p2p");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.d("Toast", Integer.toString(reason));
                        Toast.makeText(StudentDashboardActivity.this, "Attendance did not start", Toast.LENGTH_SHORT).show();
                        Toast.makeText(StudentDashboardActivity.this, "Turn on Location", Toast.LENGTH_LONG).show();
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
        ActivityCompat.requestPermissions(StudentDashboardActivity.this,
                new String[] { permission },
                requestCode);
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
        inflater.inflate(R.menu.menu_student, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_logout:
                loginViewModel.logout(this);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}