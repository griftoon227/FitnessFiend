package com.example.grift.fitnessfiend;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class Input_Food_Dialog extends AppCompatDialogFragment {
    private EditText food;
    private EditText amount;
    private FoodDialogListener foodDialogListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try { foodDialogListener = (FoodDialogListener)getTargetFragment(); }
        catch (ClassCastException e) { throw new ClassCastException(context.toString() + "Must implement FoodDialogListener."); }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.input_food_dialog, null);

        food = view.findViewById(R.id.food);
        amount = view.findViewById(R.id.amount);

        builder.setView(view)
            .setCancelable(true)
            .setTitle("Enter a food item to search and the amount consumed.")
            .setNegativeButton("Cancel", (dialog, which) -> { })
            .setPositiveButton("Submit", (dialog, which) -> {
                if (food.getText().toString().equals("") || amount.getText().toString().equals("")) {
                    Toast.makeText(requireActivity().getApplicationContext(), "Required field was left empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                foodDialogListener.getFoodInfo(food.getText().toString(), Integer.parseInt(amount.getText().toString()));
            });

        return builder.create();
    }

    public interface FoodDialogListener {
        void getFoodInfo(String food, int amount);
    }
}