package com.example.grift.fitnessfiend;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecipeSearchFragment extends Fragment {
    private EditText etSearch;
    private List<RecipeListItem> recipeList = new ArrayList<>();
    private RecipeAdapter mAdapter;
    private String q;
    private DatabaseReference historyReference;

    public RecipeSearchFragment() { }

    static RecipeSearchFragment newInstance() {
        RecipeSearchFragment fragment = new RecipeSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recipe_search, container, false);
        //Firebase Stuff
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            historyReference = FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()).child("recipe_screen")
                .child("recipeHistory");
        }
        //RecyclerView Stuff
        etSearch = v.findViewById(R.id.etLiked);
        Button btnSearch = v.findViewById(R.id.btnLiked);
        RecyclerView rvRecipes = v.findViewById(R.id.rvLiked);
        mAdapter = new RecipeAdapter(recipeList);
        rvRecipes.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
        rvRecipes.setItemAnimator(new DefaultItemAnimator());
        rvRecipes.setAdapter(mAdapter);
        //Performing a Search
        btnSearch.setOnClickListener(v1 -> {
            recipeList.clear();
            if (!etSearch.getText().toString().isEmpty()) {
                try {
                    q = etSearch.getText().toString();
                    run(q);
                } catch (Exception e){
                    Toast.makeText(requireActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(requireContext().getApplicationContext(), "A valid food entry is required.", Toast.LENGTH_SHORT).show();
            }
        });
        //Going to the recipe
        mAdapter.setOnItemClickListener(position -> {
            Intent i = new Intent(getActivity(),RecipeActivity.class);
            RecipeListItem r = recipeList.get(position);
            //Putting it into the history tab
            historyReference.child(r.getName()).setValue(r);
            //put the rest of the data in
            for (int data = 0; data < r.getIngredientsList().size(); data++)
                historyReference.child(r.getName()).child("ingredientsList").child(String.valueOf(data)).setValue(r.getIngredientsList().get(data));
            for (int data = 0; data < r.getNutrientsList().size(); data++) {
                historyReference.child(r.getName()).child("nutrientsList").child(String.valueOf(data)).child("name").setValue(r.getNutrientsList().get(data).getName());
                historyReference.child(r.getName()).child("nutrientsList").child(String.valueOf(data)).child("quantity").setValue(r.getNutrientsList().get(data).getQuantity());
                historyReference.child(r.getName()).child("nutrientsList").child(String.valueOf(data)).child("unit").setValue(r.getNutrientsList().get(data).getUnit());
            }
            historyReference.child(r.getName()).child("picture").setValue(r.getPicture());

            //Calling Recipe Activity
            i.putExtra("recipe", r);
            startActivity(i);
        });
        return v;
    }

    private void run(String q) {
        OkHttpClient client = new OkHttpClient();

        String apiURL = "https://api.edamam.com/search?q=";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(apiURL + q)).newBuilder();
        String apiID = "538c2da0";
        urlBuilder.addQueryParameter("app_id", apiID);
        String apiKey = "be2556300c684c3cb46abed351626842";
        urlBuilder.addQueryParameter("app_key", apiKey);
        urlBuilder.addQueryParameter("from","0");
        urlBuilder.addQueryParameter("to","100");
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(requireActivity().getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                call.cancel();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String myResponse = Objects.requireNonNull(response.body()).string();
                requireActivity().runOnUiThread(() -> {
                    try {
                        //Getting text into reader
                        JSONObject reader = new JSONObject(myResponse);
                        //Getting Recipe Array into an array
                        JSONArray recipes = reader.getJSONArray("hits");
                        //Putting into the recyclerView
                        for (int i = 0; i < recipes.length(); i++){
                            JSONObject r = recipes.getJSONObject(i).getJSONObject("recipe");
                            JSONArray jarrIngredients = r.getJSONArray("ingredientLines");
                            ArrayList<String> arrIngredients = new ArrayList<>();
                            for (int j = 0; j < jarrIngredients.length(); j++)
                                arrIngredients.add(jarrIngredients.getString(j));
                            JSONObject jNutrients = r.getJSONObject("totalNutrients");
                            ArrayList<RecipeListItem.Nutrient> arrNutrients = new ArrayList<>();
                            JSONObject nut = jNutrients.getJSONObject("ENERC_KCAL");
                            arrNutrients.add(0,addNutrientToArray(nut));
                            nut = jNutrients.getJSONObject("CHOCDF");
                            arrNutrients.add(1,addNutrientToArray(nut));
                            nut = jNutrients.getJSONObject("FAT");
                            arrNutrients.add(2,addNutrientToArray(nut));
                            nut = jNutrients.getJSONObject("PROCNT");
                            arrNutrients.add(3,addNutrientToArray(nut));
                            arrNutrients.get(0).setName("Calories");
                            RecipeListItem item = new RecipeListItem(
                                    r.getString("label"),
                                    r.getString("image"),
                                    r.getString("url"),
                                    arrNutrients,
                                    arrIngredients);
                            recipeList.add(item);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(requireActivity().getApplicationContext(), "Something went wrong with the recipe search.", Toast.LENGTH_SHORT).show();
                    }
                    mAdapter.notifyDataSetChanged();
                });
            }
        });
    }

    private RecipeListItem.Nutrient addNutrientToArray(JSONObject obj){
        try {
            return new RecipeListItem.Nutrient(obj.getString("label"),obj.getDouble("quantity"),obj.getString("unit"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}