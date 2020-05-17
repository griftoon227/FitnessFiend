package com.example.grift.fitnessfiend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
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

public class ViewFitnessWorkout extends AppCompatActivity implements Exercise.ExerciseFragmentListener {
    private Calendar calendar = Calendar.getInstance();
    private String oldDate = "";

    @BindView(R.id.view_workouts_title) TextView viewWorkoutsTitle;
    @BindView(R.id.date) TextView date;
    @BindView(R.id.workout_type) TextView workoutType;
    @OnClick(R.id.go_back_btn) void goBackToFitness() {
        startActivity(new Intent(ViewFitnessWorkout.this, Fitness.class));
    }
    @OnClick(R.id.date) void openDatePicker() {
        DatePickerDialog datePicker = new DatePickerDialog(ViewFitnessWorkout.this, (view, year, month, day) -> {
            oldDate = date.getText().toString();
            date.setText(String.format("%s/%s/%s", month + 1, day, year));
            removeCurrentFragments();
            displayWorkoutInfo();
        }, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        //initialize the date for the date picker to the current day
        String[] dateSplit = date.getText().toString().split("/");
        datePicker.updateDate(Integer.parseInt(dateSplit[2]), Integer.parseInt(dateSplit[0]) - 1, Integer.parseInt(dateSplit[1]));
        datePicker.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_fitness_workout);
        ButterKnife.bind(this);
        FirebaseApp.initializeApp(this);

        //set the title
        viewWorkoutsTitle.setText(Html.fromHtml(String.format("<u>%s</u>", getString(R.string.view_workouts_title_text))));

        //initialize dates
        date.setText(String.format("%s/%s/%s", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR)));
        oldDate = date.getText().toString();

        displayWorkoutInfo();
    }

    @Override
    public void getFragmentExercise(String exerciseName) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference fitness = FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()).child("fitness_screen");
            String[] currentDate = date.getText().toString().split("/");
            DatabaseReference refDate = fitness.child(String.format("%s-%s-%s", currentDate[2], currentDate[0], currentDate[1]));

            //delete the exercise that is from the fragment, then delete the fragment
            refDate.child(exerciseName).removeValue();

            removeFragmentFromStack(exerciseName);
        }
    }

    private void displayWorkoutInfo() {
        //set the workout type text and create the fragments
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference fitness = FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()).child("fitness_screen");
            String[] currentDate = date.getText().toString().split("/");
            DatabaseReference refDate = fitness.child(String.format("%s-%s-%s", currentDate[2], currentDate[0], currentDate[1]));

            //workout type text
            refDate.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    workoutType.setText(dataSnapshot.child("workoutType").getValue(String.class));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });

            //creating the fragment
            refDate.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (!Objects.equals(ds.getKey(), "workoutType")) {
                                //add the keys to the bundle
                                Bundle bundle = new Bundle();
                                bundle.putString("exerciseName", ds.getKey());
                                bundle.putString("sets", Objects.requireNonNull(ds.child("sets").getValue()).toString());
                                bundle.putString("reps", Objects.requireNonNull(ds.child("reps").getValue()).toString());
                                bundle.putString("duration", Objects.requireNonNull(ds.child("duration").getValue())
                                        .toString());
                                bundle.putString("restPeriod", Objects.requireNonNull(ds.child("restPeriod").getValue())
                                        .toString());

                                FragmentManager fragmentManager = getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                Exercise fragment = new Exercise();
                                fragment.setArguments(bundle);
                                fragmentTransaction.add(R.id.fitness_layout, fragment, ds.getKey());
                                fragmentTransaction.commit();
                            }
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "There is no information listed for this date.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }
    }

    private void removeCurrentFragments() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference fitness = FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()).child("fitness_screen");
            String[] currentDate = oldDate.split("/");
            DatabaseReference refDate = fitness.child(String.format("%s-%s-%s", currentDate[2], currentDate[0], currentDate[1]));

            refDate.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (!Objects.equals(ds.getKey(), "workoutType")) {
                                removeFragmentFromStack(ds.getKey());
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }
    }

    private void removeFragmentFromStack(String exerciseName) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(exerciseName);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (fragment != null)   fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }
}
