package com.example.grift.fitnessfiend;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.O)
public class macros_meal_fragment extends Fragment implements Input_Food_Dialog.FoodDialogListener {
    private ProgressDialog p;
    private TimeOfDay timeOfMeal;
    private String food;
    private int amount;
    private String date = LocalDate.now().toString();

    //binds
    @BindView(R.id.macros_display_cals) TextView macrosDisplayCals;
    @BindView(R.id.macros_display_protein) TextView macrosDisplayProtein;
    @BindView(R.id.macros_display_fats) TextView macrosDisplayFats;
    @BindView(R.id.macros_display_carbs) TextView macrosDisplayCarbs;
    @BindView(R.id.food_list_display) TextView foodListDisplay;
    @OnClick(R.id.add_food_button) void addFood() {
        Input_Food_Dialog input_food_dialog = new Input_Food_Dialog();
        input_food_dialog.setTargetFragment(macros_meal_fragment.this, 1);
        input_food_dialog.show(requireFragmentManager(), "Input Food Dialog");
    }

    // Required empty public constructor
    public macros_meal_fragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_macros_meal_fragment, container, false);
        ButterKnife.bind(this, view);
        FirebaseApp.initializeApp(requireContext());
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get the meal this fragment is associated with
        int id = this.getId();
        switch (id) {
            case R.id.breakfast_fragment:
                timeOfMeal = TimeOfDay.breakfast;
                break;
            case R.id.lunch_fragment:
                timeOfMeal = TimeOfDay.lunch;
                break;
            case R.id.dinner_fragment:
                timeOfMeal = TimeOfDay.dinner;
                break;
            default:
                timeOfMeal = TimeOfDay.other;
        }
    }

    void instantiateUI() {
        //initialize the fragment's info
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference macros = FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()).child("macros_screen");
            getCurrentFoods(macros, false);
            getCurrentCounts(macros, false, null);
        }
    }

    @Override
    public void getFoodInfo(String food, int amount) {
        this.food = food;
        this.amount = amount;
        MacrosAsync macrosAsync = new MacrosAsync();
        macrosAsync.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateUI(double[] output) {
        if (output[0] == -1) {
            Toast.makeText(getContext(), getString(R.string.food_not_found_msg), Toast.LENGTH_SHORT).show();
            return;
        }
        //grab the values from firebase and update the text accordingly
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference macros = FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()).child("macros_screen");
            //retrieve current foods for the meal
            getCurrentFoods(macros, true);
            getCurrentCounts(macros, true, output);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getCurrentFoods(DatabaseReference macros, boolean newCounts) {
        macros.child(date).child(timeOfMeal.toString()).child("food").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    HashMap<String, Long> data = new HashMap<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if(!Objects.equals(ds.getKey(), "placeholder"))
                            data.put(ds.getKey(), ds.getValue(Long.class));
                    }
                    if (newCounts) {
                        data.put(food, (long) amount);
                        macros.child(date).child(timeOfMeal.toString()).child("food").child(food).setValue(amount);
                    }
                    StringBuilder temp = new StringBuilder();
                    for (HashMap.Entry<String, Long> entry : data.entrySet()) {
                        temp.append(String.format("%s: %s\n", entry.getKey(), entry.getValue()));
                    }
                    foodListDisplay.setText(temp.toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void getCurrentCounts(DatabaseReference macros, boolean newCounts, double[] output) {
        macros.child(date).child(timeOfMeal.toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    double[] data = new double[4];
                    data[0] = dataSnapshot.child(Constants.mealChildren[0]).getValue(Long.class) == null ? 0 :
                        Objects.requireNonNull(dataSnapshot.child(Constants.mealChildren[0]).getValue(Long.class));
                    data[1] = dataSnapshot.child(Constants.mealChildren[1]).getValue(Long.class) == null ? 0 :
                        Objects.requireNonNull(dataSnapshot.child(Constants.mealChildren[1]).getValue(Long.class));
                    data[2] = dataSnapshot.child(Constants.mealChildren[2]).getValue(Long.class) == null ? 0 :
                        Objects.requireNonNull(dataSnapshot.child(Constants.mealChildren[2]).getValue(Long.class));
                    data[3] = dataSnapshot.child(Constants.mealChildren[3]).getValue(Long.class) == null ? 0 :
                        Objects.requireNonNull(dataSnapshot.child(Constants.mealChildren[3]).getValue(Long.class));
                    if (newCounts) {
                        //do calculations
                        double[] newCounts;
                        newCounts = new double[] {
                            data[0] + output[0]*amount,
                            data[1] + output[1]*amount,
                            data[2] + output[2]*amount,
                            data[3] + output[3]*amount
                        };
                        int ctr = 0;
                        for (String child : Constants.mealChildren)
                            macros.child(date).child(timeOfMeal.toString()).child(child).setValue(newCounts[ctr++]);
                        data = newCounts;
                    }
                    String[] temp = new String[4];
                    int tempCounter = 0;
                    for (String child : Constants.mealChildren)
                        temp[tempCounter] = String.format("%s\n%s", child, String.valueOf(Math.round(data[tempCounter++])));
                    macrosDisplayCals.setText(temp[0]);
                    macrosDisplayProtein.setText(temp[1]);
                    macrosDisplayFats.setText(temp[2]);
                    macrosDisplayCarbs.setText(temp[3]);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class MacrosAsync extends AsyncTask<Object, Void, String> {
        private static final String ERROR = "ERROR_MacrosAsync";
        private final String url = getString(R.string.macrosURL);
        private final String host = getString(R.string.macrosHOST);
        private final String apikey = getString(R.string.macrosAPI_KEY);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(getContext());
            p.setMessage(getString(R.string.Progress_Dialog_Macros_Msg));
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected String doInBackground(Object[] objects) {
            try {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                    .url(String.format(url, food.toLowerCase()))
                    .get()
                    .addHeader("x-rapidapi-host", host)
                    .addHeader("x-rapidapi-key", apikey)
                    .build();

                Response response = client.newCall(request).execute();

                return response.isSuccessful() ? response.body().string() : getString(R.string.food_not_found_msg);
            }
            catch (IOException ex) { return null; }
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                p.hide();
                try {
                    JSONObject res = new JSONObject(result);
                    //shows the data as I want it here, but returns it as null. need to do the extraction here and return that
                    JSONArray hints = res.getJSONArray("hints");
                    double[] info = null;
                    for (int i = 0; i < hints.length(); i++) {
                        JSONObject foundObject = hints.getJSONObject(i);
                        //Log.e(TAG, foundObject.getJSONObject("food").getString("label"));
                        if (foundObject.getJSONObject("food").getString("label").toLowerCase().equals(food.toLowerCase())) {
                            JSONObject nutrients = foundObject.getJSONObject("food").getJSONObject("nutrients");
                            info = new double[] {
                                nutrients.getDouble("ENERC_KCAL"),
                                nutrients.getDouble("PROCNT"),
                                nutrients.getDouble("FAT"),
                                nutrients.getDouble("CHOCDF")
                            };
                            break;
                        }
                    }
                    if (info != null) {
                        updateUI(info);
                    }
                    else {
                        updateUI(new double[]{ -1.0, -1.0, -1.0, -1.0 });
                    }
                }
                catch (JSONException e) {
                    Log.e(ERROR, e.getMessage());
                }
                finally {
                    p.dismiss();
                }
            }
            else {
                p.show();
            }
        }
    }
}

