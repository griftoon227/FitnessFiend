package com.example.grift.fitnessfiend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainScreen extends AppCompatActivity {
    @BindView(R.id.fitness) ImageButton fitness;
    @BindView(R.id.macros) ImageButton macros;
    @BindView(R.id.gym_maps) ImageButton gym_maps;
    @BindView(R.id.recipes) ImageButton recipes;
    @BindView(R.id.reminders) ImageButton reminders;
    @BindView(R.id.settings) ImageButton settings;

    @OnClick(R.id.fitness) void goToFitness() {
        startActivity(new Intent(MainScreen.this, Fitness.class));
    }
    @OnClick(R.id.macros) void goToMacros() {
        startActivity(new Intent(MainScreen.this, Macros.class));
    }
    @OnClick(R.id.gym_maps) void goToMaps() {
        startActivity(new Intent(MainScreen.this, gym_maps.class));
    }
    @OnClick(R.id.reminders) void goToReminders() {
        startActivity(new Intent(MainScreen.this, RemindersActivity.class));
    }
    @OnClick(R.id.recipes) void goToRecipes() {
        startActivity(new Intent(MainScreen.this, recipes.class));
    }
    @OnClick(R.id.settings) void goToSettings() {
        startActivity(new Intent(MainScreen.this, settings.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        ButterKnife.bind(this);
        FirebaseApp.initializeApp(this);

        //grab the values from firebase and update the text accordingly
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            DatabaseReference screens = FirebaseDatabase.getInstance().getReference().child("users").child(
                Objects.requireNonNull(email).substring(0, email.indexOf('@'))).child("screens");
            screens.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if ((boolean)dataSnapshot.child("Fitness").getValue()){
                        fitness.setVisibility(View.VISIBLE);
                    }
                    if ((boolean)dataSnapshot.child("Gym").getValue()){
                        gym_maps.setVisibility(View.VISIBLE);
                    }
                    if ((boolean)dataSnapshot.child("Macros").getValue()){
                        macros.setVisibility(View.VISIBLE);
                    }
                    if ((boolean)dataSnapshot.child("Reminders").getValue()){
                        reminders.setVisibility(View.VISIBLE);
                    }
                    if ((boolean)dataSnapshot.child("Recipe").getValue()){
                        recipes.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), R.string.register2_screen_error_msg_authFailed, Toast.LENGTH_SHORT).show();
        }

    }
}
