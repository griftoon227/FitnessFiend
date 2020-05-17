package com.example.grift.fitnessfiend;

import android.os.Build;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.util.Objects;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.O)
final class Constants {
    static final String[] mealChildren = new String[] {
        "calorie total","protein total","fat total","carbs total"
    };
    static final String[] goalsChildren = new String[] {
        "calories_goal","protein_goal","fats_goal","carbs_goal"
    };

    static final int macros_protein_per_calorie = 4;
    static final int macros_carbs_per_calorie = 4;
    static final int macros_fats_per_calorie = 9;

    static final String dateForDB = LocalDate.now().toString();
}
