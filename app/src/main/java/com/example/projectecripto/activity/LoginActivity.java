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

import com.example.projectecripto.BiometricHelper;
import com.example.projectecripto.FirebaseHelper;
import com.example.projectecripto.OnContactChangedListener;
import com.example.projectecripto.R;
import com.example.projectecripto.activity.MainActivity;
import com.example.projectecripto.model.Contact;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {
    EditText etUser;
    EditText etPass;
    Button btnLogin;
    FirebaseHelper firebaseHelper;
    TextView tvRegister;
    BiometricHelper biometricHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        Log.v("LoginActivity", "Creating login activity");
        etUser = findViewById(R.id.etUsername);
        etPass = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btLogin);
        tvRegister = findViewById(R.id.tvLogin);
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
        biometricHelper = new BiometricHelper(LoginActivity.this);
        firebaseHelper = new FirebaseHelper();
        setTitle("Inici de sessió");
        btnLogin.setOnClickListener(v -> {
            firebaseHelper.login(etUser.getText().toString(), etPass.getText().toString(), new OnContactChangedListener() {
                @Override
                public void onContactChanged(Contact contact) {
                    if (contact != null) {
                        Contact.setCurrentContact(contact);
                        biometricHelper.showBiometricPrompt();
                    } else {
                        etUser.setError("Usuario o contraseña incorrectos");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                }
            });
//            if(etUser.getText().toString().equals("a") && etPass.getText().toString().equals("a")){
//                // sharedPreference to save the user
//                Contact contact = new Contact(1, "","admin", "",0, LocalDateTime.now());
//
////                SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
////                SharedPreferences.Editor editor = sharedPreferences.edit();
//                Contact.setCurrentContact(contact);
////                editor.putString("username", etUser.getText().toString());
////                editor.putInt("id", 1);
////                editor.apply();
//                Intent intent = new Intent(this, MainActivity.class);
//                startActivity(intent);
//            }
//            if (etUser.getText().toString().equals("u") && etPass.getText().toString().equals("u")){
//                Contact contact = new Contact(2, "","user", "",0, LocalDateTime.now());
//                Contact.setCurrentContact(contact);
//                Intent intent = new Intent(this, MainActivity.class);
//                startActivity(intent);
//            }
//            else{
//                etUser.setError("Usuario o contraseña incorrectos");
//            }
        });

    }
}
