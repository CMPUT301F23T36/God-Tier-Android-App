package com.example.godtierandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        WebView webView = findViewById(R.id.webViewPrivacyPolicy);

        // Load the HTML content into the WebView
        String privacyPolicyHtml = getString(R.string.privacy_policy);
        Intent sourceIntent = getIntent();
        if (sourceIntent != null) {
            final String sourceActivityName = sourceIntent.getStringExtra("SOURCE_ACTIVITY");

            webView.loadDataWithBaseURL(null, privacyPolicyHtml, "text/html", "UTF-8", null);

            Button btnContinue = findViewById(R.id.btnContinue);
            btnContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ("LoginActivity".equals(sourceActivityName)) {
                        navigateBackToLoginActivity();
                    } else if ("SignUpActivity".equals(sourceActivityName)) {
                        navigateBackToSignupActivity();
                    }
                }
            });
        }
    }

    private void navigateBackToLoginActivity() {
        Intent intent = new Intent(PrivacyPolicyActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void navigateBackToSignupActivity() {
        Intent intent = new Intent(PrivacyPolicyActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
}