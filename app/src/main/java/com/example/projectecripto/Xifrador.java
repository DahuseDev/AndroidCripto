package com.example.projectecripto;

import android.util.Log;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Xifrador {
    private static KeyPair keyPair;
    private static PublicKey serverPublicKey;


    private static KeyPair randomGenerate(int len) {
        KeyPair keys = null;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(len);
            keys = keyGen.genKeyPair();
        } catch (Exception ex) {
            System.err.println("Generador no disponible.");
        }
        return keys;
    }
    public static void generateKeyPair() {
        keyPair = randomGenerate(1024);
    }

    public static PublicKey getPublicKey() {
        if(keyPair == null) generateKeyPair();
        return keyPair.getPublic();
    }

    public static void setServerPublicKey(PublicKey serverPublicKey) {
        Xifrador.serverPublicKey = serverPublicKey;
    }

    // https://drive.google.com/drive/folders/1R48zBECVVToy79lXJHQ4Zt2gF5GHsrX4

    public static byte[][] encryptWrappedData(String data) {
        byte[] byteData = data.getBytes();
        byte[][] encWrappedData = new byte[2][];
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
            SecretKey sKey = kgen.generateKey();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, sKey);
            byte[] encMsg = cipher.doFinal(byteData);
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            //cipher.init(Cipher.WRAP_MODE, keyPair.getPublic());
            cipher.init(Cipher.WRAP_MODE, serverPublicKey);
            byte[] encKey = cipher.wrap(sKey);
            encWrappedData[0] = encMsg;
            encWrappedData[1] = encKey;
        } catch (Exception ex) {
            System.err.println("Ha succeït un error xifrant: " + ex);
        }
        System.out.println(encWrappedData);
        return encWrappedData;
    }
    public static String decryptWrappedData(byte[][] data) {
        byte[] decryptedData = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.UNWRAP_MODE, keyPair.getPrivate());
            Log.v("Xifrador","Public key: " + publicKeyToString(keyPair.getPublic()));
            Log.v("Xifrador","Obtenint clau secreta desxifrada...");
            SecretKey sKey = (SecretKey) cipher.unwrap(data[1], "AES", Cipher.SECRET_KEY);
            Log.v("Xifrador", "Clau secreta desxifrada: ");
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, sKey);
            decryptedData = cipher.doFinal(data[0]);
            Log.v("Xifrador", "Missatge desxifrat: " + new String(decryptedData));
        } catch (InvalidKeyException e) {
            System.err.println("Invalid key for decryption: " + e);
        } catch (BadPaddingException e) {
            System.err.println("Bad padding for decryption: " + e);
        } catch (Exception ex) {
            System.err.println("An error occurred during decryption: " + ex);
        }
        return new String(decryptedData);
    }

    public static PublicKey getPublicKeyFromString(String key){
        try{
            byte[] byteKey = Base64.getDecoder().decode(key.getBytes());
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePublic(X509publicKey);
        }catch (Exception e) {
            throw new RuntimeException("Error while converting string to PublicKey: " + e);
        }
    }

    public static String publicKeyToString(PublicKey key){
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

}
