package com.example.grift.fitnessfiend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class reminders extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        //setup navigation
        findViewById(R.id.main_layout).setOnTouchListener(new NavigationSwipe(this) {
            public void onSwipeRight() {
                startActivity(new Intent(reminders.this, MainScreen.class));
            }
            public void onSwipeLeft() { }
            public void onSwipeTop() { }
            public void onSwipeBottom() { }
        });
    }
}
