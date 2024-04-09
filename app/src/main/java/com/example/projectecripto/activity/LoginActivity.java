package com.example.projectecripto.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectecripto.R;
import com.example.projectecripto.activity.MainActivity;
import com.example.projectecripto.model.Contact;

import java.time.LocalDateTime;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {
    EditText etUser;
    EditText etPass;
    Button btnLogin;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        etUser = findViewById(R.id.etUsername);
        etPass = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btLogin);
        btnLogin.setOnClickListener(v -> {
            if(etUser.getText().toString().equals("a") && etPass.getText().toString().equals("a")){
                // sharedPreference to save the user
                Contact contact = new Contact(1, "","admin", "",0, LocalDateTime.now());

//                SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
                Contact.setCurrentContact(contact);
//                editor.putString("username", etUser.getText().toString());
//                editor.putInt("id", 1);
//                editor.apply();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }if (etUser.getText().toString().equals("u") && etPass.getText().toString().equals("u")){
                Contact contact = new Contact(2, "","user", "",0, LocalDateTime.now());
                Contact.setCurrentContact(contact);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            else{
                etUser.setError("Usuario o contraseña incorrectos");
            }
        });

    }
}
