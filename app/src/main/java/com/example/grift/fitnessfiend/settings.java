package com.example.grift.fitnessfiend;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class settings extends AppCompatActivity {
    @BindView(R.id.change_email) EditText changeEmail;
    @BindView(R.id.change_password) EditText changePassword;
    @OnClick(R.id.edit_features) void editFeatures() {
        Edit_Screens_Dialog edit_screens_dialog = new Edit_Screens_Dialog();
        edit_screens_dialog.onAttach(getApplicationContext());
        edit_screens_dialog.show(getSupportFragmentManager(), "Edit Screens Dialog");
    }
    @OnClick(R.id.submit_btn) void submitChanges() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            if (changePassword.getText().toString().equalsIgnoreCase("") && changeEmail.getText().toString().equalsIgnoreCase(""))
                Toast.makeText(getApplicationContext(), "No fields were changed.", Toast.LENGTH_SHORT).show();
            else if (!changePassword.getText().toString().equalsIgnoreCase("") && changeEmail.getText().toString().equalsIgnoreCase("")) {
                try {
                    firebaseUser.updatePassword(changePassword.getText().toString());
                    Toast.makeText(getApplicationContext(), "Password successfully changed.", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) { Toast.makeText(getApplicationContext(), "Password could not be changed.", Toast.LENGTH_SHORT).show(); }
            }
            else if (changePassword.getText().toString().equalsIgnoreCase("") && !changeEmail.getText().toString().equalsIgnoreCase("")) {
                try {
                    firebaseUser.updateEmail(changeEmail.getText().toString());
                    Toast.makeText(getApplicationContext(), "Email successfully changed.", Toast.LENGTH_SHORT).show();
                }
                catch(Exception e) { Toast.makeText(getApplicationContext(), "Email could not be changed.", Toast.LENGTH_SHORT).show(); }
            }
            else {
                try {
                    firebaseUser.updatePassword(changePassword.getText().toString());
                    firebaseUser.updateEmail(changeEmail.getText().toString());
                    Toast.makeText(getApplicationContext(), "Email and Password successfully changed.", Toast.LENGTH_SHORT).show();
                }
                catch(Exception e) {
                    Toast.makeText(getApplicationContext(), "There as an issue changing the fields.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        FirebaseApp.initializeApp(this);

        //setup navigation
        findViewById(R.id.main_layout).setOnTouchListener(new NavigationSwipe(this) {
            public void onSwipeRight() {
                startActivity(new Intent(settings.this, MainScreen.class));
            }
            public void onSwipeLeft() { }
            public void onSwipeTop() { }
            public void onSwipeBottom() { }
        });
    }
}
