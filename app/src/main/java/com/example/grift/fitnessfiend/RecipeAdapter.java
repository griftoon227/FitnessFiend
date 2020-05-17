package com.example.grift.fitnessfiend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private RecipeAdapterListener listener;
    private List<RecipeListItem> recipeList;

    interface RecipeAdapterListener {
        void onItemClick(int position);
    }

    void setOnItemClickListener(RecipeAdapterListener listener) {
        this.listener = listener;
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView tvRecipe;
        ImageView ivRecipe;

        RecipeViewHolder(View view) {
            super(view);
            tvRecipe = view.findViewById(R.id.textViewRecipe);
            ivRecipe = view.findViewById(R.id.ivRecipe);

            view.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });

        }
    }

    RecipeAdapter(List<RecipeListItem> recipeList) {
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_recipe, parent, false);

        return new RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        RecipeListItem recipe = recipeList.get(position);
        holder.tvRecipe.setText(recipe.getName());
        Picasso.get().load(recipe.getPicture()).into(holder.ivRecipe);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    /*public void setData(List<RecipeListItem> recipeList) {
        this.recipeList = recipeList;
        notifyDataSetChanged();
    }*/
}
