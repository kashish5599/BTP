package com.project.btp.ui.teacher;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;

public class takeAttendance {

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;

    private final IntentFilter mIntentFilter = new IntentFilter();



    public static void attend(String courseId, String teacherId) {
        System.out.println(courseId + " " + teacherId);
    }

}
