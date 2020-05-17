package com.example.grift.fitnessfiend;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CustomFragment extends Fragment {
    private DatabaseReference mReference;
    private List<MedicineNames> medicineNamesList = new ArrayList<>();
    private MedicineAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_custom, container, false);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mReference = FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid());
        }

        RecyclerView recyclerViewCustom = view.findViewById(R.id.customNamesRV);
        medicineNamesList = new ArrayList<>();

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 ->
                startActivity(new Intent(requireActivity().getApplication(),
                        CustomReminderActivity.class)));

        mAdapter = new MedicineAdapter(getContext(), medicineNamesList);
        recyclerViewCustom.setHasFixedSize(true);
        recyclerViewCustom.setLayoutManager(new LinearLayoutManager(getContext()));
        medicineNamesList.clear();
        mReference.child("custom_screen").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String s) {
                MedicineNames names = new MedicineNames(snapshot.getKey());
                medicineNamesList.add(names);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
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
                AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);
            }
        });
        helper.attachToRecyclerView(recyclerViewCustom);
        return view;
    }
}

