package com.ctse.androidgamereviewer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/**
 * A login screen which acts as the starting point in the authentication flow.
 * Authentication is handled by Firebase authentication.
 *
 * See <a href="https://firebase.google.com/docs/auth/android/firebaseui">
 * FirebaseUI documentation
 * </a> for more information.
 */
public class LoginActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 1994;

    Button loginButton;

    // Choose FireBase authentication providers
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        loginButton = findViewById(R.id.btn_login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Login clicked");
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {

                // Successfully signed in
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

                setResult(RESULT_OK);
                finish();

            } else {

                // Sign in failed. If response is null the user canceled the
                if (null != response) {
                    Toast.makeText(this, "Error in log in", Toast.LENGTH_SHORT).show();
                    response.getError().printStackTrace();
                } else {
                    Toast.makeText(this, "Login canceled", Toast.LENGTH_SHORT).show();
                }
                setResult(RESULT_OK);
                finish();

            }
        }
    }
}
