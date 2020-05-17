package com.example.grift.fitnessfiend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.MyViewHolder> {

    private List<String> ingredientList;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView  tvIngredient;

        MyViewHolder(View view) {
            super(view);
            tvIngredient = view.findViewById(R.id.textViewIngredient);
        }
    }

    IngredientAdapter(List<String> ingredientList) {
        this.ingredientList = ingredientList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_ingredient, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String name = ingredientList.get(position);

        holder.tvIngredient.setText(name);
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    public void setData(List<String> ingredientList) {
        this.ingredientList = ingredientList;
        notifyDataSetChanged();
    }

}
