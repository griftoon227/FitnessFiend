package com.example.grift.fitnessfiend;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

public class Exercise extends Fragment {
    private ExerciseFragmentListener exerciseFragmentListener;

    public Exercise() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try { exerciseFragmentListener = (ExerciseFragmentListener)getContext(); }
        catch (ClassCastException e) { throw new ClassCastException(context.toString() + "Must implement ExerciseFragmentListener."); }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);

        assert getArguments() != null;
        String exerciseName = requireArguments().getString("exerciseName");
        int sets = Integer.parseInt(Objects.requireNonNull(requireArguments().getString("sets")));
        int reps = Integer.parseInt(Objects.requireNonNull(requireArguments().getString("reps")));
        int duration = Integer.parseInt(Objects.requireNonNull(requireArguments().getString("duration")));
        int restPeriod = Integer.parseInt(Objects.requireNonNull(requireArguments().getString("restPeriod")));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(5, 2, 5, 2);
        (view.findViewById(R.id.exercise_layout)).setLayoutParams(params);

        ((TextView)view.findViewById(R.id.exercise)).setText(exerciseName);
        ((TextView)view.findViewById(R.id.sets)).setText(String.format("  %s set(s)", sets));
        ((TextView)view.findViewById(R.id.reps)).setText(String.format("  %s  rep(s)", reps));
        ((TextView)view.findViewById(R.id.duration)).setText(String.format("  %s minute(s)", duration));
        ((TextView)view.findViewById(R.id.rest_period)).setText(String.format("  %s minute(s)", restPeriod));

        view.findViewById(R.id.remove).setOnClickListener(v -> exerciseFragmentListener.getFragmentExercise(exerciseName));

        return view;
    }

    public interface ExerciseFragmentListener {
        void getFragmentExercise(String exerciseName);
    }
}
