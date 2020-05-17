package com.example.grift.fitnessfiend;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RecipeActivity extends AppCompatActivity {
    private RecipeListItem r;
    private List<String> ingredientList = new ArrayList<>();
    private List<RecipeListItem.Nutrient> nutrientList = new ArrayList<>();
    private CheckBox cbLiked;
    private DatabaseReference likedReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        FirebaseApp.initializeApp(this);
        //Controls
        TextView tvRecipeName = findViewById(R.id.tvRecipeName);
        TextView tvLink = findViewById(R.id.tvLinkDirection);
        Button goBackBtn = findViewById(R.id.go_back_btn);
        ImageView ivRecipe = findViewById(R.id.ivRecipe);
        RecyclerView rvIngredients = findViewById(R.id.rvIngredients);
        RecyclerView rvNutrients = findViewById(R.id.rvNutrients);
        cbLiked = findViewById(R.id.cbLiked);
        //onclick listener go back button
        goBackBtn.setOnClickListener(v -> startActivity(new Intent(RecipeActivity.this, recipes.class)));
        //Database Stuff
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            likedReference = FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()).child("recipe_screen").child("recipeLiked");
        }
        //getting recipe
        Intent i = getIntent();
        r = i.getParcelableExtra("recipe");
        //load food name and picture
        tvRecipeName.setText(r.getName());
        Picasso.get().load(r.getPicture()).into(ivRecipe);
        //Link Stuff
        tvLink.setText(Html.fromHtml(String.format("<u>%s</u>", tvLink.getText().toString())));
        Linkify.addLinks(tvLink,Pattern.compile(""),r.getSource());
        //adapter stuff
        new IngredientAdapter(ingredientList);
        IngredientAdapter ingAdapter;
        //Ingredient RecyclerView
        for (int j = 0; j < r.getIngredientsList().size(); j++)
            ingredientList.add(r.getIngredient(j));
        for(int j = 0; j < ingredientList.size(); j++)
            ingredientList.set(j, String.format("%s\n", ingredientList.get(j)));
        ingAdapter =  new IngredientAdapter(ingredientList);
        RecyclerView.LayoutManager ingLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvIngredients.setLayoutManager(ingLayoutManager);
        rvIngredients.setItemAnimator(new DefaultItemAnimator());
        rvIngredients.setAdapter(ingAdapter);
        ingAdapter.notifyDataSetChanged();
        //Nutrient RecyclerView
        for (int j = 0; j<r.getNutrientsList().size(); j++){
            nutrientList.add(r.getNutrient(j));
        }
        NutrientAdapter nutAdapter = new NutrientAdapter(nutrientList);
        RecyclerView.LayoutManager nutLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvNutrients.setLayoutManager(nutLayoutManager);
        rvNutrients.setItemAnimator(new DefaultItemAnimator());
        rvNutrients.setAdapter(nutAdapter);
        nutAdapter.notifyDataSetChanged();
        //Checking if recipe is liked on opening screen
        likedReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(r.getName())) cbLiked.setChecked(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        //Adding a liked history
        cbLiked.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                likedReference.child(r.getName()).setValue(r);
                //put the rest of the data in
                for (int data = 0; data < r.getIngredientsList().size(); data++)
                    likedReference.child(r.getName()).child("ingredientsList").child(String.valueOf(data)).setValue(r.getIngredientsList().get(data));
                for (int data = 0; data < r.getNutrientsList().size(); data++) {
                    likedReference.child(r.getName()).child("nutrientsList").child(String.valueOf(data)).child("name").setValue(r.getNutrientsList().get(data).getName());
                    likedReference.child(r.getName()).child("nutrientsList").child(String.valueOf(data)).child("quantity").setValue(r.getNutrientsList().get(data).getQuantity());
                    likedReference.child(r.getName()).child("nutrientsList").child(String.valueOf(data)).child("unit").setValue(r.getNutrientsList().get(data).getUnit());
                }
                likedReference.child(r.getName()).child("picture").setValue(r.getPicture());
            }
            else {

                likedReference.child(r.getName()).removeValue();
            }
        });
    }
}