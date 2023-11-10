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

public class SignUpActivity extends AppCompatActivity {

    EditText signupUsername, signupPassword, signupRePassword;
    Button signupButton;
    TextView loginRedirection;
    FirebaseDatabase database;
    DatabaseReference reference;


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

                Users user = new Users(username, userPassword);
                reference.child(username).setValue(user);

                Toast.makeText(SignUpActivity.this, "Sign Up Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
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