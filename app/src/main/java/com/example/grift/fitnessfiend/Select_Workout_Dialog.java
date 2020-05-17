package com.example.grift.fitnessfiend;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class Select_Workout_Dialog extends AppCompatDialogFragment {
    private List<String> headers;
    private HashMap<String, List<String>> hashItems;
    private WorkoutDialogListener workoutDialogListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try { workoutDialogListener = (WorkoutDialogListener)getContext(); }
        catch(ClassCastException e) { throw new ClassCastException(context.toString() + "Must implement FoodDialogListener."); }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.select_workout_dialog, null);

        ExpandableListView workout_list_items = view.findViewById(R.id.workout_list_view);
        initializeData();
        ExpandableListAdapter adapter = new ExpandableListAdapter(getContext(), headers, hashItems);
        workout_list_items.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            String childItemText = ((TextView)v.findViewById(R.id.lblListItem)).getText().toString();
            workoutDialogListener.getExercise(childItemText);
            return true;
        });
        workout_list_items.setAdapter(adapter);

        builder.setView(view)
                .setCancelable(true)
                .setTitle("Choose an exercise to add to your workout list")
                .setNegativeButton("Cancel", (dialog, which) -> {
                });

        return builder.create();
    }

    private void initializeData() {
        headers = new ArrayList<>();
        headers.add("Legs");
        headers.add("Shoulders");
        headers.add("Biceps");
        headers.add("Triceps");
        headers.add("Forearms");
        headers.add("Chest");
        headers.add("Back");
        headers.add("Core");
        headers.add("Calisthenics");
        headers.add("Cardiovascular");

        hashItems = new HashMap<>();
        hashItems.put(headers.get(0), Arrays.asList(getResources().getStringArray(R.array.leg_resistance_training_exercise_list)));
        hashItems.put(headers.get(1), Arrays.asList(getResources().getStringArray(R.array.shoulder_resistance_training_exercise_list)));
        hashItems.put(headers.get(2), Arrays.asList(getResources().getStringArray(R.array.bicep_resistance_training_exercise_list)));
        hashItems.put(headers.get(3), Arrays.asList(getResources().getStringArray(R.array.tricep_resistance_training_exercise_list)));
        hashItems.put(headers.get(4), Arrays.asList(getResources().getStringArray(R.array.forearm_resistance_training_exercise_list)));
        hashItems.put(headers.get(5), Arrays.asList(getResources().getStringArray(R.array.chest_resistance_training_exercise_list)));
        hashItems.put(headers.get(6), Arrays.asList(getResources().getStringArray(R.array.back_resistance_training_exercise_list)));
        hashItems.put(headers.get(7), Arrays.asList(getResources().getStringArray(R.array.core_training_exercise_list)));
        hashItems.put(headers.get(8), Arrays.asList(getResources().getStringArray(R.array.calisthenics_training_exercise_list)));
        hashItems.put(headers.get(9), Arrays.asList(getResources().getStringArray(R.array.cardio_training_exercise_list)));
    }

    public interface WorkoutDialogListener {
        void getExercise(String exercise);
    }
}
