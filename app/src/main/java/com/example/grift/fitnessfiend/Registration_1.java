package com.example.grift.fitnessfiend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        if (fName.equals("") || lName.equals("") || emailAcct.equals("") || pWord.equals("") || hght.equals("") ||
                wght.equals("")) { Toast.makeText(getApplicationContext(),R.string.register_screen_error_msg,
                Toast.LENGTH_SHORT).show();
            return;
        }
        //get the firebase auth and save data to the database
        //instance variables
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(emailAcct, pWord).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //enter the data into the database
                DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("users");
                database.child(emailAcct.substring(0, emailAcct.indexOf('@'))).push().setValue(null);
                database.child(emailAcct.substring(0, emailAcct.indexOf('@'))).child("firstname").setValue(fName);
                database.child(emailAcct.substring(0, emailAcct.indexOf('@'))).child("lastname").setValue(lName);
                database.child(emailAcct.substring(0, emailAcct.indexOf('@'))).child("height").setValue(hght);
                database.child(emailAcct.substring(0, emailAcct.indexOf('@'))).child("weight").setValue(wght);

                DatabaseReference screens = database.child(emailAcct.substring(0, emailAcct.indexOf('@'))).child("screens");
                screens.child("Fitness").setValue(false);
                screens.child("Gym").setValue(false);
                screens.child("Macros").setValue(false);
                screens.child("Medicine").setValue(false);
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
