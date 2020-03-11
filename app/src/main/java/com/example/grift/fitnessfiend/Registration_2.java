package com.example.grift.fitnessfiend;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Registration_2 extends AppCompatActivity {
    //instance variables
    final float unselected = 1.0f;
    final float selected = 0.3f;

    @BindView(R.id.fitness) ImageButton fitness_pic;
    @BindView(R.id.macros) ImageButton macros_pic;
    @BindView(R.id.gym_maps) ImageButton gym_maps_pic;
    @BindView(R.id.recipes) ImageButton recipes_pic;
    @BindView(R.id.reminders) ImageButton reminders_pic;

    @OnClick(R.id.fitness) public void selectFitness() {
        if (fitness_pic.getAlpha() == selected)
            fitness_pic.setAlpha(unselected);
        else
            fitness_pic.setAlpha(selected);
    }
    @OnClick(R.id.macros) public void selectMacros() {
        if (macros_pic.getAlpha() == selected)
            macros_pic.setAlpha(unselected);
        else
            macros_pic.setAlpha(selected);
    }
    @OnClick(R.id.gym_maps) public void selectGym() {
        if (gym_maps_pic.getAlpha() == selected)
            gym_maps_pic.setAlpha(unselected);
        else
            gym_maps_pic.setAlpha(selected);
    }
    @OnClick(R.id.recipes) public void selectRecipe() {
        if (recipes_pic.getAlpha() == selected)
            recipes_pic.setAlpha(unselected);
        else
            recipes_pic.setAlpha(selected);
    }
    @OnClick(R.id.reminders) public void selectReminder() {
        if (reminders_pic.getAlpha() == selected)
            reminders_pic.setAlpha(unselected);
        else
            reminders_pic.setAlpha(selected);
    }
    @OnClick(R.id.submit_btn) public void submit() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            DatabaseReference screens = FirebaseDatabase.getInstance().getReference().child("users").child(
                Objects.requireNonNull(email).substring(0, email.indexOf('@'))).child("screens");
            screens.child("Fitness").setValue(fitness_pic.getAlpha() == selected);
            screens.child("Gym").setValue(gym_maps_pic.getAlpha() == selected);
            screens.child("Macros").setValue(macros_pic.getAlpha() == selected);
            screens.child("Reminders").setValue(reminders_pic.getAlpha() == selected);
            screens.child("Recipe").setValue(recipes_pic.getAlpha() == selected);
            startActivity(new Intent(Registration_2.this, MainScreen.class));
        }
        else {
            Toast.makeText(getApplicationContext(), R.string.register2_screen_error_msg_authFailed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_2);
        ButterKnife.bind(this);
        FirebaseApp.initializeApp(Registration_2.this);
    }
}
