package com.example.godtierandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Application signup screen. Users will create a unique username and then enter a password twice.
 * If password matches confirmed and username unique, user data will be added to the Firestore
 * database.
 * @author Luke
 * @version 1.0
 * @since 2023-11-10
 */
public class SignUpActivity extends AppCompatActivity {

    EditText signupUsername, signupPassword, signupRePassword;
    Button signupButton;
    TextView loginRedirection;
    FirebaseDatabase database;
    DatabaseReference reference;

    /**
     * Called when activity started through login page. Initializes activity and sets up UI. Sets up
     * signup and login buttons.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signupUsername = findViewById(R.id.edtSignUpUsername);
        signupPassword = findViewById(R.id.edtSignUpPassword);
        signupRePassword = findViewById(R.id.edtSignupRePassword);
        signupButton = findViewById(R.id.btnSignUp);
        loginRedirection = findViewById(R.id.loginRedirect);


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("Users");

                String username = signupUsername.getText().toString();
                String userPassword = signupPassword.getText().toString();
                String userRePassword = signupRePassword.getText().toString();

                if (userPassword.equals(userRePassword)) {
                    Users user = new Users(username, userPassword);
                    reference.child(username).setValue(user);

                    Toast.makeText(SignUpActivity.this, "Sign Up Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else {
                    signupRePassword.setError("Not match your password");
                    signupRePassword.requestFocus();
                }
            }
        });

        loginRedirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}