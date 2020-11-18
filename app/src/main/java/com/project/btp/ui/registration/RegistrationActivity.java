package com.project.btp.ui.registration;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.project.btp.R;
import com.project.btp.data.CustomViewPager;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        CustomViewPager viewPager = findViewById(R.id.view_pager);
        TabLayout tabs = findViewById(R.id.tabs);
        for (int i=0; i<sectionsPagerAdapter.getCount(); i++) {
            tabs.addTab(tabs.newTab().setText(sectionsPagerAdapter.getPageTitle(i)));
        }
        viewPager.setAdapter(sectionsPagerAdapter);

//        // Disable Teacher's tab access by swiping
//        viewPager.setPagingEnabled(false);
//        // Disable Teacher's tab access by clicking
//        int tabPositionDisabled = 1;
//        LinearLayout tabStrip = ((LinearLayout) tabs.getChildAt(0));
//        tabStrip.setEnabled(false);
//        tabStrip.getChildAt(tabPositionDisabled).setClickable(false);

        tabs.setupWithViewPager(viewPager);
    }
}