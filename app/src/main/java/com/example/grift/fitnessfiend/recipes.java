package com.example.grift.fitnessfiend;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
public class recipes extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        RecipePagerAdapter recipePagerAdapter = new RecipePagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(recipePagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        findViewById(R.id.main_layout).setOnTouchListener(new NavigationSwipe(this) {
            public void onSwipeRight() {
                startActivity(new Intent(recipes.this, MainScreen.class));
            }
            public void onSwipeLeft() { }
            public void onSwipeTop() { }
            public void onSwipeBottom() { }
        });
    }
}