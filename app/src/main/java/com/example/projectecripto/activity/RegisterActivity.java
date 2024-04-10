package com.example.projectecripto.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectecripto.FirebaseHelper;
import com.example.projectecripto.OnContactChangedListener;
import com.example.projectecripto.R;
import com.example.projectecripto.activity.MainActivity;
import com.example.projectecripto.model.Contact;
import com.google.firebase.database.DatabaseError;

import java.time.LocalDateTime;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {
    EditText etUser;
    EditText etPass;
    Button btnLogin;
    TextView tvLogin;
    FirebaseHelper firebaseHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        etUser = findViewById(R.id.etUsername);
        etPass = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btLogin);
        btnLogin.setText("Registra't");
        tvLogin = findViewById(R.id.tvLogin);
        tvLogin.setText("Inicia sessió aqui");
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
        firebaseHelper = new FirebaseHelper();
        btnLogin.setOnClickListener(v -> {
            firebaseHelper.addUser(etUser.getText().toString(), etPass.getText().toString(), new OnContactChangedListener() {
                @Override
                public void onContactChanged(Contact contact) {
                    if (contact != null) {
                        Contact.setCurrentContact(contact);
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        etUser.setError("L'usuari ja existeix");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            } );
        });

    }
}
