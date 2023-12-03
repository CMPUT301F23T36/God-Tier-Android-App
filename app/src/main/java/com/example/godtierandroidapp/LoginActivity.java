package com.example.godtierandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText loginUsername, loginPassword;
    TextView signupRedirect;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.edtLoginUsername);
        loginPassword = findViewById(R.id.edtLoginPassword);
        signupRedirect = findViewById(R.id.signupRedirect);
        loginButton = findViewById(R.id.btnLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validationUsername() | !validationPassword()) {

                }
                else {
                    checkUser();
                }
            }
        });

        signupRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validationUsername() {
        String username = loginUsername.getText().toString();
        if (username.isEmpty()) {
            loginUsername.setError("Username can't be empty");
            loginUsername.requestFocus();
            return false;
        }
        else {
            loginUsername.setError(null);
            return true;
        }
    }

    private boolean validationPassword() {
        String password = loginPassword.getText().toString();
        if (password.isEmpty()) {
            loginPassword.setError("Password can't be empty");
            loginPassword.requestFocus();
            return false;
        }
        else {
            loginPassword.setError(null);
            return true;
        }
    }

    private void checkUser() {
        String username = loginUsername.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(username);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    loginUsername.setError(null);
                    String passwordFromDB = snapshot.child(username).child("pwd").getValue(String.class);

                    if (password.equals(passwordFromDB)) {
                        loginUsername.setError(null);
                        Intent intent = new Intent(LoginActivity.this, ItemListView.class);
                        startActivity(intent);
                    }
                    else {
                        loginPassword.setError("Invalid password");
                        Toast.makeText(LoginActivity.this, "Invalid UserName or Password", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}