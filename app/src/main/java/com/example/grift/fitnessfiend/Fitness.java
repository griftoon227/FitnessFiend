package com.example.grift.fitnessfiend;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Fitness extends AppCompatActivity implements Select_Workout_Dialog.WorkoutDialogListener {
    private Calendar calendar = Calendar.getInstance();
    private Select_Workout_Dialog dialog;

    @BindView(R.id.insert_title) TextView insertTitle;
    @BindView(R.id.date) TextView date;
    @BindView(R.id.workout_type) Spinner workoutType;
    @BindView(R.id.exercise_chosen) TextView exerciseSelected;
    @BindView(R.id.sets) EditText sets;
    @BindView(R.id.reps) EditText reps;
    @BindView(R.id.duration) EditText duration;
    @BindView(R.id.rest_period) EditText restPeriod;

    @OnClick(R.id.date) void openDatePicker() {
        DatePickerDialog datePicker = new DatePickerDialog(Fitness.this, (view, year, month, day) ->
            date.setText(String.format("%s/%s/%s", month + 1, day, year)), calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        //initialize the date for the date picker to the current day
        String[] dateSplit = date.getText().toString().split("/");
        datePicker.updateDate(Integer.parseInt(dateSplit[2]), Integer.parseInt(dateSplit[0]) - 1, Integer.parseInt(dateSplit[1]));
        datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePicker.show();
    }

    @OnClick(R.id.navigation_btn) void goToViewFitnessWorkoutScreen() {
        startActivity(new Intent(Fitness.this, ViewFitnessWorkout.class));
    }

    @OnClick(R.id.full_workout_list) void selectWorkout() {
        dialog = new Select_Workout_Dialog();
        dialog.onAttach(getApplicationContext());
        dialog.show(getSupportFragmentManager(), "Select Workout Dialog");
    }

    @OnClick(R.id.add_workout) void addExerciseToWorkout() {
        //add the data to firebase
        if(!reps.getText().toString().equals("") && !sets.getText().toString().equals("") &&
            !duration.getText().toString().equals("") && !restPeriod.getText().toString().equals("") &&
            !exerciseSelected.getText().toString().equals("")) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                DatabaseReference fitness = FirebaseDatabase.getInstance().getReference().child("users").child(
                    FirebaseAuth.getInstance().getCurrentUser().getUid()).child("fitness_screen");
                String[] currentDate = date.getText().toString().split("/");
                DatabaseReference refDate = fitness.child(String.format("%s-%s-%s", currentDate[2], currentDate[0], currentDate[1]));

                refDate.child("workoutType").setValue(workoutType.getSelectedItem().toString());

                //add the workout data piece
                refDate.child(exerciseSelected.getText().toString()).child("reps").setValue(Integer.parseInt(reps.getText()
                        .toString()));
                refDate.child(exerciseSelected.getText().toString()).child("sets").setValue(Integer.parseInt(sets.getText()
                        .toString()));
                refDate.child(exerciseSelected.getText().toString()).child("duration").setValue(Integer.parseInt(duration.getText()
                        .toString()));
                refDate.child(exerciseSelected.getText().toString()).child("restPeriod").setValue(Integer.parseInt(restPeriod.getText()
                        .toString()));

                //clear the fields
                exerciseSelected.setText("");
                reps.setText("");
                sets.setText("");
                duration.setText("");
                restPeriod.setText("");

                //toast the addition of the exercise
                Toast.makeText(getApplicationContext(), "Exercise successfully added.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Exercise could not be added.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "One or more fields was left blank.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness);
        ButterKnife.bind(this);
        FirebaseApp.initializeApp(this);

        //set the title
        insertTitle.setText(Html.fromHtml(getString(R.string.insert_workout_title_text)));

        //initialize date
        date.setText(String.format("%s/%s/%s", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR)));

        //setup navigation
        findViewById(R.id.main_layout).setOnTouchListener(new NavigationSwipe(this) {
            public void onSwipeRight() {
                startActivity(new Intent(Fitness.this, MainScreen.class));
            }
            public void onSwipeLeft() { }
            public void onSwipeTop() { }
            public void onSwipeBottom() { }
        });
    }

    @Override
    public void getExercise(String exercise) {
        //set the text view to this exercise
        exerciseSelected.setText(exercise);
        dialog.dismiss();
    }
}
