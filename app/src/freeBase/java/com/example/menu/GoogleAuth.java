package com.example.menu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

public class GoogleAuth extends AppCompatActivity {

    private static final int RC_SIGN_IN = 40404;
    private static final String TAG = "GoogleAuth";

    // Клиент для регистрации пользователя через Google
    private GoogleSignInClient googleSignInClient;
    // Кнопка регистрации через Google
    private com.google.android.gms.common.SignInButton buttonSignIn;
    // Кнопка выхода из Google
    private MaterialButton buttonSingOut;
    private MaterialButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_auth);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Получаем клиента для регистрации и данные по клиенту
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Кнопка регистрации пользователя
        buttonSignIn = findViewById(R.id.sign_in_button);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                signIn();
                                            }
                                        }
        );

        // Кнопка выхода
        buttonSingOut = findViewById(R.id.sing_out_button);
        buttonSingOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GoogleAuth.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //      enableSign();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
 //           disableSign();
            updateUI(account.getEmail());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        enableSign();
    }

    private void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(" ");
                        enableSign();
                    }
                });
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
 //           disableSign();
            updateUI(account.getEmail());
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void updateUI(String email) {
        TextView token = findViewById(R.id.token);
        token.setText(email);
    }

    private void enableSign() {
        buttonSignIn.setEnabled(true);
        buttonSingOut.setEnabled(false);
    }

    private void disableSign() {
        buttonSignIn.setEnabled(false);
        buttonSingOut.setEnabled(true);
    }

}

