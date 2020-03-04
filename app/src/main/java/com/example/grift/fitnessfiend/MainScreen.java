package com.example.grift.fitnessfiend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Bundle;
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
    @BindView(R.id.fitness_scrn_text) TextView fitnessText;
    @BindView(R.id.gym_maps_scrn_text) TextView gymMapsText;
    @BindView(R.id.macros_scrn_text) TextView macrosText;
    @BindView(R.id.medicine_scrn_text) TextView medicineText;
    @BindView(R.id.recipe_scrn_text) TextView recipeText;

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
                        fitnessText.setText("Fitness");
                    }
                    if ((boolean)dataSnapshot.child("Gym").getValue()){
                        gymMapsText.setText("Gym Maps");
                    }
                    if ((boolean)dataSnapshot.child("Macros").getValue()){
                        macrosText.setText("Macros");
                    }
                    if ((boolean)dataSnapshot.child("Medicine").getValue()){
                        medicineText.setText("Medicine");
                    }
                    if ((boolean)dataSnapshot.child("Recipe").getValue()){
                        recipeText.setText("Recipe");
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
