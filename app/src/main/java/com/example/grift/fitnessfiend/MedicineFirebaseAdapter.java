package com.example.grift.fitnessfiend;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;


public class MedicineFirebaseAdapter extends FirebaseRecyclerAdapter<MedicineNames, MedicineFirebaseAdapter.MedicineHolder> {

    private OnItemClickListener listener;

    public MedicineFirebaseAdapter(FirebaseRecyclerOptions<MedicineNames> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(MedicineHolder medicineHolder, int position, MedicineNames medicineNames) {

        medicineHolder.medicineName.setText(medicineNames.getmMedicationName());
    }

    @NonNull
    @Override
    public MedicineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.medicine_recyclerview_item, parent, false);
        return new MedicineHolder(v);
    }
    public interface OnItemClickListener {
        void onItemClick(DataSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    class MedicineHolder extends RecyclerView.ViewHolder {
        TextView medicineName;

        public MedicineHolder(@NonNull View itemView) {
            super(itemView);
            medicineName = itemView.findViewById(R.id.nameTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), MedicineReminderActivity.class);

                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);

                    }

                }
            });

        }
    }

}
