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
            if(etUser.getText().toString().equals("admin") && etPass.getText().toString().equals("admin")){
                // sharedPreference to save the user
                SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", etUser.getText().toString());
                editor.putInt("id", 1);
                editor.apply();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }else{
                etUser.setError("Usuario o contraseña incorrectos");
            }
        });

    }
}
