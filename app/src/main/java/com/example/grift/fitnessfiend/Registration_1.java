package com.example.grift.fitnessfiend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Registration_1 extends AppCompatActivity {
    //binds
    @BindView(R.id.firstname)
    EditText firstname;
    @BindView(R.id.lastname)
    EditText lastname;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.height)
    EditText height;
    @BindView(R.id.weight)
    EditText weight;
    @BindView(R.id.age)
    EditText age;
    @BindView(R.id.gender)
    Spinner gender;
    @BindView(R.id.next_btn)
    Button next_btn;

    @OnClick(R.id.next_btn)
    public void GoToNextPartOfRegistration() {
        String fName = firstname.getText().toString();
        String lName = lastname.getText().toString();
        String emailAcct = email.getText().toString();
        String pWord = password.getText().toString();
        String hght = height.getText().toString();
        String wght = weight.getText().toString();
        String userAge = age.getText().toString();
        if (fName.equals("") || lName.equals("") || emailAcct.equals("") || pWord.equals("") || hght.equals("") ||
                wght.equals("") || userAge.equals("")) { Toast.makeText(getApplicationContext(),R.string.register_screen_error_msg,
                Toast.LENGTH_SHORT).show();
            return;
        }
        //get the firebase auth and save data to the database
        //instance variables
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(emailAcct, pWord).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("users");
                    String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    database.child(UID).child("firstname").setValue(fName);
                    database.child(UID).child("lastname").setValue(lName);
                    database.child(UID).child("height").setValue(hght);
                    database.child(UID).child("weight").setValue(wght);
                    database.child(UID).child("age").setValue(userAge);
                    database.child(UID).child("gender").setValue(gender.getSelectedItem().toString());

                    //setup weekly weight
                    DatabaseReference weeklyWeight = database.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("weight_by_week");
                    weeklyWeight.child("sunday").setValue(wght);
                    weeklyWeight.child("monday").setValue(wght);
                    weeklyWeight.child("tuesday").setValue(wght);
                    weeklyWeight.child("wednesday").setValue(wght);
                    weeklyWeight.child("thursday").setValue(wght);
                    weeklyWeight.child("friday").setValue(wght);
                    weeklyWeight.child("saturday").setValue(wght);

                    DatabaseReference screens = database.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("screens");
                    screens.child("Fitness").setValue(false);
                    screens.child("Gym").setValue(false);
                    screens.child("Macros").setValue(false);
                    screens.child("Reminders").setValue(false);
                    screens.child("Recipe").setValue(false);
                    //go to next part of registering
                    startActivity(new Intent(this, Registration_2.class));
            }
            else {
                Toast.makeText(getApplicationContext(), R.string.register_screen_error_msg_authFailed, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @OnClick(R.id.cancel_btn)
    public void ReturnToMainScreen() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_1);
        ButterKnife.bind(this);
        FirebaseApp.initializeApp(Registration_1.this);
    }
}
