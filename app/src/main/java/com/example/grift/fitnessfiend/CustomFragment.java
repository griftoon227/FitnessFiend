package com.example.grift.fitnessfiend;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class CustomFragment extends Fragment {
    private static final int SHOW_SUB_ACTIVITY = 1;
    private FloatingActionButton fab;
    private DatabaseReference mReference;

    private List<MedicineNames> medicineNamesList = new ArrayList<>();
    private RecyclerView recyclerViewCustom;
    private MedicineAdapter mAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_custom, container, false);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            mReference = FirebaseDatabase.getInstance().getReference().child("users").child(
                    Objects.requireNonNull(email).substring(0, email.indexOf('@')));
        }

        recyclerViewCustom = view.findViewById(R.id.customNamesRV);

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
                Intent i = new Intent(getActivity().getApplication(), CustomReminderActivity.class);
                startActivity(i);
            }
        });

        recyclerViewCustom.setHasFixedSize(true);
        recyclerViewCustom.setLayoutManager(new LinearLayoutManager(getContext()));
        mReference.child("custom_screen").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String name = dataSnapshot.getKey();
                    MedicineNames names = new MedicineNames(name);
                    medicineNamesList.add(names);
                }
                mAdapter = new MedicineAdapter(getContext(), medicineNamesList);
                recyclerViewCustom.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                MedicineNames positionName = medicineNamesList.get(position);
                String child = positionName.getmMedicationName();
                mReference.child("custom_screen").child(child).removeValue();
                mAdapter.deleteItem(position);
                mAdapter.notifyDataSetChanged();

                Intent intent = new Intent(getContext(), MedicineBroadcast.class);
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);

            }
        });
        helper.attachToRecyclerView(recyclerViewCustom);
        return view;
    }

//    private void setUpRecyclerView() {
//
//        Query query = mReference.child("custom_screen");
//
//        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<MedicineNames>()
//                .setQuery(query, MedicineNames.class)
//                .build();
//
//        mAdapter = new MedicineFirebaseAdapter(options);
//        recyclerViewCustom.setHasFixedSize(true);
//        recyclerViewCustom.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerViewCustom.setAdapter(mAdapter);
//
//    }
//
//
}

