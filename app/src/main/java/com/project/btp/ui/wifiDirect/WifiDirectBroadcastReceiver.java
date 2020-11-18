package com.project.btp.ui.wifiDirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;

import com.project.btp.ui.student.StudentDashboardActivity;
import com.project.btp.ui.teacher.TeacherDashboardActivity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private TeacherDashboardActivity teachActivity;
    private StudentDashboardActivity studActivity;

    public List peers = new ArrayList();

    public WifiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, TeacherDashboardActivity teachActivity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.teachActivity = teachActivity;
    }

    public WifiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, StudentDashboardActivity studActivity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.studActivity = studActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi is enabled
            } else {
                // Wifi is not enabled
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (manager != null) {
                manager.requestPeers(channel, peerListener);
            }
        }
    }

    private WifiP2pManager.PeerListListener peerListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peersList) {
            if (teachActivity != null) {
                teachActivity.allPeers.clear();
                teachActivity.allPeers.addAll(peersList.getDeviceList());

                peers = teachActivity.allPeers;
                System.out.println(peers);

                for (int i=0; i<peers.size(); i++) {
                    WifiP2pDevice device = (WifiP2pDevice) peers.get(i);

                    System.out.println(device.deviceName);

                    teachActivity.studentsList.add(device.deviceName);
                }

                if (peers.size() == 0) return;

                System.out.println("Peers added");
                // TODO : Add method to display attendance count
            } else if (studActivity != null) {
                studActivity.allPeers.clear();
                studActivity.allPeers.addAll(peersList.getDeviceList());

                peers = studActivity.allPeers;
                System.out.println(peers);

                for (int i=0; i<peers.size(); i++) {
                    WifiP2pDevice device = (WifiP2pDevice) peers.get(i);

                    System.out.println(device.deviceName);
                    if (studActivity.studentId.equals(device.deviceName)) {
                        studActivity.processState.setValue(2);
                    }
                }

                if (peers.size() == 0) return;
            }
        }
    };
}
