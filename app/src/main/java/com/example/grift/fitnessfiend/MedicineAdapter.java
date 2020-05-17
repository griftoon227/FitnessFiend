package com.example.grift.fitnessfiend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    private Context context;
    private List<MedicineNames> medicineList;
    private MedicineAdapterListener listener;

    interface MedicineAdapterListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(MedicineAdapterListener listener) {
        this.listener = listener;
    }

    public MedicineAdapter(Context context, List<MedicineNames> medicineList) {
        this.medicineList = medicineList;
        this.context = context;
    }

    @Override
    public MedicineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medicine_recyclerview_item, parent, false);
        MedicineViewHolder viewHolder = new MedicineViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MedicineViewHolder holder, int position) {
        MedicineNames medicineNames = medicineList.get(position);
        holder.medicineNameTextView.setText(medicineNames.getmMedicationName());
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateData(List<MedicineNames> viewModels) {
        medicineList.clear();
        medicineList.addAll(viewModels);
        notifyDataSetChanged();
    }

    void deleteItem(int position) {
        medicineList.remove(position);
        notifyItemRemoved(position);
    }

    public class MedicineViewHolder extends RecyclerView.ViewHolder {
        public TextView medicineNameTextView;

        public MedicineViewHolder(View itemView) {
            super(itemView);
            medicineNameTextView = itemView.findViewById(R.id.nameTextView);
        }
    }
}
