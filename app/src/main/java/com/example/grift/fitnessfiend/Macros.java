package com.example.grift.fitnessfiend;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Macros extends AppCompatActivity {
    //binds
    @BindView(R.id.date) TextView date;
    @BindView(R.id.currentCaloriesForToday) TextView currentCaloriesForToday;
    @BindView(R.id.currentProteinForToday) TextView currentProteinForToday;
    @BindView(R.id.currentFatsForToday) TextView currentFatsForToday;
    @BindView(R.id.currentCarbsForToday) TextView currentCarbsForToday;
    @BindView(R.id.goalCaloriesForToday) TextView goalCaloriesForToday;
    @BindView(R.id.goalProteinForToday) TextView goalProteinForToday;
    @BindView(R.id.goalFatsForToday) TextView goalFatsForToday;
    @BindView(R.id.goalCarbsForToday) TextView goalCarbsForToday;

    @OnClick(R.id.history_button) void goToHistory() {
        startActivity(new Intent(Macros.this, macros_history.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macros);
        ButterKnife.bind(this);
        FirebaseApp.initializeApp(this);
        date.setText(DateFormat.getDateInstance().format(Calendar.getInstance().getTime()));

        //setup navigation
        findViewById(R.id.main_layout).setOnTouchListener(new NavigationSwipe(this) {
            public void onSwipeRight() {
                startActivity(new Intent(Macros.this, MainScreen.class));
            }
            public void onSwipeLeft() { }
            public void onSwipeTop() { }
            public void onSwipeBottom() { }
        });

        //calculate and set the current calories for the day
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference macros = FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()).child("macros_screen");
            String currentDate = LocalDate.now().toString();
            macros.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(currentDate).hasChildren()) {
                        int calories = 0, protein = 0, fats = 0, carbs = 0;

                        //calories
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.breakfast.toString()).child(Constants.mealChildren[0]).getValue(Integer.class) == null))
                            calories += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.breakfast.toString()).child(Constants.mealChildren[0]).getValue(Integer.class));
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.lunch.toString()).child(Constants.mealChildren[0]).getValue(Integer.class) == null))
                            calories += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.lunch.toString()).child(Constants.mealChildren[0]).getValue(Integer.class));
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.dinner.toString()).child(Constants.mealChildren[0]).getValue(Integer.class) == null))
                            calories += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.dinner.toString()).child(Constants.mealChildren[0]).getValue(Integer.class));
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.other.toString()).child(Constants.mealChildren[0]).getValue(Integer.class) == null))
                            calories += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.other.toString()).child(Constants.mealChildren[0]).getValue(Integer.class));
                        currentCaloriesForToday.setText(String.format("%s", calories));

                        //protein
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.breakfast.toString()).child(Constants.mealChildren[1]).getValue(Integer.class) == null))
                            protein += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.breakfast.toString()).child(Constants.mealChildren[1]).getValue(Integer.class));
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.lunch.toString()).child(Constants.mealChildren[1]).getValue(Integer.class) == null))
                            protein += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.lunch.toString()).child(Constants.mealChildren[1]).getValue(Integer.class));
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.dinner.toString()).child(Constants.mealChildren[1]).getValue(Integer.class) == null))
                            protein += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.dinner.toString()).child(Constants.mealChildren[1]).getValue(Integer.class));
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.other.toString()).child(Constants.mealChildren[1]).getValue(Integer.class) == null))
                            protein += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.other.toString()).child(Constants.mealChildren[1]).getValue(Integer.class));
                        currentProteinForToday.setText(String.format("%s", protein));

                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.breakfast.toString()).child(Constants.mealChildren[2]).getValue(Integer.class) == null))
                            fats += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.breakfast.toString()).child(Constants.mealChildren[2]).getValue(Integer.class));
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.lunch.toString()).child(Constants.mealChildren[2]).getValue(Integer.class) == null))
                            fats += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.lunch.toString()).child(Constants.mealChildren[2]).getValue(Integer.class));
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.dinner.toString()).child(Constants.mealChildren[2]).getValue(Integer.class) == null))
                            fats += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.dinner.toString()).child(Constants.mealChildren[2]).getValue(Integer.class));
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.other.toString()).child(Constants.mealChildren[2]).getValue(Integer.class) == null))
                            fats += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.other.toString()).child(Constants.mealChildren[2]).getValue(Integer.class));
                        currentFatsForToday.setText(String.format("%s", fats));

                        //carbs
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.breakfast.toString()).child(Constants.mealChildren[3]).getValue(Integer.class) == null))
                            carbs += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.breakfast.toString()).child(Constants.mealChildren[3]).getValue(Integer.class));
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.lunch.toString()).child(Constants.mealChildren[3]).getValue(Integer.class) == null))
                            carbs += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.lunch.toString()).child(Constants.mealChildren[3]).getValue(Integer.class));
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.dinner.toString()).child(Constants.mealChildren[3]).getValue(Integer.class) == null))
                            carbs += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.dinner.toString()).child(Constants.mealChildren[3]).getValue(Integer.class));
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.other.toString()).child(Constants.mealChildren[3]).getValue(Integer.class) == null))
                            carbs += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.other.toString()).child(Constants.mealChildren[3]).getValue(Integer.class));
                        currentCarbsForToday.setText(String.format("%s", carbs));

                        //goals
                        int goalCalories, goalProtein, goalFats, goalCarbs;

                        goalCalories = Objects.requireNonNull(dataSnapshot.child("goals").child(Constants.goalsChildren[0])
                                .getValue(Integer.class));
                        goalProtein = Objects.requireNonNull(dataSnapshot.child("goals").child(Constants.goalsChildren[1])
                                .getValue(Integer.class));
                        goalFats = Objects.requireNonNull(dataSnapshot.child("goals").child(Constants.goalsChildren[2])
                                .getValue(Integer.class));
                        goalCarbs = Objects.requireNonNull(dataSnapshot.child("goals").child(Constants.goalsChildren[3])
                                .getValue(Integer.class));

                        goalCaloriesForToday.setText(String.valueOf(goalCalories));
                        goalProteinForToday.setText(String.valueOf(goalProtein));
                        goalFatsForToday.setText(String.valueOf(goalFats));
                        goalCarbsForToday.setText(String.valueOf(goalCarbs));

                        if (calories >= goalCalories)
                            currentCaloriesForToday.setTextColor(Color.GREEN);
                        if (protein >= goalProtein)
                            currentProteinForToday.setTextColor(Color.GREEN);
                        if (fats >= goalFats)
                            currentFatsForToday.setTextColor(Color.GREEN);
                        if (carbs >= goalCarbs)
                            currentCarbsForToday.setTextColor(Color.GREEN);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();

        macros_meal_fragment bf = (macros_meal_fragment) getSupportFragmentManager().findFragmentById(R.id.breakfast_fragment);
        macros_meal_fragment lf = (macros_meal_fragment) getSupportFragmentManager().findFragmentById(R.id.lunch_fragment);
        macros_meal_fragment df = (macros_meal_fragment) getSupportFragmentManager().findFragmentById(R.id.dinner_fragment);
        macros_meal_fragment of = (macros_meal_fragment) getSupportFragmentManager().findFragmentById(R.id.other_fragment);

        if (bf != null) bf.instantiateUI();
        if (lf != null) lf.instantiateUI();
        if (df != null) df.instantiateUI();
        if (of != null) of.instantiateUI();
    }
}