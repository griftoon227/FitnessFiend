package com.example.grift.fitnessfiend;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration_Macros extends AppCompatActivity {
    //binds
    @BindView(R.id.protein_goals) EditText protein_goals;
    @BindView(R.id.fat_goals) EditText fat_goals;
    @BindView(R.id.carbs_goals) EditText carbs_goals;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @OnClick(R.id.submit_btn) void submitRegistration() {
        try {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                DatabaseReference goals = FirebaseDatabase.getInstance().getReference().child("users").child(
                        FirebaseAuth.getInstance().getCurrentUser().getUid()).child("macros_screen")
                        .child("goals");

                //calculate calorie goals
                double total_calories = (Double.parseDouble(protein_goals.getText().toString()) *
                        Constants.macros_protein_per_calorie) + (Double.parseDouble(fat_goals.getText().toString()) *
                        Constants.macros_fats_per_calorie) + (Double.parseDouble(carbs_goals.getText().toString()) *
                        Constants.macros_carbs_per_calorie);

                goals.child(Constants.goalsChildren[0]).setValue(Math.ceil(total_calories));
                goals.child(Constants.goalsChildren[1]).setValue(Double.parseDouble(protein_goals.getText().toString()));
                goals.child(Constants.goalsChildren[2]).setValue(Double.parseDouble(fat_goals.getText().toString()));
                goals.child(Constants.goalsChildren[3]).setValue(Double.parseDouble(carbs_goals.getText().toString()));

                startActivity(new Intent(Registration_Macros.this, MainScreen.class));
            }
        }
        catch(Exception ex) {
            Toast.makeText(getApplicationContext(), "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration__macros);
        ButterKnife.bind(this);
        FirebaseApp.initializeApp(this);
    }
}
