package com.example.grift.fitnessfiend;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainScreen extends AppCompatActivity {
    private String dateForDB = Constants.dateForDB;
    private boolean isNotCurrentDate = false;
    private boolean isFitnessChosen = false;
    private boolean isMacrosChosen = false;

    @BindView(R.id.fitness) ImageButton fitness;
    @BindView(R.id.macros) ImageButton macros;
    @BindView(R.id.gym_maps) ImageButton gym_maps;
    @BindView(R.id.recipes) ImageButton recipes;
    @BindView(R.id.reminders) ImageButton reminders;
    @BindView(R.id.settings) ImageButton settings;
    @BindView(R.id.weight) TextView weight;

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
        startActivity(new Intent(MainScreen.this, reminders.class));
    }
    @OnClick(R.id.recipes) void goToRecipes() {
        startActivity(new Intent(MainScreen.this, recipes.class));
    }
    @OnClick(R.id.settings) void goToSettings() {
        startActivity(new Intent(MainScreen.this, settings.class));
    }

    @OnClick(R.id.logout) void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainScreen.this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        ButterKnife.bind(this);
        FirebaseApp.initializeApp(this);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            //update the currentDate in the database for calculations
            FirebaseDatabase.getInstance().getReference().child("dates")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!Objects.equals(dataSnapshot.child("currentDate").getValue(String.class), Constants.dateForDB)) {
                        isNotCurrentDate = true;
                        FirebaseDatabase.getInstance().getReference().child("dates").child("currentDate").setValue(Constants.dateForDB);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });

            //grab the values from firebase and update the text accordingly
            DatabaseReference screens = FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()).child("screens");
            screens.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if ((boolean)dataSnapshot.child("Fitness").getValue()){
                        fitness.setVisibility(View.VISIBLE);
                        isFitnessChosen = true;
                    }
                    if ((boolean)dataSnapshot.child("Gym").getValue()){
                        gym_maps.setVisibility(View.VISIBLE);
                    }
                    if ((boolean)dataSnapshot.child("Macros").getValue()){
                        macros.setVisibility(View.VISIBLE);
                        isMacrosChosen = true;

                        //do macros database instantiation check
                        DatabaseReference macros = FirebaseDatabase.getInstance().getReference().child("users").child(
                            FirebaseAuth.getInstance().getCurrentUser().getUid()).child("macros_screen");
                        macros.child(dateForDB).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    //setup the database fields
                                    //breakfast
                                    macros.child(dateForDB).child(TimeOfDay.breakfast.toString()).child(Constants.mealChildren[0]).setValue(0);
                                    macros.child(dateForDB).child(TimeOfDay.breakfast.toString()).child(Constants.mealChildren[1]).setValue(0);
                                    macros.child(dateForDB).child(TimeOfDay.breakfast.toString()).child(Constants.mealChildren[2]).setValue(0);
                                    macros.child(dateForDB).child(TimeOfDay.breakfast.toString()).child(Constants.mealChildren[3]).setValue(0);
                                    macros.child(dateForDB).child(TimeOfDay.breakfast.toString()).child("food").child("placeholder").setValue(0);
                                    //lunch
                                    macros.child(dateForDB).child(TimeOfDay.lunch.toString()).child(Constants.mealChildren[0]).setValue(0);
                                    macros.child(dateForDB).child(TimeOfDay.lunch.toString()).child(Constants.mealChildren[1]).setValue(0);
                                    macros.child(dateForDB).child(TimeOfDay.lunch.toString()).child(Constants.mealChildren[2]).setValue(0);
                                    macros.child(dateForDB).child(TimeOfDay.lunch.toString()).child(Constants.mealChildren[3]).setValue(0);
                                    macros.child(dateForDB).child(TimeOfDay.lunch.toString()).child("food").child("placeholder").setValue(0);
                                    //dinner
                                    macros.child(dateForDB).child(TimeOfDay.dinner.toString()).child(Constants.mealChildren[0]).setValue(0);
                                    macros.child(dateForDB).child(TimeOfDay.dinner.toString()).child(Constants.mealChildren[1]).setValue(0);
                                    macros.child(dateForDB).child(TimeOfDay.dinner.toString()).child(Constants.mealChildren[2]).setValue(0);
                                    macros.child(dateForDB).child(TimeOfDay.dinner.toString()).child(Constants.mealChildren[3]).setValue(0);
                                    macros.child(dateForDB).child(TimeOfDay.dinner.toString()).child("food").child("placeholder").setValue(0);
                                    //other
                                    macros.child(dateForDB).child(TimeOfDay.other.toString()).child(Constants.mealChildren[0]).setValue(0);
                                    macros.child(dateForDB).child(TimeOfDay.other.toString()).child(Constants.mealChildren[1]).setValue(0);
                                    macros.child(dateForDB).child(TimeOfDay.other.toString()).child(Constants.mealChildren[2]).setValue(0);
                                    macros.child(dateForDB).child(TimeOfDay.other.toString()).child(Constants.mealChildren[3]).setValue(0);
                                    macros.child(dateForDB).child(TimeOfDay.other.toString()).child("food").child("placeholder").setValue(0);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                    }
                    if ((boolean)dataSnapshot.child("Reminders").getValue()){
                        reminders.setVisibility(View.VISIBLE);
                    }
                    if ((boolean)dataSnapshot.child("Recipe").getValue()){
                        recipes.setVisibility(View.VISIBLE);
                    }
                    settings.setVisibility(View.VISIBLE);

                    //line chart
                    if (isFitnessChosen || isMacrosChosen) {
                        //populate the weight chart if fitness and macro screens exist
                        DatabaseReference user_weights = FirebaseDatabase.getInstance().getReference().child("users").child(
                                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("weight_by_week");
                        user_weights.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int sunday = Math.round(Float.parseFloat(Objects.requireNonNull(dataSnapshot.child("sunday").getValue()).toString()));
                                int monday = Math.round(Float.parseFloat(Objects.requireNonNull(dataSnapshot.child("monday").getValue()).toString()));
                                int tuesday = Math.round(Float.parseFloat(Objects.requireNonNull(dataSnapshot.child("tuesday").getValue()).toString()));
                                int wednesday = Math.round(Float.parseFloat(Objects.requireNonNull(dataSnapshot.child("wednesday").getValue()).toString()));
                                int thursday = Math.round(Float.parseFloat(Objects.requireNonNull(dataSnapshot.child("thursday").getValue()).toString()));
                                int friday = Math.round(Float.parseFloat(Objects.requireNonNull(dataSnapshot.child("friday").getValue()).toString()));
                                int saturday = Math.round(Float.parseFloat(Objects.requireNonNull(dataSnapshot.child("saturday").getValue()).toString()));

                                ArrayList<Entry> entries = new ArrayList<>();
                                entries.add(new Entry(1, sunday));
                                entries.add(new Entry(2, monday));
                                entries.add(new Entry(3, tuesday));
                                entries.add(new Entry(4, wednesday));
                                entries.add(new Entry(5, thursday));
                                entries.add(new Entry(6, friday));
                                entries.add(new Entry(7, saturday));

                                //setup the chart
                                LineChart weightLineChart = findViewById(R.id.weight_line_chart);
                                LineDataSet dataSet = new LineDataSet(entries, "Weight Change Over Previous Seven Days");
                                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                                dataSets.add(dataSet);
                                LineData lineData = new LineData(dataSets);
                                weightLineChart.setData(lineData);
                                weightLineChart.invalidate();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), getString(R.string.main_screen_loading_error_msg), Toast.LENGTH_SHORT).show();
                }
            });


        }
        else {
            Toast.makeText(getApplicationContext(), R.string.register2_screen_error_msg_authFailed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            //do calculations for displaying the user's weight
            //do macros database instantiation check
            DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid());

            user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //grab the macros calories
                    int bm = dataSnapshot.child("macros_screen").child(dateForDB).child(TimeOfDay.breakfast.toString())
                            .child("calorie total").getValue(Integer.class) == null ? 0 : Objects.requireNonNull(dataSnapshot.child("macros_screen").child(dateForDB).child(TimeOfDay.breakfast.toString())
                            .child("calorie total").getValue(Integer.class));
                    int lm = dataSnapshot.child("macros_screen").child(dateForDB).child(TimeOfDay.lunch.toString())
                            .child("calorie total").getValue(Integer.class) == null ? 0 : Objects.requireNonNull(dataSnapshot.child("macros_screen").child(dateForDB).child(TimeOfDay.lunch.toString())
                            .child("calorie total").getValue(Integer.class));
                    int dm = dataSnapshot.child("macros_screen").child(dateForDB).child(TimeOfDay.dinner.toString())
                            .child("calorie total").getValue(Integer.class) == null ? 0 : Objects.requireNonNull(dataSnapshot.child("macros_screen").child(dateForDB).child(TimeOfDay.dinner.toString())
                            .child("calorie total").getValue(Integer.class));
                    int om = dataSnapshot.child("macros_screen").child(dateForDB).child(TimeOfDay.other.toString())
                            .child("calorie total").getValue(Integer.class) == null ? 0 : Objects.requireNonNull(dataSnapshot.child("macros_screen").child(dateForDB).child(TimeOfDay.other.toString())
                            .child("calorie total").getValue(Integer.class));

                    double totalMacrosCaloriesGained = (bm + lm + dm + om) / 3500.0;

                    //grab the fitness calories burned
                    String[] date = dateForDB.split("-");
                    if (date[1].charAt(0) == '0')
                        date[1] = date[1].substring(1);
                    if (date[2].charAt(0) == '0')
                        date[2] = date[2].substring(1);
                    String newDate = String.format("%s-%s-%s", date[0], date[1], date[2]);

                    String workoutType = dataSnapshot.child("fitness_screen").child(newDate).child("workoutType").getValue(
                            String.class);
                    double userWeight = Double.parseDouble(Objects.requireNonNull(Objects.requireNonNull(dataSnapshot.child("weight").getValue()).toString()));
                    int userAge = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(dataSnapshot.child("age").getValue()).toString()));
                    int userHeight = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(dataSnapshot.child("height").getValue()).toString()));
                    String userGender = Objects.requireNonNull(Objects.requireNonNull(dataSnapshot.child("gender").getValue()).toString());
                    int totalMinutes = 0;
                    for (DataSnapshot ds : dataSnapshot.child("fitness_screen").child(newDate).getChildren()) {
                        if (!Objects.equals(ds.getKey(), "workoutType")) {
                            totalMinutes += Objects.requireNonNull(ds.child("duration").getValue(Integer.class));
                            totalMinutes += Objects.requireNonNull(ds.child("restPeriod").getValue(Integer.class));
                        }
                    }

                    //calculate BMR
                    double BMR;
                    if (userGender.equals("Male")){
                        BMR = 88.362 + (13.397 * (userWeight*0.453592)) + (4.799 * (userHeight*2.54)) - (5.677 * userAge);
                    }
                    else {
                        BMR = 447.593 + (9.247 * (userWeight*0.453592)) + (3.098 * (userHeight*2.54)) - (4.330 * userAge);
                    }

                    double totalFitnessCaloriesLost = workoutType == null ? 0 : (userWeight * totalMinutes * MET_Values.valueOf(workoutType.toLowerCase().replaceAll(" ", "_")).getV() + BMR) / 3500.0;

                    userWeight = userWeight + totalMacrosCaloriesGained - totalFitnessCaloriesLost;

                    if (isNotCurrentDate) {
                        //save the adjusted weight to each place accordingly
                        user.child("weight").setValue(userWeight);
                        DayOfWeek dayOfWeek = DayOfWeek.from(LocalDate.now());
                        user.child("weight_by_week").child(dayOfWeek.name().toLowerCase()).setValue(userWeight);
                    }
                    weight.setText(String.format("Weight: %s pounds", Math.round(userWeight)));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }
    }
}
