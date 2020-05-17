package com.example.grift.fitnessfiend;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
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

import java.util.Calendar;
import java.util.Objects;

public class macros_history extends AppCompatActivity {
    private Calendar calendar = Calendar.getInstance();
    //binds
    @BindView(R.id.date) TextView date;
    @BindView(R.id.total_cals) TextView total_cals;
    @BindView(R.id.total_protein) TextView total_protein;
    @BindView(R.id.total_fats) TextView total_fats;
    @BindView(R.id.total_carbs) TextView total_carbs;
    @BindView(R.id.met_goal_cals) TextView goal_cals;
    @BindView(R.id.met_goal_protein) TextView goal_protein;
    @BindView(R.id.met_goal_fats) TextView goal_fats;
    @BindView(R.id.met_goal_carbs) TextView goal_carbs;

    @OnClick(R.id.go_back_btn) void goBackToMacros() {
        startActivity(new Intent(macros_history.this, Macros.class));
    }

    @OnClick(R.id.date) void openDatePicker() {
        DatePickerDialog datePicker = new DatePickerDialog(macros_history.this, (view, year, month, day) -> {
            date.setText(String.format("%s/%s/%s", month + 1, day, year));
            initializeFirebaseData(day, month + 1, year);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        //initialize the date for the date picker to the current day
        String[] dateSplit = date.getText().toString().split("/");
        datePicker.updateDate(Integer.parseInt(dateSplit[2]), Integer.parseInt(dateSplit[0]) - 1, Integer.parseInt(dateSplit[1]));
        datePicker.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macros_history);
        ButterKnife.bind(this);
        FirebaseApp.initializeApp(this);

        //initialize date
        date.setText(String.format("%s/%s/%s", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR)));
        initializeFirebaseData(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
    }

    void initializeFirebaseData(int day, int month, int year) {
        String currentDate = String.format("%s-%s-%s", String.valueOf(year), month < 10 ? "0" + month : String.valueOf(month), day < 10 ? "0" + day : String.valueOf(day));
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference macros = FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()).child("macros_screen");
            macros.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
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

                        //protein
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.breakfast.toString()).child(Constants.mealChildren[1]).getValue(Integer.class) == null))
                            protein += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.breakfast.toString()).child(Constants.mealChildren[1]).getValue(Integer.class));
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.lunch.toString()).child(Constants.mealChildren[1]).getValue(Integer.class) == null))
                            protein += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.lunch.toString()).child(Constants.mealChildren[1]).getValue(Integer.class));
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.dinner.toString()).child(Constants.mealChildren[1]).getValue(Integer.class) == null))
                            protein += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.dinner.toString()).child(Constants.mealChildren[1]).getValue(Integer.class));
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.other.toString()).child(Constants.mealChildren[1]).getValue(Integer.class) == null))
                            protein += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.other.toString()).child(Constants.mealChildren[1]).getValue(Integer.class));

                        //fats
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.breakfast.toString()).child(Constants.mealChildren[2]).getValue(Integer.class) == null))
                            fats += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.breakfast.toString()).child(Constants.mealChildren[2]).getValue(Integer.class));
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.lunch.toString()).child(Constants.mealChildren[2]).getValue(Integer.class) == null))
                            fats += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.lunch.toString()).child(Constants.mealChildren[2]).getValue(Integer.class));
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.dinner.toString()).child(Constants.mealChildren[2]).getValue(Integer.class) == null))
                            fats += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.dinner.toString()).child(Constants.mealChildren[2]).getValue(Integer.class));
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.other.toString()).child(Constants.mealChildren[2]).getValue(Integer.class) == null))
                            fats += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.other.toString()).child(Constants.mealChildren[2]).getValue(Integer.class));

                        //carbs
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.breakfast.toString()).child(Constants.mealChildren[3]).getValue(Integer.class) == null))
                            carbs += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.breakfast.toString()).child(Constants.mealChildren[3]).getValue(Integer.class));
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.lunch.toString()).child(Constants.mealChildren[3]).getValue(Integer.class) == null))
                            carbs += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.lunch.toString()).child(Constants.mealChildren[3]).getValue(Integer.class));
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.dinner.toString()).child(Constants.mealChildren[3]).getValue(Integer.class) == null))
                            carbs += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.dinner.toString()).child(Constants.mealChildren[3]).getValue(Integer.class));
                        if (!(dataSnapshot.child(currentDate).child(TimeOfDay.other.toString()).child(Constants.mealChildren[3]).getValue(Integer.class) == null))
                            carbs += Objects.requireNonNull(dataSnapshot.child(currentDate).child(TimeOfDay.other.toString()).child(Constants.mealChildren[3]).getValue(Integer.class));

                        //check if goals are met
                        int goalCalories, goalProtein, goalFats, goalCarbs;
                        boolean cals_met_goal = false, protein_met_goal = false, fats_met_goal = false, carbs_met_goal = false;

                        goalCalories = Objects.requireNonNull(dataSnapshot.child("goals").child(Constants.goalsChildren[0])
                                .getValue(Integer.class));
                        goalProtein = Objects.requireNonNull(dataSnapshot.child("goals").child(Constants.goalsChildren[1])
                                .getValue(Integer.class));
                        goalFats = Objects.requireNonNull(dataSnapshot.child("goals").child(Constants.goalsChildren[2])
                                .getValue(Integer.class));
                        goalCarbs = Objects.requireNonNull(dataSnapshot.child("goals").child(Constants.goalsChildren[3])
                                .getValue(Integer.class));

                        if (calories >= goalCalories)
                            cals_met_goal = true;
                        if (protein >= goalProtein)
                            protein_met_goal = true;
                        if (fats >= goalFats)
                            fats_met_goal = true;
                        if (carbs >= goalCarbs)
                            carbs_met_goal = true;

                        //populate the view
                        total_cals.setText(String.format("%s %s", getString(R.string.total_calories_macros_history_lbl), calories));
                        goal_cals.setText(String.format("%s %s", getString(R.string.met_calorie_goal_macros_history_lbl), cals_met_goal ? "Yes" : "No"));
                        total_protein.setText(String.format("%s %sg", getString(R.string.total_protein_macros_history_lbl), protein));
                        goal_protein.setText(String.format("%s %s", getString(R.string.met_protein_goal_macros_history_lbl), protein_met_goal ? "Yes" : "No"));
                        total_fats.setText(String.format("%s %sg", getString(R.string.total_fats_macros_history_lbl), fats));
                        goal_fats.setText(String.format("%s %s", getString(R.string.met_fats_goal_macros_history_lbl), fats_met_goal ? "Yes" : "No"));
                        total_carbs.setText(String.format("%s %sg", getString(R.string.total_carbs_macros_history_lbl), carbs));
                        goal_carbs.setText(String.format("%s %s", getString(R.string.met_carbs_goal_macros_history_lbl), carbs_met_goal ? "Yes" : "No"));
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "There is no historical data for the selected date.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }
    }
}

