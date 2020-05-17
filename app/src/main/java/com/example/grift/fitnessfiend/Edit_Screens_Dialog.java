package com.example.grift.fitnessfiend;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class Edit_Screens_Dialog extends AppCompatDialogFragment {
    private CheckBox fitness;
    private CheckBox macros;
    private CheckBox recipe;
    private CheckBox gym_maps;
    private CheckBox reminders;
    private boolean isMacrosChecked = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.edit_screens_dialog, null);

        fitness = view.findViewById(R.id.fitness_chkbx);
        macros = view.findViewById(R.id.macros_chkbx);
        recipe = view.findViewById(R.id.recipe_chkbx);
        gym_maps = view.findViewById(R.id.gym_maps_chkbx);
        reminders = view.findViewById(R.id.reminders_chkbx);

        FirebaseApp.initializeApp(requireContext().getApplicationContext());
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference screens = FirebaseDatabase.getInstance().getReference().child("users").child(
                    FirebaseAuth.getInstance().getCurrentUser().getUid()).child("screens");
            screens.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if ((boolean)dataSnapshot.child("Fitness").getValue())
                        fitness.setChecked(true);
                    if ((boolean)dataSnapshot.child("Macros").getValue()) {
                        macros.setChecked(true);
                        isMacrosChecked = true;
                    }
                    if ((boolean)dataSnapshot.child("Recipe").getValue())
                        recipe.setChecked(true);
                    if ((boolean)dataSnapshot.child("Gym").getValue())
                        gym_maps.setChecked(true);
                    if ((boolean)dataSnapshot.child("Reminders").getValue())
                        reminders.setChecked(true);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            builder.setView(view)
                .setCancelable(true)
                .setTitle("Please choose which screens you would like to use.")
                .setNegativeButton("Cancel", (dialog, which) -> {
                })
                .setPositiveButton("Submit", (dialog, which) -> {
                    //save info in db, if its adding macros screen, allow user to enter goals (go to macros registration)
                    screens.child("Fitness").setValue(fitness.isChecked());
                    screens.child("Macros").setValue(macros.isChecked());
                    screens.child("Recipe").setValue(recipe.isChecked());
                    screens.child("Gym").setValue(gym_maps.isChecked());
                    screens.child("Reminders").setValue(reminders.isChecked());

                    if (macros.isChecked() && !isMacrosChecked) {
                        startActivity(new Intent(requireContext().getApplicationContext(), Registration_Macros.class));
                    }
                });
        }
        return builder.create();
    }
}