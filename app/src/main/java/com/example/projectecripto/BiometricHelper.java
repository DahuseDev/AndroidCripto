package com.example.projectecripto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.annotation.NonNull;


import com.example.projectecripto.activity.LoginActivity;
import com.example.projectecripto.activity.MainActivity;
import com.example.projectecripto.model.Contact;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BiometricHelper {
    private BiometricPrompt biometricPrompt;
    private Context context;
    private Executor executor;

    public BiometricHelper(AppCompatActivity context) {
        this.context = context;
        executor = Executors.newSingleThreadExecutor();
        biometricPrompt = new BiometricPrompt(context,
                executor,
                new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
    }

    public void showBiometricPrompt() {
        try {
            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric authentication")
                    .setSubtitle("Log in using your biometric credential")
                    .setNegativeButtonText("Cancel")
                    .build();
            biometricPrompt.authenticate(promptInfo);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
