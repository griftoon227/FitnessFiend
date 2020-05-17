package com.example.grift.fitnessfiend;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class gym_history_fragment extends Fragment {
    private CheckBox stillGoingCheckbox;

    public gym_history_fragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gym_history_fragment, container, false);

        FirebaseApp.initializeApp(requireContext());
        assert getArguments() != null;
        String gymLocation = getArguments().getString("GymLocation");

        //set the checkbox & its on click
        stillGoingCheckbox = view.findViewById(R.id.still_going_ckbox);
        stillGoingCheckbox.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                DatabaseReference gym_screen = FirebaseDatabase.getInstance().getReference().child("users").child(
                    FirebaseAuth.getInstance().getCurrentUser().getUid()).child("gym_screen");
                if (stillGoingCheckbox.isChecked())
                    gym_screen.child(Objects.requireNonNull(gymLocation)).child("end_date").setValue("null");
                else
                    gym_screen.child(Objects.requireNonNull(gymLocation)).child("end_date").setValue(Constants.dateForDB);
            }
        });

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference gym_screen = FirebaseDatabase.getInstance().getReference().child("users").child(
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid())).child("gym_screen");

            gym_screen.child(Objects.requireNonNull(gymLocation)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String startDate = dataSnapshot.child("start_date").getValue(String.class);
                        String endDate = dataSnapshot.child("end_date").getValue(String.class);
                        ((TextView)view.findViewById(R.id.gym_name)).setText(String.format("Gym: %s", gymLocation));
                        ((TextView)view.findViewById(R.id.start_date)).setText(String.format("Start Date: %s", startDate));
                        ((TextView)view.findViewById(R.id.end_date)).setText(String.format("End Date: %s", (Objects.equals(endDate, "null") ? "" : endDate)));
                        stillGoingCheckbox.setChecked(Objects.equals(endDate, "null"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }

        // Inflate the layout for this fragment
        return view;
    }
}
