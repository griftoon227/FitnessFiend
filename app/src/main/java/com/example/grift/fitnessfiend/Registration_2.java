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
    @BindView(R.id.fitness) ImageButton fitness_pic;
    @BindView(R.id.macros) ImageButton macros_pic;
    @BindView(R.id.gym_maps) ImageButton gym_maps_pic;
    @BindView(R.id.recipes) ImageButton recipes_pic;
    @BindView(R.id.medicine) ImageButton medicine_pic;

    @OnClick(R.id.fitness) public void selectFitness() {
        if (fitness_pic.getAlpha() == (float).3){
            fitness_pic.setAlpha((float)1);
        }
        else {
            fitness_pic.setAlpha((float).3);
        }
    }
    @OnClick(R.id.macros) public void selectMacros() {
        if (macros_pic.getAlpha() == (float).3){
            macros_pic.setAlpha((float)1);
        }
        else {
            macros_pic.setAlpha((float).3);
        }
    }
    @OnClick(R.id.gym_maps) public void selectGym() {
        if (gym_maps_pic.getAlpha() == (float).3){
            gym_maps_pic.setAlpha((float)1);
        }
        else {
            gym_maps_pic.setAlpha((float).3);
        }
    }
    @OnClick(R.id.recipes) public void selectRecipe() {
        if (recipes_pic.getAlpha() == (float).3){
            recipes_pic.setAlpha((float)1);
        }
        else {
            recipes_pic.setAlpha((float).3);
        }
    }
    @OnClick(R.id.medicine) public void selectMedicine() {
        if (medicine_pic.getAlpha() == (float).3){
            medicine_pic.setAlpha((float)1.0);
        }
        else {
            medicine_pic.setAlpha((float).3);
        }
    }
    @OnClick(R.id.submit_btn) public void submit() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            DatabaseReference screens = FirebaseDatabase.getInstance().getReference().child("users").child(
                Objects.requireNonNull(email).substring(0, email.indexOf('@'))).child("screens");
            screens.child("Fitness").setValue(fitness_pic.getAlpha() == (float).3);
            screens.child("Gym").setValue(gym_maps_pic.getAlpha() == (float).3);
            screens.child("Macros").setValue(macros_pic.getAlpha() == (float).3);
            screens.child("Medicine").setValue(medicine_pic.getAlpha() == (float).3);
            screens.child("Recipe").setValue(recipes_pic.getAlpha() == (float).3);
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
