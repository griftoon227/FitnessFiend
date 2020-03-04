package com.example.grift.fitnessfiend;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.lang.String;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    //instance variables
    private FirebaseAuth auth;

    //binds
    @BindView(R.id.copyright) TextView copyright;
    @BindView(R.id.username) EditText username;
    @BindView(R.id.password) EditText password;
    @BindView(R.id.submit_btn) Button submit;
    @OnClick(R.id.register_btn) public void GoToRegisterScreen() {
        startActivity(new Intent(this, Registration_1.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        submit.setOnClickListener(view -> {
            String uName = username.getText().toString();
            String pWord = password.getText().toString();
            if (uName.equals("") || pWord.equals("")) {
                Toast.makeText(getApplicationContext(), R.string.login_screen_error_msg, Toast.LENGTH_SHORT).show();
                return;
            }
            //retrieve the firebase instance
            auth = FirebaseAuth.getInstance();
            //check user credentials
            auth.signInWithEmailAndPassword(uName, pWord).addOnSuccessListener(authResult -> {
                //take the user to the next screen - which includes their apps.
                startActivity(new Intent(MainActivity.this, MainScreen.class));
            }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(),
                R.string.login_screen_error_msg_authFailed, Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        copyright.setText(String.format("%s %s",getString(R.string.copyright_2020_text), Calendar.getInstance().get(Calendar.YEAR)));
    }
}
